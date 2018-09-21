package com.dachen.health.api.client.pack.entity;

import java.io.Serializable;
import java.util.List;

public class CPack implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;

    /* 医生id */
    private Integer doctorId;

    /* 套餐服务名称 */
    private String name;

    /* 价格，单位分 */
    private Long price;

    /** 套餐类型 {@link PackEnum.PackType} */
    private Integer packType;

    /* 创建时间 */
    private Long createTime;

    /** 套餐状态 {@link PackEnum.PackStatus} */
    private Integer status;
    
    private List<Integer> statusList;
    
    /* 描述 */
    private String description;
    
    private String image;
    
    private String careTemplateId;
    
    private String followTemplateId;
    
    private List<CPackDoctor> packDoctors;
    
    private Integer doctorCount;

    /**
     * 以分钟为单位
     */
    private Integer timeLimit;
    
    private String groupId;
    
    /**
     * @deprecated
     */
    private String qrcodePath;
    
    private Integer isSearched;
    
    private Integer helpTimes;
    /**
	 * 是否开启答题留言功能
	 * 1表示开启，0表示未开启，默认是开启
	 * 此开关只适用于患者，医生不控制
	 */
	private Integer ifLeaveMessage;
    
    private String serviceItemId;
    
    private String hospitalId;
    
    /*咨询回复次数*/
    private Integer replyCount;

    /** 集团ID列表 add by tanyf 20160604*/
    private List<String> groupIds;
    
    //品种组id（积分问诊使用）
    private String goodsGroupId;
    
    //积分（积分问诊使用）
    private Integer point;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Integer getPackType() {
		return packType;
	}

	public void setPackType(Integer packType) {
		this.packType = packType;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<Integer> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<Integer> statusList) {
		this.statusList = statusList;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCareTemplateId() {
		return careTemplateId;
	}

	public void setCareTemplateId(String careTemplateId) {
		this.careTemplateId = careTemplateId;
	}

	public String getFollowTemplateId() {
		return followTemplateId;
	}

	public void setFollowTemplateId(String followTemplateId) {
		this.followTemplateId = followTemplateId;
	}

	public List<CPackDoctor> getPackDoctors() {
		return packDoctors;
	}

	public void setPackDoctors(List<CPackDoctor> packDoctors) {
		this.packDoctors = packDoctors;
	}

	public Integer getDoctorCount() {
		return doctorCount;
	}

	public void setDoctorCount(Integer doctorCount) {
		this.doctorCount = doctorCount;
	}

	public Integer getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getQrcodePath() {
		return qrcodePath;
	}

	public void setQrcodePath(String qrcodePath) {
		this.qrcodePath = qrcodePath;
	}

	public Integer getIsSearched() {
		return isSearched;
	}

	public void setIsSearched(Integer isSearched) {
		this.isSearched = isSearched;
	}

	public Integer getHelpTimes() {
		return helpTimes;
	}

	public void setHelpTimes(Integer helpTimes) {
		this.helpTimes = helpTimes;
	}

	public Integer getIfLeaveMessage() {
		return ifLeaveMessage;
	}

	public void setIfLeaveMessage(Integer ifLeaveMessage) {
		this.ifLeaveMessage = ifLeaveMessage;
	}

	public String getServiceItemId() {
		return serviceItemId;
	}

	public void setServiceItemId(String serviceItemId) {
		this.serviceItemId = serviceItemId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Integer getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(Integer replyCount) {
		this.replyCount = replyCount;
	}

	public List<String> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<String> groupIds) {
		this.groupIds = groupIds;
	}

	public String getGoodsGroupId() {
		return goodsGroupId;
	}

	public void setGoodsGroupId(String goodsGroupId) {
		this.goodsGroupId = goodsGroupId;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

}
