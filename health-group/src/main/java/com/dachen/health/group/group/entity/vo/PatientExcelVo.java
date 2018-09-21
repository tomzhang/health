package com.dachen.health.group.group.entity.vo;

import java.util.List;

public class PatientExcelVo {
	private String id;
	private Long registerTime;
	private String registerTimeStr;
	private String name;
	private String phone;
	private String sex;
	private String birthday;
	private String ageStr;
	private String address;
	
	/**邀请者的姓名**/
	private String inviterName;
	/**邀请人的手机号码**/
	private String inviterPhone;

	private Long lastLoginTime;
	private String lastLoginTimeStr;

	/**患者关注的疾病**/
	private List<String> labers;

    public List<String> getLabers() {
        return labers;
    }

    public void setLabers(List<String> labers) {
        this.labers = labers;
    }

    public Long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLoginTimeStr() {
		return lastLoginTimeStr;
	}

	public void setLastLoginTimeStr(String lastLoginTimeStr) {
		this.lastLoginTimeStr = lastLoginTimeStr;
	}

	public Long getRegisterTime() {
		return registerTime;
	}
	public void setRegisterTime(Long registerTime) {
		this.registerTime = registerTime;
	}
	public String getInviterPhone() {
		return inviterPhone;
	}
	public void setInviterPhone(String inviterPhone) {
		this.inviterPhone = inviterPhone;
	}
	public String getInviterName() {
		return inviterName;
	}
	public void setInviterName(String inviterName) {
		this.inviterName = inviterName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRegisterTimeStr() {
		return registerTimeStr;
	}
	public void setRegisterTimeStr(String registerTimeStr) {
		this.registerTimeStr = registerTimeStr;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getAgeStr() {
		return ageStr;
	}
	public void setAgeStr(String ageStr) {
		this.ageStr = ageStr;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
