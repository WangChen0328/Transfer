package netty.util;


import netty.pojo.Header;
import netty.pojo.NettyMessage;

import java.util.Map;

/**
 * @author wangchen
 * @date 2018/3/29 10:19
 */
public class NettyMessageUtil {
    /**
     * 返回Netty标准协议对象
     * @param type 消息头类型
     * @return
     */
    public static NettyMessage buildNettyMessage(byte type) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        message.setHeader(header);
        header.setType(type);
        return  message;
    }
    /**
     * 返回Netty标准协议对象
     * @param type 消息头类型
     * @param body 消息体
     * @return
     */
    public static NettyMessage buildNettyMessage(byte type, Object body) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        message.setHeader(header);
        header.setType(type);
        message.setBody(body);
        return  message;
    }
    /**
     * 返回Netty标准协议对象
     * @param type 消息头类型
     * @param attrMap 消息头附件
     * @param body 消息体
     * @return
     */
    public static NettyMessage buildNettyMessage(byte type, Map attrMap, Object body) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        message.setHeader(header);
        header.setType(type);
        header.setAttachment(attrMap);
        message.setBody(body);
        return  message;
    }
}
