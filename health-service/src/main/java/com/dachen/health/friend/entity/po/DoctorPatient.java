package com.dachen.health.friend.entity.po;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Indexes;

/**
 * ProjectName： health-service<br>
 * ClassName： DoctorPatient<br>
 * Description： 医患关系实体<br>
 * 
 * @author fanp
 * @crateTime 2015年6月29日
 * @version 1.0.0
 */
@Entity(value = "u_doctor_patient", noClassnameStored = true)
@Indexes(@Index("userId,status"))
public class DoctorPatient {

    @Id
    private ObjectId id;

    private Integer userId;

    private Integer toUserId;
    
    /**患者id**/
    private Integer patientId;

    /* 请求方 */
    private Integer supplicant;

    /* 标签 */
    private String[] tags;

    /* 添加时间 */
    private Long createTime;

    /* 状态 */
    @Indexed
    private Integer status;
    
    /**
     * 好友设置
     */
    @Embedded
    private FriendSetting setting;

    /**备注名**/
    private String remarkName;

    /**备注**/
    private String remark;

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getToUserId() {
        return toUserId;
    }

    public void setToUserId(Integer toUserId) {
        this.toUserId = toUserId;
    }

    public Integer getSupplicant() {
        return supplicant;
    }

    public void setSupplicant(Integer supplicant) {
        this.supplicant = supplicant;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public FriendSetting getSetting() {
		return setting;
	}

	public void setSetting(FriendSetting setting) {
		this.setting = setting;
	}

}
