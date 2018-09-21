package com.dachen.health.commons.vo;

public class SimpleUserVO {
   
    private Integer userId;// 用户Id
    
    private String username;// 用户名
    
    private Integer userType;// 用户类型

    private String telephone;

    private String name;// 姓名

    private Long birthday;// 生日
    /**
     * 年龄
     */
    private int age;

    private Integer sex;// 性别1男，2女 3 保密

    private Integer status;
    
    

    /**
     * 用户级别 0到期 1游客 2临时用户 3认证用户
     * @return
     */
    private Integer userLevel;

    /**
     * 用户级别有效期
     */
    private Long limitedPeriodTime;


	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getBirthday() {
		return birthday;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(Integer userLevel) {
		this.userLevel = userLevel;
	}

	public Long getLimitedPeriodTime() {
		return limitedPeriodTime;
	}

	public void setLimitedPeriodTime(Long limitedPeriodTime) {
		this.limitedPeriodTime = limitedPeriodTime;
	}
}
