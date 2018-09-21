package com.dachen.health.pack.consult.entity.vo;

import java.util.Map;

import com.dachen.health.pack.patient.model.CureRecord;
import com.dachen.health.pack.patient.model.Disease;

public class CureRecordAndDiseaseVo {

	private CureRecord cureRecord;
	
	/**
	 * mainDoctorName
	 * secondaryDoctorName
	 * orderType
	 * beginTime
	 * endTime
	 */
	private Map<String,Object> orderInfo;
	
	private Disease disease;

	public CureRecord getCureRecord() {
		return cureRecord;
	}

	public void setCureRecord(CureRecord cureRecord) {
		this.cureRecord = cureRecord;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Map<String, Object> getOrderInfo() {
		return orderInfo;
	}

	public void setOrderInfo(Map<String, Object> orderInfo) {
		this.orderInfo = orderInfo;
	}

	@Override
	public String toString() {
		return "CureRecordAndDiseaseVo [cureRecord=" + cureRecord + ", orderInfo=" + orderInfo + ", disease=" + disease
				+ "]";
	}
	
}
