package netty.codec.marshalling.encode;

import io.netty.buffer.ByteBuf;
import netty.codec.marshalling.MarshallingCodecFactory;
import org.jboss.marshalling.Marshaller;

import java.io.IOException;

/**
 * @author wangchen
 * @date 2018/3/20 14:03
 */
public class MarshallingEncoder {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
    private final Marshaller marshaller;

    public MarshallingEncoder() throws IOException {
        marshaller = MarshallingCodecFactory.buildMarshalling();
    }

    protected void encode(Object msg, ByteBuf out) throws Exception {
        try {
            /**
             * 可读的区域是下标区间是[readerIndex，writeIndex)，
             * 可写区间的是[writerIndex,capacity-1]，
             */
            int lengthPos = out.writerIndex();
            out.writeBytes(LENGTH_PLACEHOLDER);
            ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
            marshaller.start(output);
            marshaller.writeObject(msg);
            marshaller.finish();
            /**
             * 对象的实际容量
             */
            out.setInt(lengthPos,out.writerIndex() - lengthPos - 4);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            marshaller.close();
        }
    }
}
