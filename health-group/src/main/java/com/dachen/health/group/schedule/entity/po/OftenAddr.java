package com.dachen.health.group.schedule.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * ProjectName： health-group<br>
 * ClassName： OftenAddr<br>
 * Description：常用地址 <br>
 * 
 * @author fanp
 * @createTime 2015年8月14日
 * @version 1.0.0
 */
@Entity(value = "c_often_addr", noClassnameStored = true)
public class OftenAddr {

    @Id
    private ObjectId id;

    private Integer doctorId;

    private String hospital;

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

}
