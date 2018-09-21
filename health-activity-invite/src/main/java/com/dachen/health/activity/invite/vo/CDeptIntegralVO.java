package com.dachen.health.activity.invite.vo;

import java.io.Serializable;

/**
 * @author 钟良
 * @desc
 * @date:2017/6/19 18:45 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class CDeptIntegralVO implements Serializable {
    private String id;

    private String deptId;

    /**
     * 余额
     **/
    private Long balance;
    /**
     * 账户的状态
     **/
    private Integer status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
