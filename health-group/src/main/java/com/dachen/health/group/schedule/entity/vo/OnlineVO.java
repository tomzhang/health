package com.dachen.health.group.schedule.entity.vo;

import java.util.List;

import com.dachen.health.group.schedule.entity.param.OnlineClinicDate;

public class OnlineVO implements java.io.Serializable {

    private static final long serialVersionUID = 5284379872981677337L;

    /* 科室 */
    private String departmentId;

    /* 科室 */
    private String department;

    /* 集团 */
    private String group;

    /* 时间 */
    private Integer week;

    /* 上午、中午、下午、晚上 {@link ScheduleEnum.Period} */
    private Integer period;

    private String startTime;

    private String endTime;

    /* 值班时间时间 */
    private List<OnlineClinicDate> clinicDate;

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<OnlineClinicDate> getClinicDate() {
        return clinicDate;
    }

    public void setClinicDate(List<OnlineClinicDate> clinicDate) {
        this.clinicDate = clinicDate;
    }

}
