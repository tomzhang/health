package com.dachen.health.pack.consult.entity.po;

import java.util.HashSet;
import java.util.Set;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value="t_consultation_friend" , noClassnameStored = true)
public class ConsultationFriend {

	@Id
	private String id;
	
	//1: doctorId 为会诊医生角色，2：doctorId为医联体医生角色
	private Integer friendType;
	
	private Integer doctorId;
	
	private Set<Integer> doctorIdFriendIds = new HashSet<Integer>();
	
	private Set<Integer> specialFriendIds = new HashSet<Integer>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getFriendType() {
		return friendType;
	}

	public void setFriendType(Integer friendType) {
		this.friendType = friendType;
	}

	public Set<Integer> getDoctorIdFriendIds() {
		return doctorIdFriendIds;
	}

	public void setDoctorIdFriendIds(Set<Integer> doctorIdFriendIds) {
		this.doctorIdFriendIds = doctorIdFriendIds;
	}

	public Set<Integer> getSpecialFriendIds() {
		return specialFriendIds;
	}

	public void setSpecialFriendIds(Set<Integer> specialFriendIds) {
		this.specialFriendIds = specialFriendIds;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}
	
}
