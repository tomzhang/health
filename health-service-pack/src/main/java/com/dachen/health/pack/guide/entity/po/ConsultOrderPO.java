package com.dachen.health.pack.guide.entity.po;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.utils.IndexDirection;

import com.dachen.health.pack.guide.entity.ServiceStateEnum;
import com.dachen.util.DateUtil;
import com.dachen.util.JSONUtil;

@Entity(value = "t_consult_order",noClassnameStored = true)
@Indexes( {@Index("groupId, state"), @Index("guideId, startTime")})  
public class ConsultOrderPO {
	@Id
	private String id;// 订单Id

	 /* 患者用户id */
	@Indexed(value = IndexDirection.ASC)
    private Integer userId;

    /* 医生id */
    private Integer doctorId;

    /* 套餐id */
    private Integer packId;
    
    /* 价格 */
//    private Long price;
    
    @Embedded
    private Disease diseaseInfo;
   
    /*导医Id：导医接单时反写该字段*/
    private Integer guideId;
    
    /*会话Id*/
    private String groupId;
    
    /**
     * 由于groupId已被用作会话id
     * 被迫使用groupUnionId 作为集团id 
     * 博德嘉联需要定制化功能 需要添加该字段
     */
    private String groupUnionId;
    
    /**
     * 关联订单Id:生成订单(order表后反写该字段)
     */
//    @Indexed(value = IndexDirection.ASC)
//    private Integer orderId;
    
    /**
     *  服务状态 1:服务未开始，2：服务中，3：服务结束 {@link ServiceStateEnum}
     *  服务中的才能付款 
     */
    private Integer state;

    
    //通话状态（0：未开始；1：进行中；2、完成；3、非正常挂断；4、）
    private Integer talkState;
    
    /* 创建时间 */
    private Long createTime;
    
    /* 服务开始时间 */
    private Long startTime;
    
    /* 服务结束时间 */
    private Long closedTime;
    /* 结束服务的用户 */
    private Integer closedUserId;
    
    private String msgId;
    
    private Boolean isSendOverTime;
    
    /**
     * p_order中的id
     * 用于在转诊成电话订单的时候使用
     * 2016年5月13日11:12:13
     */
    private Integer preOrderId;
    
    /**
     * 转诊的时候调用
     */
    private Integer transferDoctorId;
    
	/**
	 * 转诊时间
	 */
	private Long transferTime;
    
    private @NotSaved Long appointStartTime;
    private @NotSaved Long appointEndTime;
    

    
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
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

	public Disease getDiseaseInfo() {
		return diseaseInfo;
	}

	public void setDiseaseInfo(Disease diseaseInfo) {
		this.diseaseInfo = diseaseInfo;
	}

//	public Long getAppointTime() {
//		return appointTime;
//	}
//
//	public void setAppointTime(Long appointTime) {
//		this.appointTime = appointTime;
//	}

	public Integer getGuideId() {
		return guideId;
	}

	public void setGuideId(Integer guideId) {
		this.guideId = guideId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

//	public Integer getOrderId() {
//		return orderId;
//	}
//
//	public void setOrderId(Integer orderId) {
//		this.orderId = orderId;
//	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	
	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

//	public Long getEndTime() {
//		return endTime;
//	}
//
//	public void setEndTime(Long endTime) {
//		this.endTime = endTime;
//	}

	public Integer getTalkState() {
		return talkState;
	}

	public void setTalkState(Integer talkState) {
		this.talkState = talkState;
	}

	public Long getClosedTime() {
		return closedTime;
	}

	public void setClosedTime(Long closedTime) {
		this.closedTime = closedTime;
	}

	public Integer getClosedUserId() {
		return closedUserId;
	}

	public void setClosedUserId(Integer closedUserId) {
		this.closedUserId = closedUserId;
	}

	public Boolean getIsSendOverTime() {
		return isSendOverTime;
	}

	public void setIsSendOverTime(Boolean isSendOverTime) {
		this.isSendOverTime = isSendOverTime;
	}

	public static class Disease{
    	
		/**
		 * 病人id
		 */
		private Integer patientId;
		
    	/**
         * 病情描述
         */
        private String diseaseDesc;
        
        /**
         * 病情语音
         */
        private String voice;
        
        /**
         * 病情图
         */
        private List<String> diseaseImgs; 
        
        /**
         * 电话号码
         */
        private String telephone;
        
        //*************2016年1月19日15:16:40  新增开始******************//
        //现病史
        private String diseaseInfo_now;
        //既往史
        private String diseaseInfo_old;
        //家族史
        private String familydiseaseInfo;
        //月经史
        private String menstruationdiseaseInfo;
        //诊治情况
        private String cureSituation;
      //就诊情况 
        private String seeDoctorMsg;   
        //是否就诊  false  没有  true  有
        private Boolean isSeeDoctor;   
        //转诊原订单的电子病历id
        private String illCaseInfoId;
        //*************2016年1月19日15:16:40  新增结束******************//
        
		public Integer getPatientId() {
			return patientId;
		}
		public String getCureSituation() {
			return cureSituation;
		}

		public void setCureSituation(String cureSituation) {
			this.cureSituation = cureSituation;
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

		public void setPatientId(Integer patientId) {
			this.patientId = patientId;
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

		public List<String> getDiseaseImgs() {
			return diseaseImgs;
		}

		public void setDiseaseImgs(List<String> diseaseImgs) {
			this.diseaseImgs = diseaseImgs;
		}

		public String getTelephone() {
			return telephone;
		}

		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
		public String getIllCaseInfoId() {
			return illCaseInfoId;
		}
		public void setIllCaseInfoId(String illCaseInfoId) {
			this.illCaseInfoId = illCaseInfoId;
		}
    }

	public Long getAppointStartTime() {
		return appointStartTime;
	}

	public void setAppointStartTime(Long appointStartTime) {
		this.appointStartTime = appointStartTime;
	}

	public Long getAppointEndTime() {
		return appointEndTime;
	}

	public void setAppointEndTime(Long appointEndTime) {
		this.appointEndTime = appointEndTime;
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


	public String getGroupUnionId() {
		return groupUnionId;
	}


	public void setGroupUnionId(String groupUnionId) {
		this.groupUnionId = groupUnionId;
	}

	
	public static void main(String[] args) {
		long t = System.currentTimeMillis();
		Date startDate =DateUtil.getFirstDayOfMonth(new Date(t));
		Date endDate = DateUtil.getLastDayOfMonth(new Date(t));
		
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		System.out.println(c.getTime());
		System.out.println(c.getTime().getTime());
		Integer dayss = c.get(Calendar.DAY_OF_MONTH);
		c.setTime(startDate);
		
		System.out.println(dayss);
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
	
}
