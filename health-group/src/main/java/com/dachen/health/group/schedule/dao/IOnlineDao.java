package com.dachen.health.group.schedule.dao;

import java.util.List;
import java.util.Map;

import com.dachen.health.group.schedule.entity.param.OnlineClinicDate;
import com.dachen.health.group.schedule.entity.param.OnlineParam;
import com.dachen.health.group.schedule.entity.po.Online;
import com.dachen.health.group.schedule.entity.vo.OnlineVO;

/**
 * ProjectName： health-group<br>
 * ClassName： IOfflineDao<br>
 * Description： 下线门诊dao<br>
 * 
 * @author fanp
 * @createTime 2015年8月11日
 * @version 1.0.0
 */
public interface IOnlineDao {

    /**
     * </p>添加门诊坐诊信息,通过更新来排队存不存在</p>
     * 
     * @param po
     * @author fanp
     * @date 2015年8月11日
     */
    void add(Online po);

    /**
     * </p>按科室查找在线值班时间表</p>
     * 
     * @param deptIds
     * @return
     * @author fanp
     * @date 2015年8月17日
     */
    OnlineVO getAllByDept(List<String> deptIds);

    /**
     * </p>查找分配的医生</p>
     * @param doctorIds
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年8月26日
     */
    List<OnlineClinicDate> getOnlineByDoctorIds(List<Integer> doctorIds,String groupId);
    
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
    
    /**
     * </p>根据groupId和doctorId删除当前医生所有的值班信息</p>
     * 
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年8月17日
     */
    public void deleteAllByDoctorData(String groupId, Integer doctorId);
}
