package com.dachen.health.pack.illhistory.entity.po;

import java.util.List;

/**
 * 病程中的会诊信息
 * Created by fuyongde on 2016/12/7.
 */
public class RecordConsultiation {
    /**订单id**/
    private Integer orderId;
    /**会诊专家**/
    private Integer consultationDoctor;
    /**参与医生**/
    private List<Integer> consultJoinDocs;
    /**主诊医生**/
    private Integer mainDoctor;
    /**就诊时间**/
    private Long startTime;
    /**图片资料**/
    private List<String> pics;
    /**结束时间**/
    private Long endTime;
    /**是否支付**/
    private Boolean isPay;
    /**会话id**/
    private String messageGroupId;
    /**用药信息（冗余订单类型的用药数据，病程中不予展示）**/
    private String drugInfos;
    /**
     * 医生id
     */
    private Integer doctorId;

    public String getDrugInfos() {
        return drugInfos;
    }

    public void setDrugInfos(String drugInfos) {
        this.drugInfos = drugInfos;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getConsultationDoctor() {
        return consultationDoctor;
    }

    public void setConsultationDoctor(Integer consultationDoctor) {
        this.consultationDoctor = consultationDoctor;
    }

//    public Integer getMainDoctor() {
//        return mainDoctor;
//    }
//
//    public void setMainDoctor(Integer mainDoctor) {
//        this.mainDoctor = mainDoctor;
//    }

    public List<Integer> getConsultJoinDocs() {
		return consultJoinDocs;
	}

	public void setConsultJoinDocs(List<Integer> consultJoinDocs) {
		this.consultJoinDocs = consultJoinDocs;
	}

	public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Boolean getPay() {
        return isPay;
    }

    public void setPay(Boolean pay) {
        isPay = pay;
    }

    public String getMessageGroupId() {
        return messageGroupId;
    }

    public void setMessageGroupId(String messageGroupId) {
        this.messageGroupId = messageGroupId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
