package netty.server;

import ch.qos.logback.core.util.FileUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ResourceLeakDetector;
import netty.MessageType;
import netty.model.RequestFile;
import netty.model.ResponseFile;
import netty.pojo.NettyMessage;
import netty.util.MD5FileUtil;
import netty.util.NettyMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.util.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangchen
 * @date 2018/3/28 15:15
 */
public class FileUploadRepsHandler extends SimpleChannelInboundHandler<NettyMessage> {

    private static final Logger log = LoggerFactory.getLogger(FileUploadRepsHandler.class);

    /**
     * 文件默认存储地址, 用户当前目录
     */
    private String localPath = System.getProperty("user.home") + File.separator + "FileServer" + File.separator + "Netty";

    private Map<String, RandomAccessFile> randomAccessFiles;

    public FileUploadRepsHandler(String localPath) {
        this.randomAccessFiles = new ConcurrentHashMap<>();
        if (localPath != null && localPath.length() > 0) {
            this.localPath = localPath + File.separator + "FileServer" + File.separator + "Netty";
        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
        /**
         * 接收客户端询问
         */
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.SERVICE_REQ.value()
                && message.getHeader().getAttachment() != null
                && "ask".equals(message.getHeader().getAttachment().get("file"))) {

            log.info("接收客户端询问文件位置！");

            RequestFile request = (RequestFile) message.getBody();
            String fileMd5 = request.getFileMd5();
            String fileName = request.getFileName();
            long fileSize = request.getFileSize();

            String localFilePath = mkdirs(fileMd5 + "_" + fileName);

            /**
             * 文件位置
             */
            File file = new File(localFilePath);
            /**`
             * 返回客户端
             */
            ResponseFile responseFile = new ResponseFile();
            if (file.exists()) {

                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

                boolean complete = fileSize == randomAccessFile.length() && fileMd5.equals(MD5FileUtil.getMD5String(file));
                /**
                 * 已经传输完毕
                 */
                if (complete) {
                    log.info("已经传输完毕！");
                    responseFile.setComplete(true);
                    responseFile.setRequestFile(request);
                }
                /**
                 * 需要断点续传
                 */
                else {
                    log.info("需要断点续传！");
                    responseFile.setComplete(false);
                    responseFile.setRequestFile(request);
                    responseFile.setEndPosition(randomAccessFile.length());
                }

                randomAccessFile.close();
            }
            /**
             * 新文件
             */
            else {
                log.info("新文件！");
                responseFile.setComplete(false);
                responseFile.setRequestFile(request);
                responseFile.setEndPosition((long) 0);
            }

            ctx.writeAndFlush(
                    NettyMessageUtil.buildNettyMessage(MessageType.SERVICE_RESP.value(),
                            Collections.singletonMap("file","transfer"),
                            responseFile));
        }
        /**
         * 传输数据包
         */
        else if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.SERVICE_REQ.value()
                && message.getHeader().getAttachment() != null
                && "transfer".equals(message.getHeader().getAttachment().get("file"))){

            log.info("写入文件！");

            RequestFile request = (RequestFile) message.getBody();

            long fileSize = request.getFileSize();
            String fileMd5 = request.getFileMd5();
            String fileName = request.getFileName();

            String localFilePath = mkdirs(fileMd5 + "_" + fileName);

            File file = new File(localFilePath);

            RandomAccessFile randomAccessFile = getAccessFile(file.getPath(), file);

            randomAccessFile.seek(request.getStartPosition());
            randomAccessFile.write(request.getContent());

            ResponseFile responseFile = new ResponseFile();

            if (fileSize == randomAccessFile.length() && fileMd5.equals(MD5FileUtil.getMD5String(file))) {
                responseFile.setComplete(true);
                responseFile.setRequestFile(request);
                responseFile.setProgress(math(randomAccessFile.length(), fileSize));
                randomAccessFile.close();
                log.info("关闭读取[" + file.getName() + "]文件流");
                this.randomAccessFiles.remove(file.getPath());
                log.info("删除缓存读取信息！");
            } else {
                responseFile.setComplete(false);
                responseFile.setRequestFile(request);
                responseFile.setEndPosition(randomAccessFile.getFilePointer());
                responseFile.setProgress(math(randomAccessFile.length(), fileSize));
                if (fileSize == randomAccessFile.length() && fileMd5.equals(MD5FileUtil.getMD5String(file))) {
                    randomAccessFile.close();
                    log.info("关闭读取[" + file.getName() + "]文件流");
                    this.randomAccessFiles.remove(file.getPath());
                    log.info("删除缓存读取信息！");
                }
            }
            ctx.writeAndFlush(
                    NettyMessageUtil.buildNettyMessage(MessageType.SERVICE_RESP.value(),
                            Collections.singletonMap("file","transfer"),
                            responseFile));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws IOException {
        cause.printStackTrace();
        if (this.randomAccessFiles.size() > 0) {
            for (Iterator<RandomAccessFile> it = this.randomAccessFiles.values().iterator(); it.hasNext();) {
                RandomAccessFile next = it.next();
                if (next != null) {
                    next.close();
                }
            }
        }
        ctx.close();
    }

    /**
     * 返回百分比
     */
    private static String math(long divisor1, long divisor2) {
        double percent = Double.parseDouble(String.valueOf(divisor1))/ Double.parseDouble(String.valueOf(divisor2));
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(0);
        String format = nt.format(percent);
        return format.substring(0, format.indexOf("%"));
    }

    private RandomAccessFile getAccessFile(String fileName, File file) throws FileNotFoundException {
        if (this.randomAccessFiles.containsKey(fileName)) {
            return this.randomAccessFiles.get(fileName);
        } else {
            RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
            this.randomAccessFiles.put(fileName, accessFile);
            return accessFile;
        }
    }

    /**
     * 默认的系统下载目录
     * @param fileName
     * @return
     */
    private String mkdirs(String fileName) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String localFilePath = localPath  + File.separator + date + File.separator + fileName;
        FileUtil.createMissingParentDirectories(new File(localFilePath));
        return localFilePath;
    }
}
