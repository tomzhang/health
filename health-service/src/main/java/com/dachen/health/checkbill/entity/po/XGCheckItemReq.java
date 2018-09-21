package com.dachen.health.checkbill.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

/**
 * Created by fuyongde on 2017/1/12.
 */
@Entity(value = "t_xg_check_item_req",noClassnameStored = true)
public class XGCheckItemReq {

    @Id
    private String id;
    private Integer patientId;
    private Integer sex;
    private String patientName;
    private String hospitalId;
    private List<String> images;
    private String medicalHistoryUrl;
    private String checkUpId;
    private Long createTime;
    private Long updateTime;
    private Boolean success;
    private String responseMessage;
    private String checkItemId;
    private String phone;
    private String address;

    public String getCheckItemId() {
        return checkItemId;
    }

    public void setCheckItemId(String checkItemId) {
        this.checkItemId = checkItemId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getMedicalHistoryUrl() {
        return medicalHistoryUrl;
    }

    public void setMedicalHistoryUrl(String medicalHistoryUrl) {
        this.medicalHistoryUrl = medicalHistoryUrl;
    }

    public String getCheckUpId() {
        return checkUpId;
    }

    public void setCheckUpId(String checkUpId) {
        this.checkUpId = checkUpId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
