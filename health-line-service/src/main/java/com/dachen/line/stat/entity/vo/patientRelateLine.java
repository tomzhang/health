package com.dachen.line.stat.entity.vo;


//@Table(name="v_patient_relate_line")
public class patientRelateLine {
	
	private int id;
	private int psId;//患者服务ID
	private int lsId;//线下服务ID	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLsId() {
		return lsId;
	}
	public int getPsId() {
		return psId;
	}
	public void setPsId(int psId) {
		this.psId = psId;
	}
	public void setLsId(int lsId) {
		this.lsId = lsId;
	}
	
}
