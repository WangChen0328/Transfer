package netty.model;

import java.io.Serializable;

/**
 * @author wangchen
 * @date 2018/3/30 12:05
 */
public class ResponseFile implements Serializable {
    /**
     * 是否完成
     */
    private boolean complete;
    /**
     * 上传进度
     */
    private String progress;
    /**
     * 文件结尾位置
     */
    private Long endPosition;

    /**
     * 。。。
     */
    RequestFile requestFile;

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public Long getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Long endPosition) {
        this.endPosition = endPosition;
    }

    public RequestFile getRequestFile() {
        return requestFile;
    }

    public void setRequestFile(RequestFile requestFile) {
        this.requestFile = requestFile;
    }
}
