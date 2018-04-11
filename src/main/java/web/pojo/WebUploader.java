package web.pojo;

import java.util.Date;

/**
 * @author wangchen
 * @date 2018/4/10 15:49
 */
public class WebUploader {
    /**
     * 文件唯一标识
     */
    private String id;
    /**
     * 文件名称
     */
    private String name;
    /**
     * 文件类型
     */
    private String type;
    /**
     * 最后修改时间
     */
    private String lastModifiedDate;
    /**
     * 片段长度
     */
    private String size;
    /**
     * 总分片数
     */
    private String chunks;
    /**
     * 当前第几块
     */
    private String chunk;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getChunks() {
        return chunks;
    }

    public void setChunks(String chunks) {
        this.chunks = chunks;
    }

    public String getChunk() {
        return chunk;
    }

    public void setChunk(String chunk) {
        this.chunk = chunk;
    }
}
