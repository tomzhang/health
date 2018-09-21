package com.dachen.health.pack.order.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.order.entity.po.OrderDoctor;
import com.dachen.health.pack.order.entity.po.OrderDoctorExample;

public interface OrderDoctorMapper {
    int countByExample(OrderDoctorExample example);

    int deleteByExample(OrderDoctorExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OrderDoctor record);

    int insertSelective(OrderDoctor record);

    List<OrderDoctor> selectByExample(OrderDoctorExample example);

    OrderDoctor selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OrderDoctor record, @Param("example") OrderDoctorExample example);

    int updateByExample(@Param("record") OrderDoctor record, @Param("example") OrderDoctorExample example);

    int updateByPrimaryKeySelective(OrderDoctor record);

    int updateByPrimaryKey(OrderDoctor record);

	List<Integer> findOrderIdByDoctorId(Integer doctorId);

	List<Integer> findOrderIdByRelationDoctor(Map<String, Object> paramMap);

	void updateOrderDoctor(OrderDoctor conOrderDoctor);

	List<Integer> findDoctorByOrderId(Integer orderId);
}