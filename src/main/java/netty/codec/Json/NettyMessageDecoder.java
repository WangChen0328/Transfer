package netty.codec.Json;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import netty.pojo.NettyMessage;

import java.util.List;

/**
 * @author wangchen
 * @date 2018/4/2 13:44
 * .addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)))
 * .addLast(new NettyMessageDecoder())
 * .addLast(new ObjectEncoder())
 * .addLast(new NettyMessageEncoder())
 */
public class NettyMessageDecoder extends MessageToMessageDecoder<Object> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Object msg, List<Object> list) throws Exception {
        NettyMessage message = JSON.parseObject(msg.toString(), NettyMessage.class);
        Object body = message.getBody();
        if (body != null) {
            Class bodyClass = message.getBodyClass();
            Object obj = JSON.parseObject(body.toString(), bodyClass);
            message.setBody(obj);
        }
        list.add(message);
    }
}
