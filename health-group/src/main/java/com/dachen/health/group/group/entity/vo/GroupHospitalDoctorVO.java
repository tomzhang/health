package com.dachen.health.group.group.entity.vo;

/**
 * 医院与医生关系信息VO
 * @author wangqiao
 * @date 2016年3月29日
 */
public class GroupHospitalDoctorVO {

	/**
	 * 医院与医生关系id
	 * @author wangqiao
	 * @date 2016年3月30日
	 */
	private String id;
	
	/**
	 * 医生id
	 * @author wangqiao
	 * @date 2016年3月29日
	 */
	private Integer doctorId;
	
	/**
	 * 医院id
	 * @author wangqiao
	 * @date 2016年3月29日
	 */
	private String hospitalId;
	
	/**
	 * 集团医院id
	 * @author wangqiao
	 * @date 2016年3月29日
	 */
	private String groupHospitalId;
	
	/**
	 * 医院名称
	 * @author wangqiao
	 * @date 2016年3月29日
	 */
	private String hospitalName;
	
	/**
	 * 医生科室id
	 * @author wangqiao
	 * @date 2016年3月29日
	 */
	private String departmentId;
	
	/**
	 * 医生科室名称
	 * @author wangqiao
	 * @date 2016年3月29日
	 */
	private String departmentName;
	
	/**
	 * 医生职称
	 * @author wangqiao
	 * @date 2016年3月29日
	 */
	private String title;
	
	/**
	 * 医生加入医院的状态 （unregistered=未注册，register=已注册没加入医院，hospitalDoctor=已加入医院，
	 * root=已加入医院并且是超级管理员，onlyManage=已加入医院并且是唯一管理员）
	 * @author wangqiao
	 * @date 2016年3月29日
	 */
	private String hospitalStatus;
	
	/**
	 * 是否允许成员邀请医生加入
	 */
	private boolean memberInvite;
	
	/**
	 * 邀请加入的集团医院id
	 * @author wangqiao
	 * @date 2016年4月1日
	 */
	private String  inviteGroupHospitalId;
	
	/**
	 * 医生手机号
	 * @author wangqiao
	 * @date 2016年4月1日
	 */
	private String telephone;

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getGroupHospitalId() {
		return groupHospitalId;
	}

	public void setGroupHospitalId(String groupHospitalId) {
		this.groupHospitalId = groupHospitalId;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHospitalStatus() {
		return hospitalStatus;
	}

	public void setHospitalStatus(String hospitalStatus) {
		this.hospitalStatus = hospitalStatus;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isMemberInvite() {
		return memberInvite;
	}

	public void setMemberInvite(boolean memberInvite) {
		this.memberInvite = memberInvite;
	}

	public String getInviteGroupHospitalId() {
		return inviteGroupHospitalId;
	}

	public void setInviteGroupHospitalId(String inviteGroupHospitalId) {
		this.inviteGroupHospitalId = inviteGroupHospitalId;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	
	
}
