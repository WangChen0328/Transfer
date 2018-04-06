package netty.codec.marshalling.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import netty.pojo.Header;
import netty.pojo.NettyMessage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/**
 * @author wangchen
 * @date 2018/3/20 8:59
 * 消息编码器
 */
public class NettyMessageEncoder extends MessageToByteEncoder<NettyMessage> {

    MarshallingEncoder marshallingEncoder;

    public NettyMessageEncoder() throws IOException {
        this.marshallingEncoder = new MarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage msg, ByteBuf sendBuf) {
        Header header = msg.getHeader();
        if (msg == null || header == null) {
            throw new UnsupportedOperationException("消息或消息头不能为空！");
        }
        sendBuf.writeInt(header.getCrcCode());
        sendBuf.writeInt(header.getLength());
        sendBuf.writeLong(header.getSessionID());
        sendBuf.writeByte(header.getType());
        sendBuf.writeByte(header.getPriority());
        /**
         * 记录附件的数量，解码时通过数量来循环取值
         */
        sendBuf.writeInt(header.getAttachment().size());

        String key = null;
        byte[] keyArray = null;
        Object value = null;

        for (Iterator<String> it = header.getAttachment().keySet().iterator(); it.hasNext(); ){
            key = it.next();
            try {
                keyArray = key.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
            value = header.getAttachment().get(key);
            /**
             * 使用 Jboss Marshalling 序列化 value
             * 加入到 sendBuf 中
             */
            try {
                marshallingEncoder.encode(value, sendBuf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        key = null;
        keyArray = null;
        value = null;
        if (msg.getBody() != null) {
            try {
                marshallingEncoder.encode(msg.getBody(), sendBuf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            sendBuf.writeInt(0);
        }
        sendBuf.setInt(4, sendBuf.readableBytes() - 8);
    }
}
