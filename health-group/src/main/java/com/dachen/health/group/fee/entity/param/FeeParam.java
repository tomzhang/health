package com.dachen.health.group.fee.entity.param;

public class FeeParam {
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
    /*最低价*/
    private Integer appointmentMin;
    /*最高价*/
    private Integer appointmentMax;
    /*默认价*/
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
