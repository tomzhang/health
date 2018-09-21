package com.dachen.health.group.schedule.entity.po;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import com.dachen.util.DateUtil;

/**
 * ProjectName： health-group<br>
 * ClassName： Offline<br>
 * Description： 线下门诊表<br>
 * 
 * @author fanp
 * @createTime 2015年8月11日
 * @version 1.0.0
 */
@Entity(value = "c_offline", noClassnameStored = true)
public class Offline implements Cloneable {

    @Id
    private ObjectId id;

    /* 医生id */
    @Indexed
    private Integer doctorId;

    /* 医院 */
    private String hospital;
    
    /*医院id*/
    private String hospitalId;

    /* 门诊类型 */
    private Integer clinicType;

    /* 门诊价格 */
    private Long price;

    /* 时间 */
    private Integer week;

    /* 上午、中午、下午、晚上 {@link ScheduleEnum.Period} */
    private Integer period;
    
    /* 坐诊开始时间（主要获取时间） */
    private Long startTime;
    
    /* 坐诊结束时间（主要获取时间） */
    private Long endTime;
    
    /*排班日期*/
    private Long dateTime;
    
    /* 坐诊开始时间(主要获取时间)*/
    private String startTimeString;
    
    /* 坐诊结束时间(主要获取时间)*/
    private String endTimeString;
    
    /*排班日期(字符串格式)*/
    private String dateTimeString;
    
    /*医生排班数据更新时间*/
    private Long updateTime;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
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
	
	public Long getDateTime() {
		return dateTime;
	}

	public void setDateTime(Long dateTime) {
		this.dateTime = dateTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	
	

	public String getStartTimeString() {
		if(startTime!=null){
			return DateUtil.getMinuteTimeByLong(startTime);
		}
		return startTimeString;
	}

	public void setStartTimeString(String startTimeString) {
		this.startTimeString = startTimeString;
	}

	public String getEndTimeString() {
		if(endTime!=null){
			return DateUtil.getMinuteTimeByLong(endTime);
		}
		return endTimeString;
	}

	public void setEndTimeString(String endTimeString) {
		this.endTimeString = endTimeString;
	}

	public String getDateTimeString() {
		if(dateTime!=null){
			return DateUtil.formatDate(dateTime);
		}
		return dateTimeString;
	}

	public void setDateTimeString(String dateTimeString) {
		this.dateTimeString = dateTimeString;
	}

	@Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
