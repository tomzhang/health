package com.dachen.health.group.common.entity.vo;

/**
 * 
 * @author pijingwei
 * @date 2015/8/19
 */
public class QrCodeInfo {

	private String uuid;
	
	private Integer doctorId;
	
	private String qrPath;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getQrPath() {
		return qrPath;
	}

	public void setQrPath(String qrPath) {
		this.qrPath = qrPath;
	}
}
