package com.dachen.health.pack.consult.entity.po;

import java.util.List;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

import com.dachen.util.JSONUtil;

@Entity(value = "t_ill_case_info",noClassnameStored = true)
public class IllCaseInfo {

	@Id
	private String id;
	
	private Integer doctorId;
	
	private Integer orderId;
	/*主诉*/
	private String mainCase;

	/*主诉图片*/
	private List<String> imageUlrs; 
	
	/**
	 * order(1, "咨询套餐"),
    checkIn(2, "报到套餐"),
	outPatient(3, "门诊套餐"),
	care(4, "健康关怀套餐"),
	followUp(5,"随访套餐"),
	throughTrain(6, "直通车套餐"),
	consultation(7, "会诊套餐");
	 */
	private Integer treateType;
	
	
	/*创建患者的用户Id*/
	private Integer userId;
	
	private Integer patientId;
	
	private Long createTime;
	
	private Long updateTime;
	
	private boolean isSaved;
	
	private @NotSaved String seeDoctorMsg;

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public Integer getDoctorId() {
		return doctorId;
	}


	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}


	public Integer getPatientId() {
		return patientId;
	}


	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}


	public Long getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Integer getUserId() {
		return userId;
	}


	public void setUserId(Integer userId) {
		this.userId = userId;
	}


	public boolean isSaved() {
		return isSaved;
	}


	public void setSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}


	public Integer getOrderId() {
		return orderId;
	}


	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}


	public String getMainCase() {
		return mainCase;
	}


	public void setMainCase(String mainCase) {
		this.mainCase = mainCase;
	}


	public Integer getTreateType() {
		return treateType;
	}


	public void setTreateType(Integer treateType) {
		this.treateType = treateType;
	}

	public List<String> getImageUlrs() {
		return imageUlrs;
	}


	public void setImageUlrs(List<String> imageUlrs) {
		this.imageUlrs = imageUlrs;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public String getSeeDoctorMsg() {
		return seeDoctorMsg;
	}

	public void setSeeDoctorMsg(String seeDoctorMsg) {
		this.seeDoctorMsg = seeDoctorMsg;
	}


	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

	
}
