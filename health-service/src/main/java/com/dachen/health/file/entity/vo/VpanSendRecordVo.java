package com.dachen.health.file.entity.vo;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by fuyongde on 2017/3/21.
 */
public class VpanSendRecordVo {

    private static double ONE_BYTES = 1;
    private static double ONE_KB = ONE_BYTES * 1024;
    private static double ONE_MB = ONE_KB * 1024;
    private static double ONE_GB = ONE_MB * 1024;
    private static double ONE_TB = ONE_GB * 1024;
    private static double ONE_PB = ONE_TB * 1024;

    private String id;
    /**
     * 文件id，对应VpanUploadFile
     **/
    private String fileId;
    /**
     * 发送人id
     **/
    private Integer sendUserId;
    /**
     * 接收人id
     **/
    private List<Integer> receiveUserId;
    /**
     * 文件发送时间
     */
    private Long sendDate;
    /**
     * 文件名（冗余）
     */
    private String name;
    /**
     * 文件后缀（冗余）
     */
    private String suffix;
    /**
     * mime类型（冗余）
     */
    private String mimeType;
    /**
     * 文件分类（冗余）
     */
    private String type;
    /**
     * 文件大小（冗余）
     */
    private Long size;
    /**
     * 文件下载地址（冗余）
     */
    private String url;
    /**
     * 文件类型（公有，私有，会影响下载所以必须冗余）
     */
    private String bucketType;
    /**
     * 会话组id
     **/
    private String gid;
    /**
     * 会话组名称
     **/
    private String imName;

    /**
     * 来源名称
     **/
    private String sourceName;

    /**
     * 文件发送给谁
     */
    private String targetName;

    /**
     * 是否为发送者
     **/
    private Boolean sender;

    /**
     * 文件大小（不满1KB，显示单位为bytes；不满1MB，显示单位为KB；不满1GB的，显示单位为MB）
     */
    private String sizeStr;

    /**
     * 文件所属的七牛的bucket，用来判断是从vpanUploadFile还是MessageUploadFile
     */
    private String bucket;

    /**
     * 消息id
     */
    private String msgId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Integer getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(Integer sendUserId) {
        this.sendUserId = sendUserId;
    }

    public List<Integer> getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(List<Integer> receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public Long getSendDate() {
        return sendDate;
    }

    public void setSendDate(Long sendDate) {
        this.sendDate = sendDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBucketType() {
        return bucketType;
    }

    public void setBucketType(String bucketType) {
        this.bucketType = bucketType;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getImName() {
        return imName;
    }

    public void setImName(String imName) {
        this.imName = imName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Boolean getSender() {
        return sender;
    }

    public void setSender(Boolean sender) {
        this.sender = sender;
    }

    public String getSizeStr() {
        return sizeStr;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setSizeStr() {
        String sizeStr = null;
        double size = 0d;
        // 格式化小数
        DecimalFormat df = new DecimalFormat("0.0");
        if (this.size < ONE_BYTES) {
            sizeStr = "0bytes";
        } else if (ONE_BYTES <= this.size && this.size < ONE_KB) {
            size = this.size / ONE_BYTES;
            sizeStr = df.format(size) + "bytes";
        } else if (ONE_KB <= this.size && this.size < ONE_MB) {
            size = this.size / ONE_KB;
            sizeStr = df.format(size) + "KB";
        } else if (ONE_MB <= this.size && this.size < ONE_GB) {
            size = this.size / ONE_MB;
            sizeStr = df.format(size) + "MB";
        } else if (ONE_GB <= this.size && this.size < ONE_TB) {
            size = this.size / ONE_GB;
            sizeStr = df.format(size) + "GB";
        } else if (ONE_TB <= this.size && this.size < ONE_PB) {
            size = this.size / ONE_TB;
            sizeStr = df.format(size) + "TB";
        } else {
            size = this.size / ONE_PB;
            sizeStr = df.format(size) + "PB";
        }
        this.sizeStr = sizeStr;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
