package com.dachen.health.pack.illhistory.entity.po;

import java.util.List;

/**
 * 病程中健康关怀的信息
 * Created by fuyongde on 2016/12/7.
 */
public class RecordCare {
    /**关怀计划的名称**/
    private String name;
    /**订单id**/
    private Integer orderId;
    /**会话id**/
    private String messageGroupId;
    /**主医生**/
    private Integer mainDoctor;
    /**专家组**/
    private List<Integer> groupDoctors;
    /**就诊时间**/
    private Long startTime;
    /**所患疾病**/
    private String diseaseId;
    /**病症时长**/
    private String diseaseDuration;
    /**病情描述**/
    private String diseaseDesc;
    /**药物商品id**/
    private List<String> drugGoodsIds;
    /**用药图片资料**/
    private List<String> drugPicUrls;
    /**用药情况**/
    private String drugCase;
    /**希望获得医生什么帮助**/
    private String hopeHelp;
    /**图片信息**/
    private List<String> pics;
    /**是否就诊过**/
    private Boolean isSeeDoctor;
    /**诊治情况**/
    private String treatCase;
    /**是否已付款**/
    private Boolean isPay;
    /**
     * 医生id
     */
    private Integer doctorId;

    /**用药信息（冗余订单类型的用药数据，病程中不予展示）**/
    private String drugInfos;

    public String getDrugInfos() {
        return drugInfos;
    }

    public void setDrugInfos(String drugInfos) {
        this.drugInfos = drugInfos;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getMessageGroupId() {
        return messageGroupId;
    }

    public void setMessageGroupId(String messageGroupId) {
        this.messageGroupId = messageGroupId;
    }

    public Integer getMainDoctor() {
        return mainDoctor;
    }

    public void setMainDoctor(Integer mainDoctor) {
        this.mainDoctor = mainDoctor;
    }

    public List<Integer> getGroupDoctors() {
        return groupDoctors;
    }

    public void setGroupDoctors(List<Integer> groupDoctors) {
        this.groupDoctors = groupDoctors;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public String getDiseaseDesc() {
        return diseaseDesc;
    }

    public void setDiseaseDesc(String diseaseDesc) {
        this.diseaseDesc = diseaseDesc;
    }

    public String getHopeHelp() {
        return hopeHelp;
    }

    public void setHopeHelp(String hopeHelp) {
        this.hopeHelp = hopeHelp;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public Boolean getSeeDoctor() {
        return isSeeDoctor;
    }

    public void setSeeDoctor(Boolean seeDoctor) {
        isSeeDoctor = seeDoctor;
    }

    public Boolean getPay() {
        return isPay;
    }

    public void setPay(Boolean pay) {
        isPay = pay;
    }

    public String getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(String diseaseId) {
        this.diseaseId = diseaseId;
    }

    public String getDiseaseDuration() {
        return diseaseDuration;
    }

    public void setDiseaseDuration(String diseaseDuration) {
        this.diseaseDuration = diseaseDuration;
    }

    public List<String> getDrugGoodsIds() {
        return drugGoodsIds;
    }

    public void setDrugGoodsIds(List<String> drugGoodsIds) {
        this.drugGoodsIds = drugGoodsIds;
    }

    public List<String> getDrugPicUrls() {
        return drugPicUrls;
    }

    public void setDrugPicUrls(List<String> drugPicUrls) {
        this.drugPicUrls = drugPicUrls;
    }

    public String getDrugCase() {
        return drugCase;
    }

    public void setDrugCase(String drugCase) {
        this.drugCase = drugCase;
    }

    public String getTreatCase() {
        return treatCase;
    }

    public void setTreatCase(String treatCase) {
        this.treatCase = treatCase;
    }
}
