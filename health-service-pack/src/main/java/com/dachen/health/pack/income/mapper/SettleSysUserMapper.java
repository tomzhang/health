package com.dachen.health.pack.income.mapper;

import com.dachen.health.pack.income.entity.po.SettleSysUser;

public interface SettleSysUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SettleSysUser record);

    int insertSelective(SettleSysUser record);

    SettleSysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SettleSysUser record);

    int updateByPrimaryKey(SettleSysUser record);
}