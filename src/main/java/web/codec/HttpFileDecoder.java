package web.codec;

import com.sun.deploy.util.StringUtils;
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
import web.util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
             * 分隔符
             */
            String regex = request.headers().get(HttpHeaders.Names.CONTENT_TYPE).split("----")[1];
            /**
             * 获得文件属性
             */
            ByteBuf content = request.content();
            byte[] bytes = new byte[content.readableBytes()];
            content.readBytes(bytes);
            String[] attrs  = new String(bytes, "UTF-8").split("------" + regex);
            /**
             * 文件属性
             */
            WebUploader webUploader = new WebUploader();
            /**
             * 实例属性
             */
            for (String attr : attrs) {
                attr = RegexUtil.StringFilter(attr);
                Matcher matcher = pattern.matcher(attr);
                if (matcher.find()) {
                    ReflectionUtil.setFieldValue(webUploader, matcher.group(1), matcher.group(2));
                }
            }
            /**
             * call next
             */
            out.add(new NettyMessage(webUploader, null));
        }
    }
}
