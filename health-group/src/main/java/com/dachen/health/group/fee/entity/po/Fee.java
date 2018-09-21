package com.dachen.health.group.fee.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

/**
 * ProjectName： health-group<br>
 * ClassName： Fee<br>
 * Description： 集团收费设置实体<br>
 * 
 * @author fanp
 * @createTime 2015年9月21日
 * @version 1.0.0
 */
@Entity(value = "c_fee", noClassnameStored = true)
@Indexes(@Index("groupId"))
public class Fee {
    
    @Id
    private String id;

    /* 集团id */
    private String groupId;

    /* 图文咨询最低价 */
    private Integer textMin;

    /* 图文咨询最高价 */
    private Integer textMax;

    /* 电话咨询最低价 */
    private Integer phoneMin;

    /* 电话咨询最高价 */
    private Integer phoneMax;

    /* 门诊最低价 */
    private Integer clinicMin;

    /* 门诊最高价 */
    private Integer clinicMax;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getTextMin() {
        return textMin;
    }

    public void setTextMin(Integer textMin) {
        this.textMin = textMin;
    }

    public Integer getTextMax() {
        return textMax;
    }

    public void setTextMax(Integer textMax) {
        this.textMax = textMax;
    }

    public Integer getPhoneMin() {
        return phoneMin;
    }

    public void setPhoneMin(Integer phoneMin) {
        this.phoneMin = phoneMin;
    }

    public Integer getPhoneMax() {
        return phoneMax;
    }

    public void setPhoneMax(Integer phoneMax) {
        this.phoneMax = phoneMax;
    }

    public Integer getClinicMin() {
        return clinicMin;
    }

    public void setClinicMin(Integer clinicMin) {
        this.clinicMin = clinicMin;
    }

    public Integer getClinicMax() {
        return clinicMax;
    }

    public void setClinicMax(Integer clinicMax) {
        this.clinicMax = clinicMax;
    }

}
