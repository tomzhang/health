package com.dachen.health.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.health.base.dao.IBaseUserDao;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.util.StringUtil;


/**
 * ProjectName： health-service-base<br>
 * ClassName： BaseUserServiceImpl<br>
 * Description： 用户信息基础公共service<br>
 * @author fanp
 * @createTime 2015年8月17日
 * @version 1.0.0
 */
@Service
public class BaseUserServiceImpl implements IBaseUserService {

    @Autowired
    protected IBaseUserDao baseUserDao;

    /**
     * </p>获取医生真实信息</p>
     * 
     * @param userId
     * @return
     * @author fanp
     * @date 2015年8月17日
     */
    public BaseUserVO getUser(Integer userId){
        return baseUserDao.getUser(userId);
    }
    
    
    /**
     * </p>通过id查询用户</p>
     * @param userIds
     * @return
     * @author fanp
     * @date 2015年8月19日
     */
    public List<BaseUserVO> getByIds(Integer[] userIds){
        return baseUserDao.getByIds(userIds);
    }
    
    /**
     * </p>通过id查询用户</p>
     * @param userIds
     * @return
     * @author fanp
     * @date 2015年8月19日
     */
    public List<BaseUserVO> getByIds(Integer[] userIds, Integer status){
        return baseUserDao.getByIds(userIds, status);
    }
    
    /**
     * </p>查找集团下所有医生</p>
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年9月1日
     */
    public List<Integer> getDoctorIdByGroup(String groupId){
        return baseUserDao.getDoctorIdByGroup(groupId);
    }
    
    /**
     * </p>查找集团下所有医生</p>
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年9月1日
     */
    public List<Integer> getDoctorIdByGroup(String groupId,String[] statuses,String keyword){
        return baseUserDao.getDoctorIdByGroup(groupId,statuses,keyword);
    }
    public List<Integer> getDoctorIdByGroup(String groupId,String[] statuses){
    	return baseUserDao.getDoctorIdByGroup(groupId,statuses,null);
    }
    
    /**
     * </p>设置医患关系</p>
     * 
     * @param doctorId
     * @param patientId
     * @author fanp
     * @date 2015年9月9日
     */
    public void setDoctorPatient(Integer doctorId, Integer patientId){
        baseUserDao.setDoctorPatient(doctorId, patientId);
    }

    /**
     * </p>设置医患关系</p>
     *
     * @param doctorId
     * @param patientId
     * @author 傅永德
     * @date 2016年12月9日
     */
    public void addDoctorPatient(Integer doctorId, Integer patientId, Integer userId){
        baseUserDao.addDoctorPatient(doctorId, patientId, userId, null, null);
    }

    /**
     * </p>设置医患关系</p>
     *
     * @param doctorId
     * @param patientId
     * @author 傅永德
     * @date 2016年12月9日
     */
    public void addDoctorPatient(Integer doctorId, Integer patientId, Integer userId, String remarkName, String remark){
        baseUserDao.addDoctorPatient(doctorId, patientId, userId, remarkName, remark);
    }
    
    /**
     * </p>通过用户id查找集团信息</p>
     * @param userId
     * @return
     * @author fanp
     * @date 2015年9月15日
     */
    public BaseUserVO getGroupById(Integer userId, String groupId){
    	if (StringUtil.isNotBlank(groupId)) {
    		return baseUserDao.getGroupById(userId, groupId);
    	}
    	return baseUserDao.getGroupByUserId(userId);
    }
    
    /**
     * 通过用户id、集团id查找组织信息
     * @param userId
     * @param groupId
     * @return
     */
    public BaseUserVO getDepartment(Integer userId, String groupId) {
    	return baseUserDao.getDepartment(userId, groupId);
    }


	@Override
	public BaseUserVO getUserByTelephoneAndType(Integer type,String telephone) {
		
		return baseUserDao.getUserByTelephoneAndType(type, telephone);
	}


	@Override
	public BaseUserVO getUserByDoctorNum(String doctorNum) {
		
		return baseUserDao.getUserByDoctorNum(doctorNum);
	}


	@Override
	public List<BaseUserVO> getTopLevelByIds(Integer[] userIds) {
		 return baseUserDao.getTopLevelByIds(userIds);
	}


}
