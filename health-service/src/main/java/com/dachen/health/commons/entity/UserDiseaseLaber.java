package com.dachen.health.commons.entity;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by Administrator on 2016/12/2.
 */
@Entity(noClassnameStored = true, value = "t_user_disease_laber")
public class UserDiseaseLaber {

    @Id
    private String id;

    private Integer userId;

    private String diseaseId;
    
    private String parentId;

    private Long createTime;

    private Integer status;
    
    private Integer weight;//权重:1患者自行关注 2患者治疗时自动添加 3患者关注且看过对应疾病
    
    private Boolean fromTreat; //通过问诊自动添加，不对用户显示

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(String diseaseId) {
        this.diseaseId = diseaseId;
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

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Boolean getFromTreat() {
		return fromTreat;
	}

	public void setFromTreat(Boolean fromTreat) {
		this.fromTreat = fromTreat;
	}
    
}
