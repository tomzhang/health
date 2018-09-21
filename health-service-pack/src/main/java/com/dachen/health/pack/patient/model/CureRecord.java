package com.dachen.health.pack.patient.model;

import com.dachen.drug.api.entity.CRecipeView;
import com.dachen.health.base.entity.po.CheckSuggest;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.commons.vo.User;

import java.util.List;
import java.util.Map;

/**
 * 咨询记录表
 * @author 李淼淼
 * @version 1.0 2015-11-25
 */
public class CureRecord {
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
    
    /**
     * 咨询记录所有订单的集团Id
     */
    private String groupId;
    
    private String groupName;
    
    private User user;
    
    private Patient patient;
    
    private String videoUrl;//录音地址
    
    
    /**
     * 提交状态 0--保存 1--提交
     */
    private String submitState;
    
    //是否需要导医的帮助  0--需要 1--不需要
    private String isNeedHelp;
    //咨询记录状态
    private Integer recordStatus;
    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }
   
	/**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    
    public String getSubmitState() {
		return submitState;
	}

	public void setSubmitState(String submitState) {
		this.submitState = submitState;
	}

	/**
     * 获取订单id
     *
     * @return order_id - 订单id
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置订单id
     *
     * @param orderId 订单id
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取医生id
     *
     * @return doctor_id - 医生id
     */
    public Integer getDoctorId() {
        return doctorId;
    }

    /**
     * 设置医生id
     *
     * @param doctorId 医生id
     */
    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * 获取患者id
     *
     * @return patient_id - 患者id
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者id
     *
     * @param patientId 患者id
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * @return treat_advise
     */
    public String getTreatAdvise() {
        return treatAdvise;
    }

    /**
     * @param treatAdvise
     */
    public void setTreatAdvise(String treatAdvise) {
        this.treatAdvise = treatAdvise == null ? null : treatAdvise.trim();
    }

    /**
     * @return drug_advise
     */
    public String getDrugAdvise() {
        return drugAdvise;
    }

    /**
     * @param drugAdvise
     */
    public void setDrugAdvise(String drugAdvise) {
        this.drugAdvise = drugAdvise == null ? null : drugAdvise.trim();
    }

    /**
     * @return attention
     */
    public String getAttention() {
        return attention;
    }

    /**
     * @param attention
     */
    public void setAttention(String attention) {
        this.attention = attention == null ? null : attention.trim();
    }

    /**
     * @return consult_advise
     */
    public String getConsultAdvise() {
        return consultAdvise;
    }

    /**
     * @param consultAdvise
     */
    public void setConsultAdvise(String consultAdvise) {
        this.consultAdvise = consultAdvise == null ? null : consultAdvise.trim();
    }

    /**
     * 获取咨询结果病种
     *
     * @return consult_advise_diseases - 咨询结果病种
     */
    public String getConsultAdviseDiseases() {
        return consultAdviseDiseases;
    }

    /**
     * 设置咨询结果病种
     *
     * @param consultAdviseDiseases 咨询结果病种
     */
    public void setConsultAdviseDiseases(String consultAdviseDiseases) {
        this.consultAdviseDiseases = consultAdviseDiseases == null ? null : consultAdviseDiseases.trim();
    }

	public List<CheckSuggest> getCheckSuggestList() {
		return checkSuggestList;
	}

	public void setCheckSuggestList(List<CheckSuggest> checkSuggestList) {
		this.checkSuggestList = checkSuggestList;
	}

	public List<DiseaseTypeVO> getConsultAdviseDiseaseList() {
		return consultAdviseDiseaseList;
	}

	public void setConsultAdviseDiseaseList(List<DiseaseTypeVO> consultAdviseDiseaseList) {
		this.consultAdviseDiseaseList = consultAdviseDiseaseList;
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

	public String getIsNeedHelp() {
		return isNeedHelp;
	}

	public void setIsNeedHelp(String isNeedHelp) {
		this.isNeedHelp = isNeedHelp;
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

	public Map<String, Object> getVoice() {
		return voice;
	}

	public void setVoice(Map<String, Object> voice) {
		this.voice = voice;
	}

	public String[] getVideo() {
		return video;
	}

	public void setVideo(String[] video) {
		this.video = video;
	}

	public Long getVideoStopTime() {
		return videoStopTime;
	}

	public void setVideoStopTime(Long videoStopTime) {
		this.videoStopTime = videoStopTime;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(Integer recordStatus) {
		this.recordStatus = recordStatus;
	}

}