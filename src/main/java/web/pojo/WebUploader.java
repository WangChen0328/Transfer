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
    private Date lastModifiedDate;
    /**
     * 偏移量
     */
    private long start;
    /**
     * 片段长度
     */
    private long size;
    /**
     * 总分片数
     */
    private int chunks;
    /**
     * 当前第几块
     */
    private int chunk;

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

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getChunks() {
        return chunks;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }

    public int getChunk() {
        return chunk;
    }

    public void setChunk(int chunk) {
        this.chunk = chunk;
    }
}
