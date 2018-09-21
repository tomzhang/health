package com.dachen.health.pack.patient.model;

import java.util.List;

import com.dachen.util.DateUtil;

/**
 * 病情信息
 * @author 李淼淼
 * @version 1.0 2015-09-11
 */
public class Disease {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 患者id
     */
    private Integer patientId;

    /**
     * 需要帮助
     */
    private Boolean needHelp;

    /**
     * 创建时间
     */
    private Long createdTime;

    /**
     * 创建人
     */
    private Integer createUserId;

    /**
     * 电话号码
     */
    private String telephone;
    
    /**
     * 所在地区
     */
    private String area;

    /**
     * 关系
     */
    private String relation;

    /**
     * 生日
     */
    private Long birthday;

    /**
     * 性别
     */
    private String userName;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别
     */
    private Integer sex;
    
    /**
     * 就诊时间
     */
    private Long visitTime;
    
    
    /**
     * 身高
     */
    private String heigth;

    /**
     * 体重
     */
    private String weigth;

    /**
     * 婚姻
     */
    private String marriage;

    /**
     * 职业
     */
    private String profession;
    
    public Long getVisitTime() {
		return visitTime;
	}


	public void setVisitTime(Long visitTime) {
		this.visitTime = visitTime;
	}


	public String getHeigth() {
		return heigth;
	}


	public void setHeigth(String heigth) {
		this.heigth = heigth;
	}


	public String getWeigth() {
		return weigth;
	}


	public void setWeigth(String weigth) {
		this.weigth = weigth;
	}


	public String getMarriage() {
		return marriage;
	}


	public void setMarriage(String marriage) {
		this.marriage = marriage;
	}


	public String getProfession() {
		return profession;
	}


	public void setProfession(String profession) {
		this.profession = profession;
	}

	/**
     * 病情信息
     */
    private String diseaseInfo;
    //*************2016年1月19日15:16:40  新增开始******************//
    /**
     * isSeeDoctor
     */
    private Boolean isSeeDoctor;

    /**
     * 现病史
     */
    private String diseaseInfoNow;

    /**
     * 既往史
     */
    private String diseaseInfoOld;

    /**
     * 家族史
     */
    private String familyDiseaseInfo;

    /**
     * 月经史
     */
    private String menstruationdiseaseInfo;

    /**
     * 就诊情况
     */
    private String seeDoctorMsg;
    
  //诊治情况 无用的接口
    private String cureSituation;
    //*************2016年1月19日15:16:40  新增结束******************//
    
    
    
	public String getMenstruationdiseaseInfo() {
		return menstruationdiseaseInfo;
	}

	
	public String getCureSituation() {
		return cureSituation;
	}


	public void setCureSituation(String cureSituation) {
		this.cureSituation = cureSituation;
	}


	public Boolean getIsSeeDoctor() {
		return isSeeDoctor;
	}

	public void setIsSeeDoctor(Boolean isSeeDoctor) {
		this.isSeeDoctor = isSeeDoctor;
	}

	public String getDiseaseInfoNow() {
		return diseaseInfoNow;
	}

	public void setDiseaseInfoNow(String diseaseInfoNow) {
		this.diseaseInfoNow = diseaseInfoNow;
	}

	public String getDiseaseInfoOld() {
		return diseaseInfoOld;
	}

	public void setDiseaseInfoOld(String diseaseInfoOld) {
		this.diseaseInfoOld = diseaseInfoOld;
	}

	public String getFamilyDiseaseInfo() {
		return familyDiseaseInfo;
	}

	public void setFamilyDiseaseInfo(String familyDiseaseInfo) {
		this.familyDiseaseInfo = familyDiseaseInfo;
	}

	public String getSeeDoctorMsg() {
		return seeDoctorMsg;
	}

	public void setSeeDoctorMsg(String seeDoctorMsg) {
		this.seeDoctorMsg = seeDoctorMsg;
	}

