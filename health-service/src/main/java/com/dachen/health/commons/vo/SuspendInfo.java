package com.dachen.health.commons.vo;

/**
 * Author: xuhuanjie
 * Date: 2018-04-20
 * Time: 14:57
 * Description:
 */
public class SuspendInfo {

    private String reason;

    private long createTime;

    public SuspendInfo(String reason, long createTime) {
        this.reason = reason;
        this.createTime = createTime;
    }

    public SuspendInfo() {

    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

}
