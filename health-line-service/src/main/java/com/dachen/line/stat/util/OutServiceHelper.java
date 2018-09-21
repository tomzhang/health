package com.dachen.line.stat.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dachen.sdk.exception.HttpApiException;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.net.HttpHelper;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.spring.SpringBeansUtils;
import com.dachen.health.checkbill.entity.po.CheckItem;
import com.dachen.health.checkbill.entity.vo.CheckItemSearchParam;
import com.dachen.health.checkbill.service.CheckBillService;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.vo.OrderDetailVO;
import com.dachen.health.pack.order.entity.vo.OrderVO;
import com.dachen.health.pack.order.service.IOrderService;
import com.dachen.util.JSONUtil;
import com.dachen.util.ReqUtil;

/**
 * 调用外部借口服务类型
 * 
 * @author weilit
 *
 */
public class OutServiceHelper {

	/**
	 * 生成直通车订单（生成基础订单，为支付做基础准备）
	 * 
	 * @param access_token
	 * @param doctorId
	 * @param patientId
	 * @param packType
	 * @param price
	 * @apiParam {String} remarks 医院（备注字段暂存线下医院）
	 */

	public static JSONMessage throughTrainOrder(String access_token,
			Integer doctorId, Integer patientId, Integer packType, Long price,
			String remarks, String telephone) {

		JSONMessage json = null;
		String methodUrl = Constant.OUT_SERVER_URL()
				+ "pack/order/throughTrainOrder";

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("access_token", access_token);
		if (null != doctorId) {
			paramMap.put("doctorId", String.valueOf(doctorId));
		} else {
			doctorId = -1;
			paramMap.put("doctorId", String.valueOf(doctorId));
		}
		paramMap.put("patientId", String.valueOf(patientId));
		paramMap.put("packType", String.valueOf(packType));
		paramMap.put("price", String.valueOf(price * 100));
		paramMap.put("remarks", remarks);
		paramMap.put("telephone", telephone);
		String response = HttpHelper.post(methodUrl, paramMap);
		if (StringUtils.isNotEmpty(response)) {
			json = JSONUtil.parseObject(JSONMessage.class, response);
		}
		return json;
	}

	/**
	 * 生成基础订单
	 * @param doctorId
	 * @param patientId
	 * @param packType
	 * @param price
	 * @param remarks
	 * @param telephone
	 * @return
	 */
	public static Order throughTrainOrder(Integer doctorId, Integer patientId,
			Integer packType, Long price, String remarks, String telephone) {
		IOrderService orderService = SpringBeansUtils.getBean("orderServiceImpl");
		OrderParam param = new OrderParam();
    	param.setUserId(ReqUtil.instance.getUserId());
		param.setDoctorId(doctorId);
		param.setPatientId(patientId);
		param.setPackType(packType);
		param.setPrice(price);
		param.setRemarks(remarks);
		param.setTelephone(telephone);
//		param.setDiseaseId(0);
		Order order = orderService.addThroughTrainOrder(param);
		return order;
	}
    
	/**
	 * 更新基础订单状态
	 * @param orderId
	 * @param orderStatus
	 */
	public void updateOrderStatus(
			String orderId, Integer orderStatus) {
		try {
			IOrderService orderService = SpringBeansUtils.getBean("orderServiceImpl");
			OrderParam param = new OrderParam();
	    	param.setUserId(ReqUtil.instance.getUserId());
			param.setOrderId(Integer.parseInt(orderId));
			param.setOrderStatus(orderStatus);
			orderService.updateOrderStatus(param);
		} catch (Exception e) {
			throw new ServiceException("更新订单状态失败");
		}
		
	}
	
	
	/**
	 * /pack/order/updateOrderStatus 修改订单状态
	 * 
	 * @param access_token
	 * @param orderId
	 * @param orderStatus
	 *            、 已支付(3,"已支付"), 下单 已完成(4,"已完成"), 已取消(5,"已取消"), 进行中(6,"进行中"),
	 *            预约成功(10, "预约成功");
	 */

	public static JSONMessage updateOrderStatus(String access_token,
			String orderId, Integer orderStatus) {
		JSONMessage json = null;
		String methodUrl = Constant.OUT_SERVER_URL()
				+ "pack/order/updateOrderStatus";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("access_token", access_token);
		paramMap.put("orderId", String.valueOf(orderId));
		paramMap.put("orderStatus", String.valueOf(orderStatus));
		String response = HttpHelper.post(methodUrl, paramMap);
		if (StringUtils.isNotEmpty(response)) {
			json = JSONUtil.parseObject(JSONMessage.class, response);
		}
		return json;
	}

	
	/**
	 * 取消订单
	 * @param orderId
	 */
	public void cancelThroughTrainOrder(
			String orderId) {
		try {
			IOrderService orderService = SpringBeansUtils.getBean("orderServiceImpl");
			orderService.cancelThroughTrainOrder(Integer.parseInt(orderId));
		} catch (ServiceException e) {
			throw new ServiceException(e.getResultCode(),e.getMessage());
		}
		
	}
	
	
	/**
	 * /pack/order/cancelThroughTrainOrder 取消订单
	 * 
	 * @param orderId
	 *            已支付(3,"已支付"), 下单 已完成(4,"已完成"), 已取消(5,"已取消"), 进行中(6,"进行中"),
	 *            预约成功(10, "预约成功");
	 */

