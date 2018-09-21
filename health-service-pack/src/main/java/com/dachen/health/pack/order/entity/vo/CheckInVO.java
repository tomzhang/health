package com.dachen.health.pack.order.entity.vo;

import java.util.List;

import com.dachen.util.DateUtil;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： CheckInVO<br>
 * Description：报到vo <br>
 * 
 * @author fanp
 * @createTime 2015年9月7日
 * @version 1.0.0
 */
public class CheckInVO implements java.io.Serializable {

    private static final long serialVersionUID = 8557170399856595400L;

    /* 报到id */
    private Integer checkInId;

    private Long createTime;

    private String patientName;

    private Short sex;

    private Long birthday;

    private Integer age;
    
    /* 报道来源*/
    private Integer checkInFrom;
    
    /* 是否具有报道赠送套餐 */
    private Integer freePack;
    
    /*所在地区*/
    private String area;

    /* 用户id */
    private Integer userId;
    
    /* 用户姓名 */
    private String userName;

    /* 病例id */
    private Integer caseId;

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
    
    private Integer orderId;
    

    private List<String> imageUrls;

    /* 报到状态 {@link OrderEnum.CheckInStatus} */
    private Integer status;

    private String topPath;
    
    private  Integer patientId;
    
    private  Integer doctorId;
    
    private String doctorName;
    private String doctorPhone;
    
    private String diseaseId;
    
    private String patientAge;

    /**备注名**/
    private String remarkName;

    /**备注**/
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public String getDoctorPhone() {
		return doctorPhone;
	}

	public void setDoctorPhone(String doctorPhone) {
		this.doctorPhone = doctorPhone;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPatientAge() {
    	if(birthday!=null) {
    		int ages=DateUtil.calcAge(birthday);
    		if (ages == 0) {
    			return DateUtil.calcMonth(birthday)==0?"1个月":DateUtil.calcMonth(birthday)+"个月";
    		}
    		return ages + "岁";
    	}else {
    		return null;
    	}
	}

	public void setPatientAge(String patientAge) {
		this.patientAge = patientAge;
	}

	public Integer getCheckInId() {
        return checkInId;
    }

    public void setCheckInId(Integer checkInId) {
        this.checkInId = checkInId;
    }
    

    public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getCheckInFrom() {
		return checkInFrom;
	}

	public void setCheckInFrom(Integer checkInFrom) {
		this.checkInFrom = checkInFrom;
	}

	public Integer getFreePack() {
		return freePack;
	}

	public void setFreePack(Integer freePack) {
		this.freePack = freePack;
	}

	public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getCaseId() {
        return caseId;
    }

    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
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

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTopPath() {
        return topPath;
    }

    public void setTopPath(String topPath) {
        this.topPath = topPath;
    }

	public String getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(String diseaseId) {
		this.diseaseId = diseaseId;
	}

}
