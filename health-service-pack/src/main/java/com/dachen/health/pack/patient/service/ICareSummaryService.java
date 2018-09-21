package com.dachen.health.pack.patient.service;

import java.util.List;

import com.dachen.health.pack.patient.model.CareSummary;
import com.dachen.sdk.exception.HttpApiException;

public interface ICareSummaryService extends IBaseService<CareSummary, Integer>{
	
	List<CareSummary> findByOrderId(int orderId) throws HttpApiException;
	
	public int save(CareSummary intance, String[] images, String[] voices) throws HttpApiException;

	void sendNotice(CareSummary intance) throws HttpApiException;
	

}
