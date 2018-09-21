package com.dachen.health.group.schedule.entity.param;

import java.util.List;

import org.bson.types.ObjectId;

public class OnlineParam {

    /* 科室 */
    private ObjectId departmentId;
    
    /* 时间 */
    private Integer week;

    /* 上午、中午、下午、晚上 {@link ScheduleEnum.Period} */
    private Integer period; 

    /* 值班时间时间 */
    private List<OnlineClinicDate> clinicDate;

    /* 医生id */
    private Integer doctorId;

    public ObjectId getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = new ObjectId(departmentId);
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

    public List<OnlineClinicDate> getClinicDate() {
        return clinicDate;
    }

    public void setClinicDate(List<OnlineClinicDate> clinicDate) {
        this.clinicDate = clinicDate;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

}
