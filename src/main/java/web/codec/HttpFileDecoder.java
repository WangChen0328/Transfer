package web.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
import web.pojo.NettyMessage;
import web.pojo.WebUploader;
import web.util.HeaderUtil;
import web.util.ReflectionUtil;
import web.util.RegexUtil;

import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangchen
 * @date 2018/4/8 15:22
 */
public class HttpFileDecoder extends MessageToMessageDecoder<FullHttpRequest> {

    Pattern pattern = Pattern.compile("Content-Disposition:form-data;name=\"([a-zA-Z]+?)\"(.+)");

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest request, List<Object> out) throws Exception {

        /**
         * 预检通道
         */
        if (request.getMethod() == HttpMethod.OPTIONS) {

            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

            HeaderUtil.setHeader(response);

            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
        /**
         * 不支持Get请求
         */
        else if (request.getMethod() == HttpMethod.GET) {
            HeaderUtil.sendError(ctx, HttpResponseStatus.FORBIDDEN);
        }
        /**
         * POST
         */
        else if (request.getMethod() == HttpMethod.POST) {
            /**
             * URL
             */
            String uri = request.getUri();
            uri = URLDecoder.decode(uri, "UTF-8");
            /**
             * head
             */
            WebUploader webUploader = new WebUploader();
            /**
             * webUploader attr
             */
            for (String attr : uri.split("&")) {
                String name = attr.split("=")[0];
                String value = attr.split("=")[1];
                int index = name.indexOf("?");
                name = index > 0 ? name.substring(index + 1, name.length()) : name;
                ReflectionUtil.setFieldValue(webUploader, name, value);
            }
            /**
             * body
             */
            ByteBuf content = request.content();
            int readableBytes = content.readableBytes();
            byte[] bytes = new byte[readableBytes];
            content.readBytes(bytes);
            /**
             * call next
             */
            out.add(new NettyMessage(webUploader, bytes));
        }
    }
}
