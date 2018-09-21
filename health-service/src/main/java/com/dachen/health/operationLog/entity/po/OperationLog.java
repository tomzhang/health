package com.dachen.health.operationLog.entity.po;

import io.swagger.annotations.ApiModelProperty;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

/**
 * @author 钟良
 * @desc
 * @date:2017/9/29 9:55 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Entity(value = "t_operation_log", noClassnameStored = true)
public class OperationLog {
    @Id
    @ApiModelProperty(value = "id",required = true)
    private String id;
    @ApiModelProperty(value = "时间",required = true)
    private Long date;

    @NotSaved
    @ApiModelProperty(value = "用户",required = true)
    private String user;

    @NotSaved
    @ApiModelProperty(value = "电话",required = true)
    private String phone;

    @ApiModelProperty(value = "操作类型",required = true)
    private String operationType;
    @ApiModelProperty(value = "操作内容",required = true)
    private String content;
    @ApiModelProperty(value = "用户Id", hidden = true)
    private Integer userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
