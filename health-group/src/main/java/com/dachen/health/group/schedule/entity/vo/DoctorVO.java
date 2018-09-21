package com.dachen.health.group.schedule.entity.vo;

public class DoctorVO implements java.io.Serializable{

	
	private static final long serialVersionUID = 8647562577944607185L;

	
	/* 医生Id*/
	private Integer doctorId;
	
	/*医生姓名*/
	private String doctorName;
	
	/* 集团Id*/
	private String groupId;
	
	/* 所属医院 */
    private String hospitalName;

    /* 所属医院Id */
    private String hospitalId;

    /* 所属科室 */
    private String departments;
    
    /* 科室id*/
	private String departmentId;

    /* 职称 */
    private String title;

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
    
    
}
