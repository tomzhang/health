package com.dachen.health.pack.income.mapper;

import java.util.List;

import com.dachen.health.pack.income.entity.vo.IncomeMonthVO;

public interface IncomeMonthMapper {

    /**
     * </p>获取医生月收入统计</p>
     * 
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年8月18日
     */
    List<IncomeMonthVO> getIncomeMonth(Integer doctorId);
}