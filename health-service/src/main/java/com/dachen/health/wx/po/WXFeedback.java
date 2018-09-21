package com.dachen.health.wx.po;

import org.mongodb.morphia.annotations.Entity;

/**
 * Created by fuyongde on 2017/2/14.
 */
@Entity(value = "w_feedback", noClassnameStored = true)
public class WXFeedback {
    private String id;
    /**反馈手机**/
    private String phone;
    /**反馈内容**/
    private String content;
    /**反馈时间**/
    private Long createTime;
    /**微信返回的code**/
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
