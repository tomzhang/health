package com.dachen.health.pack.patient.model;

import com.dachen.drug.api.entity.CRecipeView;
import com.dachen.health.base.entity.po.CheckSuggest;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.commons.vo.User;

import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName:  关怀小结  
 * @Description:记录健康关怀订单的关怀咨询结果
 * @author: qinyuan.chen 
 * @date:   2016年12月6日 下午5:33:55   
 *
 */
public class CareSummary {

	 /**
     * 主键
     */
    private Integer id;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 医生id
     */
    private Integer doctorId;

    /**
     * 患者id
     */
    private Integer patientId;

    /**
     * 创建时间
     */
    private Long createTime;

    private String treatAdvise;

    private String drugAdvise;

    private String attention;

    private String consultAdvise;
    
    private String drugAdviseJson;
    
    private CRecipeView recipeView;
    
    //更新时间
    private Long updateTime;
    
    /**
     * 检查建议数组（字段attention里保存着以逗号分隔的id）
     */
    private List<CheckSuggest> checkSuggestList;
    

    /**
     * 咨询结果病种
     */
    private String consultAdviseDiseases;
    
    private List<DiseaseTypeVO>  consultAdviseDiseaseList;
    
    private Map<String,Object> voice;

    /**
     * 图片
     */
    private String images[];
    
    /**
     * 语音
     */
    private String voices[];
    
    //录音结束时间
    private Long videoStopTime;
    
    /**
     * 录音
     */
    private String video[];
    
    //咨询记录所有订单的集团Id
    private String groupId;
    
    private String groupName;
    
    private User user;
    
    private Patient patient;
    
    private String videoUrl;//录音地址
    
    
    // 提交状态 0--保存 1--提交
    private String submitState;
    
    //是否需要导医的帮助  0--需要 1--不需要
    private String isNeedHelp;
    //咨询记录状态
    private Integer recordStatus;
    
    
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
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
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public String getTreatAdvise() {
		return treatAdvise;
	}
	public void setTreatAdvise(String treatAdvise) {
		this.treatAdvise = treatAdvise;
	}
	public String getDrugAdvise() {
		return drugAdvise;
	}
	public void setDrugAdvise(String drugAdvise) {
		this.drugAdvise = drugAdvise;
	}
	public String getAttention() {
		return attention;
	}
	public void setAttention(String attention) {
		this.attention = attention;
	}
	public String getConsultAdvise() {
		return consultAdvise;
	}
	public void setConsultAdvise(String consultAdvise) {
		this.consultAdvise = consultAdvise;
	}
	public String getDrugAdviseJson() {
		return drugAdviseJson;
	}
	public void setDrugAdviseJson(String drugAdviseJson) {
		this.drugAdviseJson = drugAdviseJson;
	}
	public CRecipeView getRecipeView() {
		return recipeView;
	}
	public void setRecipeView(CRecipeView recipeView) {
		this.recipeView = recipeView;
	}
	public Long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	public List<CheckSuggest> getCheckSuggestList() {
		return checkSuggestList;
	}
	public void setCheckSuggestList(List<CheckSuggest> checkSuggestList) {
		this.checkSuggestList = checkSuggestList;
	}
	public String getConsultAdviseDiseases() {
		return consultAdviseDiseases;
	}
	public void setConsultAdviseDiseases(String consultAdviseDiseases) {
		this.consultAdviseDiseases = consultAdviseDiseases;
	}
	public List<DiseaseTypeVO> getConsultAdviseDiseaseList() {
		return consultAdviseDiseaseList;
	}
	public void setConsultAdviseDiseaseList(List<DiseaseTypeVO> consultAdviseDiseaseList) {
		this.consultAdviseDiseaseList = consultAdviseDiseaseList;
	}
	public Map<String, Object> getVoice() {
		return voice;
	}
	public void setVoice(Map<String, Object> voice) {
		this.voice = voice;
	}
	public String[] getImages() {
		return images;
	}
	public void setImages(String[] images) {
		this.images = images;
	}
	public String[] getVoices() {
		return voices;
	}
	public void setVoices(String[] voices) {
		this.voices = voices;
	}
	public Long getVideoStopTime() {
		return videoStopTime;
	}
	public void setVideoStopTime(Long videoStopTime) {
		this.videoStopTime = videoStopTime;
	}
	public String[] getVideo() {
		return video;
	}
	public void setVideo(String[] video) {
		this.video = video;
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
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	public String getSubmitState() {
		return submitState;
	}
	public void setSubmitState(String submitState) {
		this.submitState = submitState;
	}
	public String getIsNeedHelp() {
		return isNeedHelp;
	}
	public void setIsNeedHelp(String isNeedHelp) {
		this.isNeedHelp = isNeedHelp;
	}
	public Integer getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(Integer recordStatus) {
		this.recordStatus = recordStatus;
	}
    
    
    
    
    
    
    
    
}
