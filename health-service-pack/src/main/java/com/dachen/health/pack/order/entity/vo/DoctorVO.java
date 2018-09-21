package com.dachen.health.pack.order.entity.vo;

import java.util.List;

import com.dachen.health.disease.entity.DiseaseType;

public class DoctorVO {
		/**
		 * 会诊用
		 * 1：主诊医生
		 * 2：发起会诊医生
		 * 3：普通会诊医生
		 */
		private int doctorRole;
	 	private String doctorName;
	    
	    private String doctorPath;
	    
	    private String doctorSpecialty;
	    
	    private String doctorGroup;
	    
	    private String title;
	    
	    private String telephone;

	    private String hospital;
	    
	    private Integer doctorId;
	    
	    private String city;
	    private String sex;
	    
	    private String skill;
	    
	    private List<DiseaseType> expertise;
	    
	    private String introduction;
	    
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getDoctorRole() {
			return doctorRole;
		}

		public void setDoctorRole(int doctorRole) {
			this.doctorRole = doctorRole;
		}

		public String getDoctorName() {
			return doctorName;
		}

		public void setDoctorName(String doctorName) {
			this.doctorName = doctorName;
		}

		public String getDoctorPath() {
			return doctorPath;
		}

		public void setDoctorPath(String doctorPath) {
			this.doctorPath = doctorPath;
		}

		public String getDoctorSpecialty() {
			return doctorSpecialty;
		}

		public void setDoctorSpecialty(String doctorSpecialty) {
			this.doctorSpecialty = doctorSpecialty;
		}

		public String getDoctorGroup() {
			return doctorGroup;
		}

		public void setDoctorGroup(String doctorGroup) {
			this.doctorGroup = doctorGroup;
		}

		public String getTelephone() {
			return telephone;
		}

		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}

		public String getHospital() {
			return hospital;
		}

		public void setHospital(String hospital) {
			this.hospital = hospital;
		}

		public Integer getDoctorId() {
				return doctorId;
			}

		public void setDoctorId(Integer doctorId) {
			this.doctorId = doctorId;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getSex() {
			return sex;
		}

		public String getSkill() {
			return skill;
		}

		public void setSkill(String skill) {
			this.skill = skill;
		}

		public List<DiseaseType> getExpertise() {
			return expertise;
		}

		public void setExpertise(List<DiseaseType> expertise) {
			this.expertise = expertise;
		}

		public String getIntroduction() {
			return introduction;
		}

		public void setIntroduction(String introduction) {
			this.introduction = introduction;
		}

		public void setSex(Integer sex) {
			if(sex == null){
				sex = 3;
			}
			switch (sex) {
			case 1:
				this.sex="男";
				break;
			case 2:
				this.sex="女";
				break;
			default:
				this.sex="保密";
				break;
			}
		}
}
