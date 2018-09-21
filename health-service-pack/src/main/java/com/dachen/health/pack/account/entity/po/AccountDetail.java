package com.dachen.health.pack.account.entity.po;

/**
 * ProjectName： health-service<br>
 * ClassName： AccountDetail<br>
 * Description： 账单明细表<br>
 * 
 * @author fanp
 * @createTime 2015年8月5日
 * @version 1.0.0
 */
public class AccountDetail {

    /* 主键 */
    private Integer id;

    /* 用户id */
    private Integer userId;

    /* 变化金额 */
    private Long changeMoney;

    /* 可用金额 */
    private Long usableMoney;

    /* 冻结金额 */
    private Long frozenMoney;

    /* 创建时间 */
    private Long createTime;

    /* 历史可用金额 */
    private Long historyUsableMoney;

    /* 历史冻结金额 */
    private Long historyFrozenMoney;

    /* 历史创建时间 */
    private Long historyCreateTime;

    /* 来源类型 {@link AccountEnum.SourceType} */
    private Integer sourceType;

    /* 来源id */
    private Integer sourceId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getChangeMoney() {
        return changeMoney;
    }

    public void setChangeMoney(Long changeMoney) {
        this.changeMoney = changeMoney;
    }

    public Long getUsableMoney() {
        return usableMoney;
    }

    public void setUsableMoney(Long usableMoney) {
        this.usableMoney = usableMoney;
    }

    public Long getFrozenMoney() {
        return frozenMoney;
    }

    public void setFrozenMoney(Long frozenMoney) {
        this.frozenMoney = frozenMoney;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getHistoryUsableMoney() {
        return historyUsableMoney;
    }

    public void setHistoryUsableMoney(Long historyUsableMoney) {
        this.historyUsableMoney = historyUsableMoney;
    }

    public Long getHistoryFrozenMoney() {
        return historyFrozenMoney;
    }

    public void setHistoryFrozenMoney(Long historyFrozenMoney) {
        this.historyFrozenMoney = historyFrozenMoney;
    }

    public Long getHistoryCreateTime() {
        return historyCreateTime;
    }

    public void setHistoryCreateTime(Long historyCreateTime) {
        this.historyCreateTime = historyCreateTime;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

}
