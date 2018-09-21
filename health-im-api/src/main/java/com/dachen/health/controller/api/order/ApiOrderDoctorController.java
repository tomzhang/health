package com.dachen.health.controller.api.order;

import java.util.List;

import javax.annotation.Resource;

import com.dachen.health.pack.order.entity.vo.DoctoreRatioVO;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.pack.order.entity.po.OrderDoctor;
import com.dachen.health.pack.order.service.IOrderDoctorService;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/order/doctor")
public class ApiOrderDoctorController extends ApiBaseController {

	@Resource
	protected IOrderDoctorService orderDoctorService;

	@RequestMapping(value = "byOrder/{orderId}", method = RequestMethod.GET)
	public JSONMessage byOrder(@PathVariable Integer orderId) {
		String tag = "byOrder/{orderId}";
		logger.info("{}. orderId={}", tag, orderId);
		
		List<OrderDoctor> orderDoctors = orderDoctorService.findOrderDoctors(orderId);
		if (CollectionUtils.isEmpty(orderDoctors)) {
			return JSONMessage.success();
		}

		return JSONMessage.success(null, orderDoctors);
	}
	
	@RequestMapping(value = "getDoctorRatiosByOrder/{orderId}", method = RequestMethod.GET)
	public JSONMessage getDoctorRatiosByOrder(@PathVariable Integer orderId,
			@RequestParam(required = true) Integer mainDoctorId) {
		String tag = "getDoctorRatiosByOrder/{packId}";
		logger.info("{}. orderId={}, mainDoctorId={}", tag, orderId, mainDoctorId);

		List<DoctoreRatioVO> vos = orderDoctorService.getDoctorRatiosByOrder(orderId, mainDoctorId);
		if (CollectionUtils.isEmpty(vos)) {
			return JSONMessage.success();
		}
		
		return JSONMessage.success(null, vos);
	}
}
