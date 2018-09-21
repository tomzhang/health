package com.dachen.line.stat.entity.vo;


//name="v_patient_Line")
public class PatientLine {
	
	private int id;
	private int psId;//患者服务ID
	private int lsId;//线下服务ID	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPsId() {
		return psId;
	}
	public void setPsId(int psId) {
		this.psId = psId;
	}
	public int getLsId() {
		return lsId;
	}
	public void setLsId(int lsId) {
		this.lsId = lsId;
	}
	
	
	
	
}
