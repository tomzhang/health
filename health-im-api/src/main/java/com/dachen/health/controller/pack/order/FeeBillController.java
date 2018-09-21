package com.dachen.health.controller.pack.order;

import java.util.Arrays;

import javax.annotation.Resource;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.util.ReqUtil;

@RestController
@RequestMapping("/pack/feebill")
public class FeeBillController {
    
    @Resource
    protected IOrderService orderSerivce;
    
    
   
    /**
	 * @api {get} /pack/feebill/sendFeeBill 生成收费单
	 * @apiVersion 1.0.0
	 * @apiName sendFeeBill
	 * @apiGroup 收费单
	 * @apiDescription 发送服务项生成收费单
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {String} serviceItemId 服务项Id（多个服务项Id，中间用","分隔；单个服务项和数量（大于1）之间用"#"分隔。如"AA01,AA02#2,AA03"）
	 * @apiParam {String} price 总金额
	 * @apiParam {String} hospitalId 医院Id
	 * @apiParam {String} groupId 集团Id
	 * @apiParam {Integer[]} userIds  患者用户数组
	 * 
	 * @apiAuthor 谢平
	 * @date 2016年4月28日
	 */
	@RequestMapping("/sendFeeBill")
	public JSONMessage sendFeeBill(Pack pack, Integer[] userIds) throws HttpApiException {
		pack.setDoctorId(ReqUtil.instance.getUserId());
		orderSerivce.saveSendFeeBill(pack, Arrays.asList(userIds));
		return JSONMessage.success();
	}
    
	/**
	 * @api {get} /pack/feebill/queryFeeBillByOrder 查询收费单服务项
	 * @apiVersion 1.0.0
	 * @apiName queryFeeBillByOrder
	 * @apiGroup 收费单
	 * @apiDescription 查询收费单服务项
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单Id
	 * 
	 * @apiSuccess {FeeBillDetail} feeBillDetail 对象
	 * @apiSuccess {Long} orderAmt 订单金额
	 * @apiSuccess {List} serviceItem 服务项
	 * @apiSuccess {String} serviceItem.id 服务项Id
	 * @apiSuccess {String} serviceItem.name 服务项名称
	 * @apiSuccess {Integer} serviceItem.price 价格
	 * @apiSuccess {Integer} serviceItem.count 数量
	 * 
	 * @apiAuthor 谢平
	 * @date 2016年4月28日
	 */
	@RequestMapping("/queryFeeBillByOrder")
	public JSONMessage queryFeeBillByOrder(Integer orderId) {
		return JSONMessage.success(orderSerivce.queryFeeBillByOrder(orderId));
	}
	   
}
