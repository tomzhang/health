package com.dachen.health.file.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

/**
 * 文件发送记录
 * Created by fuyongde on 2017/3/21.
 */
@Entity(value = "f_vpan_send_record", noClassnameStored = true)
public class VpanSendRecord {

    public static final Integer STATUS_NORMAL = 0;
    public static final Integer STATUS_DELETE = 1;

    @Id
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

    /**来源名称**/
    private String sourceName;

    /**
     * 文件发送给谁
     */
    private String targetName;

    /**
     * 文件所属的七牛的bucket，用来判断是从vpanUploadFile还是MessageUploadFile
     */
    private String bucket;

    /***
     * 用户类型（用来判断是药企的用户还是health的用户）
     */
    private Integer userType;

    /****
     * 消息id
     */
    private String msgId;

    /**
     * 状态
     */
    private Integer status;

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

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
