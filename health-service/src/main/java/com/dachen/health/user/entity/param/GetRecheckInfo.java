package com.dachen.health.user.entity.param;

import io.swagger.annotations.ApiModelProperty;

/**
 * Author: xuhuanjie
 * Date: 2018-05-02
 * Time: 18:26
 * Description:
 */
public class GetRecheckInfo {

    @ApiModelProperty(value = "信息状态")
    private Integer infoStatus;

    @ApiModelProperty(value = "医生姓名")
    private String name;

    @ApiModelProperty(value = "页码")
    private Integer pageIndex;

    @ApiModelProperty(value = "每页大小")
    private Integer pageSize;

    public Integer getInfoStatus() {
        return infoStatus;
    }

    public void setInfoStatus(Integer infoStatus) {
        this.infoStatus = infoStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

}
