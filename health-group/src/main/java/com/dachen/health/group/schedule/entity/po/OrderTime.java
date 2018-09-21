package com.dachen.health.group.schedule.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;


/**
 * ProjectName： health-group<br>
 * ClassName： OrderTime<br>
 * Description： 可预约时间表<br>
 * @author fanp
 * @createTime 2015年8月11日
 * @version 1.0.0
 */
public class OrderTime {
    
    @Id
    private ObjectId id;

    /* 集团id */
    private ObjectId groupId;

    /* 医生id */
    private Integer doctorId;

    /* 预约时间 */
    private Long time;

    /* 预约人数 */
    private Integer orderNum;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getGroupId() {
        return groupId;
    }

    public void setGroupId(ObjectId groupId) {
        this.groupId = groupId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
    
}
