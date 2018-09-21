package com.dachen.health.pack.order.entity.param;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.AccountEnum;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.pack.illhistory.entity.po.DrugInfo;
import com.dachen.util.JSONUtil;

import java.util.List;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： OrderParam<br>
 * Description：购买套餐订单参数接收类 <br>
 * 
 * @author fanp
 * @createTime 2015年8月10日
 * @version 1.0.0
 */
public class OrderParam extends PageVO{
	
	private Integer type;
    /* 购买用户id */
    private Integer userId;
    
    private String orderNo;
    
    /* 上午、下午、晚上 {@link ScheduleEnum.Period} */
    private Integer period;
    
    private Integer week;

    /* 医生id */
    private Integer doctorId;

    /*医生助手ID*/
    private Integer assistantId;
    
    /* 套餐id */
    private Integer packId;

    /* 支付类型 */
    /** @see AccountEnum.PayType */
    private Integer payType;
    
    private Long price;
    
    /* 病人id */
    private Integer patientId;

    
    /* 订单状态 */
    private Integer orderStatus;

    /* 是否查询待处理订单：1.查询待处理，2.查询全部*/
    private Integer pendingOrderStatus;

    /* 用药json字符串 创建订单时候使用 */
    private String drugInfos;

    private List<DrugInfo> drugInfoList;

    
    private String assistantComment;
	public Integer getPendingOrderStatus() {
		return pendingOrderStatus;
	}

	public void setPendingOrderStatus(Integer pendingOrderStatus) {
		this.pendingOrderStatus = pendingOrderStatus;
	}

	private Integer refundStatus;
    
    private Integer recordStatus;
    
    private Integer orderId;
    /**
     * 转诊之前的orderId
     */
    private Integer preOrderId;
    
    private List<Integer> orderStatusList;
    
    private List<Integer> orderIds;
    
    private List<Integer> userIds;

	private List<Integer> doctorIds;
    
    private List<Integer> patientIds;
    
    private List<String> hospitalIds;


    
    /*订单类型*/
    /** @see OrderEnum.OrderType */
    private Integer orderType;
    
    
    private List<String> ghnrIds;
    
    private List<String> bqjhIds;
    
    private String ghnrType;
    
    private String guideId;
    
    /*订单所属医生集团（可以为空）*/
    private String groupId;
    
    private Long startCreateTime;
    
    private Long endCreateTime;
    
    
    private Long oppointTime;
    
  //诊治情况
    private String cureSituation;
    
    private String voice;
    
    private String telephone;
    
    private String diseaseTel;
    
    private Integer userType;
    
    private String[] imagePaths;
    
    private String userName;
    
    private Integer packType;
    
    private List<Integer> moreStatus;
    
    private String remarks;
    
    private Integer activateStatus =1;//默认订单是已激活的
    
    private String gid;//会话id


	@Deprecated
	private Integer diseaseId;

	//*******************2016-12-06 新增************************//
	private String diseaseID;//所患疾病id 从初始化数据表中获取
	private String diseaseDuration;//病症时长
	private String diseaseDesc;//病情描述
	private Boolean isSeeDoctor; //是否就诊
	private String treatCase;//诊治情况
	private String drugCase; //用药情况
	private List<String> drugGoodsIds;//药物商品id
	private List<String> drugPicUrls;//用药图片资料
	private String hopeHelp;//希望获得的帮助
	private List<String> picUrls;//图片资料
	private Boolean isPay;//是否已经支付 ， 支付的订单才会显示
	private Integer careOrderId;//健康关怀订单id
	private String msgGroupId;

	/**患者报道留言**/
	private String message;
	/**报道时间**/
	private Long checkInTime;
	/**患者报道时所填写的就诊医院**/
	private String hospital;
	/**患者报道时所填写的病历号**/
	private String illHistoryInfoNo;
	/**疾病的列表**/
	private List<String> diseaseIds;
	/**患者报道时所填写的最后就诊时间**/
	private Long lastTime;

	private String isSecondTreate;

    //**********************2016-12-06 **********************//
    
    //*************2016年1月19日15:16:40  新增开始******************//
    //现病史
    private String diseaseInfo_now;
    //既往史
    private String diseaseInfo_old;
    //家族史
    private String familydiseaseInfo;
    //月经史
    private String menstruationdiseaseInfo;
    
