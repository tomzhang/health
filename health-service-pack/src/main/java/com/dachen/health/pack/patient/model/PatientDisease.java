package com.dachen.health.pack.patient.model;

/**
 * 患者病种标签表
 * @author 李淼淼
 * @version 1.0 2016-06-06
 */
public class PatientDisease {
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 患者id
     */
    private Integer patientId;

    /**
     * 医生id
     */
    private Integer doctorId;

    /**
     * 订单id
     */
    private Integer orderId;

    private String diseaseTypeId;

    private String diseaseTypeName;

    private String groupId;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取患者id
     *
     * @return patient_id - 患者id
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者id
     *
     * @param patientId 患者id
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取医生id
     *
     * @return doctor_id - 医生id
     */
    public Integer getDoctorId() {
        return doctorId;
    }

    /**
     * 设置医生id
     *
     * @param doctorId 医生id
     */
    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * 获取订单id
     *
     * @return order_id - 订单id
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置订单id
     *
     * @param orderId 订单id
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * @return disease_type_id
     */
    public String getDiseaseTypeId() {
        return diseaseTypeId;
    }

    /**
     * @param diseaseTypeId
     */
    public void setDiseaseTypeId(String diseaseTypeId) {
        this.diseaseTypeId = diseaseTypeId == null ? null : diseaseTypeId.trim();
    }

    /**
     * @return disease_type_name
     */
    public String getDiseaseTypeName() {
        return diseaseTypeName;
    }

    /**
     * @param diseaseTypeName
     */
    public void setDiseaseTypeName(String diseaseTypeName) {
        this.diseaseTypeName = diseaseTypeName == null ? null : diseaseTypeName.trim();
    }

    /**
     * @return group_id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId == null ? null : groupId.trim();
    }
}