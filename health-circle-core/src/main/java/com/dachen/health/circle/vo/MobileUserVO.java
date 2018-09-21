package com.dachen.health.circle.vo;

import com.dachen.health.commons.vo.User;

import java.io.Serializable;

public class MobileUserVO implements Serializable {
    private Integer userId;
    private String name;
    private String headPicUrl;

    public MobileUserVO() {
    }

    public MobileUserVO(User user) {
        this.userId = user.getUserId();
        this.name = user.getName();
        this.headPicUrl = user.getHeadPicFileName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getHeadPicUrl() {
        return headPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        this.headPicUrl = headPicUrl;
    }
}
