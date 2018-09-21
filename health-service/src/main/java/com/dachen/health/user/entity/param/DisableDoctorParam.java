package com.dachen.health.user.entity.param;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

/**
 * Author: xuhuanjie
 * Date: 2018-04-20
 * Time: 15:23
 * Description:
 */
public class DisableDoctorParam {

    @ApiModelProperty(value = "封号原因", required = true)
    private String reason;

    @ApiModelProperty(value = "被封号医生Id", required = true)
    private Integer userId;

    @ApiParam(hidden = true)
    @ApiModelProperty(value = "封号管理员Id(前端忽略)")
    private Integer adminId;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

}
