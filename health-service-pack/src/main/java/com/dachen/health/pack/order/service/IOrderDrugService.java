package com.dachen.health.pack.order.service;

import com.dachen.health.pack.order.entity.param.OrderDrugParam;
import com.dachen.medice.vo.PatientDrugSuggestList;
import com.dachen.sdk.exception.HttpApiException;

public interface IOrderDrugService {
	
	public void deleteOrderDrug(OrderDrugParam param);
	
	public void saveOrderDrug(OrderDrugParam param) throws HttpApiException;
	
	public PatientDrugSuggestList findDrugInfo(OrderDrugParam param) throws HttpApiException;
	
	public PatientDrugSuggestList findDrugInfo(String access_tonke, Integer orderId) throws HttpApiException;
	
	public void saveOrderDrug(Integer packId,Integer orderId);
	
	public Integer countOrderDrug(Integer orderId);
	
}
