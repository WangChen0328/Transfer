package netty.server;

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
 * @date 2018/3/21 15:18
 */
public class HeartBeatRespHandler extends ChannelHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(HeartBeatRespHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        /**
         * 返回客户端发送心跳应答
         */
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
            log.info("服务端应答心跳 ：" + new Date().toString());
            /**
             * 应答客户端心跳
             */
            ctx.writeAndFlush(NettyMessageUtil.buildNettyMessage(MessageType.HEARTBEAT_RESP.value()));
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireChannelRead(cause);
    }
}
