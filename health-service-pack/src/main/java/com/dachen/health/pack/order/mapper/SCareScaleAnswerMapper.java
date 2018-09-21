package com.dachen.health.pack.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.order.entity.po.SCareScaleAnswer;
import com.dachen.health.pack.order.entity.po.SCareScaleAnswerExample;

public interface SCareScaleAnswerMapper {
    int countByExample(SCareScaleAnswerExample example);

    int deleteByExample(SCareScaleAnswerExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SCareScaleAnswer record);

    int insertSelective(SCareScaleAnswer record);

    List<SCareScaleAnswer> selectByExample(SCareScaleAnswerExample example);

    SCareScaleAnswer selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SCareScaleAnswer record, @Param("example") SCareScaleAnswerExample example);

    int updateByExample(@Param("record") SCareScaleAnswer record, @Param("example") SCareScaleAnswerExample example);

    int updateByPrimaryKeySelective(SCareScaleAnswer record);

    int updateByPrimaryKey(SCareScaleAnswer record);
}