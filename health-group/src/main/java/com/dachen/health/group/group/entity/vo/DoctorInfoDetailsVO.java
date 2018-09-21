package com.dachen.health.group.group.entity.vo;

import java.util.Set;

import com.dachen.health.commons.vo.User;
import com.dachen.health.group.department.entity.po.DepartmentDoctor;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.po.GroupProfit;

public class DoctorInfoDetailsVO {
	public User user;
	public Group group;
	public GroupDoctor groupDoctor;
	public GroupProfit groupProfit;
	public InviteRelation inviteRelation;
	public DepartmentDoctor departmentDoctor;
	public String departmentFullName;
	public Set<String> packName;
	
	public InviteRelation getInviteRelation() {
		return inviteRelation;
	}
	public String getDepartmentFullName() {
		return departmentFullName;
	}
	public void setDepartmentFullName(String departmentFullName) {
		this.departmentFullName = departmentFullName;
	}
	public DepartmentDoctor getDepartmentDoctor() {
		return departmentDoctor;
	}
	public void setDepartmentDoctor(DepartmentDoctor departmentDoctor) {
		this.departmentDoctor = departmentDoctor;
	}
	public void setInviteRelation(InviteRelation inviteRelation) {
		this.inviteRelation = inviteRelation;
	}
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public GroupDoctor getGroupDoctor() {
		return groupDoctor;
	}
	public void setGroupDoctor(GroupDoctor groupDoctor) {
		this.groupDoctor = groupDoctor;
	}
	public GroupProfit getGroupProfit() {
		return groupProfit;
	}
	public void setGroupProfit(GroupProfit groupProfit) {
		this.groupProfit = groupProfit;
	}
	public Set<String> getPackName() {
		return packName;
	}
	public void setPackName(Set<String> packName) {
		this.packName = packName;
	}
	
}
