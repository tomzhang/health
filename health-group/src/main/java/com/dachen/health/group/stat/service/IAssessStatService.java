package com.dachen.health.group.stat.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.group.stat.entity.param.StatParam;

/**
 * ProjectName： health-group<br>
 * ClassName： IAssessStatService<br>
 * Description： 考核统计service<br>
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
public interface IAssessStatService {

    /**
     * </p>统计邀请医生数</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    PageVO inviteDoctor(StatParam param);
    
    /**
     * </p>统计添加患者数</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    PageVO addPatient(StatParam param);
    
    
}
