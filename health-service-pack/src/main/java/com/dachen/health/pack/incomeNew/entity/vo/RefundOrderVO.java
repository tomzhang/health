package com.dachen.health.pack.incomeNew.entity.vo;

public class RefundOrderVO {
	
	private String refundTime;
	
	private Long refundAmt;
	
	private Integer refundStatus;
	
	private String serialNumber;
	
	//医生名、上级医生名、集团名
	private String name;
	
	//医生金额、上级医生金额、集团金额
	private Double amount;
	
	private RefundOrderVO groupInfo;
	
	private RefundOrderVO parentDoctorInfo;

	public String getRefundTime() {
		return refundTime;
	}

	public void setRefundTime(String refundTime) {
		this.refundTime = refundTime;
	}

	public Long getRefundAmt() {
		return refundAmt;
	}

	public void setRefundAmt(Long refundAmt) {
		this.refundAmt = refundAmt;
	}

	public Integer getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(Integer refundStatus) {
		this.refundStatus = refundStatus;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public RefundOrderVO getGroupInfo() {
		return groupInfo;
	}

	public void setGroupInfo(RefundOrderVO groupInfo) {
		this.groupInfo = groupInfo;
	}

	public RefundOrderVO getParentDoctorInfo() {
		return parentDoctorInfo;
	}

	public void setParentDoctorInfo(RefundOrderVO parentDoctorInfo) {
		this.parentDoctorInfo = parentDoctorInfo;
	}


}
