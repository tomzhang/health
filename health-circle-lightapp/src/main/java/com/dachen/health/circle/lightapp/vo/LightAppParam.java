package com.dachen.health.circle.lightapp.vo;

import java.util.List;

/**
 * @author sharp
 * @desc
 * @date:2017/6/1416:02 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class LightAppParam {

    private List<Integer> userIdList;

    private List<String> openIdList;

    public List<Integer> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Integer> userIdList) {
        this.userIdList = userIdList;
    }

    public List<String> getOpenIdList() {
        return openIdList;
    }

    public void setOpenIdList(List<String> openIdList) {
        this.openIdList = openIdList;
    }

}
