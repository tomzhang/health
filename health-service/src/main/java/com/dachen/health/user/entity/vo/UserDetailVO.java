package com.dachen.health.user.entity.vo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.mongodb.morphia.annotations.Property;

import com.dachen.health.base.helper.UserHelper;

public class UserDetailVO implements Serializable {

    private static final long serialVersionUID = -824126746373143270L;

    /* 用户id */
    @Property("_id")
    private Integer userId;

    /* 用户名称 */
    private String name;

    /* 手机号 */
    private String telephone;

    /* 性别 */
    private Integer sex;

    /* 年龄 */
    private Integer age;

    /* 区域 */
    private String area;

    /* 头像 */
    private String headPicFileName;

    /* 医院 */
    private String hospital;

    /* 科室 */
    private String departments;

    /* 医生号 */
    private String doctorNum;

    /* 职称 */
    private String title;

    /* 好友设置 */
    private Object setting;

    /* 用户类型 */
    private Integer userType;
    
    private Integer status;
    
    /* 集团及集团科室列表 */
    private List<Map<String, Object>> groupList;
    
    private Object msg;
    
    /* 集团id */
    private String groupId;

    /* 集团名称 */
    private String groupName;


    /* 个人介绍 */
    private String introduction;


    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getHeadPicFileName() {
    	return UserHelper.buildHeaderPicPath(headPicFileName, userId, sex, userType);
    }

    public void setHeadPicFileName(String headPicFileName) {
        this.headPicFileName = headPicFileName;
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

    public String getDoctorNum() {
        return doctorNum;
    }

    public void setDoctorNum(String doctorNum) {
        this.doctorNum = doctorNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getSetting() {
        return setting;
    }

    public void setSetting(Object setting) {
        this.setting = setting;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<Map<String, Object>> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<Map<String, Object>> groupList) {
		this.groupList = groupList;
	}

	public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msgs) {
        this.msg = msgs;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public UserDetailVO deepClone() throws IOException, ClassNotFoundException {
        // 将对象写到流里
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(this);
        // 从流里读出来
        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);
        return (UserDetailVO) (oi.readObject());
    }

}
