package com.dachen.health.enterprise.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.enterprise.IEnterpriseUserService;
import com.dachen.health.friend.dao.IFriendReqDao;
import com.dachen.util.StringUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Service
public class EnterpriseUserServiceImpl extends NoSqlRepository implements IEnterpriseUserService {
   
	Logger logger = LoggerFactory.getLogger(EnterpriseUserServiceImpl.class);
	
	@Autowired
	private UserManager userManager;

	@Autowired
	private IFriendReqDao friendReqDao;
	
	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;
	
	//企业信息key
	public  static final String  ENTERPRISE_MSG_KEY ="DACHEN_ENTERPRISE_MSG_KEY";
	//企业角色key
	public  static final String  ENTERPRISE_ROLE_MSG_KEY ="DACHEN_ENTERPRISE_ROLE_MSG_KEY";
	
	@Override
	public Map<String,Object> getEnterpriseUserByUserId(User user) {
		long startTime = System.currentTimeMillis();
		long endTwo =0;
		Map<String,Object> enterpriseUser = new HashMap<String, Object>();
		if (null != user) {
			Integer userId = user.getUserId();
			enterpriseUser.put("userId",userId);
			String enterpriseId = null;
			
			 Map<String, Object> userCompany = getUserDurgCompanyByUserId(userId);
			if (null == userCompany) {
				throw new ServiceException("用户企业信息不存在！");
			} else {
				Object enterpriseObj = userCompany.get("enterpriseId");
				if(null!=enterpriseObj)
				{	
					enterpriseId = enterpriseObj.toString();
				}
				enterpriseUser.putAll(userCompany);
			}
			 Map<String, Object> enterprise = getEnterpriseBasicInfoByUserId(enterpriseId);
//			Map<String, Object> enterprise = getEnterpriseMsgCashInfo( enterpriseId);
			long endOne = System.currentTimeMillis();
			logger.info("execute getEnterpriseMsgCashInfo---------"+(endOne-startTime));
//			System.out.println("execute getEnterpriseMsgCashInfo---------"+(endOne-startTime));
			if (null != enterprise&&enterprise.size()>0) {
				enterpriseUser.putAll(enterprise);
			}
			// 获取用户信息
//			User user = userManager.getUser(userId);
			enterpriseUser.put("telephone", user.getTelephone());
			enterpriseUser.put("name", user.getName());
			enterpriseUser.put("remarks", user.getRemarks());
			enterpriseUser.put("headPicFileName", user.getHeadPicFileName());
			// 获取用户组织关系
			List<Map<String, Object>> userOrganizationList = getUserOrganizationList(userId);
			 endTwo = System.currentTimeMillis();
			logger.info("execute userOrganizationList---------"+(endTwo-endOne));
//			System.out.println("execute userOrganizationList---------"+(endTwo-endOne));
//			if (null != userOrganizationList && userOrganizationList.size() > 0) {
//				for (Map<String, Object> userOrganization : userOrganizationList) {
//					enterpriseUser.putAll(userOrganization);
//					String orgId = userOrganization.get("orgId").toString();
//					Map<String, Object> organization= getEnterpriseOrganization(enterpriseId,orgId);
//					if (null != organization) {
//						enterpriseUser.putAll(organization);
//						List<Map<String, Object>> ss = getUserRoleList(userId, enterpriseId,1);
//						enterpriseUser.put("role", ss);
//						break;
//					}
//				}
//			}
			if (null != userOrganizationList && userOrganizationList.size() > 0) {
				 Map<String, Object> userOrganization =userOrganizationList.get(0);
					enterpriseUser.putAll(userOrganization);
					String orgId = userOrganization.get("orgId").toString();
					Map<String, Object> organization= getEnterpriseOrganization(enterpriseId,orgId);
					if (null != organization) {
						enterpriseUser.putAll(organization);
						List<Map<String, Object>> ss = getUserRoleList(userId, enterpriseId,1);
						enterpriseUser.put("role", ss);
					}
			}
		}
		long endTh = System.currentTimeMillis();
		logger.info("getEnterpriseOrganization---------"+(endTh-endTwo));
		System.out.println("getEnterpriseOrganization---------"+(endTh-endTwo));
		return enterpriseUser;
	
	}
	

