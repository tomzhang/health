package com.dachen.health.pack.conference.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("t_conf_detail")
public class ConfDetail {
	
	@Id
	private ObjectId id;
	private String crId;//电话纪录关联ID
	private String memberId;//用户ID
	private String telephone;//用户对应的电话号码
	private String callId;//第三方回调callId
	private Integer role;//与会者角色（1：普通，2：主持人）
	private Integer status;//状态（1: 管理员挂断;2：用户挂断;3:用户无应答;4：用户忙;5：系统端口不足;6：号码不存在;7：会议已满;8：会议安全认证不通过;9:会议不允许外呼等）
	private Boolean isNow;//是否为当前状态
	private long joinTime;//加入会议时
	private long unJoinTime;//退出会议时间
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getCrId() {
		return crId;
	}
	public void setCrId(String crId) {
		this.crId = crId;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getCallId() {
		return callId;
	}
	public void setCallId(String callId) {
		this.callId = callId;
	}
	public Integer getRole() {
		return role;
	}
	public void setRole(Integer role) {
		this.role = role;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Boolean getIsNow() {
		return isNow;
	}
	public void setIsNow(Boolean isNow) {
		this.isNow = isNow;
	}
	public long getJoinTime() {
		return joinTime;
	}
	public void setJoinTime(long joinTime) {
		this.joinTime = joinTime;
	}
	public long getUnJoinTime() {
		return unJoinTime;
	}
	public void setUnJoinTime(long unJoinTime) {
		this.unJoinTime = unJoinTime;
	}
}
