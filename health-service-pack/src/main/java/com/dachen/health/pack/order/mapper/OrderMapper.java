
package com.dachen.health.pack.order.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.vo.AppointOrderGuideVo;
import com.dachen.health.pack.order.entity.vo.OrderDetailVO;
import com.dachen.health.pack.order.entity.vo.OrderVO;
import com.dachen.health.pack.patient.model.OrderSession;


/**
 * ProjectName： health-service-pack<br>
 * ClassName： OrderMapper<br>
 * Description： 订单mapper<br>
 * @author fanp
 * @createTime 2015年8月10日
 * @version 1.0.0
 */
public interface OrderMapper {

	/**
	 * </p>
	 * 新建订单
	 * </p>
	 * 
	 * @param param
	 * @author fanp
	 * @date 2015年8月10日
	 */
	void add(Order order);

	/**
	 * </p>
	 * 查询订单
	 * </p>
	 * 
	 * @param param
	 * @author peiX
	 * @date 2015年8月17日
	 */
	Order getOne(int orderId);
	List<Order> findOrderByIds(List<Integer> ids);

	List<Order> getAll();

	/**
	 * 查询所有的订单
	 * @return
	 */
	List<Order> findAllWithNoActive();

	Order findOrderBydiseaseId(int id);
	
	Order findOrderByCarePlanId(String carePlanId);
	List<Order> findByCarePlanIds(List<String> carePlanIds);

	void update(Order order);

	void deleteById(int id);

	/**
	 * 根据订单类型查询订单数据
	 * 
	 * @param userId
	 * @param orderStatus
	 * @return
	 */
	List<OrderVO> findOrders(OrderParam param);

	Integer findOrdersCount(OrderParam param);
	
	List<OrderVO> findPaidOrders(OrderParam param);

	Integer findPaidOrdersCount(OrderParam param);

	List<OrderVO> getOrderByRecordStatus(OrderParam param);
	
	Integer getOrderByRecordStatusCount(OrderParam param);

	OrderVO detail(OrderParam param);

	Integer countNewOrder(Integer doctorId);
	
	Integer countByPatientId(Integer patientId);

	OrderDetailVO findOrderDisease(Integer OrderId);

	List<Integer> selectServingOrderByDocId(Integer docId);

	/**
	 * 根据订单类型查询订单数据（不包含取消状态的订单）
	 * 
	 * @param userId
	 * @param orderStatus
	 * @return
	 */
	List<OrderVO> findOrdersNoLimit(OrderParam param);

	String findMinDay(OrderParam param);

	OrderVO getOrderByDocIdAndUserId(OrderParam param);

	List<OrderSession> getOverTimeOrderSession();

	List<OrderVO> findOrderExistByStatus(OrderParam param);
	
	List<Order> getNoAcStatusByUserId(OrderParam param);
	
	/**
	 * 查询图文，电话咨询超时订单
	 * @param orderParam
	 * @return
	 */
	List<OrderVO> findOrderExpiredStatus(OrderParam orderParam);

	List<Integer> getAllPaidConsultationOrderIds();

	String getLastGidByDoctorIdAndPatientId(Map<String, Object> params);

	List<Integer> findOrderIdByIllCaseInfoId(String illCaseInfoId);

	List<Order> findByIllCaseInfoId(String illCaseInfoId);

	void updateOrderIllCaseInfoId(Map<String, Object> params);

	List<Order> findConsultationOrder(Map<String, Object> params);

	Long findConsultationOrderCount(Map<String, Object> params);

	/**
	 * 根据传入的集合id 去查询订单结果集
	 * @param id
	 * @return
	 */
	List<OrderVO> findByIdsMap(OrderParam param);
	
	
	List<OrderVO> findByIds(OrderParam param);
	
	/**
	 * 根据传入的集合id 去查询订单结果集数
	 * @param id
	 * @return
	 */
	Integer findByIdsMapCount(OrderParam param);
	
	List<Integer> findOrderIdByDoctorId(Map<String, Object> orderMapParam);
	
	/**
	 * 查询导医已经处理过的订单
	 * @param param
	 * @return
	 */
	List<OrderVO> getGuideAlreadyServicedOrder(OrderParam param);

	/**
	 * 查询导医已经处理过的订单shu
	 * 
	 * @param param
	 * @return
	 */
	Integer getGuideAlreadyServicedOrderCount(OrderParam param);

	void updateCareOrder(Order order);
	
	List<OrderVO> getRefundOrders(OrderParam param);
	
	Integer getRefundOrdersCount(OrderParam param);

	List<Integer> findDiseaseIdByIllCaseInfoId(String illCaseInfoId);
	
	List<OrderVO> getLastEveryDoctor(Integer userId);

	long getAppointmentOrdersCount(Map<String, Object> sqlparams);

	List<AppointOrderGuideVo> getAppointmentOrders(Map<String, Object> sqlparams);

	void updateHospital(Map<String, Object> sqlParams);
	
	List<OrderVO> getOrderListByMoreCond(OrderParam param);
	
	Integer getOrderListByMoreCondCount(OrderParam param);

	void clearOrderIllCaseInfo(Map<String, String> sqlParam);
	
	/**
     * 根据pack_type和日期来查询订单
     * @return
     */
    List<Order> getByPackTypeAndTime(OrderParam param);

	List<Order> getOrdersToRepairIncomes(OrderParam param);

	List<Map<String, Object>> getAppointmentPaidOrdersGroupByDoctorId(Map<String, Object> map);
	
	List<OrderVO> getAppointmentListByCondition(OrderParam param);
	
	Integer getHaveAppointmentListByDate(OrderParam param);

	List<OrderVO> searchAppointmentOrder4Guide(Map<String, Object> sqlParam);

	long searchAppointmentOrder4GuideCount(Map<String, Object> sqlParam);
	
	//------------------------------------------------医生助手电话订单功能修改---------------------------------------------
	List<OrderVO> findOrdersForAssistant(OrderParam param);

	List<OrderVO> findOrdersForAssistantAll(OrderParam param);

	Integer findOrdersForAssistantCount(OrderParam param);

	OrderVO doctorAssistantQueryOrderById(Integer orderId);

	void updateAssistantComment(Map<String ,Object> params);
	
	void updateOrderDocAssitant(@Param("param")OrderParam param,@Param("newAssistantId")Integer newAssistantId);
	//------------------------------------------------集团平台与运营后台,订单管理---------------------------------------------
	List<OrderVO> queryOrders(OrderParam param);

	Integer queryOrdersCount(OrderParam param);
	
	/**
	 * 查看患者是否有正在进行的某个关怀计划套餐
	 * @param params
	 * @return
	 */
	OrderVO getOngoingCareOrderByPatient(Map<String, Object> params);

	void updateExpectAppointmentIds(Map<String, Object> sqlmap);

    List<Order> findAllCareOrder();
    
    List<Map<String,Object>> getConsultationTimesByDocIdAndGroupId(@Param("docId")Integer docId,@Param("groupId")String  groupId);
}