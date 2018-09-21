package com.dachen.health.pack.evaluate.dao;

import java.util.List;

import com.dachen.health.pack.evaluate.entity.Evaluation;
import com.dachen.health.pack.evaluate.entity.vo.EvaluationStatVO;

public interface IEvaluationDao {

	Evaluation add(Evaluation eva);
	
	Evaluation getByOrderId(Integer orderId);
	
	List<EvaluationStatVO> getEvaluationStatVO(Integer doctorId);
	
	List<Evaluation> getEvaluations(Integer doctorId);
}
