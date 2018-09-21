package com.dachen.health.group.doctor.service.impl;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.UserEnum.UserHospitalStatus;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.doctor.service.ICommonGroupDoctorService;
import com.dachen.im.server.enums.GroupTypeEnum;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommonGroupDoctorServiceImpl extends NoSqlRepository implements ICommonGroupDoctorService {

	public List<Map<String,Object>> getGroupListForDocGuide(User user){
		List<Map<String,Object>> groupList=new ArrayList<Map<String,Object>>();
		DBObject q=new BasicDBObject();
		q.put("_id", new ObjectId(user.getDoctorGuider().getGroupId()));
		DBCursor gCursor = dsForRW.getDB().getCollection("c_group").find(q);
		while (gCursor.hasNext()) {
			Map<String, Object> group = new HashMap<String, Object>();
			DBObject gObj = gCursor.next();
			group.put("id", MongodbUtil.getString(gObj, "_id"));
			group.put("name", MongodbUtil.getString(gObj, "name"));
			group.put("logoUrl", MongodbUtil.getString(gObj, "logoUrl"));
			groupList.add(group);
		}
		return groupList;
	}

	public User getGroupListByUserId(User user) {
		
		// 读取主集团信息
		String mainGroupId = getMainGroupId(user.getUserId());
		
		// 读取集团信息
		List<Map<String, Object>> groupList = getGroupList(user.getUserId(),"group");

		//读取医院信息
		List<Map<String, Object>> hospitalList = getGroupList(user.getUserId(),"hospital");

		//读取科室信息
		List<Map<String, Object>> deptList = getGroupList(user.getUserId(), "dept");
		
		user.setGroupList(groupList);
		user.setHospitalList(hospitalList);
		user.setDeptList(deptList);
		user.setHospitalStatus(hospitalList.size()>0?UserHospitalStatus.join.getIndex():UserHospitalStatus.unjoin.getIndex());

		//登录的医生集团id, 有主集团就用主集团，没有主集团就随便用个兼职集团
		if (StringUtils.isEmpty(user.getLoginGroupId())) {
			if (!StringUtils.isEmpty(mainGroupId)) {
				user.setLoginGroupId(mainGroupId);
			} else if (groupList.size() > 0) {
				Map<String, Object> group = groupList.get(0);
				user.setLoginGroupId((String)group.get("id"));
			}
		}

		addPlatformInfo(user);

		return user;
	}

	/**
	 * 通过用户id查询用户所有的集团信息（主集团排序优先，然后按加入时间排序）
	 * @author wangqiao
	 * @date 2016年3月26日
	 * @param doctorId
	 * @return
	 */
	private List<Map<String, Object>> getGroupList(Integer doctorId,String type) {
		List<Map<String, Object>> groupList = new ArrayList<Map<String, Object>>();

		DBObject gdQuery = new BasicDBObject();
		gdQuery.put("doctorId", doctorId);
		gdQuery.put("status", "C");
		gdQuery.put("type", type);
		// 添加按加入时间排序
		DBObject orderBy = new BasicDBObject();
		orderBy.put("creatorDate", 1);

		DBCursor gdCursor = dsForRW.getDB().getCollection("c_group_doctor").find(gdQuery).sort(orderBy);

		while (gdCursor.hasNext()) {
			Map<String, Object> group = new HashMap<String, Object>();

			DBObject gdObj = gdCursor.next();

			Map<String, Object> groupUser = new HashMap<String, Object>();
			groupUser.put("doctorId", MongodbUtil.getInteger(gdObj, "doctorId"));
			groupUser.put("remarks", MongodbUtil.getString(gdObj, "remarks"));
			groupUser.put("contactWay", MongodbUtil.getString(gdObj, "contactWay"));
			boolean isMain = false;
			if (gdObj.get("isMain") != null) {
				isMain = (boolean) gdObj.get("isMain");
			}

			groupUser.put("isMain", isMain);
			groupUser.put("onLineState", gdObj.get("onLineState"));
			groupUser.put("taskDuration", gdObj.get("taskDuration"));
			groupUser.put("dutyDuration", gdObj.get("dutyDuration"));
			groupUser.put("status", gdObj.get("status"));

			group.put("groupUser", groupUser);
			String groupId = MongodbUtil.getString(gdObj, "groupId");
			
			DBObject obj = dsForRW.getDB().getCollection("c_group")
					.findOne(new BasicDBObject("_id", new ObjectId(groupId)));
			if (obj != null) {
				
				//TODO 添加对已经屏蔽的集团的过滤
				
				group.put("id", MongodbUtil.getString(obj, "_id"));
				group.put("groupId", MongodbUtil.getString(obj, "_id"));
				// group.put("groupStatus", "P");
				group.put("name", MongodbUtil.getString(obj, "name"));
				int groupCert = 0;
				if(MongodbUtil.getString(obj, "certStatus").equals("P")){
					groupCert = 1;
				}
				group.put("groupCert", groupCert);

				DBObject config = (BasicDBObject) obj.get("config");
				if (config != null) {
					group.put("hasPermission",
							config.get("memberInvite") == null ? false : (Boolean) config.get("memberInvite"));
				}
				String skip = MongodbUtil.getString(obj, "skip");
				group.put("skip", StringUtils.isBlank(skip) ? "N" : skip);
				group.put("introduction", MongodbUtil.getString(obj, "introduction"));
				group.put("logoUrl", MongodbUtil.getString(obj, "logoUrl"));
			}

			//如果是医院，需要查询 当前用户是否是医院的管理员
			if("hospital".equals(type)){
				DBObject query = new BasicDBObject();
				query.put("doctorId", doctorId);
				query.put("objectId", groupId);
				query.put("status", "C");
				List<DBObject> groupUserList= dsForRW.getDB().getCollection("c_group_user").find(query).toArray();
				if(groupUserList != null && groupUserList.size() >0){
					DBObject groupUserDBObject = groupUserList.get(0);
					if(groupUserDBObject != null){
						group.put("manage", true);
						group.put("joinDate", MongodbUtil.getLong(groupUserDBObject, "updatorDate"));
					}else{
						group.put("manage", false);
					}	
				}else{
					group.put("manage", false);
				}
			}
			
			
			//查询集团部门信息
			DBObject query = new BasicDBObject();
			query.put("doctorId", doctorId);
			query.put("groupId", groupId);
			DBCursor grosor = dsForRW.getDB().getCollection("c_department_doctor").find(query);
			BasicDBList departIds = new BasicDBList();
			while (grosor.hasNext()) {// 通过doctorId和groupId查询目前只有一条数据
				DBObject gbj = grosor.next();
				departIds.add(new ObjectId(gbj.get("departmentId").toString()));
			}

			DBCursor dorsor = dsForRW.getDB().getCollection("c_department")
					.find(new BasicDBObject("_id", new BasicDBObject("$in", departIds)));
			List<Map<String, Object>> departmentList = new ArrayList<Map<String, Object>>();
			while (dorsor.hasNext()) {
				DBObject dbj = dorsor.next();
				Map<String, Object> department = new HashMap<String, Object>();
				department.put("id", dbj.get("_id").toString());
				department.put("name", dbj.get("name").toString());
				department.put("groupId", dbj.get("groupId").toString());
				departmentList.add(department);
			}
			group.put("departmentList", departmentList);
			// 主集团排在前面 add by wangqiao
			if (isMain) {
				groupList.add(0, group);
			} else {
				groupList.add(group);
			}

		}
		return groupList;
	}
	
	/**
	 * 查询主集团id，没有主集团则返回""
	 * @author wangqiao
	 * @date 2016年3月26日
	 * @param doctorId
	 * @return
	 */
	private String getMainGroupId(Integer doctorId){
		
		String mainGroupId = "";
		DBObject gdQuery = new BasicDBObject();
		gdQuery.put("doctorId", doctorId);
		gdQuery.put("status", "C");
		gdQuery.put("isMain", true);

		// 添加按加入时间排序
		DBObject orderBy = new BasicDBObject();
		orderBy.put("creatorDate", 1);

		DBCursor gdCursor = dsForRW.getDB().getCollection("c_group_doctor").find(gdQuery).sort(orderBy);
		while (gdCursor.hasNext()) {
			DBObject gdObj = gdCursor.next();
			mainGroupId = MongodbUtil.getString(gdObj, "groupId");
		}
		
		return  mainGroupId;
	}

	/**
	 * 添加平台医生信息
	 * 
	 * @param user
	 */
	private void addPlatformInfo(User user) {
		// 非集团用户添加平台信息
		//if (user.getGroupList().size() != 0)
		//	return;

		DBObject query = new BasicDBObject();
		query.put("doctorId", user.getUserId());
		DBObject obj = dsForRW.getDB().getCollection("c_platform_doctor").findOne(query);
		Map<String, Object> platform = new HashMap<String, Object>();
		platform.put("onLineState", obj == null ? "2" : obj.get("onLineState"));
		platform.put("dutyDuration", obj == null ? 0 : obj.get("dutyDuration"));
		user.setPlatform(platform);
	}

	/**
	 * </p>
	 * 如果两个医生在同一个医生集团，则返回查看医生的联系方式和备注
	 * </p>
	 * 
	 * @param userId
	 *            登录医生id
	 * @param doctorId
	 *            查看医生id
	 * @return {groupContact:联系方式,groupRemark:备注,groupSame:同一集团,hasGroup:属于集团}
	 * @author fanp
	 * @date 2015年8月27日
	 */
	public Map<String, Object> getContactBySameGroup(Integer userId, Integer doctorId) {
		DBObject query = new BasicDBObject();
		query.put("doctorId", new BasicDBObject("$in", new Integer[] { userId, doctorId }));
		query.put("status", "C");

		DBObject project = new BasicDBObject();
		project.put("groupId", 1);
		project.put("doctorId", 1);
		project.put("contactWay", 1);
		project.put("remarks", 1);

		String userGroupId = "", doctorGroupId = "";
		Map<String, Object> map = new HashMap<String, Object>();

		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query, project);
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			if (doctorId.intValue() == MongodbUtil.getInteger(obj, "doctorId").intValue()) {
				map.put("groupContact", MongodbUtil.getString(obj, "contactWay"));
				map.put("groupRemark", MongodbUtil.getString(obj, "remarks"));
				doctorGroupId = MongodbUtil.getString(obj, "groupId");
			} else {
				userGroupId = MongodbUtil.getString(obj, "groupId");
			}
		}
		// 查找医生属于集团
		if (StringUtil.equals("", doctorGroupId)) {
			map.put("hasGroup", 0);
		} else {
			map.put("hasGroup", 1);
		}

		if (!StringUtil.equals(userGroupId, "")
				&& ((StringUtil.equals(userGroupId, doctorGroupId) || userId.intValue() == doctorId.intValue()))) {
			map.put("groupSame", 1);
		} else {
			map.remove("groupContact");
			map.remove("groupRemark");
			map.put("groupSame", 0);
		}

		return map;
	}

	/**
	 * 更新医生集团医生属性
	 * 
	 * @return
	 */
	public boolean updateGroupDoctor(Integer doctorId, String deptName) {
		if (StringUtil.isEmpty(deptName)) {
			return false;
		}
		DBObject update = new BasicDBObject();
		update.put("deptName", deptName);

		DBObject query = new BasicDBObject();
		query.put("doctorId", doctorId);
		dsForRW.getDB().getCollection("c_group_doctor").update(query, new BasicDBObject("$set", update), false, true);
		return true;
	}

	@Override
	public String getGroupIdByUser(String uId) {
		String groupId = null;
		DBObject gdQuery = new BasicDBObject();
		DBObject obj;
		try {
			int id = Integer.parseInt(uId);
			gdQuery.put("doctorId", id);
			gdQuery.put("status", "C");
			obj = dsForRW.getDB().getCollection("c_group_doctor").findOne(gdQuery);
			if (obj != null) {
				groupId = MongodbUtil.getString(obj, "groupId");
			}
		} catch (NumberFormatException e) {
			return null;
		}
		return groupId;
	}

	@Override
	public String getGroupNameByGroupId(String groupId) {
		if (StringUtil.isEmpty(groupId))
			return "";
		DBObject gdQuery = new BasicDBObject();
		gdQuery.put("_id", new ObjectId(groupId));
		DBObject obj = dsForRW.getDB().getCollection("c_group").findOne(gdQuery);
		return (String) obj.get("name");
	}

	@Override
	public List<String> getGroupListIdByUser(String uid) {
		List<String> listId = new ArrayList<String>();
		String groupId = null;
		DBObject gdQuery = new BasicDBObject();
		int id = Integer.parseInt(uid);
		gdQuery.put("doctorId", id);
		gdQuery.put("status", "C");
		gdQuery.put("type","group");
		
		// 这里先获取到屏蔽的集团，再查询i_article表时过滤掉屏蔽的集团的(2016-6-6傅永德)
		List<String> skipGroupIds = getSkipGroupIds();		
		gdQuery.put("groupId", new BasicDBObject("$nin", skipGroupIds));
		
		DBCursor gdCursor = dsForRW.getDB().getCollection("c_group_doctor").find(gdQuery);
		
		while (gdCursor.hasNext()) {
			DBObject gdObj = gdCursor.next();
			groupId = MongodbUtil.getString(gdObj, "groupId");
			listId.add(groupId);
		}
		return listId;
	}

	@Override
	public List<String> getSkipGroupIds() {
		List<String> skipGroupIds = Lists.newArrayList();
		DBObject skipGroupQuery = new BasicDBObject();
		skipGroupQuery.put("skip", "S");
		DBCursor cursor = dsForRW.getDB().getCollection("c_group").find(skipGroupQuery);
		
		while(cursor.hasNext()) {
			DBObject skipGroup = cursor.next();
			String id = MongodbUtil.getString(skipGroup, "_id");
			skipGroupIds.add(id);
		}
		
		return skipGroupIds;
	}

}
