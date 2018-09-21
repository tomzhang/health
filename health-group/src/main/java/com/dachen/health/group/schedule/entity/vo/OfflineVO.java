package com.dachen.health.group.schedule.entity.vo;

import java.util.List;

import com.dachen.util.JSONUtil;

public class OfflineVO implements java.io.Serializable {

    private static final long serialVersionUID = 5284379872981677337L;

    private String id;

    /* 医院 */
    private String hospital;
    
    
    private String hospitalId;
    
    private Integer doctorId;

    /* 门诊类型 {@link ScheduleEnum.ClinicType} */
    private Integer clinicType;

    /* 门诊价格 */
    private Long price;

    /* 时间 */
    private Integer week;

    /* 上午、中午、下午、晚上 {@link ScheduleEnum.Period} */
    private Integer period;
    /*
     * 和用户的距离，km
     */
    private String distance;
    
    private  List<OfflineVO> offlins;
    
    /*********名医面对面功能新增****************/
    /* 坐诊开始时间（主要获取时间） */
    private Long startTime;
    
    /* 坐诊结束时间（主要获取时间） */
    private Long endTime;
    
    /*排班日期*/
    private Long dateTime;
    
    /*坐诊开始时间（字符串显示）*/
    private String startTimeString;
    
    /*坐诊结束时间（字符串显示）*/
    private String endTimeString;
    
    /*排班日期（字符串显示）*/
    private String dateTimeString;




	public List<OfflineVO> getOfflins() {
		return offlins;
	}

	public void setOfflins(List<OfflineVO> offlins) {
		this.offlins = offlins;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
    
    public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public Integer getClinicType() {
        return clinicType;
    }

    public void setClinicType(Integer clinicType) {
        this.clinicType = clinicType;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	
	public String getStartTimeString() {
		return startTimeString;
	}

	public void setStartTimeString(String startTimeString) {
		this.startTimeString = startTimeString;
	}

	public String getEndTimeString() {
		return endTimeString;
	}

	public void setEndTimeString(String endTimeString) {
		this.endTimeString = endTimeString;
	}
	

	public Long getDateTime() {
		return dateTime;
	}

	public void setDateTime(Long dateTime) {
		this.dateTime = dateTime;
	}

	public String getDateTimeString() {
		return dateTimeString;
	}

	public void setDateTimeString(String dateTimeString) {
		this.dateTimeString = dateTimeString;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
    

}
