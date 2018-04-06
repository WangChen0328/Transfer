package netty.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import netty.MessageType;
import netty.pojo.NettyMessage;
import netty.util.NettyMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author wangchen
 * @date 2018/3/21 13:44
 */
public class LoginAuthReqHandler extends ChannelHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(LoginAuthReqHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端请求握手：" + new Date().toString());
        ctx.writeAndFlush(NettyMessageUtil.buildNettyMessage(MessageType.LOGIN_REQ.value()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        /**
         * 是否为握手应答消息
         */
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            byte loginResult = Byte.parseByte(message.getBody().toString());
            /**
             * 约定如果捂手成功为0 否则失败 关闭链路
             */
            if (loginResult != (byte) 0) {
                log.info("握手失败,没有权限,未被列入可以访问的名单！");
                ctx.close();
            }
            log.info("客户端收到，服务端应答。三次握手完成：" + new Date().toString());
            ctx.fireChannelRead(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireChannelRead(cause);
    }

}
