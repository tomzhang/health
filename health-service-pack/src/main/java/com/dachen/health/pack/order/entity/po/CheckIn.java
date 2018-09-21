package com.dachen.health.pack.order.entity.po;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： Case<br>
 * Description： 病例信息表<br>
 * 
 * @author fanp
 * @createTime 2015年9月7日
 * @version 1.0.0
 */
public class CheckIn {
    /* 主键 */
    private Integer id;

    /* 用户id */
    private Integer userId;

    /* 患者id */
    private Integer patientId;
    
    /* 医生id */
    private Integer doctorId;

    /* 上一次就诊时间 */
    private Long createTime;
    private Long lastUpdateTime;

    /* 状态 {@link OrderEnum.CheckInStatus} */
    private Integer status;
    
    /* 报道来源 {@link CheckInParam.CheckInFrom} */
    private Integer checkInFrom;
    
    /* 是否具有报道赠送服务 */
    private Integer freePack;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public Integer getCheckInFrom() {
		return checkInFrom;
	}

	public void setCheckInFrom(Integer checkInFrom) {
		this.checkInFrom = checkInFrom;
	}

	public Integer getFreePack() {
		return freePack;
	}

	public void setFreePack(Integer freePack) {
		this.freePack = freePack;
	}
    
}