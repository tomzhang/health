package com.dachen.health.pack.income.mapper;

import java.util.List;

import com.dachen.health.pack.income.entity.param.IncomeDetailsParam;
import com.dachen.health.pack.income.entity.po.IncomeDetails;
import com.dachen.health.pack.income.entity.vo.IncomeDetailsVO;
import com.dachen.health.pack.income.entity.vo.PageIncome;

public interface IncomeDetailsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IncomeDetailsParam record);

    int insertSelective(IncomeDetailsParam record);

    IncomeDetails selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeDetailsParam record);

    int updateByPrimaryKey(IncomeDetailsParam record);
    
    List<IncomeDetailsVO> getListByDoctorID(IncomeDetailsParam record);
    
    /**
     * 获取医生账户余额与未完成订单
     * @param param
     */
    List<IncomeDetailsVO> getDocIncomeList(IncomeDetailsParam param);
    
    int getDocIncomeCount(IncomeDetailsParam param);
    
    /**
     * 医生历史收入列表
     * @param param
     * @return
     */
    List<IncomeDetailsVO> getDocHisIncomeList(IncomeDetailsParam param);
    
    /**
     * 获取集团收入列表
     * @param param
     * @return
     */
    List<IncomeDetailsVO> getGroupIncomeList(IncomeDetailsParam param);
    int getGroupIncomeListCount(IncomeDetailsParam param);
    
    /**
     * 根据医生ID获取对应提提成
     * @param param
     * @return
     */
    List<IncomeDetailsVO> getUpDocIncome(IncomeDetailsParam param);
    
    /**
     * 集团内医生收入
     * @param param
     * @return
     */
    List<IncomeDetailsVO> getGroupDoctorIncomeList(IncomeDetailsParam param);
    Integer getGroupDoctorIncomeListCount(IncomeDetailsParam param);
    /**
     * 获取提成或订单收益明细
     * @param param
     * @return
     */
    List<IncomeDetailsVO> getOrderOrCommissionIncomeDetail(IncomeDetailsParam param);
    
    /**
     * 获取集团指定时间范围内不同医生的不同状态订单统计
     * @param param
     * @return
     */
    List<IncomeDetailsVO> getGroupDoctorIncomeByTime(IncomeDetailsParam param);
    
    /**
     * 根据年月统计指定集团订单总数与总集团抽成总金额
     * @param param
     * @return
     */
    List<PageIncome>  getGroupIncomeByYM(IncomeDetailsParam param);
    Integer getGroupIncomeByYMCount(IncomeDetailsParam param);
    
    Integer getUnFinishBalance(int doctorId);
}