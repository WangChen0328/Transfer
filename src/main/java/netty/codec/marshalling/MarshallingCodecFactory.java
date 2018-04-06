package netty.codec.marshalling;

import org.jboss.marshalling.*;

import java.io.IOException;

/**
 * @author wangchen
 * @date 2018/3/20 14:08
 */
public class MarshallingCodecFactory {
    /**
     * 创建Jboss Marshaller
     * @return
     * @throws IOException
     */
    public static Marshaller buildMarshalling() throws IOException {
        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
        return marshaller;
    }
    /**
     * 创建Jboss Unmarshaller
     *
     * @return
     * @throws IOException
     */
    public static Unmarshaller buildUnMarshalling() throws IOException {
        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        Unmarshaller unmarshaller = marshallerFactory.createUnmarshaller(configuration);
        return unmarshaller;
    }
}
