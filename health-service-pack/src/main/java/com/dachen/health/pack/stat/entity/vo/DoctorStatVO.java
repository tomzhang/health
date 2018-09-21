package com.dachen.health.pack.stat.entity.vo;

import java.util.List;

import com.dachen.util.Json;

public class DoctorStatVO {

	private Integer doctorId;
	
	private String doctorName;
	
	private String groupCert;//所在集团是否已认证
	
	private String doctorPath;
	
	private String doctorTitle;
	
	private String doctorDept;
	
	private String hospital;
	
	private String hospitalId;
	
	private Integer doctorCureNum;
	
	private Integer role;
	
	private String goodRate;//好评率
	
	private String doctorSkill;
	
	private String myDoctor;//"1"：问诊过；"0"：非
	
	private String is3A;//"1"：三甲；"0"：非三甲
	
	private String price;

	/**最低价格**/
	private String minPrice;
	
	private List<Object> offline;
	
	/**是否为集团的创建者**/
	private boolean isGroupOwner;
	
	private String recommendId;
	
	private String isShow;//是否显示 
	
	private String url;
	
	/**距离**/
	private String distance;
	
	private Integer timeLimit;
	
	/**消息回复次数**/
	private Integer replyCount;
	
	private String lng;//经度
	private String lat;//纬度

	public String getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(String minPrice) {
		this.minPrice = minPrice;
	}

	private boolean isCurCondition;//是否是当前条件搜索的结果
	public Integer getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public boolean isGroupOwner() {
		return isGroupOwner;
	}

	public void setGroupOwner(boolean isGroupOwner) {
		this.isGroupOwner = isGroupOwner;
	}

	public List<Object> getOffline() {
		return offline;
	}

	public void setOffline(List<Object> offline) {
		this.offline = offline;
	}

	private String textOpen = "0";
	private String phoneOpen = "0";
	private String careOpen = "0";
	private String clinicOpen = "0";
	private String consultationOpen = "0";
	private String appointmentOpen = "0";

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getGroupCert() {
		return groupCert;
	}

	public void setGroupCert(String groupCert) {
		this.groupCert = groupCert;
	}

	public String getDoctorPath() {
		return doctorPath;
	}

	public void setDoctorPath(String doctorPath) {
		this.doctorPath = doctorPath;
	}

	public String getDoctorTitle() {
		return doctorTitle;
	}

	public void setDoctorTitle(String doctorTitle) {
		this.doctorTitle = doctorTitle;
	}

	public String getDoctorDept() {
		return doctorDept;
	}

	public void setDoctorDept(String doctorDept) {
		this.doctorDept = doctorDept;
	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Integer getDoctorCureNum() {
		return doctorCureNum;
	}

	public void setDoctorCureNum(Integer doctorCureNum) {
		this.doctorCureNum = doctorCureNum;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public String getGoodRate() {
		return goodRate;
	}

	public void setGoodRate(String goodRate) {
		this.goodRate = goodRate;
	}

	public String getDoctorSkill() {
		return doctorSkill;
	}

	public void setDoctorSkill(String doctorSkill) {
		this.doctorSkill = doctorSkill;
	}

	public String getMyDoctor() {
		return myDoctor;
	}

	public void setMyDoctor(String myDoctor) {
		this.myDoctor = myDoctor;
	}

	public String getIs3A() {
		return is3A;
	}

	public void setIs3A(String is3a) {
		is3A = is3a;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getTextOpen() {
		return textOpen;
	}

	public void setTextOpen(String textOpen) {
		this.textOpen = textOpen;
	}

	public String getPhoneOpen() {
		return phoneOpen;
	}

	public void setPhoneOpen(String phoneOpen) {
		this.phoneOpen = phoneOpen;
	}

	public String getCareOpen() {
		return careOpen;
	}

	public void setCareOpen(String careOpen) {
		this.careOpen = careOpen;
	}

	public String getClinicOpen() {
		return clinicOpen;
	}

	public void setClinicOpen(String clinicOpen) {
		this.clinicOpen = clinicOpen;
	}

	public String getConsultationOpen() {
		return consultationOpen;
	}

	public void setConsultationOpen(String consultationOpen) {
		this.consultationOpen = consultationOpen;
	}

	public String getAppointmentOpen() {
		return appointmentOpen;
	}

	public void setAppointmentOpen(String appointmentOpen) {
		this.appointmentOpen = appointmentOpen;
	}

	public String getRecommendId() {
		return recommendId;
	}

	public void setRecommendId(String recommendId) {
		this.recommendId = recommendId;
	}

	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(Integer replyCount) {
		this.replyCount = replyCount;
	}

	@Override
	public String toString() {
		return Json.toString(this);
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public boolean isCurCondition() {
		return isCurCondition;
	}

	public void setCurCondition(boolean isCurCondition) {
		this.isCurCondition = isCurCondition;
	}


	
}
