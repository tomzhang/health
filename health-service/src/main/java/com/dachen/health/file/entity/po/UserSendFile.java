package com.dachen.health.file.entity.po;

import io.swagger.annotations.ApiModelProperty;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-03-02
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Entity(value = "t_send_file", noClassnameStored = true)
public class UserSendFile {

    @Id
    private ObjectId id;

    @ApiModelProperty(value = "文件发送人id")
    private Integer sendUserId;

    @ApiModelProperty(value = "文件接收人id")
    private Integer receiveUserId;

    @ApiModelProperty(value = "文件id")
    private String fileId;

    @ApiModelProperty(value = "文件发送时间")
    private Long sendTime;

    @ApiModelProperty(value = "文件名（冗余）")
    private String name;

    @ApiModelProperty(value = "文件后缀（冗余）")
    private String suffix;

    @ApiModelProperty(value = "mime类型（冗余）")
    private String mimeType;

    @ApiModelProperty(value = "文件分类（冗余）")
    private String fileType;

    @ApiModelProperty(value = "文件大小（冗余）")
    private Long size;

    @ApiModelProperty(value = "文件下载地址（冗余）")
    private String url;

    @ApiModelProperty(value = "文件类型（公有，私有，会影响下载所以必须冗余）")
    private String bucketType;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Integer getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(Integer sendUserId) {
        this.sendUserId = sendUserId;
    }

    public Integer getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(Integer receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
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

}