	public static JSONMessage cancelThroughTrainOrder(String access_token,
			String orderId) {
		JSONMessage json = null;
		String methodUrl = Constant.OUT_SERVER_URL()
				+ "pack/order/cancelThroughTrainOrder";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("access_token", access_token);
		paramMap.put("orderId", orderId);
		String response = HttpHelper.post(methodUrl, paramMap);
		if (StringUtils.isNotEmpty(response)) {
			json = JSONUtil.parseObject(JSONMessage.class, response);
		}
		return json;
	}

	/**
	 * 生成基础订单
	 * @return
	 */
	public static OrderDetailVO detail( String orderId) throws HttpApiException {
		IOrderService orderService = SpringBeansUtils.getBean("orderServiceImpl");
		OrderParam param = new OrderParam();
    	param.setUserId(ReqUtil.instance.getUserId());
		param.setOrderId(Integer.parseInt(orderId));
		
		OrderDetailVO detailVO = orderService.detail(param,ReqUtil.instance.getUser());
		return detailVO;
	}
	
	
	/**
	 * /pack/order/detail 取消订单
	 * @param orderId
	 *            已支付(3,"已支付"), 下单 已完成(4,"已完成"), 已取消(5,"已取消"), 进行中(6,"进行中"),
	 *            预约成功(10, "预约成功");
	 */

	public static JSONMessage detail(String access_token, String orderId) {
		JSONMessage json = null;
		String methodUrl = Constant.OUT_SERVER_URL() + "pack/order/detail";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("access_token", access_token);
		paramMap.put("orderId", orderId);
		String response = HttpHelper.post(methodUrl, paramMap);
		if (StringUtils.isNotEmpty(response)) {
			json = JSONUtil.parseObject(JSONMessage.class, response);
		}
		return json;
	}

	/**
	 * /checkbill/getCheckItemList 取消订单 * @apiParam {Integer} pageIndex 页码数
	 * 获取检查项 以及 检查单结果列表
	 * @apiParam {Integer} pageSize 条数
	 */
	public static JSONMessage getCheckItemList(String access_token,
			String fromId) {
		JSONMessage json = null;
		String serviceUrl = Constant.OUT_SERVER_URL();
		String methodUrl = serviceUrl + "checkbill/getCheckItemList";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("access_token", access_token);
		paramMap.put("fromId", fromId);
		paramMap.put("pageIndex", "0");
		paramMap.put("pageSize", "10000");
		String response = HttpHelper.get(methodUrl, paramMap);
		if (StringUtils.isNotEmpty(response)) {
			json = JSONUtil.parseObject(JSONMessage.class, response);
		}
		return json;
	}
	
	
	/**
	 * 
	 * /checkbill/getCheckItemList 取消订单 * @apiParam {Integer} pageIndex 页码数
	 * com.dachen.health.checkbill.service.impl.CheckBillServiceImpl
	 * @apiParam {Integer} pageSize 条数
	 */
	public static PageVO getCheckItemList(String fromId) {
		PageVO page = null;
		CheckBillService checkBillService = SpringBeansUtils.getBean("checkBillServiceImpl");
		CheckItemSearchParam  param = new CheckItemSearchParam ();
		param.setFromId(fromId);
		param.setPageIndex(0);
		param.setPageSize(Integer.MAX_VALUE);
		page =checkBillService.getCheckItemListByFromId(param);
		return page;
	}

	/**
	 * 批量上传结果
	 * @param checkList
	 */
	public static void batchAddCheckItem(
			List<CheckItem> checkList) {
		try {
			CheckBillService checkBillService = SpringBeansUtils.getBean("checkBillServiceImpl");
			checkBillService.batchAddOrUpdateCheckItem(JSON.toJSONString(checkList));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("批量上传检查结果失败");
		}
		
	}
	
	
	/**
	 * /checkbill/
	 * 
	 * @param access_token
	 * @return
	 */
	public static JSONMessage batchAddCheckItem(String access_token,
			List<CheckItem> checkList) {
		JSONMessage json = null;
		String serviceUrl = Constant.OUT_SERVER_URL();
		String methodUrl = serviceUrl + "checkbill/batchAddOrUpdateCheckItem";// batchAddOrUpdateCheckItem
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("access_token", access_token);
		paramMap.put("checkItemString", JSON.toJSONString(checkList));
		String response = HttpHelper.post(methodUrl, paramMap);
		if (StringUtils.isNotEmpty(response)) {
			json = JSONUtil.parseObject(JSONMessage.class, response);
		}
		return json;
	}

