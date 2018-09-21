package com.dachen.health.pack.order.entity.po;

import java.util.List;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： Case<br>
 * Description： 病例信息表<br>
 * 
 * @author fanp
 * @createTime 2015年9月7日
 * @version 1.0.0
 */
public class Case {

    /* 主键 */
    private Integer id;

    /* 医生id */
    private Integer doctorId;

    /* 用户id */
    private Integer userId;

    /* 患者id */
    private Integer patientId;

    /* 患者姓名 */
    private String patientName;

    /* 性别 */
    private Short sex;

    /* 生日 */
    private Long birthday;

    /* 报到id */
    private Integer checkInId;

    /* 手机 */
    private String phone;

    /* 就诊医院 */
    private String hospital;

    /* 病例号 */
    private String recordNum;

    /* 上一次就诊时间 */
    private Long lastCureTime;

    /* 诊断描述 */
    private String description;

    /* 留言 */
    private String message;
    
    /* 疾病ID */
    private String diseaseId;
    
    private List<String> caseImgs;
    
    public List<String> getCaseImgs() {
		return caseImgs;
	}

	public void setCaseImgs(List<String> caseImgs) {
		this.caseImgs = caseImgs;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Short getSex() {
        return sex;
    }

    public void setSex(Short sex) {
        this.sex = sex;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Integer getCheckInId() {
        return checkInId;
    }

    public void setCheckInId(Integer checkInId) {
        this.checkInId = checkInId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(String recordNum) {
        this.recordNum = recordNum;
    }

    public Long getLastCureTime() {
        return lastCureTime;
    }

    public void setLastCureTime(Long lastCureTime) {
        this.lastCureTime = lastCureTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

	public String getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(String diseaseId) {
		this.diseaseId = diseaseId;
	}

}