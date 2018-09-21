package com.dachen.health.pack.patient.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.patient.model.CallResult;
import com.dachen.health.pack.patient.model.CallResultExample;

public interface CallResultMapper {
    int countByExample(CallResultExample example);

    int deleteByExample(CallResultExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CallResult record);

    int insertSelective(CallResult record);

    List<CallResult> selectByExample(CallResultExample example);
    /**
     * 查询当前会议已经结束的订单（判断标准stoptime不为空） 
     * @return
     */
    List<CallResult> selectByTime();

    CallResult selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") CallResult record, @Param("example") CallResultExample example);

    int updateByExample(@Param("record") CallResult record, @Param("example") CallResultExample example);

    int updateByPrimaryKeySelective(CallResult record);

    int updateByPrimaryKey(CallResult record);
    
    //通过订单id查询通话信息
    List<CallResult> getAllCallResultByOrderId(Integer orderId);
}