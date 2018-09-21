package com.dachen.health.pack.income.entity.po;

/**
 * @author 李淼淼
 * @version 1.0 2016-01-23
 */
public class IncomeSettle {
    private Integer id;

    private Integer incomeId;

    private Integer settleId;

    private Long createTime;

    /**
     * 关联的收入类型：di=医生收入，dv=医生提成，gv=集团提成
     */
    private String type;

    /**
     * @return id
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
     * @return income_id
     */
    public Integer getIncomeId() {
        return incomeId;
    }

    /**
     * @param incomeId
     */
    public void setIncomeId(Integer incomeId) {
        this.incomeId = incomeId;
    }

    /**
     * @return settle_id
     */
    public Integer getSettleId() {
        return settleId;
    }

    /**
     * @param settleId
     */
    public void setSettleId(Integer settleId) {
        this.settleId = settleId;
    }

    /**
     * @return create_time
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取关联的收入类型：di=医生收入，dv=医生提成，gv=集团提成
     *
     * @return type - 关联的收入类型：di=医生收入，dv=医生提成，gv=集团提成
     */
    public String getType() {
        return type;
    }

    /**
     * 设置关联的收入类型：di=医生收入，dv=医生提成，gv=集团提成
     *
     * @param type 关联的收入类型：di=医生收入，dv=医生提成，gv=集团提成
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }
}