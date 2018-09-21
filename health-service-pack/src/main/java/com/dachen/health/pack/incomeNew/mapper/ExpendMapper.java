package com.dachen.health.pack.incomeNew.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.incomeNew.entity.po.Expend;
import com.dachen.health.pack.incomeNew.entity.po.ExpendExample;

public interface ExpendMapper {
    int countByExample(ExpendExample example);

    int deleteByExample(ExpendExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Expend record);

    int insertSelective(Expend record);

    List<Expend> selectByExample(ExpendExample example);

    Expend selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Expend record, @Param("example") ExpendExample example);

    int updateByExample(@Param("record") Expend record, @Param("example") ExpendExample example);

    int updateByPrimaryKeySelective(Expend record);

    int updateByPrimaryKey(Expend record);
}