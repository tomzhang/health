package com.dachen.health.group.schedule.entity.po;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * ProjectName： health-group<br>
 * ClassName： Online<br>
 * Description： 在线值班表<br>
 * 
 * @author fanp
 * @createTime 2015年8月11日
 * @version 1.0.0
 */
@Entity(value = "c_online", noClassnameStored = true)
public class Online {
    @Id
    private ObjectId id;

    /* 集团id */
    private String groupId;

    /* 科室id */
    private String departmentId;

    /* 科室名称 */
    private String department;

    /* 医生信息 */
    @Embedded
    private List<OnlineDoctorInfo> doctors;

    /* 日期 */
    private Integer week;

    /* 上午、中午、下午、晚上 {@link ScheduleEnum.Period} */
    private Integer period;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

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

    public List<OnlineDoctorInfo> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<OnlineDoctorInfo> doctors) {
        this.doctors = doctors;
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

}
