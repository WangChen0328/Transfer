package web;

import ch.qos.logback.core.util.FileUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.pojo.NettyMessage;
import web.pojo.WebUploader;
import web.util.Base64Util;
import web.util.HeaderUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wangchen
 * @date 2018/3/19 14:07
 */
public class HttpFileUploadServerHandler extends SimpleChannelInboundHandler<NettyMessage> {

    private static final Logger log = LoggerFactory.getLogger(HttpFileUploadServerHandler.class);

    private String SecretKey = "6950810f0d2bba97a6f710c7b965b84e";

    private String localPath = System.getProperty("user.home") + File.separator + "FileServer" + File.separator + "Web";

    RandomAccessFile randomAccessFile;

    public HttpFileUploadServerHandler(String localPath) {
        if (localPath != null && localPath.length() > 0) {
            this.localPath = localPath + File.separator + "FileServer" + File.separator + "Web";
        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, NettyMessage msg) {
        WebUploader header = msg.getHeader();
        byte[] body = msg.getBody();

        String localFilePath = mkdirs(header.getId() + "_$_" + header.getName());

        try {
            randomAccessFile = new RandomAccessFile(new File(localFilePath), "rw");
            randomAccessFile.seek(header.getStart());
            randomAccessFile.write(body);
            randomAccessFile.close();
        } catch (FileNotFoundException e) {
            log.error("找不到文件 [" + e.getMessage() + "] !");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("IO异常 [" + e.getMessage() + "] !");
            e.printStackTrace();
        } finally {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Base64Util base64Util = null;
        try {
            base64Util = new Base64Util(SecretKey, "utf-8");
        } catch (Exception e) {
            log.error("创建BASE64 错误 [" + e.getMessage() + "] !");
            e.printStackTrace();
        }

        String returnPath = "";
        try {
            returnPath = base64Util.encode(localFilePath);
        } catch (Exception e) {
            log.error("返回路径，转BASE64 错误 [" + e.getMessage() + "] !");
            e.printStackTrace();
        }

        DefaultFullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                        Unpooled.copiedBuffer(returnPath, CharsetUtil.UTF_8));
        HeaderUtil.setHeader(response);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        try {
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ctx.close();
    }

    /**
     * 默认的系统下载目录
     * @param filePath
     * @return
     */
    private String mkdirs(String filePath) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String localFilePath = localPath  + File.separator + date + File.separator + filePath;
        FileUtil.createMissingParentDirectories(new File(localFilePath));
        return localFilePath;
    }
}
