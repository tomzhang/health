package com.dachen.health.pack.stat.entity.vo;


/**
 * ProjectName： health-group<br>
 * ClassName： PatientStatVO<br>
 * Description：患者统计VO <br>
 * 
 * @author liwei 
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
public class PatientGroupStatVO implements java.io.Serializable {

    private static final long serialVersionUID = -4954653798336508529L;

    /* 患者id */
    private Integer userId;

    /* 集团id */
    private String groupId;
    
    private String doctorId;
    

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

}
