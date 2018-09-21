package com.dachen.health.enterprise;

import java.util.List;
import java.util.Map;

import com.dachen.health.commons.vo.User;

/**
 * 
 * @author pijingwei
 * @date 2015/8/26
 */
public interface IEnterpriseUserService {
	/**
	 * 根据userId获取企业员工信息
	 * @param user
	 * @return
	 */
	 Map<String,Object> getEnterpriseUserByUserId(User userId);
	
	
	/**
	 * 根据userId获取企业员工信息
	 * @param user
	 * @return
	 */
	Map<String,Object> getUserDurgCompanyByUserId(Integer userId);
	
	
	/**
	 * 根据企业id获取企业信息
	 * @param user
	 * @return
	 */
	Map<String,Object> getEnterpriseBasicInfoByUserId(String enterpriseId);
	
	
	List<Map<String,Object>> getUserOrganizationList(Integer userId);
	
	Map<String,Object> getEnterpriseOrganization(String enterpriseId,String orgId);
	
	
	
	List<Map<String,Object>> getUserRoleList(Integer userId, String bizId, Integer bizType);
	
	
	public Map<String,Object>  getRoleById(String id);
	
}
