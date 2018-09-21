package com.dachen.health.pack.income.entity.po;

/**
 * 收入表明细表基于订单来设定
 */
public class IncomeDetails {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 医生id
     */
    private Integer doctorId;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 订单总金额
     */
    private double money;

    /**
     * 医生实际在订单实际获益金额
     */
    private double shareMoney;

    /**
     * 收益类型(1:订单收益，2:分成收益)
     */
    private Integer incomeType;

    /**
     * 该医生的直接上级医生所提成比例
     */
    private Integer docProportion;

    /**
     * 该医生的直接上级医生ID
     */
    private Integer upDoc;

    /**
     * 该医生在获取订单时的集团收益比例
     */
    private Integer groupProportion;

    /**
     * 该医生在获取订单时的集团ID
     */
    private String upGroup;

    /**
     * 玄关健康平台的收益比例
     */
    private Integer sysProportion;

    /**
     * 自动分成比例
     */
    private Integer selfProportion;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Long createTime;

    private Long modifyTime;

    /**
     * 备用字段
     */
    private String extend1;

    /**
     * 备用字段
     */
    private String extend2;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取医生id
     *
     * @return doctor_id - 医生id
     */
    public Integer getDoctorId() {
        return doctorId;
    }

    /**
     * 设置医生id
     *
     * @param doctorId 医生id
     */
    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * 获取订单id
     *
     * @return order_id - 订单id
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置订单id
     *
     * @param orderId 订单id
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取订单总金额
     *
     * @return money - 订单总金额
     */
    public double getMoney() {
        return money;
    }

    /**
     * 设置订单总金额
     *
     * @param money 订单总金额
     */
    public void setMoney(double money) {
        this.money = money;
    }

    /**
     * 获取医生实际在订单实际获益金额
     *
     * @return share_money - 医生实际在订单实际获益金额
     */
    public double getShareMoney() {
        return shareMoney;
    }

    /**
     * 设置医生实际在订单实际获益金额
     *
     * @param shareMoney 医生实际在订单实际获益金额
     */
    public void setShareMoney(double shareMoney) {
        this.shareMoney = shareMoney;
    }

    /**
     * 获取收益类型(1:订单收益，2:分成收益)
     *
     * @return income_type - 收益类型(1:订单收益，2:分成收益)
     */
    public Integer getIncomeType() {
        return incomeType;
    }

    /**
     * 设置收益类型(1:订单收益，2:分成收益)
     *
     * @param incomeType 收益类型(1:订单收益，2:分成收益)
     */
    public void setIncomeType(Integer incomeType) {
        this.incomeType = incomeType;
    }

    /**
     * 获取该医生的直接上级医生所提成比例
     *
     * @return doc_proportion - 该医生的直接上级医生所提成比例
     */
    public Integer getDocProportion() {
        return docProportion;
    }

    /**
     * 设置该医生的直接上级医生所提成比例
     *
     * @param docProportion 该医生的直接上级医生所提成比例
     */
    public void setDocProportion(Integer docProportion) {
        this.docProportion = docProportion;
    }

    /**
     * 获取该医生的直接上级医生ID
     *
     * @return up_doc - 该医生的直接上级医生ID
     */
    public Integer getUpDoc() {
        return upDoc;
    }

    /**
     * 设置该医生的直接上级医生ID
     *
     * @param upDoc 该医生的直接上级医生ID
     */
    public void setUpDoc(Integer upDoc) {
        this.upDoc = upDoc;
    }

    /**
     * 获取该医生在获取订单时的集团收益比例
     *
     * @return group_proportion - 该医生在获取订单时的集团收益比例
     */
    public Integer getGroupProportion() {
        return groupProportion;
    }

    /**
     * 设置该医生在获取订单时的集团收益比例
     *
     * @param groupProportion 该医生在获取订单时的集团收益比例
     */
    public void setGroupProportion(Integer groupProportion) {
        this.groupProportion = groupProportion;
    }

    /**
     * 获取该医生在获取订单时的集团ID
     *
     * @return up_group - 该医生在获取订单时的集团ID
     */
    public String getUpGroup() {
        return upGroup;
    }

    /**
     * 设置该医生在获取订单时的集团ID
     *
     * @param upGroup 该医生在获取订单时的集团ID
     */
    public void setUpGroup(String upGroup) {
        this.upGroup = upGroup == null ? null : upGroup.trim();
    }

    /**
     * 获取玄关健康平台的收益比例
     *
     * @return sys_proportion - 玄关健康平台的收益比例
     */
    public Integer getSysProportion() {
        return sysProportion;
    }

    /**
     * 设置玄关健康平台的收益比例
     *
     * @param sysProportion 玄关健康平台的收益比例
     */
    public void setSysProportion(Integer sysProportion) {
        this.sysProportion = sysProportion;
    }

    /**
     * 获取其它分成比例
     *
     * @return self_proportion - 自己分成比例
     */
    public Integer getselfProportion() {
        return selfProportion;
    }

    /**
     * 设置其它分成比例
     *
     * @param otherProportion 其它分成比例
     */
    public void setSelfProportion(Integer selfProportion) {
        this.selfProportion = selfProportion;
    }

    /**
     * 获取状态是否退款,结算，未结算，等
     *
     * @return status - 
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态是否退款,结算，未结算，等
     *
     * @param status 
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * @return modify_time
     */
    public Long getModifyTime() {
        return modifyTime;
    }

    /**
     * @param modifyTime
     */
    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * 获取备用字段
     *
     * @return extend_1 - 备用字段
     */
    public String getExtend1() {
        return extend1;
    }

    /**
     * 设置备用字段
     *
     * @param extend1 备用字段
     */
    public void setExtend1(String extend1) {
        this.extend1 = extend1 == null ? null : extend1.trim();
    }

    /**
     * 获取备用字段
     *
     * @return extend_2 - 备用字段
     */
    public String getExtend2() {
        return extend2;
    }

    /**
     * 设置备用字段
     *
     * @param extend2 备用字段
     */
    public void setExtend2(String extend2) {
        this.extend2 = extend2 == null ? null : extend2.trim();
    }
}