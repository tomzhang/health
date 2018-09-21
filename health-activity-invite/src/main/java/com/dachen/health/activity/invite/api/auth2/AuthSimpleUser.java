package com.dachen.health.activity.invite.api.auth2;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/18 15:10 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class AuthSimpleUser {
    private Integer id;

    private String name;

    private String headPic;

    private int userType;

    private String telephone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
