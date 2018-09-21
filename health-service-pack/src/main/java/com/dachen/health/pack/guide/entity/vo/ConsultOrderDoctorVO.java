package com.dachen.health.pack.guide.entity.vo;


public class ConsultOrderDoctorVO {
	/*医生Id*/
	private Integer doctorId;
	/**
	 * 订单状态  默认未预约
	 */
	private String status;
	/**
	 * 创建时间
	 */
	private long createTime;
	/**
	 * 是否就诊  false  没有  true  有
	 */
	private Boolean isSeeDoctor;
	/**
	 * 就诊情况
	 */
	 private String seeDoctorMsg;
	public Integer getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public Boolean getIsSeeDoctor() {
		return isSeeDoctor;
	}
	public void setIsSeeDoctor(Boolean isSeeDoctor) {
		this.isSeeDoctor = isSeeDoctor;
	}
	public String getSeeDoctorMsg() {
		return seeDoctorMsg;
	}
	public void setSeeDoctorMsg(String seeDoctorMsg) {
		this.seeDoctorMsg = seeDoctorMsg;
	}
}
