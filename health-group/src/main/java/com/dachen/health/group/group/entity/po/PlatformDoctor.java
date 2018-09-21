package com.dachen.health.group.group.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.health.group.common.util.GroupUtil;

@Entity(value = "c_platform_doctor", noClassnameStored = true)
public class PlatformDoctor {

	@Id
	private ObjectId id;
	
	//医生Id
	private Integer doctorId;

	//在线状态 1在线、2离线
	private String onLineState;
	
	// 最后一次上线时间
	private Long onLineTime;
	
	// 最后一次下线时间
	private Long offLineTime;

	//值班时长（秒）
	private Long dutyDuration;

	
	//集团Id，平台作为Group中的一条固定记录
	private String groupId = GroupUtil.PLATFORM_ID;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getOnLineState() {
		return onLineState;
	}

	public void setOnLineState(String onLineState) {
		this.onLineState = onLineState;
	}

	public Long getOnLineTime() {
		return onLineTime;
	}

	public void setOnLineTime(Long onLineTime) {
		this.onLineTime = onLineTime;
	}

	public Long getOffLineTime() {
		return offLineTime;
	}

	public void setOffLineTime(Long offLineTime) {
		this.offLineTime = offLineTime;
	}

	public Long getDutyDuration() {
		return dutyDuration;
	}

	public void setDutyDuration(Long dutyDuration) {
		this.dutyDuration = dutyDuration;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
}