	@Override
	public Map<String, Object> getUserDurgCompanyByUserId(Integer userId) {
		Map<String, Object> result = new HashMap<String, Object>();
		BasicDBObject query = new BasicDBObject();
		query.put("userId", userId);
		DBCollection collection = dsForRW.getDB().getCollection(
				"d_user_durgcompany");
		DBCursor cursor = collection.find(query);
		if (cursor.hasNext()) {// while  ---->if
			DBObject object = cursor.next();
			Object id = object.get("enterpriseId");
			if (null != id) {
				result.put("enterpriseId", id.toString());
			}
			Object status = object.get("status");
			if (null != status) {
				result.put("status", status);
			}
		}
		closeDBCursor(cursor);
		return result;
	}

	@Override
	public Map<String, Object> getEnterpriseBasicInfoByUserId(
			String enterpriseId) {

		Map<String, Object> result = new HashMap<String, Object>();
		BasicDBObject query = new BasicDBObject();
		query.put("hippoId", enterpriseId);
		DBCollection collection = dsForRW.getDB().getCollection(
				"t_enterprise_basicinfo");
		DBCursor cursor = collection.find(query);
		while (cursor.hasNext()) {
			DBObject object = cursor.next();
			Object name = object.get("name");
			if (null != name) {
				result.put("companyName", name);
			}
		}
		closeDBCursor(cursor);
		return result;
	}

