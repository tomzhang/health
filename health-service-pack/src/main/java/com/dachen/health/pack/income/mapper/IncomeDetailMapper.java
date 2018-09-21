package com.dachen.health.pack.income.mapper;

import java.util.List;

import com.dachen.health.pack.income.entity.param.IncomeParam;
import com.dachen.health.pack.income.entity.vo.IncomeDetailVO;

public interface IncomeDetailMapper {

    /**
     * </p>获取医生收入详细</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年8月18日
     */
    List<IncomeDetailVO> getIncomeDetails(IncomeParam param);
}