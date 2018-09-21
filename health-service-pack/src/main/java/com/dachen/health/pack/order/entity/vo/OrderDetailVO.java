package com.dachen.health.pack.order.entity.vo;

import java.util.List;

import com.dachen.health.commons.vo.User;
import com.dachen.health.pack.patient.model.CureRecord;

public class OrderDetailVO {
	
	private OrderVO orderVo;
	
	private String diseaseDesc;
	
	private String voice;
	
	private String telephone;
	
	private User user;
	
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
    
    private Integer diseaseId;
    
    private Boolean isSeeDoctor;
    
    private String cureSituation;//诊治情况
    
    private Integer replyCount;
    
	public String getCureSituation() {
		return cureSituation;
	}

	public void setCureSituation(String cureSituation) {
		this.cureSituation = cureSituation;
	}

	public Integer getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(Integer diseaseId) {
		this.diseaseId = diseaseId;
	}

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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	private  List<String> imgStringPath;
	
	/**
	 * 诊疗记录
	 */
	private List<CureRecord> cureRecordList;
	
	
	public List<CureRecord> getCureRecordList() {
		return cureRecordList;
	}

	public void setCureRecordList(List<CureRecord> cureRecordList) {
		this.cureRecordList = cureRecordList;
	}

	public String getDiseaseDesc() {
		return diseaseDesc;
	}

	public void setDiseaseDesc(String diseaseDesc) {
		this.diseaseDesc = diseaseDesc;
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public List<String> getImgStringPath() {
		return imgStringPath;
	}

	public void setImgStringPath(List<String> imgStringPath) {
		this.imgStringPath = imgStringPath;
	}

	public OrderVO getOrderVo() {
		return orderVo;
	}

	public void setOrderVo(OrderVO orderVo) {
		this.orderVo = orderVo;
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

	public String getMenstruationdiseaseInfo() {
		return menstruationdiseaseInfo;
	}

	public void setMenstruationdiseaseInfo(String menstruationdiseaseInfo) {
		this.menstruationdiseaseInfo = menstruationdiseaseInfo;
	}

	public String getSeeDoctorMsg() {
		return seeDoctorMsg;
	}

	public void setSeeDoctorMsg(String seeDoctorMsg) {
		this.seeDoctorMsg = seeDoctorMsg;
	}

	public Boolean getIsSeeDoctor() {
		return isSeeDoctor;
	}

	public void setIsSeeDoctor(Boolean isSeeDoctor) {
		this.isSeeDoctor = isSeeDoctor;
	}

	public Integer getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(Integer replyCount) {
		this.replyCount = replyCount;
	}

}
