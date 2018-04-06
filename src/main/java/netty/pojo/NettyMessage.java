package netty.pojo;

/**
 * @author wangchen
 * @date 2018/3/19 16:22
 */
public class NettyMessage {
    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息体
     */
    private Object body;

    /**
     * 消息体类型, 使用 fastjson 必须指定解码类型
     */
    private Class bodyClass;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public NettyMessage setBody(Object body) {
        this.body = body;
        return this;
    }

    public Class getBodyClass() {
        return bodyClass;
    }

    public void setBodyClass(Class bodyClass) {
        this.bodyClass = bodyClass;
    }
}
