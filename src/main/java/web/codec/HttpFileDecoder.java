package web.codec;

import com.sun.deploy.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
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

        else if (request.getMethod() == HttpMethod.GET) {
            HeaderUtil.sendError(ctx, HttpResponseStatus.FORBIDDEN);
        }

        else if (request.getMethod() == HttpMethod.POST) {

            String s1 = request.headers().get(HttpHeaders.Names.CONTENT_TYPE);

            String[] split = s1.split("----");

            String regex = split[1];

            ByteBuf content = request.content();

            int readableBytes = content.readableBytes();

            byte[] bytes = new byte[readableBytes];

            content.readBytes(bytes);

            String s = new String(bytes, "UTF-8");

            String[] split1 = s.split("------" + regex);

            Pattern pattern = Pattern.compile("Content-Disposition:form-data;name=\"([a-z]+)\"(.+)");

            WebUploader webUploader = new WebUploader();

            Class<? extends WebUploader> uploaderClass = webUploader.getClass();

            for (String str : split1) {

                str = RegexUtil.StringFilter(str);

                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {

                    String name = matcher.group(1);
                    String value = matcher.group(2);

                    ReflectionUtil.setField(webUploader, name, value);
                }
            }

            out.add(webUploader);
        }
    }
    
    public static void main(String[] args){
        String str = "\\r\\nContent-Disposition: form-data; name=\"id\"\\r\\n\\r\\nWU_FILE_0\\r\\n------";
        boolean matches = str.matches("\\r\\nContent-Disposition: form-data; name=\"id\"\\r\\n\\r\\nWU_FILE_0\\r\\n------");
        System.out.println(matches);
    }
}
