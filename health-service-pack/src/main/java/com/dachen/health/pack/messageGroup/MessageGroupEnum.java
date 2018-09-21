package com.dachen.health.pack.messageGroup;

/**
 * Created by Administrator on 2017/3/31.
 */
public enum MessageGroupEnum {
    NEW_ORDER(1, "添加订单"),
    CANCEL_ORDER(2, "取消订单"),
    CONFIRM_CONSULTATION(3, "确认会诊"),
    FINISH_SERVICE(4, "结束服务"),
    CHANGE_APPOINTMENT_TIME(5, "更改预约时间"),
    REFUSE_ORDER(6, "拒绝订单"),
    START_SERVICE(7, "开始服务"),
    ABANDON_SERVICE(8, "放弃服务"),
    PREPARE_SERVICE(9, "准备服务"),
    PAY_ORDER(10, "支付完成");

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

    MessageGroupEnum(int index, String type) {
        this.index = index;
        this.type = type;
    }
}
