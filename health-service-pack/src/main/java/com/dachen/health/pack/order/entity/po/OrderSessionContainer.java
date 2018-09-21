package com.dachen.health.pack.order.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.alibaba.fastjson.JSON;

/**
 * 目的 ：1-下单 图文/电话/健康关怀 之前 通过是否有任意一条数据来判断初诊和复诊
 *      2-针对单独的图文电话订单判断能否在重新下订单 通过status 和 packType 判断  (同一医生患者只有一条记录)
 *      3-针对报道判断会话是否存在，是否需要重新创建会话
 *      4-健康关怀目前还是同一患者医生可以多个会话组，改po会有多条记录
 */

@Entity(value="t_order_session_container" , noClassnameStored=false)
public class OrderSessionContainer {

	@Id
	private String id;
	private Integer doctorId;
	private Integer userId;
	private Integer patientId;
	private Integer orderId; // 患者与医生最近一次的订单id （针对图文/电话）  
	private Integer careOrderId;// 健康关怀下的图文/电话 需要记录原健康关怀订单id
	private Integer orderSessionId; //患者与医生最近一次的订单会话id  
	private String msgGroupId; // 会话组id 
	private Integer sessionType;// session分类 （健康关怀同一个医生患者可以存在多个会话）
	private Integer packType;// 套餐类型
	private Integer status; // 用订单的{已支付/已完成/已取消} 表示 目前（2016-12-02）仅此3种状态

	/**
	 * （消息总数）
	 */
	private Integer totalReplyCount;

	/**
	 * （已回复消息数量）
	 */
	private Integer replidCount;

	@Deprecated
	private String assistantDoctorGroupId;

	@Deprecated
	private String assistantPatientGroupId;



	public String getId() {
		return id;
	}
	public Integer getDoctorId() {
		return doctorId;
	}
	public Integer getUserId() {
		return userId;
	}
	public Integer getPatientId() {
		return patientId;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public Integer getPackType() {
		return packType;
	}
	public Integer getStatus() {
		return status;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	
	public Integer getSessionType() {
		return sessionType;
	}
	public void setSessionType(Integer sessionType) {
		this.sessionType = sessionType;
	}
	public void setPackType(Integer packType) {
		this.packType = packType;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMsgGroupId() {
		return msgGroupId;
	}
	public void setMsgGroupId(String msgGroupId) {
		this.msgGroupId = msgGroupId;
	}
	public Integer getTotalReplyCount() {
		return totalReplyCount;
	}

	public void setTotalReplyCount(Integer totalReplyCount) {
		this.totalReplyCount = totalReplyCount;
	}

	public Integer getReplidCount() {
		return replidCount;
	}

	public void setReplidCount(Integer replidCount) {
		this.replidCount = replidCount;
	}
	public Integer getOrderSessionId() {
		return orderSessionId;
	}
	public void setOrderSessionId(Integer orderSessionId) {
		this.orderSessionId = orderSessionId;
	}

	public String getAssistantDoctorGroupId() {
		return assistantDoctorGroupId;
	}

	public void setAssistantDoctorGroupId(String assistantDoctorGroupId) {
		this.assistantDoctorGroupId = assistantDoctorGroupId;
	}

	public String getAssistantPatientGroupId() {
		return assistantPatientGroupId;
	}

	public void setAssistantPatientGroupId(String assistantPatientGroupId) {
		this.assistantPatientGroupId = assistantPatientGroupId;
	}

	public Integer getCareOrderId() {
		return careOrderId;
	}
	public void setCareOrderId(Integer careOrderId) {
		this.careOrderId = careOrderId;
	}
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	
	
	
}
