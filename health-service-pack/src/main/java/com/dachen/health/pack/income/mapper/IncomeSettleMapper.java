package com.dachen.health.pack.income.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.income.entity.po.IncomeSettle;
import com.dachen.health.pack.income.entity.po.IncomeSettleExample;

public interface IncomeSettleMapper {
    int countByExample(IncomeSettleExample example);

    int deleteByExample(IncomeSettleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(IncomeSettle record);

    int insertSelective(IncomeSettle record);

    List<IncomeSettle> selectByExample(IncomeSettleExample example);

    IncomeSettle selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") IncomeSettle record, @Param("example") IncomeSettleExample example);

    int updateByExample(@Param("record") IncomeSettle record, @Param("example") IncomeSettleExample example);

    int updateByPrimaryKeySelective(IncomeSettle record);

    int updateByPrimaryKey(IncomeSettle record);
}