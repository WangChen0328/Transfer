package web.pojo;

import netty.pojo.Header;

/**
 * @author wangchen
 * @date 2018/3/19 16:22
 */
public class NettyMessage {
    /**
     * 消息头
     */
    private WebUploader header;
    /**
     * 消息体
     */
    private Object body;

    public NettyMessage(WebUploader header, Object body) {
        this.header = header;
        this.body = body;
    }

    public WebUploader getHeader() {
        return header;
    }

    public void setHeader(WebUploader header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
