package com.dachen.health.pack.account.mapper;

import com.dachen.health.pack.account.entity.po.Account;

public interface AccountMapper {
    
	
	/**
	 * 添加 一条账户记录 
	 * @param account
	 */
	void addAccount(Account account);
	
	/**
	 * 修改一条账户记录
	 * @param account
	 */
	void updateAccount(Account account);
	
	/**
	 * 查询一条记录
	 * @param userId
	 * @return
	 */
	Account findOne(Integer userId);
	
}