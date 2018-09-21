package com.dachen.health.base.dao;

import java.util.List;

import com.dachen.health.base.entity.vo.BaseUserVO;

/**
 * ProjectName： health-service-base<br>
 * ClassName： IBaseUserDao<br>
 * Description： 用户信息基础公共dao <br>
 *
 * @author fanp
 * @createTime 2015年8月17日
 * @version 1.0.0
 */
public interface IBaseUserDao {

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
     * </p>获取医生真实信息</p>
     *
     * @param telephone
     * @return
     * @author zhangyin
     * @date 2015年10月14日
     */
    BaseUserVO getUserByTelephoneAndType(Integer type,String telephone);


    BaseUserVO getUserByDoctorNum(String doctorNum);

    /**
     * </p>通过id查询用户</p>
     * @param userIds
     * @return
     * @author fanp
     * @date 2015年8月19日
     */
    List<BaseUserVO> getByIds(Integer[] userIds);

    public List<BaseUserVO> getByIds(Integer[] userIds,Integer status);
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

    /**
     * 添加医患关系
     *
     * @param doctorId  医生id
     * @param patientId 患者id
     * @param userId    患者所在的用户的id
     */
    void addDoctorPatient(Integer doctorId, Integer patientId, Integer userId, String remarkName, String remark);

    /**
     * </p>通过用户id查找集团信息</p>
     * @param userId
     * @return
     * @author fanp
     * @date 2015年9月15日
     */
    BaseUserVO getGroupByUserId(Integer userId);

    BaseUserVO getGroupById(Integer userId, String groupId);

    /**
     * 通过用户id、集团id查找组织信息
     * @param userId
     * @param groupId
     * @return
     */
    BaseUserVO getDepartment(Integer userId, String groupId);

    /**
     * 查找集团下的患者
     * @param groupId
     * @return
     */
    List<Integer> getUserPatientIdByGroup(String groupId);

    /**
     * 获取集团信息
     * @param userId
     * @return
     */
    public BaseUserVO getGroupMsgByUserId(Integer userId) ;

    /**
     * 查询多个医生相关的患者id
     * @author wangqiao
     * @date 2016年3月2日
     * @param doctorIdList
     * @return
     */
    public List<Integer> getUserPatientIdByDoctorIds(List<Integer> doctorIdList);

    /**
     * 查询某个医生相关的患者id
     * @author wangqiao
     * @date 2016年3月2日
     * @param doctorId
     * @return
     */
    public List<Integer> getUserPatientIdByDoctorId(Integer doctorId);

    /**
     * 过滤掉未激活的患者账号id
     * @author wangqiao
     * @date 2016年3月2日
     * @param PatientIdList
     * @return
     */
    public List<Integer> filterInactivePatientIds(List<Integer> PatientIdList);

    /**
     *
     * @param userIds
     * @return
     */
	List<BaseUserVO> getTopLevelByIds(Integer[] userIds);

}
