package com.dachen.health.pack.stat.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.stat.entity.param.PackStatParam;

/**
 * ProjectName： health-group<br>
 * ClassName： IAssessStatService<br>
 * Description： 考核统计service<br>
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
public interface IPackStatService {

    /**
     * </p>统计订单金额数</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    PageVO orderMoney(PackStatParam param);
    
}
