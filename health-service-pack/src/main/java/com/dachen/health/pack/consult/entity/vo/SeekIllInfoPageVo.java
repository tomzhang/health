package com.dachen.health.pack.consult.entity.vo;

import com.dachen.health.checkbill.entity.vo.CheckBillPageVo;
import com.dachen.health.pack.consult.entity.po.IllTransferRecord;
import com.dachen.util.JSONUtil;

public class SeekIllInfoPageVo {

	private Long createTime;
	
	/*1:手动添加的就医建议，2：诊疗记录中的建议和病情资料，3、检查单中的建议*/
	private Integer type;
	
	private CureRecordAndDiseaseVo cureRecordAndDiseaseVo;
	
	private CheckBillPageVo checkBillPageVo;

	private IllCaseTypeContentPageVo illCaseTypeContentPageVo;
	
	private IllTransferRecord illTransferRecord;
	
	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public CureRecordAndDiseaseVo getCureRecordAndDiseaseVo() {
		return cureRecordAndDiseaseVo;
	}

	public void setCureRecordAndDiseaseVo(CureRecordAndDiseaseVo cureRecordAndDiseaseVo) {
		this.cureRecordAndDiseaseVo = cureRecordAndDiseaseVo;
	}

	public CheckBillPageVo getCheckBillPageVo() {
		return checkBillPageVo;
	}

	public void setCheckBillPageVo(CheckBillPageVo checkBillPageVo) {
		this.checkBillPageVo = checkBillPageVo;
	}

	public IllCaseTypeContentPageVo getIllCaseTypeContentPageVo() {
		return illCaseTypeContentPageVo;
	}

	public void setIllCaseTypeContentPageVo(IllCaseTypeContentPageVo illCaseTypeContentPageVo) {
		this.illCaseTypeContentPageVo = illCaseTypeContentPageVo;
	}
	

	public IllTransferRecord getIllTransferRecord() {
		return illTransferRecord;
	}

	public void setIllTransferRecord(IllTransferRecord illTransferRecord) {
		this.illTransferRecord = illTransferRecord;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
}
