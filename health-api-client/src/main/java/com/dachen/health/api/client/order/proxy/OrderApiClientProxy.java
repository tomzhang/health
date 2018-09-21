package com.dachen.health.api.client.order.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.health.api.client.order.entity.COrder;
import com.dachen.sdk.exception.HttpApiException;

@Component
public class OrderApiClientProxy extends HealthApiClientProxy {

	/**
	 * 根据id查找订单实体
	 * 
	 * @param id
	 * @return COrder 订单实体
	 * @throws HttpApiException
	 */
	public COrder findById(Integer id) throws HttpApiException {
		try {
			String url = "order/{id}";
			COrder corder = this.openRequest(url, COrder.class, id.toString());
			return corder;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}

	/**
	 * 根据关怀计划的id查找对应的订单
	 * 
	 * @param carePlanId
	 *            关怀计划的id
	 * @return COrder 详单实体
	 * @throws HttpApiException
	 */
	public COrder findByCarePlanId(String carePlanId) throws HttpApiException {
		try {
			String url = "order/byCareplan/{carePlanId}";
			COrder corder = this.openRequest(url, COrder.class, carePlanId.toString());
			return corder;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 根据ids查找订单列表
	 * 
	 * @param ids
	 * @return COrder 订单实体
	 * @throws HttpApiException
	 */
	public List<COrder> findByIds(List<Integer> ids) throws HttpApiException {
		if (null == ids || 0 == ids.size()) {
			return null;
		}
		Map<String, String> params = new HashMap<String, String>(1);
		putArrayIfNotBlank(params, "ids", ids);
		try {
			String url = "order/findByIds";
			List<COrder> corder = this.openRequestList(url, params, COrder.class);
			return corder;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 根据关怀计划的ids查找对应的订单列表
	 * 
	 * @param carePlanIds
	 *            关怀计划的id
	 * @return COrder 详单实体
	 * @throws HttpApiException
	 */
	public List<COrder> findByCarePlanIds(List<String> carePlanIds) throws HttpApiException {
		if (null == carePlanIds || 0 == carePlanIds.size()) {
			return null;
		}
		Map<String, String> params = new HashMap<String, String>(1);
		putArrayIfNotBlank(params, "carePlanIds", carePlanIds);
		try {
			String url = "order/byCareplans";
			List<COrder> corder = this.openRequestList(url, params, COrder.class);
			return corder;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 发送关怀计划开始服务的IM消息
	 * 
	 * @param orderId
	 * @param patientMsg
	 * @param doctorMsg
	 * @throws HttpApiException
	 */
	public void sendBeginIMMsg(Integer orderId, String patientMsg, String doctorMsg) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(2);
		putIfNotBlank(params, "patientMsg", patientMsg);
		putIfNotBlank(params, "doctorMsg", doctorMsg);
		try {
			String url = "order/sendBeginIMMsg/{orderId}";
			this.postRequest(url, params, String.class, orderId.toString());
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
}
