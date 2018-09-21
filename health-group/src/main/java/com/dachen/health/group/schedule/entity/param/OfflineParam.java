package com.dachen.health.group.schedule.entity.param;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import com.dachen.util.DateUtil;
import com.dachen.util.JSONUtil;

public class OfflineParam {

    private ObjectId id;
    
    private String offlineItemId;

    /* 医生id */
    private Integer doctorId;

    /* 医院名称*/
    private String hospital;
    
    /*医院id*/
    private String hospitalId;

    /* 门诊类型 {@link ScheduleEnum.ClinicType} */
    private Integer clinicType;

    /* 门诊价格 */
    private Long price;

    /* 坐诊时间 */
    private List<OfflineClinicDate> clinicDate;

    /* 时间 */
    private Integer week;

    /* 上午、中午、下午、晚上 {@link ScheduleEnum.Period} */
    private Integer period;
    
    /* 排班开始时间（主要获取时间） */
    private Long startTime;
    
    /* 排班结束时间（主要获取时间） */
    private Long endTime;
    
    /* 排班日期*/
    private Long dateTime;

    /*接收客户端的开始时间*/
    private String startTimeString;
    
    /*接收客户端的结束时间*/
    private String endTimeString;
    
    /* 排班日期*/
    private String dateTimeString;
    

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getOfflineItemId() {
		return offlineItemId;
	}

	public void setOfflineItemId(String offlineItemId) {
		this.offlineItemId = offlineItemId;
	}

	public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
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

    public List<OfflineClinicDate> getClinicDate() {
        return clinicDate;
    }

    public void setClinicDate(List<OfflineClinicDate> clinicDate) {
        this.clinicDate = clinicDate;
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
		if(StringUtils.isNotBlank(startTimeString)){
			startTime = DateUtil.parseTimeStringToToDayTime(startTimeString);
		}
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		if(StringUtils.isNotBlank(endTimeString)){
			endTime = DateUtil.parseTimeStringToToDayTime(endTimeString);
		}
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
		if(StringUtils.isNotBlank(dateTimeString)){
			dateTime=DateUtil.toDate(dateTimeString).getTime();
		}
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
