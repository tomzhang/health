package com.dachen.health.pack.account.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.health.pack.account.entity.po.AccountDetail;
import com.dachen.health.pack.account.mapper.AccountDetailMapper;
import com.dachen.health.pack.account.service.IAccountDetailService;



@Service
public class AccountDetailServiceImpl implements IAccountDetailService{
	
	@Autowired
	private AccountDetailMapper accountDetailMapper;
	
	public void addAccountDetail(AccountDetail accountDetail) {
		accountDetailMapper.add(accountDetail);
	}
	
	
	
}
