package web;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.File;
import java.net.InetSocketAddress;

/**
 * @author wangchen
 * @date 2018/3/7 15:30
 */
public class HttpFileDownloadServer {
    private static final String DEFAULT_URL = System.getProperty("user.home") + File.separator + "FileServer" + File.separator + "Web";

    public void run(final int port) throws  Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    /**
                                     * Http 解码器
                                     */
                                    .addLast("http-decoder", new HttpRequestDecoder())
                                    /**
                                     * 将多个消息转换为单一的 FullHttpRequest 或者 FullHttpResponse
                                     */
                                    .addLast("http-aggregator", new HttpObjectAggregator(10000 * 1024))
                                    /**
                                     * Http 编码器
                                     */
                                    .addLast("http-encoder", new HttpResponseEncoder())
                                    /**
                                     * 大文件传输优化，防止内存溢出
                                     */
                                    .addLast("http-chunked", new ChunkedWriteHandler())
                                    /**
                                     * 处理业务逻辑
                                     */
                                    .addLast("fileServerHandler", new HttpFileDownloadServerHandler(DEFAULT_URL));
                        }
                    });
            ChannelFuture future = bootstrap.bind(new InetSocketAddress(port)).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
