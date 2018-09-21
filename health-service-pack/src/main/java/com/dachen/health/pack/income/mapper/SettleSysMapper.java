package com.dachen.health.pack.income.mapper;

import com.dachen.health.pack.income.entity.param.SettleParam;
import com.dachen.health.pack.income.entity.po.SettleSys;

public interface SettleSysMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SettleParam record);

    int insertSelective(SettleSys record);

    SettleSys selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SettleSys record);

    int updateByPrimaryKey(SettleSys record);
}