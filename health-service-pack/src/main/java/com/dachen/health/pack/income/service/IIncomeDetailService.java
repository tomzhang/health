package com.dachen.health.pack.income.service;

import java.util.List;

import com.dachen.health.pack.income.entity.param.IncomeParam;
import com.dachen.health.pack.income.entity.vo.IncomeDetailVO;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： IIncomeService<br>
 * Description：医生收入service <br>
 * 
 * @author fanp
 * @createTime 2015年8月18日
 * @version 1.0.0
 */
public interface IIncomeDetailService {

    /**
     * </p>获取医生收入明细</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年8月18日
     */
    List<IncomeDetailVO> getIncomeDetail(IncomeParam param);

}