	public void setMenstruationdiseaseInfo(String menstruationdiseaseInfo) {
		this.menstruationdiseaseInfo = menstruationdiseaseInfo;
	}

	private List<String> diseaseImgs;
	
	private String[] imagePaths;
    
    
    private String voice;
    
    public String[] getImagePaths() {
		return imagePaths;
	}


	public void setImagePaths(String[] imagePaths) {
		this.imagePaths = imagePaths;
	}


	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public List<String> getDiseaseImgs() {
		return diseaseImgs;
	}

	public void setDiseaseImgs(List<String> diseaseImgs) {
		this.diseaseImgs = diseaseImgs;
	}

	/**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
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
     * 获取需要帮助
     *
     * @return need_help - 需要帮助
     */
    public Boolean getNeedHelp() {
        return needHelp;
    }

    /**
     * 设置需要帮助
     *
     * @param needHelp 需要帮助
     */
    public void setNeedHelp(Boolean needHelp) {
        this.needHelp = needHelp;
    }

    /**
     * 获取创建时间
     *
     * @return created_time - 创建时间
     */
    public Long getCreatedTime() {
        return createdTime;
    }

    /**
     * 设置创建时间
     *
     * @param createdTime 创建时间
     */
    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * 获取创建人
     *
     * @return create_user_id - 创建人
     */
    public Integer getCreateUserId() {
        return createUserId;
    }

    /**
     * 设置创建人
     *
     * @param createUserId 创建人
     */
    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    /**
     * 获取电话号码
     *
     * @return telephone - 电话号码
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * 设置电话号码
     *
     * @param telephone 电话号码
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }
    
    /**
     * 获取所在地区
     *
     * @return area - 所在地区
     */
    public String getArea() {
        return area;
    }

    /**
     * 设置所在地区
     *
     * @param area 所在地区
     */
    public void setArea(String area) {
        this.area = area == null ? null : area.trim();
    }

    /**
     * 获取关系
     *
     * @return relation - 关系
     */
    public String getRelation() {
        return relation;
    }

    /**
     * 设置关系
     *
     * @param relation 关系
     */
    public void setRelation(String relation) {
        this.relation = relation == null ? null : relation.trim();
    }

    /**
     * 获取生日
     *
     * @return birthday - 生日
     */
    public Long getBirthday() {
        return birthday;
    }

    /**
     * 设置生日
     *
     * @param birthday 生日
     */
    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    /**
     * 获取性别
     *
     * @return user_name - 性别
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置性别
     *
     * @param userName 性别
     */
    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    /**
     * 获取年龄
     *
     * @return age - 年龄
     */
    public Integer getAge() {
    	return age;
    }
    
    public String getPatientAge() {
    	if(birthday==null) {
    		return null;
    	}
    	age=DateUtil.calcAge(birthday);
		if (age == 0) {
			return DateUtil.calcMonth(birthday)==0?"1个月":DateUtil.calcMonth(birthday)+"个月";
		}
		return age + "岁";
    }
    

    /**
     * 设置年龄
     *
     * @param age 年龄
     */
    public void setAge(Integer age) {
        this.age = age;
    }
    
   
    
    /**
     * 获取性别
     *
     * @return sex - 性别
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 设置性别
     *
     * @param sex 性别
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }
    
    /**
     * 获取病情信息
     *
     * @return disease_info - 病情信息
     */
    public String getDiseaseInfo() {
        return diseaseInfo;
    }

