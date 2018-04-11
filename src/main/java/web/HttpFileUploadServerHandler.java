package web;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import web.pojo.NettyMessage;
import web.pojo.WebUploader;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author wangchen
 * @date 2018/3/19 14:07
 */
public class HttpFileUploadServerHandler extends SimpleChannelInboundHandler<NettyMessage> {


    @Override
    protected void messageReceived(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
        WebUploader header = msg.getHeader();
        Object body = msg.getBody();



        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
