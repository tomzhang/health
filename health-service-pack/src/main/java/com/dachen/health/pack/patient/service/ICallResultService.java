package com.dachen.health.pack.patient.service;

import java.util.List;

import com.dachen.health.pack.patient.model.CallResult;

/**
 * 
 * @author vincent
 *
 */
public interface ICallResultService extends IBaseService<CallResult, Integer>{
	
	public List<CallResult> findByCallid(String callId);
	/**
	 * 通过订单id去查询通话信息
	 * @param orderId
	 * @return
	 */
	public List<CallResult> getAllCallResultByOrderId(Integer orderId);
	
	
	List<CallResult> get8HourCallResultList();

}
