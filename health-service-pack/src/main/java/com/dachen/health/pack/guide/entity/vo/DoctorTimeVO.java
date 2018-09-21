package com.dachen.health.pack.guide.entity.vo;

import java.util.List;

import com.dachen.health.pack.guide.entity.po.DoctorTimePO.Remark;
import com.dachen.health.pack.guide.entity.po.DoctorTimePO.Time;
import com.dachen.health.pack.pack.entity.po.Pack;

public class DoctorTimeVO {
	private Integer userId;
	 /* 医生号 */
    private String doctorNum;
	private String name;
	private String headImg;
	
	//免打扰（1：正常，2：免打扰）
	private String troubleFree;
			
	
	private Integer sex;
	
	private int state;
	
	private String hospital;
	  /* 所属科室 */
    private String departments;
    //集团名称
    private String groupName;

    /* 职称 */
    private String title;
    
    private String telephone;
    
    private List<Pack> packList;
    
    private List<Time>timeList;
    
    private List<Remark> remarkList;
    
    private long createTime;
    
    
	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<Remark> getRemarkList() {
		return remarkList;
	}

	public void setRemarkList(List<Remark> remarkList) {
		this.remarkList = remarkList;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getDoctorNum() {
		return doctorNum;
	}

	public void setDoctorNum(String doctorNum) {
		this.doctorNum = doctorNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public List<Pack> getPackList() {
		return packList;
	}

	public void setPackList(List<Pack> packList) {
		this.packList = packList;
	}

	public List<Time> getTimeList() {
		return timeList;
	}

	public void setTimeList(List<Time> timeList) {
		this.timeList = timeList;
	}

	public String getTroubleFree() {
		return troubleFree;
	}

	public void setTroubleFree(String troubleFree) {
		this.troubleFree = troubleFree;
	}
}
