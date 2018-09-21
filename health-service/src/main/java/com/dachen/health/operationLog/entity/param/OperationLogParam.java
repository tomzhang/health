package com.dachen.health.operationLog.entity.param;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

/**
 * @author 钟良
 * @desc
 * @date:2017/9/29 17:01 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class OperationLogParam implements Serializable {

    @ApiModelProperty(value = "页码", required = true)
    private Integer pageIndex = 0;
    @ApiModelProperty(value = "每页显示大小", required = true)
    private Integer pageSize = 10;

    @ApiModelProperty(hidden = true)
    private Integer offset;

    @ApiModelProperty(value = "关键字", required = true)
    private String keywords;

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

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Integer getOffset() {
        return pageIndex * pageSize;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
