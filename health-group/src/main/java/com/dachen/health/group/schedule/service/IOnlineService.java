package com.dachen.health.group.schedule.service;

import java.util.Map;

import com.dachen.health.group.schedule.entity.param.OnlineParam;
import com.dachen.health.group.schedule.entity.vo.OnlineVO;

/**
 * ProjectName： health-group<br>
 * ClassName： IOfflineService<br>
 * Description：线下门诊service <br>
 * 
 * @author fanp
 * @createTime 2015年8月11日
 * @version 1.0.0
 */
public interface IOnlineService {

    /**
     * </p>添加在线值班信息</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月11日
     */
    void add(OnlineParam param);

    /**
     * </p>按科室查找在线值班时间表</p>
     * 
     * @param doctorId
     * @return List<OfflineVO> Set<hospital>
     * @author fanp
     * @date 2015年8月11日
     */
    OnlineVO getAllByDept(String deptId);

    /**
     * </p>按医生查找在线值班时间表</p>
     * 
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年8月17日
     */
    Map<String, Object> getAllByDoctor(Integer doctorId);
    
    /**
     * </p>删除在线值班信息</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月13日
     */
    void delete(OnlineParam param);
}
