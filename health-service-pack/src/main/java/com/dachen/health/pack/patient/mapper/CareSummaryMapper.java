package com.dachen.health.pack.patient.mapper;

import java.util.List;

import com.dachen.health.pack.patient.model.CareSummary;

public interface CareSummaryMapper {

	int insert(CareSummary careSummary);
	
	int deleteByPrimaryKey(Integer id);

	CareSummary selectByPrimaryKey(Integer id);

	int updateByPrimaryKey(CareSummary careSummary);
	
	int updateByPrimaryKeySelective(CareSummary careSummary);

	List<CareSummary> selectByOrderId(int orderId);

	//List<CareSummary> selectByParam(CareSummaryParam param);

	List<CareSummary> selectByOrderIds(List<Integer> orderIds);
}
