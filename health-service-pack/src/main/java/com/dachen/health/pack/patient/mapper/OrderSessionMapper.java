package com.dachen.health.pack.patient.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.model.OrderSessionExample;

public interface OrderSessionMapper {
    int countByExample(OrderSessionExample example);

    int deleteByExample(OrderSessionExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OrderSession record);

    int insertSelective(OrderSession record);

    List<OrderSession> selectByExample(OrderSessionExample example);

    OrderSession selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OrderSession record, @Param("example") OrderSessionExample example);

    int updateByExample(@Param("record") OrderSession record, @Param("example") OrderSessionExample example);

    int updateByPrimaryKeySelective(OrderSession record);

    int updateByPrimaryKey(OrderSession record);

	List<OrderSession> getAllMoringBeginConsultationOrders(Map<String, Object> params);

	List<Integer> getTimeAreaOrderIds(Map<String, Long> map);

	void changeAppointmentTime(Map<String,Object> sqlMap);
	
	List<OrderSession> selectByGroupId(@Param("groupId")String groupId);

	void updateFirstMessage(Map<String, Object> param);

	OrderSession findByOrderId(Integer orderId);
}