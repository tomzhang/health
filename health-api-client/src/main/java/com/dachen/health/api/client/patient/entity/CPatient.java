package com.dachen.health.api.client.patient.entity;

import java.io.Serializable;

public class CPatient implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
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
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getTopPath() {
		return topPath;
	}
	public void setTopPath(String topPath) {
		this.topPath = topPath;
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
	

}
