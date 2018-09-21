package com.dachen.health.pack.guide.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dachen.health.pack.guide.entity.OrderCache;
import com.dachen.health.pack.guide.entity.po.ConsultOrderDoctorPO;
import com.dachen.health.pack.guide.entity.po.ConsultOrderPO;
import com.dachen.health.pack.guide.entity.po.CustomerPatientRecord;
import com.dachen.health.pack.guide.entity.po.OrderRelationPO;
import com.dachen.health.pack.guide.entity.vo.OrderDiseaseVO;

public interface IGuideDAO {
	public String addConsultOrder(ConsultOrderPO order);
	
	public OrderCache getOrderCacheByGroup(String groupId);
	
	public OrderCache getOrderCache(String id);
	
//	public void updateOrderCache(String groupId,ConsultOrderPO order);
//	public void updateOrderCache(String id,String key,String value);
	
	public List<ConsultOrderPO> getOrderByUser(int userId);
	
	public List<ConsultOrderPO> getOrderByGuide(Integer userId,Long time,Integer count);
	
	public List<ConsultOrderPO> getOrderListByGuide(Integer userId,Date startDate,Date endDate);
	
	public ConsultOrderPO getOrderByGroup(String groupId);
	
	public ConsultOrderPO getObjectByOrderId(Integer orderId);
	
	public ConsultOrderPO getConsultOrderPO(String id);
	
	public ConsultOrderPO getConsultOrderPOAndState(String id);
	
	public ConsultOrderPO updateConsultOrder(String id,Map<String,Object>updateValue);
	
	public long count(String guideId);
	
	public List<ConsultOrderPO> getOrderList(Set<String>ids, String bdjlGroupId);
	
	/**
	 * 查询患者是否存在未开始服务以及服务中的订单
	 * @param userId
	 * @return
	 */
	public boolean exist(int userId);
	
	/**
	 * 获取预约时间（appointTime）在startTime和endTime之间的已支付订单
	 * @param userId 导医Id
	 * @param startTime 
	 * @param endTime
	 * @return
	 */
	public List<ConsultOrderPO> getHasPayOrderByGuide(Integer userId,long startTime,long endTime);
	
	public ConsultOrderPO updateOrderDisease(OrderDiseaseVO param);
	
	public List<ConsultOrderPO> getTimeOutOrderList();
	
	public List<ConsultOrderPO> getNotSendTimeOutOrderList();
	
	public Long getLastServiceTime(Integer userId);
	
	public void dataUpgrade();
	
	public void addOrderRelation(OrderRelationPO orderRelation);
	
	public List<OrderRelationPO> getOrderIdList(String guideOrderId);
	
	public OrderRelationPO getGuideIdByOrderId(Integer orderId);
	
	public ConsultOrderDoctorPO get2HourNoPay();

	public void syncDiseaseFromIllCase(com.dachen.health.pack.guide.entity.po.ConsultOrderPO.Disease dis);

	public CustomerPatientRecord addCustomerPatientRecord(CustomerPatientRecord record);

	public void updateCustomerPatientRecord(CustomerPatientRecord record);

	public List<Object> getCustomerWorkDate(Long dateTime);

	public long getDayRecordsCount(Long dateTime);

	public List<CustomerPatientRecord> getDayRecords(Long dateTime, Integer pageIndex, Integer pageSize);

	public long getCustomerWorkDateTotal();

	public long getFirstWorkDataTime();

	public Set<String> findAll();

	List<CustomerPatientRecord> getRecordsByUserId(Integer userId);
}
