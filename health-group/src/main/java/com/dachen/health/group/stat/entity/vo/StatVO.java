package com.dachen.health.group.stat.entity.vo;

/**
 * ProjectName： health-group<br>
 * ClassName： AssessStatVO<br>
 * Description：考核统计VO <br>
 * 
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
public class StatVO implements java.io.Serializable {

    private static final long serialVersionUID = 599951218433682914L;

    /* 医生id */
    private Integer id;

    /* 医生姓名 */
    private String name;

    private String hospital;

    private String departments;

    private String title;
    
    // 病种Id
    private String diseaseId;

    /* 统计数量 */
    private Integer value;

    private String telephone;

    /* 时间 */
    private String time;

    private String headPicFileName;

    private Integer sex;

    private Integer age;
    
    private String ageStr;

    /* 金额，单位元 */
    private Long money;

    /* 订单号 */
    private String orderNo;

    /* 订单类型 */
    private Integer packType;

    /* 订单名称 */
    private Integer packName;
    
    private Integer status;// 状态

    /**患者所属用户的id**/
    private Integer userId;

    private String statusName;// 状态名称

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getAgeStr() {
		return ageStr;
	}

	public void setAgeStr(String ageStr) {
		this.ageStr = ageStr;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(String diseaseId) {
		this.diseaseId = diseaseId;
	}

	public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHeadPicFileName() {
        return headPicFileName;
    }

    public void setHeadPicFileName(String headPicFileName) {
        this.headPicFileName = headPicFileName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getPackType() {
        return packType;
    }

    public void setPackType(Integer packType) {
        this.packType = packType;
    }

    public Integer getPackName() {
        return packName;
    }

    public void setPackName(Integer packName) {
        this.packName = packName;
    }

}
