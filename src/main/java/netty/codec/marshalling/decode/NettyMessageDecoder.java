package netty.codec.marshalling.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import netty.pojo.Header;
import netty.pojo.NettyMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangchen
 * @date 2018/3/20 15:10
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    MarshallingDecoder marshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        marshallingDecoder = new MarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        /**
         * 定长分隔符 解码
         */
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        /**
         * 继承 LengthFieldBasedFrameDecoder
         * 按照空格结尾 粘粘包处理，发送一行接收一行
         */
        header.setCrcCode(frame.readInt());
        header.setLength(frame.readInt());
        header.setSessionID(frame.readLong());
        header.setType(frame.readByte());
        header.setPriority(frame.readByte());

        int attachmentSize = frame.readInt();
        if (attachmentSize > 0) {
            Map<String, Object> attach = new HashMap<>(attachmentSize);
            int keyLength = 0;
            byte[] keyArray = null;
            String key = null;
            /**
             * 取附件
             */
            for (int i = 0; i < attachmentSize; i++) {
                keyLength = frame.readInt();
                keyArray = new byte[keyLength];
                /**
                 * Key的数组
                 */
                frame.readBytes(keyArray);
                /**
                 * Key的值
                 */
                key = new String(keyArray, "UTF-8");
                attach.put(key, marshallingDecoder.decode(frame));
            }
            keyArray = null;
            key = null;
            header.setAttachment(attach);
        }
        if (frame.readableBytes() > 4) {
            message.setBody(marshallingDecoder.decode(frame));
        }
        message.setHeader(header);
        return message;
    }
}
