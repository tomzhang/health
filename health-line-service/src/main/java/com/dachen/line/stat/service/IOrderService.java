package com.dachen.line.stat.service;

import java.util.List;
import java.util.Map;

import com.dachen.health.user.entity.vo.NurseVO;
import com.dachen.line.stat.entity.param.PalceOrderParam;
import com.dachen.line.stat.entity.vo.NurseOrderDetail;
import com.dachen.line.stat.entity.vo.PatientOrder;


/**
 * 护士订单服务
 * @author weilit
 * 2015 12 04 
 */
public interface IOrderService {
	
	
	
	public  List<PatientOrder> getUserOrder(Integer  userId);
	/**
	 * 获取护士可以接的订单
	 * @param userId
	 * @param pageNo 当前页
	 * @param pageSize 每页的大小
	 * @return
	 */
	public  Map<String,Object> getNurseOrder(Integer  userId,Integer  pageNo,Integer  pageSize);
	
	/**
	 * 获取护士可以接的订单
	 * @param userId
	 * @param pageNo 当前页
	 * @param pageSize 每页的大小
	 * @return
	 */
	public  void filterNurseOrder(Integer  userId,List<PatientOrder> patientOrder);
	
	
	/**
	 * 护士抢单
	 * @param userId
	 * @param id
	 * @return
	 */
	public  Map<String,Object> getTheOrder(Integer  userId,String  orderId);
		/**
	 * 患者下单
	 * @param palceOrderParam
	 */
	public Map<String,Object> insertUserOrder(PalceOrderParam  palceOrderParam);
	
	
	/**
	 * 取消订单
	 */
	public void cancleUserOrder(String basicOrderId, Integer userId);
	
	/**
	 * 取消订单
	 */
	public Map<String,Object> checkCancleUserOrder(String orderId);
	
	/**
	 * 基础订单回调借口
	 */
	public void callBackBasicOrder(String basicOrderId);
	
	/**
	 * 更新
	 * @param orderId
	 * @param status
	 */
	public void  endAppraise(String orderId,Integer status);
	/**
	 * 查询患者下单超过2小时没人接的订单
	 * author：jianghj
	 * date:2015年12月17日13:55:06
	 * 
	 *//*
	public void getOderOvertopHour();*/
	
	/**
	 * 获取订单详情
	 * @param basicOrderId  基础订单id
	 * @return
	 */
	public  Map<String,Object> getOrderDetail(String  orderId);
	
	/**
	 * 获取就医直通车详情
	 * @param orderId
	 * @return
	 */
	public NurseOrderDetail getThroughTrainOrderDetail(String orderId);
}
