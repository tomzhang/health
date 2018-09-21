package com.dachen.im.server.enums;

/**
 * Created by Administrator on 2017/3/29.
 */
public enum NotificationEnum {

    ADD_ORDER(1, "新建订单");

    private int index;
    private String type;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    NotificationEnum(int index, String type) {
        this.index = index;
        this.type = type;
    }
}
