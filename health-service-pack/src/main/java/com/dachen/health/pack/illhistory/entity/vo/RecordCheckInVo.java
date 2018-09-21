package com.dachen.health.pack.illhistory.entity.vo;

import com.dachen.health.pack.illhistory.entity.po.RecordCheckIn;
import com.dachen.util.BeanUtil;

import java.util.List;

/**
 * Created by fuyongde on 2016/12/14.
 */
public class RecordCheckInVo {

    /**订单id**/
    private Integer orderId;

    /**留言**/
    private String message;
    /**报道时间**/
    private Long checkInTime;
    /**图片资料**/
    private List<String> pics;
    /**就诊医院**/
    private String hospital;
    /**病历号**/
    private String illHistoryInfoNo;
    /**所患疾病的id**/
    private List<String> diseaseIds;
    /**所患的疾病**/
    private List<String> diseases;
    /**最后就诊时间**/
    private Long lastTime;
    /**订单类型**/
    private Integer orderType;

    /**会话id**/
    private String messageGroupId;

    /**是否能进入IM**/
    private Boolean canJoinIM;

    /**医生姓名**/
    private String doctorName;

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

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getMessageGroupId() {
        return messageGroupId;
    }

    public void setMessageGroupId(String messageGroupId) {
        this.messageGroupId = messageGroupId;
    }

    public List<String> getDiseases() {
        return diseases;
    }

    public void setDiseases(List<String> diseases) {
        this.diseases = diseases;
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

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public static RecordCheckInVo fromRecordCheckIn(RecordCheckIn recordCheckIn) {
        return BeanUtil.copy(recordCheckIn, RecordCheckInVo.class);
    }
}