  //就诊情况 
    private String seeDoctorMsg;   
    //是否就诊  false  没有  true  有
    private String isSee;
    //*************2016年1月19日15:16:40  新增结束******************//
    
    private String hospitalId;
    private String doctorName;
    private String patientName;
    private String hostpitalName;
    
    private Integer transferDoctorId;
    
    
    private String illCaseInfoId;//病情信息ID
    private Boolean isIllCaseCommit=false;
    
    private String cancelReason;//取消原因
    /**
     * 预约订单的预约条目id
     */
    private String offlineItemId;
    //转诊时间
    private Long transferTime;
    
    //创建订单用户的角色
    private Integer createUserType;
    
    private String expectAppointmentIds;

    //会诊专家医生id
    private Integer consultDoctorId;
    
    private List<Integer> consultJoinDocs;
    //主诊医生id
    private Integer mainTreateDoctorId;
    

	//病历id
	private String illHistoryInfoId;

	public String getIllHistoryInfoId() {
		return illHistoryInfoId;
	}

	public void setIllHistoryInfoId(String illHistoryInfoId) {
		this.illHistoryInfoId = illHistoryInfoId;
	}

	public Integer getActivateStatus() {
		return activateStatus;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getSeeDoctorMsg() {
		return seeDoctorMsg;
	}

	public void setSeeDoctorMsg(String seeDoctorMsg) {
		this.seeDoctorMsg = seeDoctorMsg;
	}
	public String getIsSee() {
		return isSee;
	}

	public void setIsSee(String isSee) {
		this.isSee = isSee;
	}

	public boolean getIsSeeDoctor() {
		isSeeDoctor=Boolean.valueOf(isSee);
		return isSeeDoctor;
	}

	public void setSeeDoctor(boolean isSeeDoctor) {
		this.isSeeDoctor = isSeeDoctor;
	}

	public String getDiseaseInfo_now() {
		return diseaseInfo_now;
	}

	public void setDiseaseInfo_now(String diseaseInfo_now) {
		this.diseaseInfo_now = diseaseInfo_now;
	}

	public String getDiseaseInfo_old() {
		return diseaseInfo_old;
	}

	public void setDiseaseInfo_old(String diseaseInfo_old) {
		this.diseaseInfo_old = diseaseInfo_old;
	}

	public String getFamilydiseaseInfo() {
		return familydiseaseInfo;
	}

	public void setFamilydiseaseInfo(String familydiseaseInfo) {
		this.familydiseaseInfo = familydiseaseInfo;
	}

	public String getMenstruationdiseaseInfo() {
		return menstruationdiseaseInfo;
	}

	public void setMenstruationdiseaseInfo(String menstruationdiseaseInfo) {
		this.menstruationdiseaseInfo = menstruationdiseaseInfo;
	}

	public void setActivateStatus(Integer activateStatus) {
		this.activateStatus = activateStatus;
	}

	public List<Integer> getMoreStatus() {
		return moreStatus;
	}

	public void setMoreStatus(List<Integer> moreStatus) {
		this.moreStatus = moreStatus;
	}

	public Integer getPackType() {
		return packType;
	}

	public void setPackType(Integer packType) {
		this.packType = packType;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public List<Integer> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Integer> userIds) {
		this.userIds = userIds;
	}

	public List<Integer> getPatientIds() {
		return patientIds;
	}

	public void setPatientIds(List<Integer> patientIds) {
		this.patientIds = patientIds;
	}

	public List<String> getHospitalIds() {
		return hospitalIds;
	}

	public void setHospitalIds(List<String> hospitalIds) {
		this.hospitalIds = hospitalIds;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGhnrType() {
		return ghnrType;
	}

	public void setGhnrType(String ghnrType) {
		this.ghnrType = ghnrType;
	}

	public List<String> getGhnrIds() {
		return ghnrIds;
	}

	public void setGhnrIds(List<String> ghnrIds) {
		this.ghnrIds = ghnrIds;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Long getOppointTime() {
		return oppointTime;
	}

	public void setOppointTime(Long oppointTime) {
		this.oppointTime = oppointTime;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

    public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
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

	public String[] getImagePaths() {
		return imagePaths;
	}

	public void setImagePaths(String[] imagePaths) {
		this.imagePaths = imagePaths;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public List<Integer> getOrderIds() {
		return orderIds;
	}

	public void setOrderIds(List<Integer> orderIds) {
		this.orderIds = orderIds;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(Integer refundStatus) {
		this.refundStatus = refundStatus;
	}

	public Integer getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(Integer recordStatus) {
		this.recordStatus = recordStatus;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public Integer getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(Integer diseaseId) {
		this.diseaseId = diseaseId;
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

    public Integer getPackId() {
        return packId;
    }

    public void setPackId(Integer packId) {
        this.packId = packId;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }
 
	public String getDiseaseTel() {
		return diseaseTel;
	}

	public void setDiseaseTel(String diseaseTel) {
		this.diseaseTel = diseaseTel;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Long getStartCreateTime() {
		return startCreateTime;
	}

	public void setStartCreateTime(Long startCreateTime) {
		this.startCreateTime = startCreateTime;
	}

	public Long getEndCreateTime() {
		return endCreateTime;
	}

	public void setEndCreateTime(Long endCreateTime) {
		this.endCreateTime = endCreateTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<String> getBqjhIds() {
		return bqjhIds;
	}

	public void setBqjhIds(List<String> bqjhIds) {
		this.bqjhIds = bqjhIds;
	}

	public String getCureSituation() {
		return cureSituation;
	}

	public void setCureSituation(String cureSituation) {
		this.cureSituation = cureSituation;
	}

	public String getGuideId() {
		return guideId;
	}

	public void setGuideId(String guideId) {
		this.guideId = guideId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getHostpitalName() {
		return hostpitalName;
	}

	public void setHostpitalName(String hostpitalName) {
		this.hostpitalName = hostpitalName;
	}

	public Integer getPreOrderId() {
		return preOrderId;
	}

	public void setPreOrderId(Integer preOrderId) {
		this.preOrderId = preOrderId;
	}

	public Integer getTransferDoctorId() {
		return transferDoctorId;
	}

	public void setTransferDoctorId(Integer transferDoctorId) {
		this.transferDoctorId = transferDoctorId;
	}

	public Long getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(Long transferTime) {
		this.transferTime = transferTime;
	}
	
	public List<Integer> getOrderStatusList() {
		return orderStatusList;
	}

	public void setOrderStatusList(List<Integer> orderStatusList) {
		this.orderStatusList = orderStatusList;
	}
	
	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	

	public String getIllCaseInfoId() {
		return illCaseInfoId;
	}

	public void setIllCaseInfoId(String illCaseInfoId) {
		this.illCaseInfoId = illCaseInfoId;
	}

	public Boolean getIsIllCaseCommit() {
		return isIllCaseCommit;
	}

	public void setIsIllCaseCommit(Boolean isIllCaseCommit) {
		this.isIllCaseCommit = isIllCaseCommit;
	}

	public String getOfflineItemId() {
		return offlineItemId;
	}

	public void setOfflineItemId(String offlineItemId) {
		this.offlineItemId = offlineItemId;
	}

	public Integer getWeek() {
		return week;
	}

	public void setWeek(Integer week) {
		this.week = week;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public Integer getCreateUserType() {
		return createUserType;
	}

	public void setCreateUserType(Integer createUserType) {
		this.createUserType = createUserType;
	}

	public String getExpectAppointmentIds() {
		return expectAppointmentIds;
	}

	public void setExpectAppointmentIds(String expectAppointmentIds) {
		this.expectAppointmentIds = expectAppointmentIds;
	}

	
	public Integer getAssistantId() {
		return assistantId;
	}

	public void setAssistantId(Integer assistantId) {
		this.assistantId = assistantId;
	}

	public String getDiseaseID() {
		return diseaseID;
	}

	public void setDiseaseID(String diseaseID) {
		this.diseaseID = diseaseID;
	}

	public String getDiseaseDuration() {
		return diseaseDuration;
	}

	public void setDiseaseDuration(String diseaseDuration) {
		this.diseaseDuration = diseaseDuration;
	}

	public Boolean getSeeDoctor() {
		return isSeeDoctor;
	}

	public void setSeeDoctor(Boolean seeDoctor) {
		isSeeDoctor = seeDoctor;
	}

	public String getTreatCase() {
		return treatCase;
	}

	public void setTreatCase(String treatCase) {
		this.treatCase = treatCase;
	}

	public String getDrugCase() {
		return drugCase;
	}

	public void setDrugCase(String drugCase) {
		this.drugCase = drugCase;
	}

	public List<String> getDrugGoodsIds() {
		return drugGoodsIds;
	}

	public void setDrugGoodsIds(List<String> drugGoodsIds) {
		this.drugGoodsIds = drugGoodsIds;
	}

	public List<String> getDrugPicUrls() {
		return drugPicUrls;
	}

	public void setDrugPicUrls(List<String> drugPicUrls) {
		this.drugPicUrls = drugPicUrls;
	}

	public String getHopeHelp() {
		return hopeHelp;
	}

	public void setHopeHelp(String hopeHelp) {
		this.hopeHelp = hopeHelp;
	}

	public List<String> getPicUrls() {
		return picUrls;
	}

	public void setPicUrls(List<String> picUrls) {
		this.picUrls = picUrls;
	}

	public Boolean getIllCaseCommit() {
		return isIllCaseCommit;
	}

	public void setIllCaseCommit(Boolean illCaseCommit) {
		isIllCaseCommit = illCaseCommit;
	}

	public Boolean getPay() {
		return isPay;
	}

	public void setPay(Boolean pay) {
		isPay = pay;
	}

	public Boolean getIsPay() {
		return isPay;
	}

	public Integer getCareOrderId() {
		return careOrderId;
	}

	public void setIsSeeDoctor(Boolean isSeeDoctor) {
		this.isSeeDoctor = isSeeDoctor;
	}

	public void setIsPay(Boolean isPay) {
		this.isPay = isPay;
	}

	public void setCareOrderId(Integer careOrderId) {
		this.careOrderId = careOrderId;
	}

	public String getMsgGroupId() {
		return msgGroupId;
	}

	public void setMsgGroupId(String msgGroupId) {
		this.msgGroupId = msgGroupId;
	}

	public Integer getConsultDoctorId() {
		return consultDoctorId;
	}

	public void setConsultDoctorId(Integer consultDoctorId) {
		this.consultDoctorId = consultDoctorId;
	}

	public List<Integer> getConsultJoinDocs() {
		return consultJoinDocs;
	}

	public void setConsultJoinDocs(List<Integer> consultJoinDocs) {
		this.consultJoinDocs = consultJoinDocs;
	}

	public Integer getMainTreateDoctorId() {
		return mainTreateDoctorId;
	}

	public void setMainTreateDoctorId(Integer mainTreateDoctorId) {
		this.mainTreateDoctorId = mainTreateDoctorId;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Long checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getIllHistoryInfoNo() {
        return illHistoryInfoNo;
    }

    public void setIllHistoryInfoNo(String illHistoryInfoNo) {
        this.illHistoryInfoNo = illHistoryInfoNo;
    }

    public List<String> getDiseaseIds() {
        return diseaseIds;
    }

    public void setDiseaseIds(List<String> diseaseIds) {
        this.diseaseIds = diseaseIds;
    }

    public Long getLastTime() {
        return lastTime;
    }

    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }

	public String getIsSecondTreate() {
		return isSecondTreate;
	}

	public void setIsSecondTreate(String isSecondTreate) {
		this.isSecondTreate = isSecondTreate;
	}

	public String getDrugInfos() {
		return drugInfos;
	}

	public void setDrugInfos(String drugInfos) {
		this.drugInfos = drugInfos;
	}

	public List<DrugInfo> getDrugInfoList() {
		return drugInfoList;
	}

	public void setDrugInfoList(List<DrugInfo> drugInfoList) {
		this.drugInfoList = drugInfoList;
	}

	public List<Integer> getDoctorIds() {
		return doctorIds;
	}

	public void setDoctorIds(List<Integer> doctorIds) {
		this.doctorIds = doctorIds;
	}

	public String getAssistantComment() {
		return assistantComment;
	}

	public void setAssistantComment(String assistantComment) {
		this.assistantComment = assistantComment;
	}
	
}
