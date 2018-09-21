package com.dachen.health.pack.illhistory.entity.po;

import java.util.List;

/**
 * 病情资料，一个病历对应一个病情资料
 * @author fuyongde
 *
 */
public class IllContentInfo {
	/**主诉**/
	private String illDesc;
	/**病情描述的图片**/
	private List<String> pics;
	/**诊治情况**/
	private String treatment;
	/**更新时间**/
	private Long updateTime;
	/**更新人**/
	private Integer updater;
	public String getIllDesc() {
		return illDesc;
	}
	public void setIllDesc(String illDesc) {
		this.illDesc = illDesc;
	}
	public List<String> getPics() {
		return pics;
	}
	public void setPics(List<String> pics) {
		this.pics = pics;
	}
	public String getTreatment() {
		return treatment;
	}
	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getUpdater() {
		return updater;
	}
	public void setUpdater(Integer updater) {
		this.updater = updater;
	}
}
