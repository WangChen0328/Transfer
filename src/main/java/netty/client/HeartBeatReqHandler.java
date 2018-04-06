package netty.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import netty.MessageType;
import netty.pojo.NettyMessage;
import netty.util.NettyMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author wangchen
 * @date 2018/3/21 14:31
 */
public class HeartBeatReqHandler extends ChannelHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(HeartBeatReqHandler.class);
    
    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        /**
         * 握手成功
         */
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            /**
             * 每5秒向服务器 发送 Ping
             */
            heartBeat = ctx.executor().scheduleAtFixedRate(
                    new HeartBeatReqHandler.HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
            /**
             * 向下传递握手成功消息
             */
            ctx.fireChannelRead(msg);
        }
        /**
         * 心跳验证成功
         */
        else if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {
            log.info("接到服务端心跳回应 ：" + new Date().toString());
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireChannelRead(cause);
    }

    private class HeartBeatTask implements Runnable {

        private final ChannelHandlerContext ctx;

        public HeartBeatTask(ChannelHandlerContext context) {
            this.ctx = context;
        }

        @Override
        public void run() {
            log.info("客户端发送心跳验证 ：" + new Date().toString());
            ctx.writeAndFlush(NettyMessageUtil.buildNettyMessage(MessageType.HEARTBEAT_REQ.value()));
        }
    }
}
