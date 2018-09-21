package com.dachen.commons.asyn.event;

public enum EventType {
    /**
     * example
     * 用户信息更新（用户头像、姓名、手机号信息变更）->通过MQ通知其他系统更新用户信息
     */
    UserBaseInfoUpdated("HEALTH-USER-UPDATED","用户基本信息更新"),
    
    UserTokenInvalid("HEALTH-USER-TOKEN-INVALID","用户TOKEN失效"),
    
    UserPicUpdated("HEALTH-USER-PIC-UPDATED","用户基本信息更新"),
    
    SendPubMsg("HEALTH-PUB-SENDMSG","发送公共号消息"),
	
	UserRegisterSuccess("HEALTH-USER-REGISTER-SUCCESS","用户注册成功"),
	
	User1InfoUpdated("HEALTH-USER1-UPDATED","用户（患者）信息更新"),
	
	UserActivateSuccess("HEALTH-USER-ACTIVATE-SUCCESS","用户激活成功"),
	
	InsertDoctorInfoToEs("INSERT-DOCTOR-INFO-TO-ES","插入审核通过的医生"),
	DoctorInfoUpdateForEs("HEALTH-DOCTOR-SKILL-UPDATED","更新医生信息"),
	
	GroupInfoInsertForEs("GROUP-INFO-INSERT","插入集团信息"),
	GroupInfoUpdateForEs("GROUP-INFO-UPDATE","修改集团信息"),
	
	OrderPaySuccess("HEALTH-ORDER-PAY-SUCCESS","订单支付成功"),
	
	SendMessageAndNotify("HEALTH-SEND-MESSAGE-NOTIFY","发送消息和通知"),
	
	SendCareMessage("HEALTH-SEND-CARE-MESSAGE","发送健康关怀留言"),
	SendCareAnswerCard("HEALTH-SEND-CARE-ANSWER","发送健康关怀答案卡片"),
	
	DOCTOR_NEW_DYNAMIC("DOCTOR-NEW-DYNAMIC","医生发布新的动态"),
	
	ARM_TO_MP3("ARM-TO-MP3","音频格式转为MP3"),
	
	GROUP_NEW_DYNAMIC("GROUP-NEW-DYNAMIC","集团新的动态"),

	OrderSessionSendMsg("ORDERSESSION_SENDMSG","订单im发送消息"),
	
	OrderSessionSendFirstMsg("ORDERSESSION_SEND_FIRST_MSG","订单im医生第一次发送消息"),
	
    SEND_CHECKITEM_TO_XG("SEND-CHECKITEM-TO-XG", "发送检查项到玄关");
    
    private String queueName;
    private String alias;
    
    private EventType(String queueName,String alias)
    {
    	this.queueName = queueName;
    	this.alias = alias;
    }

	public String getQueueName() {
		return queueName;
	}

	public String getAlias() {
		return alias;
	}
    
}
