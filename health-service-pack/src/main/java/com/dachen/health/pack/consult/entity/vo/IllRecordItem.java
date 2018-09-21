package com.dachen.health.pack.consult.entity.vo;

import java.util.List;

public class IllRecordItem {

	private String illCaseInfoId;
	
	private Integer treatType;
	
	private String mainCase;
	
	private List<String> imageUrls;
	
	private Long updateTime;

	private String doctorName;
	
	public String getIllCaseInfoId() {
		return illCaseInfoId;
	}

	public void setIllCaseInfoId(String illCaseInfoId) {
		this.illCaseInfoId = illCaseInfoId;
	}

	public Integer getTreatType() {
		return treatType;
	}

	public void setTreatType(Integer treatType) {
		this.treatType = treatType;
	}

	public String getMainCase() {
		return mainCase;
	}

	public void setMainCase(String mainCase) {
		this.mainCase = mainCase;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	@Override
	public String toString() {
		return "IllRecordItem [illCaseInfoId=" + illCaseInfoId + ", treatType=" + treatType + ", mainCase=" + mainCase
				+ ", imageUrls=" + imageUrls + ", updateTime=" + updateTime + ", doctorName=" + doctorName + "]";
	}

}
