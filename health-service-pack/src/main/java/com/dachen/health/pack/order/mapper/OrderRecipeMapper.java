package com.dachen.health.pack.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.order.entity.po.OrderRecipe;
import com.dachen.health.pack.order.entity.po.OrderRecipeExample;

public interface OrderRecipeMapper {
    int countByExample(OrderRecipeExample example);

    int deleteByExample(OrderRecipeExample example);

    int deleteByPrimaryKey(String id);

    int insert(OrderRecipe record);

    int insertSelective(OrderRecipe record);

    List<OrderRecipe> selectByExample(OrderRecipeExample example);

    OrderRecipe selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") OrderRecipe record, @Param("example") OrderRecipeExample example);

    int updateByExample(@Param("record") OrderRecipe record, @Param("example") OrderRecipeExample example);

    int updateByPrimaryKeySelective(OrderRecipe record);

    int updateByPrimaryKey(OrderRecipe record);
}