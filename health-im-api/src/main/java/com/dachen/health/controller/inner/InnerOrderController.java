package com.dachen.health.controller.inner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.pack.order.service.IOrderService;

@RestController
@RequestMapping("/inner_api/order")
public class InnerOrderController {
	@Autowired
	private IOrderService orderService;
	
	@RequestMapping("/detail")
	public JSONMessage orderDetail(@RequestParam(required = true) Integer orderId) {
		return JSONMessage.success(orderService.orderDetail(orderId));
	}
}
