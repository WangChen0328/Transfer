package netty.codec.Json;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.pojo.NettyMessage;

import java.util.List;

/**
 * @author wangchen
 * @date 2018/4/2 13:43
 * .addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)))
 * .addLast(new NettyMessageDecoder())
 * .addLast(new ObjectEncoder())
 * .addLast(new NettyMessageEncoder())
 */
public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage message, List<Object> list) throws Exception {
        Object body = message.getBody();
        if (body != null) {
            message.setBody(JSON.toJSONString(body));
            message.setBodyClass(body.getClass());
        }
        list.add(JSON.toJSONString(message));
    }
}
