package com.dachen.health.controller.api.order;


import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.po.PendingOrderStatus;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.order.service.IPendingOrderStatusService;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/pendingOrderStatus")
public class ApiPendingOrderStatusController extends ApiBaseController {

    @Resource
    protected IOrderService orderService;

    @Resource
    protected IOrderSessionService orderSessionService;

    @Autowired
    protected IPendingOrderStatusService pendingOrderStatusService;

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * 生成问题时回调
     * @param careItemId
     * @param sentTime
     * @param carePlanId
     * @param orderId
     * @return
     */
    @RequestMapping(value = "createQuestionByCareItem", method = RequestMethod.POST)
    public JSONMessage createQuestionByCareItem(String careItemId, @RequestParam Long sentTime, @RequestParam String carePlanId, Integer orderId) {
    	
    	Order order = orderService.getOneByCarePlanId(carePlanId);
    	if(order == null){
    		return JSONMessage.success();
    	}
    	PendingOrderStatus pos = pendingOrderStatusService.queryByOrderId(order.getId());
    	if(pos == null){
    		return JSONMessage.success();
    	}
    	
    	if (1 == pos.getOrderStatus()) {	// 已经是待处理，返回
    		return JSONMessage.success();
    	}
    	
    	if(pos.getFlagTime() == null ){
	        pos.setOrderId(order.getId());
	        pos.setOrderStatus(1);
	        pos.setFlagTime(System.currentTimeMillis());
	        pendingOrderStatusService.updatePendingOrderStatus(pos);
    	} else if (!sdf.format(pos.getFlagTime()).equals(sdf.format(sentTime))) {	// 不是同一天
	        pos.setOrderId(order.getId());
	        pos.setOrderStatus(1);
	        pos.setFlagTime(System.currentTimeMillis());
	        pendingOrderStatusService.updatePendingOrderStatus(pos);
    	}
    	
        return JSONMessage.success();
    	
    }
    
    /**
     * 提交答卷时回调
     * @param careItemId
     * @param submitTime
     * @param carePlanId
     * @param orderId
     * @return
     */
    @RequestMapping(value = "submitAnswerSheetByCareItem", method = RequestMethod.POST)
    public JSONMessage submitAnswerSheetByCareItem(String careItemId, @RequestParam Long submitTime, @RequestParam String carePlanId, Integer orderId) {
    	Order order = orderService.getOneByCarePlanId(carePlanId);
    	if(order == null){
    		return JSONMessage.success();
    	}
    	PendingOrderStatus pos = pendingOrderStatusService.queryByOrderId(order.getId());
    	if(pos == null){
    		return JSONMessage.success();
    	}
    	
    	if (0 == pos.getOrderStatus()) {
    		return JSONMessage.success();
    	}
    	
        pos.setOrderId(order.getId());
        pos.setOrderStatus(0);
        pos.setFlagTime(System.currentTimeMillis());
        pendingOrderStatusService.updatePendingOrderStatus(pos);
    	
        return JSONMessage.success();
    }
}
