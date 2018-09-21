package com.dachen.health.pack.guide.entity.po;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.dachen.health.user.entity.po.Doctor;

@Entity(value = "t_consult_order_doctor",noClassnameStored = true)
public class ConsultOrderDoctorPO {
	/*医生Id*/
	@Id
	private String id;
	//医生id
	private String doctorId;
	/**
	 * 订单id
	 */
	private String comsultOrderId;
	
	@Embedded
    private Doctor doctor; 
	/**
	 * 订单状态  默认未预约 2--待支付  8--未预约  3--已关闭
	 */
	private int status;
	/**
	 * 创建时间
	 */
	private long createTime;
	
	public Doctor getDoctor() {
		return doctor;
	}
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	public String getComsultOrderId() {
		return comsultOrderId;
	}
	public void setComsultOrderId(String comsultOrderId) {
		this.comsultOrderId = comsultOrderId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
}
