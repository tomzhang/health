package com.dachen.health.pack.income.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.health.pack.income.entity.vo.IncomeMonthVO;
import com.dachen.health.pack.income.mapper.IncomeMonthMapper;
import com.dachen.health.pack.income.service.IIncomeMonthService;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： IncomeServiceImpl<br>
 * Description： 医生收入service实现类<br>
 * 
 * @author fanp
 * @createTime 2015年8月18日
 * @version 1.0.0
 */
@Service
public class IncomeMonthServiceImpl implements IIncomeMonthService {

    @Autowired
    private IncomeMonthMapper incomeMonthMapper;

    /**
     * </p>获取医生收入按月统计</p>
     * 
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年8月18日
     */
    public List<IncomeMonthVO> getIncomeMonth(Integer doctorId) {
        return incomeMonthMapper.getIncomeMonth(doctorId);
    }

}
