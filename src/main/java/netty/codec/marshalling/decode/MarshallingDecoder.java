package netty.codec.marshalling.decode;


import io.netty.buffer.ByteBuf;
import netty.codec.marshalling.MarshallingCodecFactory;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

/**
 * @author wangchen
 * @date 2018/3/20 15:09
 */
public class MarshallingDecoder {
    private final Unmarshaller unmarshaller;

    public MarshallingDecoder() throws IOException {
        unmarshaller = MarshallingCodecFactory.buildUnMarshalling();
    }

    protected Object decode(ByteBuf in) throws Exception {
        /**
         * 不知道在哪设置的 对象容量
         */
        int objectSize = in.readInt();
        /**
         * 获取 当前对象的 缓冲区的位置
         * 从 可读位置(当前读到的位置，也是对象的开始位置)
         * 到 对象的大小(文件的容量，对应的结尾位置)
         * 截取了ByteBuf中 当前对象的 那块空间容量
         */
        ByteBuf buf = in.slice(in.readerIndex(), objectSize);
        ByteInput input = new ChannelBufferByteInput(buf);
        try {
            unmarshaller.start(input);
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            in.readerIndex(in.readerIndex() + objectSize);
            return obj;
        } finally {
            unmarshaller.close();
        }
    }
}
