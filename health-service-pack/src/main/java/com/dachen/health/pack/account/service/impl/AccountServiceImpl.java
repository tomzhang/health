package com.dachen.health.pack.account.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.pack.account.entity.po.Account;
import com.dachen.health.pack.account.entity.po.AccountDetail;
import com.dachen.health.pack.account.mapper.AccountMapper;
import com.dachen.health.pack.account.service.IAccountDetailService;
import com.dachen.health.pack.account.service.IAccountService;

@Service
public class AccountServiceImpl implements IAccountService{
	
	@Autowired
	private AccountMapper accountMapper;
	
	@Autowired
	private IAccountDetailService  accountDetailService;
		
	public Account findOneAccount(Integer userId) {
		return accountMapper.findOne(userId);
	}

	public void addAccount(Integer userId) {
		Account account = new Account();
		account.setCreateTime(System.currentTimeMillis());
		account.setUserId(userId);
		account.setFrozenMoney(Long.valueOf(0));
		account.setUsableMoney(Long.valueOf(0));
		account.setMofidyTime(System.currentTimeMillis());
		accountMapper.addAccount(account);
	}
	
	public void updateAccount(Account account) {
		
		accountMapper.updateAccount(account);
	}

	public void updateChagerMoneyAccount(Integer userId, Long changerMoney,Integer sourceType,Integer sourceId) {
		
		Account account = this.findOneAccount(userId);
		if(null==account) {
			throw new ServiceException(600,"账户不存在");
		}
		Long timelong = System.currentTimeMillis();
		
		if((account.getUsableMoney()+changerMoney)<0) {
			throw new ServiceException(700,"余额不足");
		}
		AccountDetail accountDetail = new AccountDetail();
		accountDetail.setUserId(userId);
		accountDetail.setHistoryCreateTime(account.getMofidyTime());
		accountDetail.setHistoryFrozenMoney(account.getFrozenMoney());
		accountDetail.setHistoryUsableMoney(account.getUsableMoney());
		//设置余额变更
		account.setUsableMoney(account.getUsableMoney()+changerMoney);
		account.setMofidyTime(timelong);
		//修改账户余额
		this.updateAccount(account);
		
		accountDetail.setChangeMoney(changerMoney);
		accountDetail.setFrozenMoney(account.getFrozenMoney());
		accountDetail.setUsableMoney(account.getUsableMoney());
		accountDetail.setCreateTime(timelong);
		accountDetail.setSourceId(sourceId);
		accountDetail.setSourceType(sourceType);
		accountDetailService.addAccountDetail(accountDetail);
		
	}

}
