package com.dachen.health.commons.utils;

public class SMSUtil {
	
	//图文咨询改进文案
	public static final String NOTE_PAY_SUCCESS = "图文套餐已支付成功";
	
	public static final String SMS_PAY_SUCCESS = "尊敬的{0}医生，患者{1}购买了您的图文咨询服务，请您及时为患者服务。";
	
	public static final String NOTE_BEGIN_SERVICE = "医生已开始了本次服务";
	
	public static final String SMS_BEGIN_SERVICE = "亲爱的{0}，您购买{1}医生的图文咨询订单，医生已开始服务，请准备好问题与医生沟通。";
	
	public static final String SMS_24HOURS = "尊敬的{0}医生您好，可能您太忙了，{1}订购您的图文咨询服务已经等候超过2小时了，有空就赶紧回复吧！";
	
	public static final String SMS_48HOURS = "亲爱的{0}，您购买{1}医生的图文咨询订单已取消，您的支付金额将会沿原支付渠道退款，退款金额将在7个工作日到账";
	
	public static final String NOTE_FINISH_SERVICE = "本次服务已结束";
	
	public static final String SMS_FINISH_SERVICE = "亲爱的{0}，您购买{1}医生的图文咨询订单，医生已结束服务。您对医生服务还满意吗？请及时评价";
	
	public static final String TEMPALTE = "{0}医生给您发了{1}。加入玄关健康，随时与ta保持沟通、地址：{2} ";

	public enum SMSType {
		voice(1, "语音"),
		Text(2, "文字"),
		picture(3, "照片"),
		followUp(4, "随访计划"),
		care(5, "健康关怀"),
		other(6, "其他");
		
		private Integer index;
		private String title;
		private SMSType(Integer index, String title) {
			this.index = index;
			this.title = title;
		}
		public Integer getIndex() {
			return index;
		}
		public void setIndex(Integer index) {
			this.index = index;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
	}
	
	public static String getNote(Integer index) {
		String note = "";
		switch (index) {
		case 1:
			note = "一条语音消息";
			break;
		case 2:
			note = "一条新消息";
			break;
		case 3:
			note = "一张图片";
			break;
		case 4:
			note = "一个新的随访计划";
			break;
		case 5:
			note = "一个新的健康关怀";
			break;
		case 6:
			note = "一条新消息";
			break;
		default:
			throw new IllegalArgumentException();
		} 
		
		return note;
	}
	
}
