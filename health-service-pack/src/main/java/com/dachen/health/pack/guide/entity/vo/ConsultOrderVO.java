package com.dachen.health.pack.guide.entity.vo;


public class ConsultOrderVO {
	/*订单Id*/
	private String id;
	private Integer orderId;
	 /* 患者用户id */
    private Integer userId;
    
    /* 用户（患者）姓名*/
    private String name;
    
    /* 用户头像*/
    private String headImg;
    /*患者）姓名*/
    private String patientName;
    /* 患者性别*/
    private int sex;
    
    /* 患者性别:1男，2女 3 保密*/
    private int age;
    
    /* 订单创建时间 */
    private Long createTime;
    /* 导医接单时间 */
    private Long startTime;
    /* 预约开始时间 */
    private Long appointTime;
    /* 预约截至时间 */
    private Long endTime;
    
	/* 病情描述  */
    private String diseaseDesc;
    
    private String ageStr;
    
    /* 电话号码（患者）*/
    private String telephone;
    
    /*通话状态（0：未开始；1：进行中；2、完成；3、非正常挂断；4、）*/
    private Integer talkState=0;
    /* 医生信息*/
    private DoctorInfo doctor;
    
    /* 该订单对应会话的首条消息Id*/
    private String msgId;
    /*会话Id*/
    private String groupId;
    
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
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public String getDiseaseDesc() {
		return diseaseDesc;
	}
	public void setDiseaseDesc(String diseaseDesc) {
		this.diseaseDesc = diseaseDesc;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHeadImg() {
		return headImg;
	}
	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public Long getAppointTime() {
		return appointTime;
	}
	public void setAppointTime(Long appointTime) {
		this.appointTime = appointTime;
	}
	public DoctorInfo getDoctor() {
		return doctor;
	}
	public void setDoctor(DoctorInfo doctor) {
		this.doctor = doctor;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public Integer getTalkState() {
		return talkState;
	}
	public void setTalkState(Integer talkState) {
		this.talkState = talkState;
	}
	public static class DoctorInfo {
		private Integer id;
		private String name;
	    private String tel;
	    private String headImg;
	    
	    //免打扰（1：正常，2：免打扰）
		private String troubleFree;
		
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
		public String getTel() {
			return tel;
		}
		public void setTel(String tel) {
			this.tel = tel;
		}
		public String getHeadImg() {
			return headImg;
		}
		public void setHeadImg(String headImg) {
			this.headImg = headImg;
		}
		public String getTroubleFree() {
			return troubleFree;
		}
		public void setTroubleFree(String troubleFree) {
			this.troubleFree = troubleFree;
		} 
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getAgeStr() {
		return ageStr;
	}
	public void setAgeStr(String ageStr) {
		this.ageStr = ageStr;
	}
}
