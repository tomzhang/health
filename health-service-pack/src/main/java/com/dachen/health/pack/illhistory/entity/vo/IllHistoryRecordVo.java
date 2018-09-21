package com.dachen.health.pack.illhistory.entity.vo;

/**
 * Created by fuyongde on 2016/12/14.
 */
public class IllHistoryRecordVo {

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
    private RecordCareVo recordCare;
    /**订单信息**/
    private RecordOrderVo recordOrder;
    /**报道信息**/
    private RecordCheckInVo recordCheckIn;
    /**会诊信息**/
    private RecordConsultiationVo recordConsultiation;
    /**检查项**/
    private RecordCheckItemVo recordCheckItem;
    /**正常的手动添加的病程**/
    private RecordNormalVo recordNormal;
    /**用药**/
    private RecordDrugVo recordDrug;

    /**订单id**/
    private Integer orderId;
    /**患者姓名**/
    private String patientName;

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

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
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

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public RecordCareVo getRecordCare() {
        return recordCare;
    }

    public void setRecordCare(RecordCareVo recordCare) {
        this.recordCare = recordCare;
    }

    public RecordOrderVo getRecordOrder() {
        return recordOrder;
    }

    public void setRecordOrder(RecordOrderVo recordOrder) {
        this.recordOrder = recordOrder;
    }

    public RecordCheckInVo getRecordCheckIn() {
        return recordCheckIn;
    }

    public void setRecordCheckIn(RecordCheckInVo recordCheckIn) {
        this.recordCheckIn = recordCheckIn;
    }

    public RecordConsultiationVo getRecordConsultiation() {
        return recordConsultiation;
    }

    public void setRecordConsultiation(RecordConsultiationVo recordConsultiation) {
        this.recordConsultiation = recordConsultiation;
    }

    public RecordCheckItemVo getRecordCheckItem() {
        return recordCheckItem;
    }

    public void setRecordCheckItem(RecordCheckItemVo recordCheckItem) {
        this.recordCheckItem = recordCheckItem;
    }

    public RecordNormalVo getRecordNormal() {
        return recordNormal;
    }

    public void setRecordNormal(RecordNormalVo recordNormal) {
        this.recordNormal = recordNormal;
    }

    public RecordDrugVo getRecordDrug() {
        return recordDrug;
    }

    public void setRecordDrug(RecordDrugVo recordDrug) {
        this.recordDrug = recordDrug;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

}
