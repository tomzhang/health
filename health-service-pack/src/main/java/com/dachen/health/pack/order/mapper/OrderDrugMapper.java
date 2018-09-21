package com.dachen.health.pack.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.order.entity.po.OrderDrug;
import com.dachen.health.pack.order.entity.po.OrderDrugExample;

public interface OrderDrugMapper {
    int countByExample(OrderDrugExample example);

    int deleteByExample(OrderDrugExample example);

    int deleteByPrimaryKey(String id);

    int insert(OrderDrug record);

    int insertSelective(OrderDrug record);

    List<OrderDrug> selectByExample(OrderDrugExample example);

    OrderDrug selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") OrderDrug record, @Param("example") OrderDrugExample example);

    int updateByExample(@Param("record") OrderDrug record, @Param("example") OrderDrugExample example);

    int updateByPrimaryKeySelective(OrderDrug record);

    int updateByPrimaryKey(OrderDrug record);
}