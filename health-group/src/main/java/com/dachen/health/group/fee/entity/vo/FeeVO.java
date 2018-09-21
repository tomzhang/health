package com.dachen.health.group.fee.entity.vo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

@Entity(value = "c_group_fee", noClassnameStored = true)
@Indexes(@Index("groupId"))
public class FeeVO implements java.io.Serializable {

    private static final long serialVersionUID = -5241924712035694253L;

    /* 集团id */
    private String groupId;

    /* 图文咨询最低价 */
    private Integer textMin;

    /* 图文咨询最高价 */
    private Integer textMax;

    /* 电话咨询最低价 */
    private Integer phoneMin;

    /* 电话咨询最高价 */
    private Integer phoneMax;

    /* 门诊最低价 */
    private Integer clinicMin;

    /* 门诊最高价 */
    private Integer clinicMax;
    /* 计划关怀最低价*/
    private Integer carePlanMin;
    /* 计划关怀最高价*/
    private Integer carePlanMax;
    /* 会诊最高价*/
    private Integer consultationMax;
    /* 会诊最低价*/
    private Integer consultationMin;
    
    /*预约最低价*/
    private Integer appointmentMin;
    /*预约最高价*/
    private Integer appointmentMax;
    /** 预约默认价格 */
    private Integer appointmentDefault;
    
    public Integer getAppointmentDefault() {
		return appointmentDefault;
	}

	public void setAppointmentDefault(Integer appointmentDefault) {
		this.appointmentDefault = appointmentDefault;
	}

	public Integer getCarePlanMin() {
		return carePlanMin;
	}

	public void setCarePlanMin(Integer carePlanMin) {
		this.carePlanMin = carePlanMin;
	}

	public Integer getCarePlanMax() {
		return carePlanMax;
	}

	public void setCarePlanMax(Integer carePlanMax) {
		this.carePlanMax = carePlanMax;
	}

	public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getTextMin() {
        return textMin;
    }

    public void setTextMin(Integer textMin) {
        this.textMin = textMin;
    }

    public Integer getTextMax() {
        return textMax;
    }

    public void setTextMax(Integer textMax) {
        this.textMax = textMax;
    }

    public Integer getPhoneMin() {
        return phoneMin;
    }

    public void setPhoneMin(Integer phoneMin) {
        this.phoneMin = phoneMin;
    }

    public Integer getPhoneMax() {
        return phoneMax;
    }

    public void setPhoneMax(Integer phoneMax) {
        this.phoneMax = phoneMax;
    }

    public Integer getClinicMin() {
        return clinicMin;
    }

    public void setClinicMin(Integer clinicMin) {
        this.clinicMin = clinicMin;
    }

    public Integer getClinicMax() {
        return clinicMax;
    }

    public void setClinicMax(Integer clinicMax) {
        this.clinicMax = clinicMax;
    }

	public Integer getConsultationMax() {
		return consultationMax;
	}

	public void setConsultationMax(Integer consultationMax) {
		this.consultationMax = consultationMax;
	}

	public Integer getConsultationMin() {
		return consultationMin;
	}

	public void setConsultationMin(Integer consultationMin) {
		this.consultationMin = consultationMin;
	}

	public Integer getAppointmentMin() {
		return appointmentMin;
	}

	public void setAppointmentMin(Integer appointmentMin) {
		this.appointmentMin = appointmentMin;
	}

	public Integer getAppointmentMax() {
		return appointmentMax;
	}

	public void setAppointmentMax(Integer appointmentMax) {
		this.appointmentMax = appointmentMax;
	}

}
