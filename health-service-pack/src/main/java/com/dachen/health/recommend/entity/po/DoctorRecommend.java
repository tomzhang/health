package com.dachen.health.recommend.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;


@Entity(value = "t_doctor_recommend",noClassnameStored=true)
public class DoctorRecommend {
	
	public static final int LIMIT_RECOMMEND = 20;
	
	@Id
	private String id;
	private Integer weight;//排序权重
	private String isRecommend;//是否推荐
	private String isShow;//是否显示
	private Integer doctorId;//医生id
	private String groupDocId;//c_group_doctor表Id
	private String groupId;//冗余该字段为方便查权重
	private Long createTime;
	private Long lastUpdateTime;
	public Integer getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	public String getIsRecommend() {
		return isRecommend;
	}
	public void setIsRecommend(String isRecommend) {
		this.isRecommend = isRecommend;
	}
	public String getIsShow() {
		return isShow;
	}
	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}
	public String getGroupDocId() {
		return groupDocId;
	}
	public void setGroupDocId(String groupDocId) {
		this.groupDocId = groupDocId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public Long getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	

}
