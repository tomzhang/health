package com.dachen.health.pack.illhistory.entity.po;

/**
 * 初步诊断，一个病历包含多个初步诊断
 * @author fuyongde
 *
 */
public class Diagnosis {
	/**内容**/
	private String content;
	/**创建时间**/
	private Long createTime;
	/**更新时间**/
	private Long updateTime;
	/**疾病id**/
	private String diseaseId;
	/**疾病名称**/
	private String diseaseName;
	/**如果医生添加则保存医生id**/
	private Integer fromDoctorId;
	/**订单id**/
	private Integer orderId;


	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public String getDiseaseId() {
		return diseaseId;
	}
	public void setDiseaseId(String diseaseId) {
		this.diseaseId = diseaseId;
	}
	public String getDiseaseName() {
		return diseaseName;
	}
	public void setDiseaseName(String diseaseName) {
		this.diseaseName = diseaseName;
	}
	public Integer getFromDoctorId() {
		return fromDoctorId;
	}
	public void setFromDoctorId(Integer fromDoctorId) {
		this.fromDoctorId = fromDoctorId;
	}

}
