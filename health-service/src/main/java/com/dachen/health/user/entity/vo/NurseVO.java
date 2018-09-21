package com.dachen.health.user.entity.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.NotSaved;

public class NurseVO {

	  /* 状态 */
    private Integer status;
	
	/* 名字 */
	private String name;
	
	private String telephone;
	
	/* 工作年限 */
	private String idCard;

	private  List<Map<String,Object>> images = new ArrayList<Map<String,Object>>();

	/* 所属医院 */
	private String hospital;
	/* 所属医院Id */
	private String hospitalId;

	/* 所属科室 */
	private String departments;

	/* 职称 */
	private String title;
	
	
	/* 审核意见*/
	private String remark;

	
	
	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	private  @NotSaved String token;//为了调用获取职业资料的接口

	/* 护士号 */
    private String nurseNum;
	/* 审核信息 */
	@Embedded
	private Check check;


	public String getNurseNum() {
		return nurseNum;
	}


	public void setNurseNum(String nurseNum) {
		this.nurseNum = nurseNum;
	}


	public String getToken() {
		return token;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setToken(String token) {
		this.token = token;
	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
	public Check getCheck() {
		return check;
	}

	public void setCheck(Check check) {
		this.check = check;
	}


	// 审核信息
	public static class Check {
		/* 所属医院 */
		private String hospital;

		/* 所属医院Id */
		private String hospitalId;

		/* 所属科室 */
		private String departments;

		/* 职称 */
		private String title;

		/* 审核人员 */
		private String checker;

		/* 审核人员Id */
		private String checkerId;

		/* 审核意见 */
		private String remark;

		public String getHospital() {
			return hospital;
		}

		public void setHospital(String hospital) {
			this.hospital = hospital;
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

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}



		public String getChecker() {
			return checker;
		}

		public void setChecker(String checker) {
			this.checker = checker;
		}

		public String getCheckerId() {
			return checkerId;
		}

		public void setCheckerId(String checkerId) {
			this.checkerId = checkerId;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

	}


	public String getIdCard() {
		return idCard;
	}


	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public List<Map<String, Object>> getImages() {
		return images;
	}


	public void setImages(List<Map<String, Object>> images) {
		this.images = images;
	}


	public Integer getStatus() {
		return status;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}


	public String getTelephone() {
		return telephone;
	}


	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	
}
