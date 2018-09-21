package com.dachen.line.stat.service;

import java.util.List;
import java.util.Map;

import com.dachen.line.stat.entity.param.CheckResultsParm;
import com.dachen.line.stat.entity.vo.CheckResults;

/**
 * 护士订单服务
 * 
 * @author weilit 2015 12 04
 */
public interface ICheckResultsService {

	
	public List<Map<String, Object>> getCheckResultsServiceMapListByCheckId(
			String checkId);
	/**
	 * 通过订单id查询对应的检查项结果
	 * @param orderId
	 * @return
	 */
	public List<CheckResults> getCheckResultsServiceList(String orderId);
	
	/**
	 * 通过订单id查询对应的检查项结果
	 * @param orderId
	 * @return
	 */
	public List<Map<String, Object>> getCheckResultsServiceMapList(String orderId);
	
	
	public  void   insertCheckResults(CheckResultsParm param);

}
