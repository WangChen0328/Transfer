package web.util;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * @author wangchen
 * @date 2018/4/10 15:31
 */
public class HeaderUtil {
    /**
     * 适配 WebUploader.js
     * 设置消息头
     * @param response
     */
    public static void setHeader(DefaultFullHttpResponse response) {
        /**
         * 不适用证书
         */
        response.headers().set(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_CREDENTIALS, false);
        /**
         * 允许所有域名访问
         * 生产环境绝对不允许设置为“*”
         */
        response.headers().set(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        /**
         * 消息头
         */
        response.headers().set(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.Names.CONTENT_TYPE);
        /**
         * GET、POST、OPTIONS ...
         */
        response.headers().set(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_METHODS, "*");
        /**
         * 编码格式
         */
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json;charset=UTF-8");
    }

    /**
     * 返回错误信息， 浏览器接受
     * 消息体类型 "text/html;charset=UTF-8"
     * @param ctx
     * @param status
     */
    public static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer("Failure: " + status.toString(), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
