package com.dachen.health.pack.consult.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

import com.dachen.util.JSONUtil;
/**
 * 转诊记录
 * @author wangl
 * @date 2016年5月13日10:36:07
 * @desc 该表数据目前只做展示
 */

@Entity(value = "t_ill_transfer_record",noClassnameStored = true)
public class IllTransferRecord {

	@Id
	private String id;
	
	/**
	 * 电子病历id
	 */
	private String illCaseInfoId;
	
	/**
	 * 原订单id
	 */
	private Integer preOrderId;
	
	/**
	 * 新订单id
	 */
	private Integer targetOrderId;
	
	/**
	 * 转诊时间
	 */
	private Long transferTime;
	
	/**
	 * 接诊时间
	 */
	private Long receiveTime;
	
	/**
	 * 转诊医生id
	 */
	private Integer transferDoctorId;
	
	/**
	 * 接诊医生id
	 */
	private Integer receiveDoctorId;
	
	private @NotSaved String transferDoctorName;
	
	private @NotSaved String receiveDoctorName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIllCaseInfoId() {
		return illCaseInfoId;
	}

	public void setIllCaseInfoId(String illCaseInfoId) {
		this.illCaseInfoId = illCaseInfoId;
	}

	public Integer getPreOrderId() {
		return preOrderId;
	}

	public void setPreOrderId(Integer preOrderId) {
		this.preOrderId = preOrderId;
	}

	public Integer getTargetOrderId() {
		return targetOrderId;
	}

	public void setTargetOrderId(Integer targetOrderId) {
		this.targetOrderId = targetOrderId;
	}

	public Long getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(Long transferTime) {
		this.transferTime = transferTime;
	}

	public Long getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Long receiveTime) {
		this.receiveTime = receiveTime;
	}

	public Integer getTransferDoctorId() {
		return transferDoctorId;
	}

	public void setTransferDoctorId(Integer transferDoctorId) {
		this.transferDoctorId = transferDoctorId;
	}

	public Integer getReceiveDoctorId() {
		return receiveDoctorId;
	}

	public void setReceiveDoctorId(Integer receiveDoctorId) {
		this.receiveDoctorId = receiveDoctorId;
	}

	public String getTransferDoctorName() {
		return transferDoctorName;
	}

	public void setTransferDoctorName(String transferDoctorName) {
		this.transferDoctorName = transferDoctorName;
	}

	public String getReceiveDoctorName() {
		return receiveDoctorName;
	}

	public void setReceiveDoctorName(String receiveDoctorName) {
		this.receiveDoctorName = receiveDoctorName;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
	
}
