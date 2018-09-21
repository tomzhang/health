package com.dachen.health.pack.incomeNew.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.incomeNew.entity.param.IncomelogParam;
import com.dachen.health.pack.incomeNew.entity.po.Incomelog;
import com.dachen.health.pack.incomeNew.entity.po.IncomelogExample;
import com.dachen.health.pack.incomeNew.entity.vo.BaseDetailVO;
import com.dachen.health.pack.incomeNew.entity.vo.DoctorMoneyVO;

public interface IncomelogMapper {
    int countByExample(IncomelogExample example);

    int deleteByExample(IncomelogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Incomelog record);

    int insertSelective(Incomelog record);

    List<Incomelog> selectByExample(IncomelogExample example);

    Incomelog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Incomelog record, @Param("example") IncomelogExample example);

    int updateByExample(@Param("record") Incomelog record, @Param("example") IncomelogExample example);

    int updateByPrimaryKeySelective(Incomelog record);

    int updateByPrimaryKey(Incomelog record);
    /**
     * 总收入与笔数
     * @param param
     * @return
     */
    DoctorMoneyVO getTotalMoneyAndCountIncomByParam(IncomelogParam param);
    /**
     * 账户余额和对应笔数
     * @param param
     * @return
     */
    DoctorMoneyVO getBalanceMoneyAndCountIncomByParam(IncomelogParam param);
    /**
     * 未完成订间金额和笔数
     * @param param
     * @return
     */
    DoctorMoneyVO getUnFinishedMoneyAndCountByParam(IncomelogParam param);
    
    
    
    /**
     * 获取账户余额
     * @param param 预留参数
     * @return
     */
    List<BaseDetailVO>  getBalanceDetail(IncomelogParam param);
    int getBalanceDetailCount(IncomelogParam param);
    /**
     * 按年月获取对应列收入列表
     * @param param
     * @return
     */
    List<DoctorMoneyVO> getTotalMoneyYMList(IncomelogParam param);
    int getTotalMoneyYMListCount(IncomelogParam param);
    
    /**
     * 按业务类型和时间排序查询指定时间内的明细列表
     * @param param
     * @return
     */
    List<BaseDetailVO> getTotalMoneyYMDetail(IncomelogParam param);
    int getTotalMoneyYMDetailCount(IncomelogParam param);
    /**
     * 多条件查询指定月份和集团内的贡献收入列表
     * @return
     */
    List<BaseDetailVO> getMoreConditionMMList(IncomelogParam param);
    int getMoreConditionMMCount(IncomelogParam param);
    
    /**
     * 查询医生未完成订单
     * @param param
     * @return
     */
    List<BaseDetailVO> getUnFinishedMoneyList(IncomelogParam param);
    int getUnFinishedMoneyCount(IncomelogParam param);
    
}