	@Override
	public List<Map<String, Object>> getUserOrganizationList(Integer userId) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDBObject query = new BasicDBObject();
		query.put("userId", userId);
		DBCollection collection = dsForRW.getDB().getCollection(
				"d_user_org");
		DBCursor cursor = collection.find(query);
		while (cursor.hasNext()) {
			Map<String, Object> result = new HashMap<String, Object>();
			DBObject object = cursor.next();
			Object orgId = object.get("orgId");
			if (null != orgId) {
				result.put("orgId", orgId);
			}
			Object title = object.get("title");
			if (null != title) {
				result.put("position", title);
			}
			list.add(result);
		}
		closeDBCursor(cursor);
      return list;
	}

	@Override
	public Map<String, Object> getEnterpriseOrganization(String enterpriseId,
			String orgId) {

		Map<String, Object> result = new HashMap<String, Object>();
		BasicDBObject query = new BasicDBObject();
		query.put("enterpriseId", enterpriseId);
		ObjectId id = new ObjectId(orgId);
		query.put("_id",id);
		DBCollection collection = dsForRW.getDB().getCollection(
				"d_org");
		DBCursor cursor = collection.find(query);
		while (cursor.hasNext()) {
			DBObject object = cursor.next();
			Object name = object.get("name");
			if (null != name) {
				result.put("department", name);
			}
			result.put("id", orgId);
		}
		closeDBCursor(cursor);
		return result;
	
	}

	@Override
	public List<Map<String, Object>> getUserRoleList(Integer userId,
			String bizId, Integer bizType) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDBObject query = new BasicDBObject();
		query.put("userId", userId);
		query.put("bizId", bizId);
		query.put("bizType", bizType);
		DBCollection collection = dsForRW.getDB().getCollection(
				"d_user_role");
		DBCursor cursor = collection.find(query);
		while (cursor.hasNext()) {
			Map<String, Object> result = new HashMap<String, Object>();
			DBObject object = cursor.next();
			Object roleId = object.get("roleId");
			if (null != roleId) {
				result=getRoleById(roleId.toString()) ;
//				result=getEnterpriseRoleMsgCashInfo(roleId.toString()) ;
				list.add(result);
			}
		}
		closeDBCursor(cursor);
      return list;
	
	}

	@Override
	public Map<String, Object> getRoleById(String id) {
		// TODO Auto-generated method stub d_role
		Map<String, Object> result = new HashMap<String, Object>();
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		DBCollection collection = dsForRW.getDB().getCollection(
				"d_role");
		DBCursor cursor = collection.find(query);
		while (cursor.hasNext()) {
			DBObject object = cursor.next();
			Object key = object.get("name");
			if (null != key) {
				result.put("name", key);
			}
			result.put("id", id);
		}
		closeDBCursor(cursor);
		return result;
	
	
	}
	
	/**
	 * 获取企业名称
	 * @param enterpriseId
	 * @return
	 */
	private Map<String, Object> getEnterpriseMsgCashInfo(String  enterpriseId) {

		Map<String, Object>  result  = new HashMap<String, Object>();
		
		Map<String, String>  enterpriseCash = jedisTemplate.hgetAll(ENTERPRISE_MSG_KEY);
		if(null == enterpriseCash || enterpriseCash.size()==0)
		{	
			enterpriseCash = new HashMap<String, String>();
			Map<String, Object> 	enterpriseMapTemp = getEnterpriseBasicInfoByUserId(enterpriseId);
			if(null!=enterpriseMapTemp && enterpriseMapTemp.size()>0)
			{	
				enterpriseCash.put(enterpriseId, enterpriseMapTemp.get("companyName").toString());
				jedisTemplate.hmset(ENTERPRISE_MSG_KEY, enterpriseCash);
				result.putAll(enterpriseMapTemp);
			}
		}
		else
		{	
			String  enterName =	enterpriseCash.get(enterpriseId);
			if(StringUtils.isNotEmpty(enterName))
			{	
				result.put("hippoId", enterpriseId);
				result.put("companyName", enterName);
			}
			else
			{	
				Map<String, Object> 	enterpriseMapTemp = getEnterpriseBasicInfoByUserId(enterpriseId);
				if(null!=enterpriseMapTemp && enterpriseMapTemp.size()>0)
				{	
					enterpriseCash.put(enterpriseId, enterpriseMapTemp.get("companyName").toString());
					jedisTemplate.hmset(ENTERPRISE_MSG_KEY, enterpriseCash);
					result.putAll(enterpriseMapTemp);
				}
			}
		}
		return result;
	
	}
	
	/**
	 * 获取企业角色缓存信息
	 * @param roleId
	 * @return
	 */
	public Map<String, Object> getEnterpriseRoleMsgCashInfo(String  roleId) {
		Map<String, Object>  roleMap  = new HashMap<String, Object>();
		
		Map<String, String>  enterpriseCash = jedisTemplate.hgetAll(ENTERPRISE_ROLE_MSG_KEY);
		if(null == enterpriseCash || enterpriseCash.size()==0)
		{	
			//查询所有的角色信息
			Map<String, String> enterpriseMap = getRoleList();
			if(null!=enterpriseMap && enterpriseMap.size()>0)
			{	
				jedisTemplate.hmset(ENTERPRISE_ROLE_MSG_KEY, enterpriseMap);
				roleMap.put("id", roleId);
				roleMap.put("name", enterpriseMap.get(roleId));
			}
		}
		else
		{	
			String  name = enterpriseCash.get(roleId);
			roleMap.put("id", roleId);
			roleMap.put("name", name);
		}
		
		return roleMap;
	}

	/**
	 * 获取所有的用户角色信息
	 * @return
	 */
	private Map<String, String> getRoleList() {
		Map<String, String> result = new HashMap<String, String>();
		BasicDBObject query = new BasicDBObject();
		DBCollection collection = dsForRW.getDB().getCollection("d_role");
		DBCursor cursor = collection.find(query);
		while (cursor.hasNext()) {
			DBObject object = cursor.next();
			Object name = object.get("name");
			Object id = object.get("_id");
			if (null != name && null != id) {
				result.put(id.toString(), name.toString());
			}
		}
		closeDBCursor(cursor);
		return result;
	}
	
	/**
	 * 关闭资源
	 * @param cursor
	 */
	private void  closeDBCursor(DBCursor cursor)
	{
		try {
			if(null!=cursor)
			{	
				cursor.close();
			}
		} catch (Exception e) {
			logger.error("close  DBCursor  fail!");
		}
	}
}
