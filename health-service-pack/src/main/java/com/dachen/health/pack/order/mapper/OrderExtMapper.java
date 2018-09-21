package com.dachen.health.pack.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.order.entity.po.OrderExt;

public interface OrderExtMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderExt record);

    int insertSelective(OrderExt record);

    OrderExt selectByPrimaryKey(Integer id);
    
    OrderExt selectByDocAndOrderId(@Param("orderId")Integer orderId,@Param("doctorId")Integer docId);
    
    List<OrderExt> getOrderExtList(Integer orderId);

    int updateByPrimaryKeySelective(OrderExt record);

    int updateByPrimaryKey(OrderExt record);
}