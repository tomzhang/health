package com.dachen.health.pack.account.service;


import com.dachen.health.pack.account.entity.po.Account;

public interface IAccountService {

    /**
     * </p>根据userId查找用户余额 </p>
     * 
     * @param userID
     * @return
     * @author peiX
     * @date 2015年8月18日
     */
    Account findOneAccount(Integer userId);

    void addAccount(Integer userId);

    void updateAccount(Account account);
     
    void updateChagerMoneyAccount(Integer userId,Long changerMoney,Integer sourceType,Integer sourceId);

}
