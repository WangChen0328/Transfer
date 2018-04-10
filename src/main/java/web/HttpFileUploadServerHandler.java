package web;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import netty.pojo.NettyMessage;
import web.pojo.WebUploader;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author wangchen
 * @date 2018/3/19 14:07
 */
public class HttpFileUploadServerHandler extends SimpleChannelInboundHandler<WebUploader> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, WebUploader msg) throws Exception {

    }
}
