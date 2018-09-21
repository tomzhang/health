package com.dachen.health.checkbill.entity.vo;

import java.util.List;

import com.dachen.health.checkbill.entity.po.CheckItem;
import com.dachen.util.JSONUtil;

public class CheckBillPageVo {

	private String id;
	
	private Integer patientId;
	
	private String patientName;
	
	private Integer doctorId;
	
	private String doctorName;
	
	private String hospitalId;
	
	private String hospitalName;
	
	private Long createTime;
	
	//1：未下订单，2：已经下订单，3：已接单，4:已上传结果
	private Integer checkBillStatus;
	
	/**
     * 订单类型（1：套餐订单；2：报到；3：门诊订单）
     */
    private Integer orderType;

    /**
     * 套餐类型
     */
    private Integer packType;
	
	private Long price;

	/**订单id**/
	private Integer orderId;
	
	private List<CheckItem> checkItemList ;
	
	private CheckBusInfo checkBusInfo;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public List<CheckItem> getCheckItemList() {
		return checkItemList;
	}

	public void setCheckItemList(List<CheckItem> checkItemList) {
		this.checkItemList = checkItemList;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Integer getCheckBillStatus() {
		return checkBillStatus;
	}

	public void setCheckBillStatus(Integer checkBillStatus) {
		this.checkBillStatus = checkBillStatus;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getPackType() {
		return packType;
	}

	public void setPackType(Integer packType) {
		this.packType = packType;
	}

	public CheckBusInfo getCheckBusInfo() {
		return checkBusInfo;
	}

	public void setCheckBusInfo(CheckBusInfo checkBusInfo) {
		this.checkBusInfo = checkBusInfo;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
	
}
