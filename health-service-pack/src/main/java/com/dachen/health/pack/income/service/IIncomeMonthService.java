package com.dachen.health.pack.income.service;

import java.util.List;

import com.dachen.health.pack.income.entity.vo.IncomeMonthVO;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： IIncomeService<br>
 * Description：医生收入service <br>
 * 
 * @author fanp
 * @createTime 2015年8月18日
 * @version 1.0.0
 */
public interface IIncomeMonthService {

    /**
     * </p>获取医生收入按月统计</p>
     * 
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年8月18日
     */
    List<IncomeMonthVO> getIncomeMonth(Integer doctorId);

}
