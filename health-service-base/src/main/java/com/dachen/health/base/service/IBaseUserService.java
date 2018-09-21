package com.dachen.health.base.service;

import java.util.List;

import com.dachen.health.base.entity.vo.BaseUserVO;

/**
 * ProjectName： health-service-base<br>
 * ClassName： IBaseUserService<br>
 * Description： 获取用户信息基础公共service<br>
 * 
 * @author fanp
 * @createTime 2015年8月17日
 * @version 1.0.0
 */
public interface IBaseUserService {

    /**
     * </p>获取医生真实信息</p>
     * 
     * @param userId
     * @return
     * @author fanp
     * @date 2015年8月17日
     */
    BaseUserVO getUser(Integer userId);
    
    
    /**
     * 根据医生号来醒找医生
     * @param doctorNum
     * @return
     */
    BaseUserVO getUserByDoctorNum(String doctorNum);
    
    
    /**
     * 根据电话查找医生信息
     * @param telephone
     * @return
     * @author zhangyin
     * @date 2015年10月14日
     */
    BaseUserVO getUserByTelephoneAndType(Integer type,String telephone) ;
    
    /**
     * </p>通过id查询用户</p>
     * @param userIds
     * @return
     * @author fanp
     * @date 2015年8月19日
     */
    List<BaseUserVO> getByIds(Integer[] userIds);
    
    /**
     * </p>通过id查询用户</p>
     * @param userIds
     * @return
     * @author fanp
     * @date 2015年8月19日
     */
    List<BaseUserVO> getByIds(Integer[] userIds, Integer status);
    
    /**
     * </p>查找集团下所有医生</p>
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年9月1日
     */
    List<Integer> getDoctorIdByGroup(String groupId);
    
    /**
     * </p>查找集团下所有医生</p>
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年9月1日
     */
    List<Integer> getDoctorIdByGroup(String groupId,String[] statuses,String keyword);
    List<Integer> getDoctorIdByGroup(String groupId,String[] statuses);
    
    /**
     * </p>设置医患关系</p>
     * 
     * @param doctorId
     * @param patientId
     * @author fanp
     * @date 2015年9月9日
     */
    void setDoctorPatient(Integer doctorId, Integer patientId);

    void addDoctorPatient(Integer doctorid, Integer patientId, Integer userId);

    void addDoctorPatient(Integer doctorid, Integer patientId, Integer userId, String remarkName, String remark);
    
    /**
     * </p>通过用户id查找集团信息</p>
     * @param userId
     * @return
     * @author fanp
     * @date 2015年9月15日
     */
    BaseUserVO getGroupById(Integer userId, String groupId);
    
    /**
     * 通过用户id、集团id查找组织信息
     * @param userId
     * @param groupId
     * @return
     */
    BaseUserVO getDepartment(Integer userId, String groupId);


    List<BaseUserVO> getTopLevelByIds(Integer[] array);
    
}
