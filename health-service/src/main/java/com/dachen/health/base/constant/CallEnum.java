package com.dachen.health.base.constant;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CallEnum {
	
	public static final Integer CONFERENCE_DURATION = 20;//会议最大时长
	public static final Integer CONFERENCE_PLAYTOONE = 0;//播放不播放
	public static final Integer CONFERENCE_MEDIATYPE = 1;//语音会议
	public static final Integer CONFERENCE_MAXMEMBER = 3;//会议最大方数
	public static final Integer CONFERENCE_ROLE_GUIDE = 2;//会议主持人角色(导医)
	public static final Integer CONFERENCE_ROLE_PATIENT = 1;//患者
	public static final Integer CONFERENCE_ROLE_DOCTOR = 3;//医生
	public static final Integer CONFERENCE_NOTICE_INTERVAL = 48;//48小时未填纪录
	public static final String  MAX_ALLOW_TIME = String.valueOf(30*60);//双向通话最大通话时长为30分钟
	public static final String  RECORD_URL = "D:\\record\\";
	public static final String RINGTONEID = "1243" ;
	
	public enum RecordStatus{
		
		recordFail(60,"录音失败"),
		recordIng(61,"录音中"),
		recordWait(62,"录音停止"),
		recordSuccess(63,"录音成功结束");
		
		
		private int index;
		private String title;
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		private RecordStatus(int index, String title){
			this.index = index;
			this.title = title;
		}
	}
	
	public enum ConferenceStatus{
//		会议状态。1：未开始；2：进行中；3：已结束。
//		0：默认场景;1：接口调用；2：会议超时解散；3：余额不足；4：其它原因。
		createReqFail(100,"会议创建失败"),
		createIng(1,"会议分创建中"),
		createSuccess(0,"会议成功创建，准备开始服务"),
		overBydefault(20,"默认解散"),
		overByInterface(21,"接口调用解散"),
		overByTimeOut(22,"会议超时解散"),
		overByNoMoney(23,"余额不足解散"),
		overByOther(24,"其它原因解散");
		
		
		private int index;
		private String title;
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		
		private ConferenceStatus(int index,String title){
			this.index = index;
			this.title = title;
		}
	}
	
	
	public enum MidiaStatus{
//		result	Int	必选	操作结果：0、成功；其它值为失败:1、操作执行失败；2 没有主持人；3、服务器繁忙；4、非法操作
//		notifyType	Int	必选	1静音；2取消禁音；3禁听；4取消禁听;5成员由来宾变成主席；6 成员由主席变成来宾
		muteSuccess(31,"静音成功"),
		muteFail(32,"静音失败"),
		unmuteSuccess(33,"取消静音成功"),
		unmuteFail(34,"取消静音失败"),
		deafSuccess(35,"禁听成功"),
		deafFail(36,"禁听失败"),
		undeafSuccess(37,"取消禁听成功"),
		undeafFail(38,"取消禁听失败");
		
		
		private int index;
		private String title;
		
		private MidiaStatus(int index,String title){
			this.index = index;
			this.title = title;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
	}
	
	public enum CallStatus{
		
		refuse(50,"已拒接"),//没有接通
		overByOthor(43,"未知场景挂断"),
		overByHost(42,"被动挂断"),
		overBySelf(41,"主动挂断"),
		success(30,"通话中"),
		inviting(20,"邀请中"),
		zero(0,"尚未拨打");
		private int index;
		private String title;
		
		private CallStatus(int index,String title){
			this.index = index;
			this.title = title;
		}
		
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		
	}
	
	

	public enum CallType{
		
		guideToPatient(1,"导医致电患者"),
		guideToDoctor(2,"导医致电医生"),
		doctorToPatient(3,"医生致电患者"),
		conference(4,"导医开启会议"),
		assistantToPatient(5,"医生助手致电患者"), 
		assistantToDoctor(6,"医生助手致电医生");
		
		private  int index;
		private String  title;
		private CallType(int index,String title){
			this.index = index;
			this.title =title;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		
	}
	
	public static void main(String[] args) {
		long time = 1448938107237L;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
		System.err.println(sdf.format(new Date(time)));
	}
}
