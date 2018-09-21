package com.dachen.health.pack.income.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.income.entity.param.DoctorIncomeParam;
import com.dachen.health.pack.income.entity.po.DoctorIncome;
import com.dachen.health.pack.income.entity.po.DoctorIncomeExample;
import com.dachen.health.pack.income.entity.vo.DoctorIncomeVO;

public interface DoctorIncomeMapper {
    int countByExample(DoctorIncomeExample example);

    int deleteByExample(DoctorIncomeExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(DoctorIncome record);

    int insertSelective(DoctorIncome record);

    List<DoctorIncome> selectByExample(DoctorIncomeExample example);

    DoctorIncome selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") DoctorIncome record, @Param("example") DoctorIncomeExample example);

    int updateByExample(@Param("record") DoctorIncome record, @Param("example") DoctorIncomeExample example);

    int updateByPrimaryKeySelective(DoctorIncome record);

    int updateByPrimaryKey(DoctorIncome record);
    
    /**
     * 查询 settle相关的 income
     * @param settleId
     * @return
     *@author wangqiao
     *@date 2016年1月25日
     */
    List<DoctorIncome> selectBySettleId(Integer settleId);
    
    /**
     * 读取集团下所属医生 的收入汇总信息 
     * @param param
     * @return
     *@author wangqiao
     *@date 2016年1月25日
     */
    List<DoctorIncomeVO> getTotalDoctorIncomeByGroupId(DoctorIncomeParam param);
    
    /**
     * 读取集团下所属医生 的收入汇总信息(查询总数)
     * @param param
     * @return
     *@author wangqiao
     *@date 2016年1月25日
     */
    Integer countTotalDoctorIncomeByGroupId(DoctorIncomeParam param);
    
}