    /**
     * 设置病情信息
     *
     * @param diseaseInfo 病情信息
     */
    public void setDiseaseInfo(String diseaseInfo) {
        this.diseaseInfo = diseaseInfo == null ? null : diseaseInfo.trim();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((age == null) ? 0 : age.hashCode());
		result = prime * result + ((area == null) ? 0 : area.hashCode());
		result = prime * result
				+ ((birthday == null) ? 0 : birthday.hashCode());
		result = prime * result
				+ ((createUserId == null) ? 0 : createUserId.hashCode());
		result = prime * result
				+ ((createdTime == null) ? 0 : createdTime.hashCode());
		result = prime * result
				+ ((diseaseInfo == null) ? 0 : diseaseInfo.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((needHelp == null) ? 0 : needHelp.hashCode());
		result = prime * result
				+ ((patientId == null) ? 0 : patientId.hashCode());
		result = prime * result
				+ ((relation == null) ? 0 : relation.hashCode());
		result = prime * result + ((sex == null) ? 0 : sex.hashCode());
		result = prime * result
				+ ((telephone == null) ? 0 : telephone.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		result = prime * result + ((voice == null) ? 0 : voice.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Disease other = (Disease) obj;
		if (age == null) {
			if (other.age != null)
				return false;
		} else if (!age.equals(other.age))
			return false;
		if (area == null) {
			if (other.area != null)
				return false;
		} else if (!area.equals(other.area))
			return false;
		if (birthday == null) {
			if (other.birthday != null)
				return false;
		} else if (!birthday.equals(other.birthday))
			return false;
		if (createUserId == null) {
			if (other.createUserId != null)
				return false;
		} else if (!createUserId.equals(other.createUserId))
			return false;
		if (createdTime == null) {
			if (other.createdTime != null)
				return false;
		} else if (!createdTime.equals(other.createdTime))
			return false;
		if (diseaseInfo == null) {
			if (other.diseaseInfo != null)
				return false;
		} else if (!diseaseInfo.equals(other.diseaseInfo))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (needHelp == null) {
			if (other.needHelp != null)
				return false;
		} else if (!needHelp.equals(other.needHelp))
			return false;
		if (patientId == null) {
			if (other.patientId != null)
				return false;
		} else if (!patientId.equals(other.patientId))
			return false;
		if (relation == null) {
			if (other.relation != null)
				return false;
		} else if (!relation.equals(other.relation))
			return false;
		if (sex == null) {
			if (other.sex != null)
				return false;
		} else if (!sex.equals(other.sex))
			return false;
		if (telephone == null) {
			if (other.telephone != null)
				return false;
		} else if (!telephone.equals(other.telephone))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		if (voice == null) {
			if (other.voice != null)
				return false;
		} else if (!voice.equals(other.voice))
			return false;
		return true;
	}

	 public static void main(String[] args) {
		Disease di = new Disease();
		di.setBirthday(306864000000l);
		
		System.out.println(di.getPatientAge());
		
		
		
		
	}
    

   /* @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Disease other = (Disease) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPatientId() == null ? other.getPatientId() == null : this.getPatientId().equals(other.getPatientId()))
            && (this.getNeedHelp() == null ? other.getNeedHelp() == null : this.getNeedHelp().equals(other.getNeedHelp()))
            && (this.getCreatedTime() == null ? other.getCreatedTime() == null : this.getCreatedTime().equals(other.getCreatedTime()))
            && (this.getCreateUserId() == null ? other.getCreateUserId() == null : this.getCreateUserId().equals(other.getCreateUserId()))
            && (this.getTelephone() == null ? other.getTelephone() == null : this.getTelephone().equals(other.getTelephone()))
            && (this.getDiseaseInfo() == null ? other.getDiseaseInfo() == null : this.getDiseaseInfo().equals(other.getDiseaseInfo()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPatientId() == null) ? 0 : getPatientId().hashCode());
        result = prime * result + ((getNeedHelp() == null) ? 0 : getNeedHelp().hashCode());
        result = prime * result + ((getCreatedTime() == null) ? 0 : getCreatedTime().hashCode());
        result = prime * result + ((getCreateUserId() == null) ? 0 : getCreateUserId().hashCode());
        result = prime * result + ((getTelephone() == null) ? 0 : getTelephone().hashCode());
        result = prime * result + ((getDiseaseInfo() == null) ? 0 : getDiseaseInfo().hashCode());
        return result;
    }*/
    
    
    
}