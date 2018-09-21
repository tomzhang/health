package com.dachen.health.controller.pack.order;

import javax.annotation.Resource;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.pack.order.entity.param.OrderDrugParam;
import com.dachen.health.pack.order.service.IOrderDrugService;
import com.dachen.util.ReqUtil;

@RestController
@RequestMapping("/pack/order/drug")
public class OrderDrugController {

	@Resource
	protected IOrderDrugService orderDrugService;

	/**
	 * @api {get} /pack/order/drug/addOrderDrug 关怀用药提交药品信息生成药方，在用？
	 * @apiVersion 1.0.0
	 * @apiName addOrderDrug
	 * @apiGroup 关怀计划
	 * @apiDescription 关怀用药提交药品信息生成药方
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单ID
	 * @apiParam {String} drugReiceJson 药品信息以jession格式提交
	 * @apiExample {javascript} Example usage: 传递一个参数drugReiceJson，以json格式，如下：
	 *             [{'drug':'药品ID', 'period'
	 *             :'用药周期[格式：数字+空格+单位（单位必须是秒、分、时、日、周、月、年,且必须对应的英文，且开头字母大写,例如Second,Minute,Day,Week,Month
	 *             Year)]', 'times': '用药次数', 'quantity': '每次用量', 'patients':
	 *             '患者所属人群', 'method': '使用方法', 'days': '使用方法' 'unit': '单位 --
	 *             可以不传 。数据如：瓶、箱', 'requires_quantity':'要求数量'}]
	 * 
	 * @apiAuthor 谢佩
	 * @date 2015年12月10日
	 */
	@RequestMapping("addOrderDrug")
	public JSONMessage addOrderDrug(OrderDrugParam param) throws HttpApiException {
		param.setAccess_tonke(ReqUtil.instance.getToken());
		orderDrugService.saveOrderDrug(param);
		return JSONMessage.success();
	}

	/**
	 * @api {get} /pack/order/drug/findOrderDrug 查询关怀用药药品信息，在用？
	 * @apiVersion 1.0.0
	 * @apiName findOrderDrug
	 * @apiGroup 关怀计划
	 * @apiDescription 查询关怀用药药品信息
	 *
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} orderId 订单ID
	 * 
	 * @apiSuccess {Object} PatientDrugSuggestList 患者药品详细信息
	 * @apiSuccess {Object[]} c_patient_drug_suggest_list 药箱详细信息
	 * @apiSuccess {Object} PatientDrugSuggest.unit 药品名称
	 * @apiSuccess {Object} unit.id
	 * @apiSuccess {Object} unit.name
	 * @apiSuccess {String} PatientDrugSuggest.pack_specification 药品规格
	 * @apiSuccess {String} PatientDrugSuggest.requires_quantity 药品厂家
	 * @apiSuccess {String} PatientDrugSuggest.general_name 药品名称
	 * @apiSuccess {String} PatientDrugSuggest.trade_name 商用名称
	 * @apiSuccess {String} PatientDrugSuggest.manufacturer 生产厂家
	 * @apiSuccess {Object} PatientDrugSuggest.drug 品种对象
	 * @apiSuccess {String} PatientDrugSuggest.days 天数
	 * @apiSuccess {String} drug.id
	 * @apiSuccess {String} drug.title
	 * 
	 * 
	 * @apiAuthor 谢佩
	 * @date 2015年12月10日
	 */
	@RequestMapping("findOrderDrug")
	public JSONMessage findOrderDrug(OrderDrugParam param) throws HttpApiException {
		param.setAccess_tonke(ReqUtil.instance.getToken());
		return JSONMessage.success(null, orderDrugService.findDrugInfo(param));
	}

}
