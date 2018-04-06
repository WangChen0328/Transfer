package netty.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangchen
 * @date 2018/3/19 16:24
 * 根据Netty规范创建 消息头结构
 */
public class Header {
    /**
     * Netty消息校验码
     */
    private int crcCode = 0xABEF0101;
    /**
     * 消息长度，整个消息，包括消息头和消息体
     */
    private int length;
    /**
     * 集群节点内全局唯一，由会话ID生成器生成
     */
    private long sessionID;
    /**
     * 0：业务请求消息
     * 1：业务响应消息
     * 2：业务ONE WAY 消息(即是请求消息又是响应消息)
     * 3：握手请求消息
     * 4：握手应答消息
     * 5：心跳请求消息
     * 6：心跳应答消息
     */
    private byte type;
    /**
     * 优先级 8位 0~255
     */
    private byte priority;
    /**
     * 附件 变长 用于扩展消息头
     */
    private Map<String, Object> attachment = new HashMap<>();

    public int getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }
}
