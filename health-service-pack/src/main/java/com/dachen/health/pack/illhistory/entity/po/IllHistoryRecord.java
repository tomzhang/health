package com.dachen.health.pack.illhistory.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * 病程记录
 */
@Entity(value = "t_ill_history_record", noClassnameStored = true)
public class IllHistoryRecord {

    @Id
    private String id;
    /**名称**/
    private String name;
    /**电子病历的id**/
    private String illHistoryInfoId;
    /**病程的类型 @see IllHistoryEnum.IllRecordTypeUsed**/
    private Integer type;
    /**该病程的创建时间**/
    private Long createTime;
    /**最后一次更新时间**/
    private Long updateTime;
    /**健康关怀信息**/
    private RecordCare recordCare;
    /**订单信息**/
    private RecordOrder recordOrder;
    /**报道信息**/
    private RecordCheckIn recordCheckIn;
    /**会诊信息**/
    private RecordConsultiation recordConsultiation;
    /**检查项**/
    private RecordCheckItem recordCheckItem;
    /**正常的手动添加的病程**/
    private RecordNormal recordNormal;
    /**用药**/
    private RecordDrug recordDrug;
    
    /**是否保密，true表示患者不能查看，false表示患者可以查看**/
    private Boolean secret;
    /**病程创建者，用于病程保密时的判断**/
    private Integer creater;
    /**医生助手添加时会用到该字段，用于标识医生助手帮助哪个医生添加在患者病历中的**/
    private Integer forDoctorId;

    public Integer getForDoctorId() {
        return forDoctorId;
    }

    public void setForDoctorId(Integer forDoctorId) {
        this.forDoctorId = forDoctorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIllHistoryInfoId() {
        return illHistoryInfoId;
    }

    public void setIllHistoryInfoId(String illHistoryInfoId) {
        this.illHistoryInfoId = illHistoryInfoId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public RecordCare getRecordCare() {
        return recordCare;
    }

    public void setRecordCare(RecordCare recordCare) {
        this.recordCare = recordCare;
    }

    public RecordOrder getRecordOrder() {
        return recordOrder;
    }

    public void setRecordOrder(RecordOrder recordOrder) {
        this.recordOrder = recordOrder;
    }

    public RecordCheckIn getRecordCheckIn() {
        return recordCheckIn;
    }

    public void setRecordCheckIn(RecordCheckIn recordCheckIn) {
        this.recordCheckIn = recordCheckIn;
    }

    public RecordConsultiation getRecordConsultiation() {
        return recordConsultiation;
    }

    public void setRecordConsultiation(RecordConsultiation recordConsultiation) {
        this.recordConsultiation = recordConsultiation;
    }

    public RecordCheckItem getRecordCheckItem() {
        return recordCheckItem;
    }

    public void setRecordCheckItem(RecordCheckItem recordCheckItem) {
        this.recordCheckItem = recordCheckItem;
    }

    public RecordNormal getRecordNormal() {
        return recordNormal;
    }

    public void setRecordNormal(RecordNormal recordNormal) {
        this.recordNormal = recordNormal;
    }

    public RecordDrug getRecordDrug() {
        return recordDrug;
    }

    public void setRecordDrug(RecordDrug recordDrug) {
        this.recordDrug = recordDrug;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

	public Boolean getSecret() {
		return secret;
	}

	public void setSecret(Boolean secret) {
		this.secret = secret;
	}

	public Integer getCreater() {
		return creater;
	}

	public void setCreater(Integer creater) {
		this.creater = creater;
	}
}
