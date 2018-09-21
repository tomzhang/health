package com.dachen.health.pack.income.mapper;

import com.dachen.health.pack.income.entity.vo.IncomeVO;


/**
 * ProjectName： health-service-pack<br>
 * ClassName： IncomeMapper<br>
 * Description： 医生收入mapper<br>
 * @author fanp
 * @createTime 2015年8月18日
 * @version 1.0.0
 */
public interface IncomeMapper {
    
    /**
     * </p>获取医生收入</p>
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年8月18日
     */
    IncomeVO getIncome(Integer doctorId);
    
    /**
     *  </p>统计指定医生收入</p>
     * @param doctorId
     * @return
     * @author 张垠
     * @date 2015年8月23日
     */
    IncomeVO sumIncome(Integer doctorId);
    
}