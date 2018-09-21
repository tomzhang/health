package com.dachen.health.controller.api.order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.api.client.order.entity.COrder;
import com.dachen.health.api.client.order.entity.COrderSession;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.util.BeanUtil;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/order")
public class ApiOrderController extends ApiBaseController {

	@Resource
	protected IOrderService orderService;

	@Resource
	protected IOrderSessionService orderSessionService;

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public JSONMessage findById(@PathVariable Integer id) {
		String tag = "{id}";
		logger.info("{}. id={}", tag, id);
		
		Order order = orderService.getOne(id);
		if (null == order) {
			return JSONMessage.failure("订单未找到:"+id);
		}
		OrderSession orderSession = orderSessionService.findOneByOrderId(order.getId());
		if (null == orderSession) {
			return JSONMessage.failure("OrderSession未找到:"+id);
		}

		COrder c = BeanUtil.copy(order, COrder.class);
		COrderSession cs = BeanUtil.copy(orderSession, COrderSession.class);
		c.setOrderSession(cs);

		return JSONMessage.success(null, c);
	}
	
	@RequestMapping(value = "findByIds", method = RequestMethod.GET)
	public JSONMessage findByIds(Integer[] ids) {
		String tag = "findByIds";
		logger.info("{}. ids={}", tag, ids);
		
		if (null == ids || 0 == ids.length) {
			return JSONMessage.failure("参数有误");
		}
		
		List<Integer> orderIds = Arrays.asList(ids);
		
		return returnWrapCOrder(orderIds);
	}

	private JSONMessage returnWrapCOrder(List<Integer> orderIds) {
		List<Order> orders = orderService.findOrderByIds(orderIds);
		if (CollectionUtils.isEmpty(orders)) {
			return JSONMessage.success();
		}
		
		List<OrderSession> orderSessions = orderSessionService.findByOrderIds(orderIds);
		List<COrder> list = new ArrayList<COrder>(orders.size());
		for (Order order:orders) {
			COrder c = BeanUtil.copy(order, COrder.class);
			wrapOrderSession(c, orderSessions);
			list.add(c);
		}
		
		return JSONMessage.success(null, list);
	}
	
	@RequestMapping(value = "sendBeginIMMsg/{orderId}", method = RequestMethod.POST)
	public JSONMessage sendBeginIMMsg(@PathVariable Integer orderId, 
			@RequestParam String patientMsg, @RequestParam String doctorMsg) throws HttpApiException {
		String tag = "sendBeginIMMsg/{orderId}";
		logger.info("{}. orderId={}, patientMsg={}, doctorMsg={}", tag, orderId, patientMsg, doctorMsg);
		
		orderService.sendBeginIMMsg(orderId, patientMsg, doctorMsg);
		
		return JSONMessage.success();
	}
	
	@RequestMapping(value = "byCareplans", method = RequestMethod.GET)
	public JSONMessage getByCarePlan(String[] carePlanIds) {
		String tag = "byCareplans";
		logger.info("{}. carePlanIds={}", tag, carePlanIds);
		
		if (null == carePlanIds || 0 == carePlanIds.length) {
			return JSONMessage.failure("参数有误");
		}
		
		List<String> cpIds = Arrays.asList(carePlanIds);
		
		List<Order> orders = orderService.findByCarePlanIds(cpIds);
		if (CollectionUtils.isEmpty(orders)) {
			return JSONMessage.success();
		}
		
		List<Integer> orderIds = new ArrayList<Integer>(orders.size());
		for (Order o:orders) {
			orderIds.add(o.getId());
		}
		
		return returnWrapCOrder(orderIds);
	}
	
	private void wrapOrderSession(COrder c, List<OrderSession> orderSessions) {
		if (CollectionUtils.isEmpty(orderSessions)) {
			return;
		}
		for (OrderSession os:orderSessions) {
			if (os.getOrderId().equals(c.getId())) {
				COrderSession cs = BeanUtil.copy(os, COrderSession.class);
				c.setOrderSession(cs);
				break;
			}
		}
	}
	
	@RequestMapping(value = "byCareplan/{carePlanId}", method = RequestMethod.GET)
	public JSONMessage getByCarePlan(@PathVariable String carePlanId) {
		String tag = "byCareplan/{carePlanId}";
		logger.info("{}. carePlanId={}", tag, carePlanId);
		
		Order order = orderService.getOneByCarePlanId(carePlanId);
		if (null == order) {
			return JSONMessage.failure("订单未找到:"+carePlanId);
		}
		
		OrderSession orderSession = orderSessionService.findOneByOrderId(order.getId());
		if (null == orderSession) {
			return JSONMessage.failure("OrderSession未找到:"+carePlanId);
		}

		COrder c = BeanUtil.copy(order, COrder.class);
		COrderSession cs = BeanUtil.copy(orderSession, COrderSession.class);
		c.setOrderSession(cs);

		return JSONMessage.success(null, c);
	}
	
	
}
