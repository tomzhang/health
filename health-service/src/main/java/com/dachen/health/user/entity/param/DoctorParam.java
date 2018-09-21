package com.dachen.health.user.entity.param;

import com.dachen.commons.page.PageVO;

public class DoctorParam extends PageVO{

//	  * @apiSuccess {int}     	provinceId                  省份Id
//	     * @apiSuccess {String}     provinceName                省份名称
//	     * @apiSuccess {int}     	cityId                      城市Id
//	     * @apiSuccess {String}     cityName                    城市名称
//	     * @apiParam   {int}     countyId               	区县Id
//	     * @apiParam   {String}     countyName              区县名称
	
	public int provinceId;
	public String provinceName;
	public int cityId;
	public String cityName;
	public int countyId;
	public String countyName;
	
    private Integer userId;

    /* 姓名 */
    private String name;

    /* 医院 */
    private String hospital;

    /* 医院 */
    private String hospitalId;

    /* 科室 */
    private String departments;
    
    /* 科室 */
    private String deptId;
    
    private String guideOrderId;
    
    /* 职称 */
    private String title;
    
    private Integer status;
    
    /**
     * 医生号
     */
    private String doctorNum;
    
    /**
     * 电话
     */
    private String telephone;
    
    /* 用户状态 */
    private Integer statuses[];
    
    /**
     * 查询关键字
     */
    private String keyWord;

    public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public int getCountyId() {
		return countyId;
	}

	public void setCountyId(int countyId) {
		this.countyId = countyId;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer[] getStatuses() {
        return statuses;
    }

    public void setStatuses(Integer[] statuses) {
        this.statuses = statuses;
    }

	public String getDoctorNum() {
		return doctorNum;
	}

	public void setDoctorNum(String doctorNum) {
		this.doctorNum = doctorNum;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getGuideOrderId() {
		return guideOrderId;
	}

	public void setGuideOrderId(String guideOrderId) {
		this.guideOrderId = guideOrderId;
	}
}
