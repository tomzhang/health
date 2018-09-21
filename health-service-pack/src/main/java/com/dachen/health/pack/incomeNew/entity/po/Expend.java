package com.dachen.health.pack.incomeNew.entity.po;

public class Expend {
    private Integer id;

    private Integer doctorId;

    private String groupId;

    /**
     * 1=医生，2=医生集团
     */
    private Integer objectType;

    /**
     * 1=提现手续费，2=其它
     */
    private Integer expendType;

    private Double money;

    /**
     * 4=完成
     */
    private Integer status;

    private Integer createUserId;

    private Long createDate;

    private Integer lastUpdateUserId;

    private Integer lastUpdateDate;

    /**
     * @return ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return doctor_id
     */
    public Integer getDoctorId() {
        return doctorId;
    }

    /**
     * @param doctorId
     */
    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * @return group_id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId == null ? null : groupId.trim();
    }

    /**
     * 获取1=医生，2=医生集团
     *
     * @return object_type - 1=医生，2=医生集团
     */
    public Integer getObjectType() {
        return objectType;
    }

    /**
     * 设置1=医生，2=医生集团
     *
     * @param objectType 1=医生，2=医生集团
     */
    public void setObjectType(Integer objectType) {
        this.objectType = objectType;
    }

    /**
     * 获取1=提现手续费，2=其它
     *
     * @return expend_type - 1=提现手续费，2=其它
     */
    public Integer getExpendType() {
        return expendType;
    }

    /**
     * 设置1=提现手续费，2=其它
     *
     * @param expendType 1=提现手续费，2=其它
     */
    public void setExpendType(Integer expendType) {
        this.expendType = expendType;
    }

    /**
     * @return money
     */
    public Double getMoney() {
        return money;
    }

    /**
     * @param money
     */
    public void setMoney(Double money) {
        this.money = money;
    }

    /**
     * 获取4=完成
     *
     * @return status - 4=完成
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置4=完成
     *
     * @param status 4=完成
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return create_user_id
     */
    public Integer getCreateUserId() {
        return createUserId;
    }

    /**
     * @param createUserId
     */
    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    /**
     * @return create_date
     */
    public Long getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate
     */
    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    /**
     * @return last_update_user_id
     */
    public Integer getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    /**
     * @param lastUpdateUserId
     */
    public void setLastUpdateUserId(Integer lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    /**
     * @return last_update_date
     */
    public Integer getLastUpdateDate() {
        return lastUpdateDate;
    }

    /**
     * @param lastUpdateDate
     */
    public void setLastUpdateDate(Integer lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}