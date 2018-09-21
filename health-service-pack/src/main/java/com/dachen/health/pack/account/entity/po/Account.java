package com.dachen.health.pack.account.entity.po;

/**
 * ProjectName： health-service<br>
 * ClassName： Account<br>
 * Description： 会员账单表<br>
 * 
 * @author fanp
 * @createTime 2015年8月5日
 * @version 1.0.0
 */
public class Account {

    /* 主键 */
    private Integer id;

    /* 用户id */
    private Integer userId;

    /* 可以余额 */
    private Long usableMoney;

    /* 冻结余额 */
    private Long frozenMoney;

    /* 创建时间 */
    private Long createTime;

    /* 最好修改时间 */
    private Long mofidyTime;

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

    public Long getMofidyTime() {
        return mofidyTime;
    }

    public void setMofidyTime(Long mofidyTime) {
        this.mofidyTime = mofidyTime;
    }

}
