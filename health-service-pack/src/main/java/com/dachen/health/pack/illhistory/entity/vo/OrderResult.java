package com.dachen.health.pack.illhistory.entity.vo;

import java.util.List;

/**
 * 咨询结果
 * Created by fuyongde on 2016/12/14.
 */
public class OrderResult {
    private Integer orderId;
    /**结束时间**/
    private Long endTime;
    /**咨询结果**/
    private String result;
    /**图片信息**/
    private List<String> pics;
    /**录音信息**/
    private List<String> voices;
    /**检查项名称**/
    private String checkSuggestName;
    /**初步诊断**/
    private String diagnosis;
    /**用药建议ID**/
    private String drugAdviseId;
    /**用药建议**/
    private DrugAdviseVo drugAdviseVo;
    /**医生姓名**/
    private String doctorName;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public String getCheckSuggestName() {
        return checkSuggestName;
    }

    public void setCheckSuggestName(String checkSuggestName) {
        this.checkSuggestName = checkSuggestName;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDrugAdviseId() {
        return drugAdviseId;
    }

    public void setDrugAdviseId(String drugAdviseId) {
        this.drugAdviseId = drugAdviseId;
    }

    public DrugAdviseVo getDrugAdviseVo() {
        return drugAdviseVo;
    }

    public void setDrugAdviseVo(DrugAdviseVo drugAdviseVo) {
        this.drugAdviseVo = drugAdviseVo;
    }

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

    public List<String> getVoices() {
        return voices;
    }

    public void setVoices(List<String> voices) {
        this.voices = voices;
    }
}
