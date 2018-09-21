package com.dachen.health.msg.entity.vo;


public class MsgGroupVO {
	
	/**
	 * 时间戳
	 */
	private long ts;
	
	/**
	 * 是否还有未更新数据:count>list.size(),则说明还有数据，more=true；
	 */
	private boolean more;
	
	/**
	 * 下次轮询时间间隔（单位ms，默认值10,000）
	 */
	private long tms;
	/**
	 * 患者会话组
	 */
	private MsgGroupList patientlist;
	/**
	 * 医生会话组
	 */
	private MsgGroupList  doctorlist;
	/**
	 * 患者医生会话组
	 */
	private MsgGroupList guidelist;
	/**
	 * 医助会话组
	 */
	private MsgGroupList assistantlist;
	
	private MsgGroupList notificationGroup;
	
	private MsgGroupList customerGroup;
	
	
	public MsgGroupList getNotificationGroup() {
		return notificationGroup;
	}
	public void setNotificationGroup(MsgGroupList notificationGroup) {
		this.notificationGroup = notificationGroup;
	}
	public MsgGroupList getGuidelist() {
		return guidelist;
	}
	public void setGuidelist(MsgGroupList guidelist) {
		this.guidelist = guidelist;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public boolean isMore() {
		return more;
	}
	public void setMore(boolean more) {
		this.more = more;
	}
	public MsgGroupList getPatientlist() {
		return patientlist;
	}
	public void setPatientlist(MsgGroupList patientlist) {
		this.patientlist = patientlist;
	}
	public MsgGroupList getDoctorlist() {
		return doctorlist;
	}
	public void setDoctorlist(MsgGroupList doctorlist) {
		this.doctorlist = doctorlist;
	}
 
	public MsgGroupList getAssistantlist() {
		return assistantlist;
	}
	public void setAssistantlist(MsgGroupList assistantlist) {
		this.assistantlist = assistantlist;
	}
	public MsgGroupVO(long ts, boolean more, MsgGroupList patientlist, MsgGroupList doctorlist,
			MsgGroupList assistantlist) {
		super();
		this.ts = ts;
		this.more = more;
		this.patientlist = patientlist;
		this.doctorlist = doctorlist;
		this.assistantlist = assistantlist;
	}
	public MsgGroupVO() {
		super();
	}
	public long getTms() {
		return tms;
	}
	public void setTms(long tms) {
		this.tms = tms;
	}
	public MsgGroupList getCustomerGroup() {
		return customerGroup;
	}
	public void setCustomerGroup(MsgGroupList customerGroup) {
		this.customerGroup = customerGroup;
	}
	
	
}
