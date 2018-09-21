package com.dachen.health.base.entity.param;

public class OpenDoctorParam {
    private Long modifyTime;

    private Integer pageIndex=0;
    private Integer pageSize=200;
    private Integer start;

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
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

    public Integer getStart() {
        return start =(pageIndex * pageSize);
    }
}
