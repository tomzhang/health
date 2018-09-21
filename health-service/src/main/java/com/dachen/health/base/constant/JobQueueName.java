package com.dachen.health.base.constant;

import com.dachen.health.commons.constants.IBaseEnum;

public enum JobQueueName implements IBaseEnum {
	CANCEL_OUTPATIENT(1, "取消超时患者未开始的门诊订单"),
	CANCEL_NOAPPOINT_ORDER(2, "取消超时未预约订单"),
	CANCEL_NOPAY_ORDER(3, "取消超时未支付订单"),
	CANCEL_PAID_ORDER(4, "取消超时未开始订单"),
	DOCTOR_OFFLINE(5, "医生超时强制下线"), //队列类型重复
	WX_REFUDN_QUERY(6, "微信退款结果查询"),
	GUIDE_END_SERVICE(7, "导医超时自动结束服务"),
	GUIDE_NO_PAY_END_SERVICE(8, "导医超时未支付自动结束服务"),
	DOWN_FROM_YZX2QN(9, "从云之讯下载录音上传到七牛"),
	REMIND_CONSULTATION(10, "提醒会诊时间"),
	SEND_APPOINTMENT(11,"预约名医患者短信提醒"),
	AUTO_EVALUATION(12,"订单结束自动评价"),
	REMIND_MESSAGE(13, "提醒医生开始图文咨询服务"),
	SEND_MSGTOGUIDER(14, "预约时间前10分钟"),
	SEND_MSGTOGUIDEROF_HALF_HOUR(15, "付款成功半小时后导医仍未开启电话服务"),
	AUTO_FINISH_AFTER_BEGINSERVICE(16, "电话订单开始服务之后2 小时自动结束服务"),
	UPDATE_DOCTOR_OFFLINEITEM_RECURRING(17, "每天凌晨更新医生的排班信息"),
	AUTO_FINISH_APPOINTMENT_AFTER_BEGINSERVICE(18, "名医面对面订单开始服务之后2 小时自动结束服务"), 
	AUTO_CANCEL_APPOINTMENT_NO_PROCESS(19,"名义面对面医生没有同意或患者在预约4小时没有支付订单自动取消"),
	ORDER_CREATE_ASYNC_MESSAGE(20,"订单创建的异步消息"),
	REMIND_ASSISTANT_APPOINTMENT_MESSAGE(21, "短信提醒医生助手患者的预约时间要开始了"),
	REMIND_MEDICAL_SCHEDULE_PLAN_MESSAGE(22, "用药提醒"), 
	DOCTOR_OFFLINE_FORCE(23, "不在服务时间内强制下线"), 
	DOCTOR_OFFLINE_TWO_HOURS(25, "医生超时(2小时)强制下线"),
	NOTIFY_WHEN_REPLY_COUNT_EQ_ZERO(26, "消息回复次数为0取消订单"),
	FINISH_WHEN_REPLY_COUNT_EQ_ZERO(27, "消息回复次数为0取消订单"),
	CARE_ITEM_IMMEDIATELY(28, "需要即刻发送的关怀项"),
	CANCEL_OUTPATIENT_NOPARPARE(29, "取消超时医生未接单的门诊订单"),
	RE_SEND_IMAGES_TO_XG(30, "向玄关重新发送失败的请求");

	private int value;
	private String alias;

	private JobQueueName(int value, String alias) {
		this.value = value;
		this.alias = alias;
	}
	
	@Override
	public int getValue() {
		return value;
	}

	@Override
	public String getAlias() {
		return alias;
	}

}
