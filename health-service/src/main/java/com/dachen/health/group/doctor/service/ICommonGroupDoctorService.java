package com.dachen.health.group.doctor.service;

import java.util.List;
import java.util.Map;

import com.dachen.health.commons.vo.User;

/**
 * 
 * @author pijingwei
 * @date 2015/8/26
 */
public interface ICommonGroupDoctorService {

	
	/**
	 * 根据userId获取集团信息
	 * @param user
	 * @return
	 */
	User getGroupListByUserId(User user);
	
	/**
	 * </p>如果两个医生在同一个医生集团，则返回查看医生的联系方式和备注</p>
	 * @param userId 登录医生id
	 * @param doctorId 查看医生id
	 * @return {groupContact:联系方式,groupRemark:备注,groupSame:同一集团,hasGroup:属于集团}
	 * @author fanp
	 * @date 2015年8月27日
	 */
	Map<String,Object> getContactBySameGroup(Integer userId,Integer doctorId);
	
	boolean updateGroupDoctor(Integer doctorId,String deptName);
	
	String getGroupIdByUser(String uid);
	
	public String getGroupNameByGroupId(String groupId);
	
	/**
	 * 根据用户的id查询该用户加入了哪些集团
	 * @param uid
	 * @return
	 */
	List<String> getGroupListIdByUser(String uid);
	
	
	/**
	 * 
	 * @Title: getGroupListForDocGuide   
	 * @Description: 为导医设置医生集团信息
	 * @param: @param user
	 * @param: @return      
	 * @return: List<Map<String,Object>>      
	 * @throws
	 */
	public List<Map<String,Object>> getGroupListForDocGuide(User user);
	
	/**
	 * 获取被屏蔽集团的id
	 * @return
	 */
	List<String> getSkipGroupIds();
}
