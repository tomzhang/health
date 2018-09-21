package com.dachen.health.group.schedule.entity.param;

import java.util.List;

import com.dachen.health.group.schedule.entity.po.OnlineDoctorInfo;

public class OnlineClinicDate {

    /* 时间 */
    private Integer week;

    /* 上午、中午、下午、晚上 {@link ScheduleEnum.Period} */
    private Integer period;

    private List<OnlineDoctorInfo> doctors;

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

    public List<OnlineDoctorInfo> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<OnlineDoctorInfo> doctors) {
        this.doctors = doctors;
    }

}