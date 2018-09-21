package com.dachen.health.pack.illhistory.entity.vo;

import com.dachen.health.pack.illhistory.entity.po.RecordConsultiation;
import com.dachen.util.BeanUtil;

import java.util.List;

/**
 * Created by fuyongde on 2016/12/14.
 */
public class RecordConsultiationVo {

    /**订单id**/
    private Integer orderId;
    /**会诊医生**/
    private Integer consultationDoctor;
    /**会诊医生姓名**/
    private String consultationDoctorName;
    
    /**参与医生**/
    private List<Integer> consultJoinDocs;
    private List<String>  consultJoinDocNames;
    /**主诊医生**/
    private Integer mainDoctor;
    /**主诊医生姓名**/
    private String mainDoctorName;
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
    /**是否能进入IM**/
    private Boolean canJoinIM;
    /**医生id**/
    private Integer doctorId;

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Boolean getCanJoinIM() {
        return canJoinIM;
    }

    public void setCanJoinIM(Boolean canJoinIM) {
        this.canJoinIM = canJoinIM;
    }

    public String getConsultationDoctorName() {
        return consultationDoctorName;
    }

    public void setConsultationDoctorName(String consultationDoctorName) {
        this.consultationDoctorName = consultationDoctorName;
    }

    public String getMainDoctorName() {
        return mainDoctorName;
    }

    public void setMainDoctorName(String mainDoctorName) {
        this.mainDoctorName = mainDoctorName;
    }

    public List<Integer> getConsultJoinDocs() {
		return consultJoinDocs;
	}

	public void setConsultJoinDocs(List<Integer> consultJoinDocs) {
		this.consultJoinDocs = consultJoinDocs;
	}

	public List<String> getConsultJoinDocNames() {
		return consultJoinDocNames;
	}

	public void setConsultJoinDocNames(List<String> consultJoinDocNames) {
		this.consultJoinDocNames = consultJoinDocNames;
	}

	private OrderResult orderResult;

    public Integer getConsultationDoctor() {
        return consultationDoctor;
    }

    public void setConsultationDoctor(Integer consultationDoctor) {
        this.consultationDoctor = consultationDoctor;
    }

    public Integer getMainDoctor() {
        return mainDoctor;
    }

    public void setMainDoctor(Integer mainDoctor) {
        this.mainDoctor = mainDoctor;
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

    public OrderResult getOrderResult() {
        return orderResult;
    }

    public void setOrderResult(OrderResult orderResult) {
        this.orderResult = orderResult;
    }

    public static RecordConsultiationVo fromRecornConsultiation(RecordConsultiation recordConsultiation) {
        return BeanUtil.copy(recordConsultiation, RecordConsultiationVo.class);
    }
}
