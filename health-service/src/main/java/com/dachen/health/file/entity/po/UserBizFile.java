package com.dachen.health.file.entity.po;

import com.dachen.health.file.constant.UserFileEnum.BizType;
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
@Entity(value = "t_biz_file", noClassnameStored = true)
public class UserBizFile {

    @Id
    private ObjectId id;

    /**
     * @see BizType
     */
    @ApiModelProperty(value = "业务类型")
    private Integer bizType;

    @ApiModelProperty(value = "业务id")
    private String bizId;

    @ApiModelProperty(value = "文件id")
    private String fileId;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

}
