package com.dachen.health.pack.guide.entity.vo;

import java.util.List;

import com.dachen.health.commons.vo.User;

public class OrderDiseaseVO {
    /*订单Id（ConsultOrderPO）*/
	private String orderId;
	
	private PatientVO orderVo;
	
	private String ysTelephone;
	
	private User user;

    /*病情图*/
    private List<String> imgStringPath; 
    
    /*病情描述*/
    private String diseaseDesc;
    /*病情id*/
    private String diseaseId;

    /*病人电话*/
    private String telephone;
    
    //*************2016年1月19日15:16:40  新增开始******************//
    //现病史
    private String diseaseInfo_now;
    //既往史
    private String diseaseInfo_old;
    //家族史
    private String familydiseaseInfo;
    //月经史
    private String menstruationdiseaseInfo;
    //诊治情况
    private String cureSituation;
  //就诊情况 
    private String seeDoctorMsg;   
    //是否就诊  false  没有  true  有
    private String isSeeDoctor;   
    //*************2016年1月19日15:16:40  新增结束******************//
    
    public String getDiseaseId() {
		return diseaseId;
	}

	public String getDiseaseInfo_now() {
		return diseaseInfo_now;
	}

	public void setDiseaseInfo_now(String diseaseInfo_now) {
		this.diseaseInfo_now = diseaseInfo_now;
	}

	public String getDiseaseInfo_old() {
		return diseaseInfo_old;
	}

	public void setDiseaseInfo_old(String diseaseInfo_old) {
		this.diseaseInfo_old = diseaseInfo_old;
	}

	public String getFamilydiseaseInfo() {
		return familydiseaseInfo;
	}

	public void setFamilydiseaseInfo(String familydiseaseInfo) {
		this.familydiseaseInfo = familydiseaseInfo;
	}

	public String getMenstruationdiseaseInfo() {
		return menstruationdiseaseInfo;
	}

	public void setMenstruationdiseaseInfo(String menstruationdiseaseInfo) {
		this.menstruationdiseaseInfo = menstruationdiseaseInfo;
	}

	public String getCureSituation() {
		return cureSituation;
	}

	public void setCureSituation(String cureSituation) {
		this.cureSituation = cureSituation;
	}

	public String getIsSeeDoctor() {
		return isSeeDoctor;
	}

	public void setIsSeeDoctor(String isSeeDoctor) {
		this.isSeeDoctor = isSeeDoctor;
	}

	public void setDiseaseId(String diseaseId) {
		this.diseaseId = diseaseId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getDiseaseDesc() {
		return diseaseDesc;
	}

	public void setDiseaseDesc(String diseaseDesc) {
		this.diseaseDesc = diseaseDesc;
	}
	public PatientVO getOrderVo() {
		return orderVo;
	}

	public void setOrderVo(PatientVO orderVo) {
		this.orderVo = orderVo;
	}

	public List<String> getImgStringPath() {
		return imgStringPath;
	}

	public void setImgStringPath(List<String> imgStringPath) {
		this.imgStringPath = imgStringPath;
	}
	
	public String getYsTelephone() {
		return ysTelephone;
	}

	public void setYsTelephone(String ysTelephone) {
		this.ysTelephone = ysTelephone;
	}



	public static class PatientVO
	{
		
		private Integer orderType;

		private Integer packType;
		    
		private Integer userId;
		
		/* 病人id  对应Patient表主键 */
	    private Integer patientId;
	    
	    /*患者姓名*/
	    private String patientName;
	    
	    /*性别*/
	    private Short sex;

	    /*关系*/
	    private String relation;
	    
	    /*年龄*/
	    private int age;
	    
	    /*电话号码*/
	    private String telephone;
	    
	    /**
	     * 患者信息备注
	     */
	    private String remark;
	    
	    
	    public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
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

		public String getRelation() {
			return relation;
		}

		public void setRelation(String relation) {
			this.relation = relation;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public String getTelephone() {
			return telephone;
		}

		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}

		public Integer getOrderType() {
			return orderType;
		}

		public void setOrderType(Integer orderType) {
			this.orderType = orderType;
		}

		public Integer getPackType() {
			return packType;
		}

		public void setPackType(Integer packType) {
			this.packType = packType;
		}
	    
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	
	public String getSeeDoctorMsg() {
		return seeDoctorMsg;
	}

	public void setSeeDoctorMsg(String seeDoctorMsg) {
		this.seeDoctorMsg = seeDoctorMsg;
	}

}
