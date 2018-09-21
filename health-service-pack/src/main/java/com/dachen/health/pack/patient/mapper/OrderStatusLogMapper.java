package com.dachen.health.pack.patient.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.patient.model.OrderStatusLog;
import com.dachen.health.pack.patient.model.OrderStatusLogExample;

public interface OrderStatusLogMapper {
    int countByExample(OrderStatusLogExample example);

    int deleteByExample(OrderStatusLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OrderStatusLog record);

    int insertSelective(OrderStatusLog record);

    List<OrderStatusLog> selectByExample(OrderStatusLogExample example);

    OrderStatusLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OrderStatusLog record, @Param("example") OrderStatusLogExample example);

    int updateByExample(@Param("record") OrderStatusLog record, @Param("example") OrderStatusLogExample example);

    int updateByPrimaryKeySelective(OrderStatusLog record);

    int updateByPrimaryKey(OrderStatusLog record);
}