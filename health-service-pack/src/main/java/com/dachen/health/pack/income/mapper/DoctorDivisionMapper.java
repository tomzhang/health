package com.dachen.health.pack.income.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.income.entity.po.DoctorDivision;
import com.dachen.health.pack.income.entity.po.DoctorDivisionExample;

public interface DoctorDivisionMapper {
    int countByExample(DoctorDivisionExample example);

    int deleteByExample(DoctorDivisionExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(DoctorDivision record);

    int insertSelective(DoctorDivision record);

    List<DoctorDivision> selectByExample(DoctorDivisionExample example);

    DoctorDivision selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") DoctorDivision record, @Param("example") DoctorDivisionExample example);

    int updateByExample(@Param("record") DoctorDivision record, @Param("example") DoctorDivisionExample example);

    int updateByPrimaryKeySelective(DoctorDivision record);

    int updateByPrimaryKey(DoctorDivision record);
    
    /**
     * 查询 settle相关的 income
     * @param settleId
     * @return
     *@author wangqiao
     *@date 2016年1月25日
     */
    List<DoctorDivision> selectBySettleId(Integer settleId);
}