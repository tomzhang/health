package com.dachen.health.pack.income.service;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.income.entity.param.SettleParam;
import com.dachen.health.pack.income.entity.po.SettleSys;

public interface ISettleService {
	
	/**
	 * 批量结算
	 * @param ids
	 */
	public Map<String ,Object> settleIncome(String[] ids);
	
	/**
	 * 根据结算记录ID获取对应结算收入明细
	 * @param id
	 */
	public PageVO getSettlIncomeDetailsList(SettleParam param);
	
	/**
	 * 根据用户类型等信息获取对应结算列表
	 * @param param
	 * @return
	 */
	public PageVO  getSettleList(SettleParam param);
	
	/**
	 * 获取医生对应结算列表
	 * @param param
	 * @return
	 */
	public PageVO getDoctorSettlelist(SettleParam param);
	
	/**
	 * 获取医打款订单明细
	 * @param param
	 */
	public PageVO getDocSettleIncomeDetails(SettleParam param);
	
	
	/**
	 * 根据时间用户类查找对应的getSettleList统计明细
	 * @param param
	 * @return
	 */
	public PageVO getSysSettleType(SettleParam param);
	
	/**
	 *自动结算
	 */
	public void autoSettleIncome();

	public List<?> getData(SettleParam param);
	
	 SettleSys getSetteSysByID(Integer id);
}
