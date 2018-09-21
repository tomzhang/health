package com.dachen.health.pack.order.entity.vo;

import com.dachen.util.DateUtil;

public class OrderKeyInfoVO {
	
	 private int orderId;
	 
	 private String patientAge;
	 
	 private String patientName;
	 
	 private String patientSex;
	 
	 private String patientArea;
	 
	 private Long birthday;
	 
	 
	public Long getBirthday() {
		return birthday;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public String getPatientArea() {
		return patientArea;
	}

	public void setPatientArea(String patientArea) {
		this.patientArea = patientArea;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientSex() {
		return patientSex;
	}

	public void setPatientSex(String patientSex) {
		this.patientSex = patientSex;
	}

	public String getPatientAge() {
		if(birthday==null) {
    		return null;
    	}
    	int ages=DateUtil.calcAge(birthday);
		if (ages == 0) {
			return DateUtil.calcMonth(birthday)==0?"1个月":DateUtil.calcMonth(birthday)+"个月";
		}
		return ages + "岁";
	}

	public void setPatientAge(String patientAge) {
		this.patientAge = patientAge;
	}
	 
	 

}
