package com.dachen.health.base.constant;

public enum UserChangeTypeEnum  {
	
	ADD_FRIEND("1","新的好友"), //加好友通知
	DEL_FRIEND("2","删好友"),   //指令
	PROFILE_CHANGE("3","个人资料变化"), //指令
	PROFILE_INVITE_COMPANY("4","公司管理员邀请函"),  //通知
	PROFILE_INVITE_GROUP("5","圈子管理员邀请函"), //通知
	PROFILE_INVITE_DOCTOR("6","加入圈子邀请函"), //通知
	PROFILE_INVITE_CREATE("7","创建圈子邀请函"), //通知
	FRIEND_REQ_CHANGE("8","新的好友"), //验证请求列表有变化通知
	DOCTOR_ONLINE("9","医生上线"),
	DOCOTR_OFFLINE("10","医生下线"),
	DOCOTR_JOIN_CARE_PLAN("11","加入健康关怀医生组通知函"),
	DOCOTR_OUT_CARE_PLAN("12","退出健康关怀医生组通知函"),
	DOCOTR_OUT_CONFERENCE("13","电话咨询服务成功"),
	GUIDE_WRTIE_CONSULT("14","电话咨询纪录48小时未填写"),
	DOCTOR_WRTIE_CONSULT("15","医生填写咨询纪录"),
	APPLY_JOIN_GROUP("16","申请加入医生圈子"),
	AFTER_SETTLE_NOTICE("17","结算成功发出的通知"),
	PROFILE_INVITE_DOCTOR_HOSPITAL("18","加入医院邀请函"),
	APPLY_JOIN_HOSPITAL("19","申请加入医院"),//通知
	APPLY_JOIN_DEPT("20","申请加入科室");
	private String value;
    private String alias;
	private UserChangeTypeEnum(String value,String alias)
	{
		this.value = value;
        this.alias =  alias;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	 
      

}
