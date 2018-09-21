package com.dachen.health.pack.evaluate.service;

import java.util.List;
import java.util.Map;

import com.dachen.health.base.entity.vo.EvaluationItemPO;
import com.dachen.health.pack.evaluate.entity.Evaluation;
import com.dachen.health.pack.evaluate.entity.vo.EvaluationDetailVO;
import com.dachen.health.pack.evaluate.entity.vo.TopSixEvaluationVO;
import com.dachen.sdk.exception.HttpApiException;

public interface IEvaluationService {

	public EvaluationItemPO getEvaluationItem(Integer orderId);
	
	public Evaluation add(Integer orderId, String... itemIds) throws HttpApiException;
	
	public Map<String, Object> isEvaluated(Integer orderId);
	
	public TopSixEvaluationVO getTopSix(Integer doctorId);
	
	public EvaluationDetailVO getEvaluationDetail(Integer doctorId);
	
	public String getGoodRate(Integer doctorId);
	
	public void sendSystem(Integer doctorId,String name) throws HttpApiException;

	List<String> getEvaluationNamesByOrderId(Integer orderId);
}
