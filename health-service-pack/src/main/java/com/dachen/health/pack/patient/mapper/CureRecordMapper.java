package com.dachen.health.pack.patient.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.patient.model.CureRecord;
import com.dachen.health.pack.patient.model.CureRecordExample;

public interface CureRecordMapper {
    int countByExample(CureRecordExample example);

    int deleteByExample(CureRecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CureRecord record);

    int insertSelective(CureRecord record);

    List<CureRecord> selectByExample(CureRecordExample example);

    CureRecord selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") CureRecord record, @Param("example") CureRecordExample example);

    int updateByExample(@Param("record") CureRecord record, @Param("example") CureRecordExample example);

    int updateByPrimaryKeySelective(CureRecord record);

    int updateByPrimaryKey(CureRecord record);
    
    List<CureRecord> getCommonDiseasesByDocId(Integer doctorId);
    
    /**
     * 查到所有需要帮助的并且医生已经录入录音的记录
     * @return
     */
    List<CureRecord> getListByCondition();
    
    
    List<CureRecord> pushMessageToDoctor(Integer doctorId);
    
    /**
     * 根据订单id去查询医生的
     * @param orderId
     * @return
     */
    List<CureRecord> getEndTime(CureRecord record);

    /**
     * 根据订单id，查询咨询结果
     * @param orderIds
     * @return
     */
    List<CureRecord> getByOrderIds(List<Integer> orderIds);
}