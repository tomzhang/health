package com.dachen.health.controller.pack.evaluate;

import javax.annotation.Resource;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.constants.OrderEnum.OrderStatus;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.pack.evaluate.entity.Evaluation;
import com.dachen.health.pack.evaluate.service.IEvaluationService;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.service.IOrderSessionService;

@RestController
@RequestMapping("/pack/evaluate")
public class EvaluationController {

	
	@Resource
	private IEvaluationService evaluationService;
	
	@Resource
	private IOrderSessionService orderSessionService;
	
	@Resource
	OrderMapper orderMapper;
	
	/**
     * @api {get} /pack/evaluate/getEvaluationItem 获取评价项
     * @apiVersion 1.0.0
     * @apiName getEvaluationItem
     * @apiGroup 评价
     * @apiDescription 获取评价项
     *
     * @apiParam    {String}          access_token               token
     * @apiParam    {Integer}         orderId                    订单Id
     * 
     * @apiSuccess  {List}        	  goodItem    			 	 List<EvaluationItem>
     * @apiSuccess  {String}       	  goodItem.id  			 	  好评项Id
     * @apiSuccess  {String}          goodItem.name    			  好评项Name
     * @apiSuccess  {List}        	  generalItem    			 List<EvaluationItem>
     * @apiSuccess  {String}       	  generalItem.id  			  一般项Id
     * @apiSuccess  {String}          generalItem.name    		  一般项Name
     * 
     * @apiAuthor  谢平
     * @date 2016年1月8日
     */
	@RequestMapping("getEvaluationItem")
	public JSONMessage getEvaluationItem(Integer orderId) {
		return JSONMessage.success(evaluationService.getEvaluationItem(orderId));
	}
	
	/**
     * @api {get} /pack/evaluate/addEvaluation 提交评价
     * @apiVersion 1.0.0
     * @apiName addEvaluation
     * @apiGroup 评价
     * @apiDescription 提交评价
     *
     * @apiParam    {String}          access_token               token
     * @apiParam    {Integer}         orderId                    订单Id
     * @apiParam	{String...}       itemIds                    评价项Id
     * 
     * @apiSuccess  {Evaluation}      Evaluation    			 Evaluation对象
     * 
     * @apiAuthor  谢平
     * @date 2016年1月8日
     */
	@RequestMapping("addEvaluation")
	public JSONMessage addEvaluation(Integer orderId, String... itemIds) throws HttpApiException {
		preEvaluation(orderId);
		return JSONMessage.success(evaluationService.add(orderId, itemIds));
	}

	private void preEvaluation(Integer orderId) throws HttpApiException {
		//先结束订单
		Order order = orderMapper.getOne(orderId);
		/**
		 * 针对图文咨询
		 * 如果次数为0 或 订单已结束才能评价
		 * 评价之后立即结束图文咨询服务
		 */
		if(order.getPackType() == PackType.message.getIndex()){
			OrderSession os = orderSessionService.findOneByOrderId(orderId);
			//满足评价之后结束服务的条件
			if(order.getOrderStatus() != OrderStatus.已完成.getIndex() &&
				(os.getTotalReplyCount() == null ||
					os.getReplidCount() == null ||
					os.getTotalReplyCount() != os.getReplidCount())
					)
			throw new ServiceException("消息次数没有完成或者订单没有结束");
		}
		if (OrderStatus.已完成.getIndex() != order.getOrderStatus().intValue()) {			
			orderSessionService.finishService(orderId, 2);
		}
	}
	
	/**
     * @api {get} /pack/evaluate/isEvaluated 是否已评价
     * @apiVersion 1.0.0
     * @apiName isEvaluated
     * @apiGroup 评价
     * @apiDescription 是否已评价
     *
     * @apiParam    {String}          access_token               token
     * @apiParam    {Integer}         orderId                    订单Id
     * 
     * @apiSuccess  {Boolean}      	  isEvaluated    			   是否已评价（true、false）
     * 
     * @apiAuthor  谢平
     * @date 2016年1月8日
     */
	@RequestMapping("isEvaluated")
	public JSONMessage isEvaluated(Integer orderId) {
		return JSONMessage.success(evaluationService.isEvaluated(orderId));
	}
	
	/**
     * @api {get} /pack/evaluate/getTopSix 获取TopSix
     * @apiVersion 1.0.0
     * @apiName getTopSix
     * @apiGroup 评价
     * @apiDescription 获取TopSix（包含用户数、好评率）
     *
     * @apiParam    {String}          access_token          	token
     * @apiParam    {Integer}         doctorId              	医生Id
     * 
     * @apiSuccess  {Integer}      	  userNum    				用户数量
     * @apiSuccess  {String}      	  goodRate    				好评率
     * @apiSuccess  {List}      	  evaluateStatList    	  	List<EvaluationStatVO>
     * @apiSuccess  {String}      	  evaluateStatList.name   	评价项
     * @apiSuccess  {Integer}      	  evaluateStatList.count  	评价项数量
     * 
     * @apiAuthor  谢平
     * @date 2016年1月8日
     */
	@RequestMapping("getTopSix")
	public JSONMessage getTopSix(Integer doctorId) {
		return JSONMessage.success(evaluationService.getTopSix(doctorId));
	}
	
	/**
     * @api {get} /pack/evaluate/getEvaluationDetail 获取评价详情
     * @apiVersion 1.0.0
     * @apiName getEvaluationDetail
     * @apiGroup 评价
     * @apiDescription 获取评价详情
     *
     * @apiParam    {String}          access_token          		token
     * @apiParam    {Integer}         doctorId              		医生Id
     * 
     * @apiSuccess  {List}      	  evaluateStatList    			List<EvaluationStatVO>
     * @apiSuccess  {String}      	  evaluateStatList.name   		评价项
     * @apiSuccess  {Integer}      	  evaluateStatList.count  		评价项数量
     * @apiSuccess  {List}      	  evaluateVOList    			List<EvaluationVO>
     * @apiSuccess  {String}      	  evaluateVOList.userName   	用户名
     * @apiSuccess  {String}      	  evaluateVOList.createTime 	评价时间
     * @apiSuccess  {String}      	  evaluateVOList.description 	评价
     * 
     * @apiAuthor  谢平
     * @date 2016年1月8日
     */
	@RequestMapping("getEvaluationDetail")
	public JSONMessage getEvaluationDetail(Integer doctorId) {
		return JSONMessage.success(evaluationService.getEvaluationDetail(doctorId));
	}
	
}
