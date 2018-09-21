package com.dachen.health.api.client.checkbill.entity;

import java.io.Serializable;
import java.util.List;

public class CCheckBill implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;

	/**订单id**/
	private Integer  orderId;

	/**患者id**/
	private Integer patientId;

	/**检查项Id**/
	private List<String> checkItemIds;
	
	/**
	 * 1：未下订单，2：已经下订单，3：已接单，4:已上传结果
	 */
	private Integer checkBillStatus;
	
	private Long createTime;
	
	private Long updateTime;

	/**建议检查时间**/
	private Long suggestCheckTime;

	/**注意事项**/
	private String attention;
	
	private List<String> checkupIds;
	
	private String careItemId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public List<String> getCheckItemIds() {
		return checkItemIds;
	}

	public void setCheckItemIds(List<String> checkItemIds) {
		this.checkItemIds = checkItemIds;
	}

	public Integer getCheckBillStatus() {
		return checkBillStatus;
	}

	public void setCheckBillStatus(Integer checkBillStatus) {
		this.checkBillStatus = checkBillStatus;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public Long getSuggestCheckTime() {
		return suggestCheckTime;
	}

	public void setSuggestCheckTime(Long suggestCheckTime) {
		this.suggestCheckTime = suggestCheckTime;
	}

	public String getAttention() {
		return attention;
	}

	public void setAttention(String attention) {
		this.attention = attention;
	}

	public List<String> getCheckupIds() {
		return checkupIds;
	}

	public void setCheckupIds(List<String> checkupIds) {
		this.checkupIds = checkupIds;
	}

	public String getCareItemId() {
		return careItemId;
	}

	public void setCareItemId(String careItemId) {
		this.careItemId = careItemId;
	}
	
}
