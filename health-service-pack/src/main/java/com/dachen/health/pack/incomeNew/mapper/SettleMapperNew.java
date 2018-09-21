package com.dachen.health.pack.incomeNew.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.incomeNew.entity.param.SettleNewParam;
import com.dachen.health.pack.incomeNew.entity.po.SettleExample;
import com.dachen.health.pack.incomeNew.entity.po.SettleNew;
import com.dachen.health.pack.incomeNew.entity.vo.SettleDetailVO;
import com.dachen.health.pack.incomeNew.entity.vo.SysSettleVO;

public interface SettleMapperNew {
    int countByExample(SettleExample example);

    int deleteByExample(SettleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SettleNew record);

    int insertSelective(SettleNew record);

    List<SettleNew> selectByExample(SettleExample example);

    SettleNew selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SettleNew record, @Param("example") SettleExample example);

    int updateByExample(@Param("record") SettleNew record, @Param("example") SettleExample example);

    int updateByPrimaryKeySelective(SettleNew record);

    int updateByPrimaryKey(SettleNew record);
    
    /**
     * 按月份统计指定类型的待结算金额与已结算金额
     * @param param
     * @return
     */
    List<SysSettleVO> selectSettleYMListByType(SettleNewParam param);
    int selectSettleYMCountByType(SettleNewParam param);
    
    /**
     * 获取指定月份和类型查询结列表
     * @param param month 不能为空，objectType 不能为空
     * @return
     */
    List<SettleDetailVO> selectListByMonth(SettleNewParam param);
    int selectListByMonthCount(SettleNewParam param);
}