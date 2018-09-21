package com.dachen.health.pack.patient.model;

import com.dachen.health.pack.patient.service.impl.PatientServiceImpl;
import com.dachen.util.DateUtil;
import com.dachen.util.PropertiesUtil;

/**
 * 患者信息
 * @author 李淼淼
 * @version 1.0 2015-09-11
 */
public class Patient {
    private Integer id;

    /**
     * 姓名
     */
    private String userName;

    /**
     * 性别
     */
    private Short sex;

    /**
     * 生日
     */
    private Long birthday;

    /**
     * 关系
     */
    private String relation;

    /**
     * 所在地区
     */
    private String area;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 手机号
     */
    private String telephone;
    
    /**
     * 年龄
     */
    private int age;

    /**
     * 头像地址
     */
    private String topPath;
    /**
     * 身份证号码
     */
	private String idcard;
	/**
	 * 身份证类型  1身份证 2护照  3军官  4 台胞  5香港身份证 
	 */
	private String idtype;
    
	/**
	 * 身高
	 */
	private String height;
	/**
	 * 体重
	 */
	private String weight;
	/**
	 * 婚姻
	 */
	private String marriage;
	/**
	 * 职业
	 */
	private String professional;
	
//	private Boolean isCheckIn;
	
	
//    public Boolean getIsCheckIn() {
//		return isCheckIn;
//	}
//
//	public void setIsCheckIn(Boolean isCheckIn) {
//		this.isCheckIn = isCheckIn;
//	}
	private Integer checkInStatus;
	
	

	public Integer getCheckInStatus() {
		return checkInStatus;
	}

	public void setCheckInStatus(Integer checkInStatus) {
		this.checkInStatus = checkInStatus;
	}

	/**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getIdtype() {
		return idtype;
	}

	public void setIdtype(String idtype) {
		this.idtype = idtype;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getMarriage() {
		return marriage;
	}

	public void setMarriage(String marriage) {
		this.marriage = marriage;
	}

	public String getProfessional() {
		return professional;
	}

	public void setProfessional(String professional) {
		this.professional = professional;
	}

	/**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取姓名
     *
     * @return user_name - 姓名
     */
    public String getUserName() {
        return userName;
    }
    
    /**
     * 设置姓名
     *
     * @param userName 姓名
     */
    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    /**
     * 获取性别
     *
     * @return sex - 性别
     */
    public Short getSex() {
        return sex;
    }

    /**
     * 设置性别
     *
     * @param sex 性别
     */
    public void setSex(Short sex) {
        this.sex = sex;
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
     * 获取手机号
     *
     * @return telephone - 手机号
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * 设置手机号
     *
     * @param telephone 手机号
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    /**
     * 获取年龄
     *
     * @return age - 年龄
     */
	public int getAge() {
		age=DateUtil.calcAge(birthday);
		return age;
	}
	
	public String getAgeStr() {
		if(birthday!=null) {
    		int ages=DateUtil.calcAge(birthday);
    		if (ages == 0 || ages == -1) {
    			return DateUtil.calcMonth(birthday)<=0?"1个月":DateUtil.calcMonth(birthday)+"个月";
    		}
    		return ages + "岁";
    	}else {
    		return null;
    	}
	}
    
    /**
     * 获取头像地址
     *
     * @return top_path - 头像地址
     * 
     * 由于在更新updateByPrimaryKey、updateByPrimaryKeySelective时都会getTopPath，导致数据库中存储的头像地址带了IP/PORT
     * 现改成取出数据时添加代码PropertiesUtil.addUrlPrefix(patient.getTopPath())
     * @see PatientServiceImpl#findByPk(Integer)
     */
    public String getTopPath() {
		return PropertiesUtil.addUrlPrefix(topPath);
    }
    
    /**
     * 设置头像地址
     *
     * @param topPath 头像地址
     */
    public void setTopPath(String topPath) {
		this.topPath = topPath;
    }

    @Override
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
        Patient other = (Patient) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserName() == null ? other.getUserName() == null : this.getUserName().equals(other.getUserName()))
            && (this.getSex() == null ? other.getSex() == null : this.getSex().equals(other.getSex()))
            && (this.getBirthday() == null ? other.getBirthday() == null : this.getBirthday().equals(other.getBirthday()))
            && (this.getRelation() == null ? other.getRelation() == null : this.getRelation().equals(other.getRelation()))
            && (this.getArea() == null ? other.getArea() == null : this.getArea().equals(other.getArea()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getTelephone() == null ? other.getTelephone() == null : this.getTelephone().equals(other.getTelephone()))
            && (this.getTopPath() == null ? other.getTopPath() == null : this.getTopPath().equals(other.getTopPath()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserName() == null) ? 0 : getUserName().hashCode());
        result = prime * result + ((getSex() == null) ? 0 : getSex().hashCode());
        result = prime * result + ((getBirthday() == null) ? 0 : getBirthday().hashCode());
        result = prime * result + ((getRelation() == null) ? 0 : getRelation().hashCode());
        result = prime * result + ((getArea() == null) ? 0 : getArea().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getTelephone() == null) ? 0 : getTelephone().hashCode());
        result = prime * result + ((getTopPath() == null) ? 0 : getTopPath().hashCode());
        return result;
    }
}