package com.dachen.health.pack.food.entity.po;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * 患者的进食记录
 * Created by fuyongde on 2017/2/23.
 */
@Entity(value = "t_diet_record",noClassnameStored = true)
public class DietRecord {
    @Id
    private String id;
    /**患者id**/
    private Integer patientId;
    /**食物名称**/
    private String foodName;
    /**不良反应**/
    private String reactions;
    /**进食时间**/
    private Long dietTime;
    /**进食日期**/
    private String dietDate;
    /**创建时间**/
    private Long createTime;
    /**更新时间**/
    private Long updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getReactions() {
        return reactions;
    }

    public void setReactions(String reactions) {
        this.reactions = reactions;
    }

    public Long getDietTime() {
        return dietTime;
    }

    public void setDietTime(Long dietTime) {
        this.dietTime = dietTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getDietDate() {
        return dietDate;
    }

    public void setDietDate(String dietDate) {
        this.dietDate = dietDate;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
