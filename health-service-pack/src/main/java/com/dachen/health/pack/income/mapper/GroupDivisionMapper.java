package com.dachen.health.pack.income.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.income.entity.param.DoctorIncomeParam;
import com.dachen.health.pack.income.entity.po.GroupDivision;
import com.dachen.health.pack.income.entity.po.GroupDivisionExample;
import com.dachen.health.pack.income.entity.vo.DoctorIncomeVO;

public interface GroupDivisionMapper {
    int countByExample(GroupDivisionExample example);

    int deleteByExample(GroupDivisionExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(GroupDivision record);

    int insertSelective(GroupDivision record);

    List<GroupDivision> selectByExample(GroupDivisionExample example);

    GroupDivision selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") GroupDivision record, @Param("example") GroupDivisionExample example);

    int updateByExample(@Param("record") GroupDivision record, @Param("example") GroupDivisionExample example);

    int updateByPrimaryKeySelective(GroupDivision record);

    int updateByPrimaryKey(GroupDivision record);
    
    /**
     * 查询 settle相关的 income
     * @param settleId
     * @return
     *@author wangqiao
     *@date 2016年1月25日
     */
    List<GroupDivision> selectBySettleId(Integer settleId);
    
    
    /**
     * 查询集团分成明细
     * @param param  groupid，pageIndex，pageSize
     * @return
     *@author wangqiao
     *@date 2016年1月25日
     */
    List<DoctorIncomeVO> getGroupDivisionByGroupId(DoctorIncomeParam param);
    
    /**
     * 查询集团分成总记录数
     * @param param  groupid，pageIndex，pageSize
     * @return
     *@author wangqiao
     *@date 2016年1月25日
     */
    Integer countGroupDivisionByGroupId(DoctorIncomeParam param);
    
}