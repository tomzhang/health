package com.dachen.health.commons.entity;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * 
 * @author vincent
 *
 */
@Entity(noClassnameStored = true, value = "t_online_record")

public class OnlineRecord {

	@Id
	private String id;
	
	//集团医生
	private String groupDoctorId;

	// 上线时间
	private Long onLineTime;
	
	// 时长（秒）
	private Long duration;
	

	// 下线时间
	private Long offLineTime;

	// 创建时间
	private Long createTime;

	// 最后修改时间
	private Long lastModifyTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getGroupDoctorId() {
		return groupDoctorId;
	}

	public void setGroupDoctorId(String groupDoctorId) {
		this.groupDoctorId = groupDoctorId;
	}

	public Long getOnLineTime() {
		return onLineTime;
	}

	public void setOnLineTime(Long onLineTime) {
		this.onLineTime = onLineTime;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Long getOffLineTime() {
		return offLineTime;
	}

	public void setOffLineTime(Long offLineTime) {
		this.offLineTime = offLineTime;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(Long lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}


}