	/**
	 * 
	 * @param access_token
	 * @param id
	 * @param checkBillStatus
	 *            1：未下订单，2：已经下订单，3：已接单，4:已上传结果
	 * @return
	 */
	public static JSONMessage updateCheckbill(String access_token, String id,
			String checkBillStatus) {
		JSONMessage json = null;
		String serviceUrl = Constant.OUT_SERVER_URL();
		String methodUrl = serviceUrl + "checkbill/updateCheckbill";//
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("access_token", access_token);
		paramMap.put("id", id);
		paramMap.put("checkBillStatus", checkBillStatus);
		String response = HttpHelper.post(methodUrl, paramMap);
		if (StringUtils.isNotEmpty(response)) {
			json = JSONUtil.parseObject(JSONMessage.class, response);
		}
		return json;
	}

	/**
	 * /checkbill/3150
	 * 
	 * @param access_token
	 * @return
	 */
	public static Integer getOrderStatus(String access_token, String orderId) {
		JSONMessage json = null;
		String serviceUrl = Constant.OUT_SERVER_URL();
		String methodUrl = serviceUrl + "pack/order/getOrderStatus";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("access_token", access_token);
		paramMap.put("orderId", orderId);
		String response = HttpHelper.post(methodUrl, paramMap);
		if (StringUtils.isNotEmpty(response)) {
			json = JSONUtil.parseObject(JSONMessage.class, response);
		}
		Integer getOrderStatus = getOrderStatus(json);

		return getOrderStatus;
	}

	private static Integer getOrderStatus(JSONMessage jsonStatus) {
		Integer status = null;
		if (null != jsonStatus) {
			int resultCode = jsonStatus.getInteger("resultCode");
			if (resultCode == 1) {
				status = Integer.parseInt(jsonStatus.getData().toString());
			}
		}
		return status;
	}

	/**
	 * /pack/order/getByStatus 获取直通车订单列表
	 * @param access_token
	 * @param status
	 * @return
	 */
	public static JSONMessage getOrderListByStatus(String access_token,
			Integer status) {
		JSONMessage json = null;
		String serviceUrl = Constant.OUT_SERVER_URL();
		String methodUrl = serviceUrl + "pack/order/getByStatus";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("access_token", access_token);
		paramMap.put("orderStatus", String.valueOf(status));
		String response = HttpHelper.post(methodUrl, paramMap);
		if (StringUtils.isNotEmpty(response)) {
			json = JSONUtil.parseObject(JSONMessage.class, response);
		}
		return json;
	}
	
	/**
	 * 根据状态获取订单列表
	 * @param orderStatus
	 * @return
	 */
	public static List<OrderVO> getOrderListByStatus(
			Integer orderStatus) {
		IOrderService orderService = SpringBeansUtils.getBean("orderServiceImpl");
		OrderParam param = new OrderParam();
		param.setUserId(ReqUtil.instance.getUserId());
		param.setOrderType(OrderEnum.OrderType.throughTrain.getIndex());
		param.setOrderStatus(orderStatus);
		
		List<OrderVO> orderList=orderService.getOrders(param);
		
		return orderList;
	}
	
	/**
	 * 根据状态获取订单列表
	 * @return
	 */
	public static void getDoctorImagesList() {
		String serviceUrl = "http://192.168.3.7:9000/";
		String methodUrl = serviceUrl + "upload/getCertPath";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("access_token", "936c914d553b4d80b4502d3fc84445b4");
		paramMap.put("userId", "11887");
		paramMap.put("certType","c1");

		String response = HttpHelper.get(methodUrl, paramMap);
		 List<Map<String,Object>> ret=new ArrayList<Map<String,Object>>();
		if(StringUtils.isNotEmpty(response))
		{	
			JSONMessage json = JSONUtil.parseObject(JSONMessage.class, response);
			if(null!=json.getData())
			{	
				JSONArray  aray = (JSONArray)json.getData();
				if(aray.size()>0)
				{	
					for(int i =0;i<aray.size();i++)
					{	
						if(null!=aray.get(i))
						{	
							Map<String,Object> map = new HashMap<String, Object>();
							map.put("id", "");
							map.put("url",aray.get(i).toString());
							ret.add(map);
						}
					}
				}
				System.out.println();
			}
			
		}
		System.out.println(ret);
	}
	
	
	

	public static void main(String[] args) {
		getDoctorImagesList();
	}
}
