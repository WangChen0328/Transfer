package web;

import ch.qos.logback.core.util.FileUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import netty.util.MD5FileUtil;
import web.pojo.NettyMessage;
import web.pojo.WebUploader;
import web.util.HeaderUtil;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wangchen
 * @date 2018/3/19 14:07
 */
public class HttpFileUploadServerHandler extends SimpleChannelInboundHandler<NettyMessage> {

    private String localPath = System.getProperty("user.home");

    private String FileServer = "FileServer";

    RandomAccessFile randomAccessFile;

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
        WebUploader header = msg.getHeader();
        byte[] body = msg.getBody();

        String localFilePath = mkdirs(header.getName());

        randomAccessFile = new RandomAccessFile(new File(localFilePath), "rw");
        randomAccessFile.seek(header.getStart());
        randomAccessFile.write(body);
        randomAccessFile.close();

        DefaultFullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                        Unpooled.copiedBuffer(MD5FileUtil.getMD5String(localFilePath), CharsetUtil.UTF_8));
        HeaderUtil.setHeader(response);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        randomAccessFile.close();
        ctx.close();
    }

    /**
     * 默认的系统下载目录
     * @param filePath
     * @return
     */
    private String mkdirs(String filePath) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String localFilePath = localPath + File.separator + FileServer + File.separator + date + File.separator + filePath;
        FileUtil.createMissingParentDirectories(new File(localFilePath));
        return localFilePath;
    }
}
