package com.dachen.health.group.schedule.entity.param;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.dachen.util.DateUtil;
import com.dachen.util.JSONUtil;

public class OfflineClinicDate {

    /* 时间 */
    private Integer week;

    /* 上午、中午、下午、晚上 {@link ScheduleEnum.Period} */
    private List<Integer> period;
    
    /*值班开始时间*/
    private Long startTime;
    
    /*值班结束时间*/
    private Long endTime;
    
    /*接收客户端的开始时间*/
    private String startTimeString;
    
    /*接收客户端的结束时间*/
    private String endTimeString;

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public List<Integer> getPeriod() {
        return period;
    }

    public void setPeriod(List<Integer> period) {
        this.period = period;
    }

	public Long getStartTime() {
		if(StringUtils.isNotBlank(startTimeString)){
			startTime = DateUtil.parseTimeStringToToDayTime(startTimeString);
		}
		return startTime;
	}

	public Long getEndTime() {
		if(StringUtils.isNotBlank(endTimeString)){
			endTime = DateUtil.parseTimeStringToToDayTime(endTimeString);
		}
		return endTime;
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

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
	
	public static void main(String[] args) throws ParseException {
		/*System.out.println(DateUtil.FORMAT_YYYY_MM_DD_HH_MM_SS.format(DateUtil.FORMAT_HH_MM.parse("0:0")));
		
		System.out.println(DateUtil.FORMAT_HH_MM.parse("00:0").getTime());
		System.out.println(DateUtil.FORMAT_HH_MM.parse("0:00").getTime());
		System.out.println(DateUtil.FORMAT_HH_MM.parse("90:00").getTime());
		
		System.out.println(DateUtil.formatDate2Str(new Date(0l)));
		
		System.out.println(DateUtil.formatDate2Str(DateUtil.getDayBegin(System.currentTimeMillis())));
		
		System.out.println(DateUtil.formatDate2Str(new Date(DateUtil.FORMAT_HH_MM.parse("16:0").getTime()+DateUtil.getDayBegin(System.currentTimeMillis())+28800000)));*/
	}

    
}