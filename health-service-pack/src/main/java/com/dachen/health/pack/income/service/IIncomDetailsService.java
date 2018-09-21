package com.dachen.health.pack.income.service;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.income.entity.param.IncomeDetailsParam;
import com.dachen.health.pack.income.entity.vo.IncomeDetailsVO;
import com.dachen.health.pack.income.entity.vo.PageIncome;

public interface IIncomDetailsService {
	
	void insertIncomeDetails(IncomeDetailsParam param);
	
	/**
	 * 主要是在发送通知时用
	 * @param param
	 * @return
	 */
	List<IncomeDetailsVO> getListByDoctorID(IncomeDetailsParam param);
	
	
	/**
	 * 获取医生收入首页基本账户余额订单或未完成订单
	 * @param param
	 * @return
	 */
	Map<String,Object> getDocIncomeIndex(IncomeDetailsParam param);
	
	
	/**
	 * 获取账户余额订单或未完成订单金额详情
	 * @param param
	 * @return
	 */
	PageVO getDocIncomeDetails(IncomeDetailsParam param);
	
	/**
	 * 获取历史收入列表
	 * @param param
	 * @return
	 */
	PageVO getDocHistoryIncome(IncomeDetailsParam param);
	
	/**
	 * 医生单月收入明细
	 */
	PageIncome getDocMonthImcomeDetail(IncomeDetailsParam param);
	
	
	/**
	 * 集团以月为单位收入列表
	 * @param param
	 */
	PageVO getGroupIncomeList(IncomeDetailsParam param);
	
	/**
	 * 获取集团某年某月的收入明细包括集团内医生收入明细
	 * @param param
	 * @return
	 */
//	PageVO getGroupDoctorIncomeList(IncomeDetailsParam param);
	
	

}
