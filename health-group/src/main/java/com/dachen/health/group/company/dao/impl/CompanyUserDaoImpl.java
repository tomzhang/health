package com.dachen.health.group.company.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.helper.UserHelper;
import com.dachen.health.commons.constants.GroupEnum.GroupRootAdmin;
import com.dachen.health.commons.constants.GroupEnum.GroupUserStatus;
import com.dachen.health.group.common.entity.vo.DoctorBasicInfo;
import com.dachen.health.group.company.dao.ICompanyUserDao;
import com.dachen.health.group.company.entity.param.GroupUserParam;
import com.dachen.health.group.company.entity.po.GroupUser;
import com.dachen.health.group.company.entity.vo.GroupUserVO;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


/**
 * 集团管理员（c_group_user）相关的Dao层实现
 * @author wangqiao
 * @date 2016年4月20日
 */
@Repository
public class CompanyUserDaoImpl extends NoSqlRepository implements ICompanyUserDao {

	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyUserDao#add(com.dachen.health.group.company.entity.po.GroupUser)
	 */
	@Override
	public GroupUser add(GroupUser cuser) {
		//参数校验
		if(cuser == null){
			throw new ServiceException("参数不能为空");
		}
		
		Object id = dsForRW.insert(cuser).getId();
		return dsForRW.createQuery(GroupUser.class).field("_id").equal(new ObjectId(id.toString())).get();
	}

	@Deprecated
	public void update(GroupUser cuser) {
		DBObject query = new BasicDBObject();
		DBObject update = new BasicDBObject();
		update.put("status", cuser.getStatus());
		
		query.put("_id", new ObjectId(cuser.getId()));
		dsForRW.getDB().getCollection("c_group_user").update(query, new BasicDBObject("$set",update));
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyUserDao#updateStatusById(java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public void updateStatusById(String id,String status,Integer updateUserId){
		//参数校验
		if(StringUtils.isEmpty(id)){
			throw new ServiceException("参数id不能为空");
		}
		if(StringUtils.isEmpty(status)){
			throw new ServiceException("参数status不能为空");
		}
		//根据id过滤查询
		DBObject query = new BasicDBObject();
		try{
			query.put("_id", new ObjectId(id));
		}catch(Exception e){
			throw new ServiceException("参数id不能转换成ObjectId");
		}
		
		//更新状态，最后更新人，最后更新时间
		DBObject update = new BasicDBObject();
		update.put("status", status);
		update.put("updatorDate", System.currentTimeMillis());
		if(updateUserId != null && updateUserId != 0){
			update.put("updator", updateUserId);
		}
		
		//持久化
		dsForRW.getDB().getCollection("c_group_user").update(query, new BasicDBObject("$set",update));
	}

	@Deprecated
	public void deleteByGroupUser(String groupId, Integer doctorId) {
		DBObject query = new BasicDBObject();
		query.put("objectId", groupId);
		query.put("doctorId", doctorId);
		query.put("type", 2);
		dsForRW.getDB().getCollection("c_group_user").remove(query);
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyUserDao#deleteByGroupIdAndDoctorId(java.lang.String, java.lang.Integer)
	 */
	@Override
	public void deleteByGroupIdAndDoctorId(String groupId, Integer doctorId){
		//参数校验
		if(StringUtils.isEmpty(groupId)){
			throw new ServiceException("参数groupId不能为空");
		}
		if(doctorId == null || doctorId == 0){
			throw new ServiceException("参数doctorId不能为空");
		}
		
		DBObject query = new BasicDBObject();
		query.put("objectId", groupId);
		query.put("doctorId", doctorId);
//		query.put("type", 2);
		dsForRW.getDB().getCollection("c_group_user").remove(query);
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyUserDao#delete(java.lang.String[])
	 */
	@Override
	public void deleteById(String[] ids) {
		//参数校验
		if(ids == null){
			throw new ServiceException("参数ids不能为空");
		}
		BasicDBList values = new BasicDBList();
		BasicDBObject in = new BasicDBObject();
		//批量删除
		for (String id : ids) {
			try {
				values.add(new ObjectId(id));
			} catch (Exception e) {
				throw new ServiceException("参数id不能转换成ObjectId");
			}
		}
		in.put("$in", values);
		//持久化
		dsForRW.getDB().getCollection("c_group_user").remove(new BasicDBObject("_id", in));
	}

	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyUserDao#search(com.dachen.health.group.company.entity.param.GroupUserParam)
	 */
	@Override
	public PageVO search(GroupUserParam param) {
		DBObject query = new BasicDBObject();
		//创建时间过滤
		if (param.getStartTime() != null || param.getEndTime() != null) {
			DBObject timeQuery = new BasicDBObject();
	        if (param.getStartTime() != null) {
	            timeQuery.put("$gte", param.getStartTime());
	        }
	        if (param.getEndTime() != null) {
	            timeQuery.put("$lt", param.getEndTime());
	        }
	        query.put("creatorDate", timeQuery);
        }
		//集团/企业 id 过滤
		if(!StringUtil.isEmpty(param.getObjectId())) {
			query.put("objectId", param.getObjectId());
		}
		//业务类型过滤
		query.put("type", param.getType());
		//状态过滤
		query.put("status", param.getStatus());
		//按创建时间排序
		DBObject sortField = new BasicDBObject();
		sortField.put("updatorDate", -1);
		//查询groupUser列表
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_user").find(query).sort(sortField).skip(param.getStart()).limit(param.getPageSize());
		BasicDBList docIdArray = new BasicDBList();
		List<GroupUserVO> cuList = new ArrayList<GroupUserVO>();
		//为列表中的对象赋值
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			GroupUserVO cuv = new GroupUserVO();
			
			cuv.setId(obj.get("_id").toString());
//			cuv.setObjectId(obj.get("objectId") == null ? "" : obj.get("objectId").toString());
//			cuv.setDoctorId(Integer.valueOf(obj.get("doctorId").toString()));
//			cuv.setType(Integer.valueOf(obj.get("type").toString()));
//			cuv.setStatus(obj.get("status").toString());
//			cuv.setCreator(Integer.valueOf(obj.get("creator").toString()));
//			cuv.setCreatorDate(Long.valueOf(obj.get("creatorDate").toString()));
//			cuv.setUpdator(Integer.valueOf(obj.get("updator").toString()));
//			cuv.setUpdatorDate(Long.valueOf(obj.get("updatorDate").toString()));
//			cuv.setRootAdmin(MongodbUtil.getString(obj, "rootAdmin"));
			
			cuv.setObjectId(MongodbUtil.getString(obj, "objectId"));
			cuv.setDoctorId(MongodbUtil.getInteger(obj, "doctorId"));
			cuv.setType(MongodbUtil.getInteger(obj, "type"));
			cuv.setStatus(MongodbUtil.getString(obj, "status"));
			cuv.setCreator(MongodbUtil.getInteger(obj, "creator"));
			cuv.setCreatorDate(MongodbUtil.getLong(obj, "creatorDate"));
			cuv.setUpdator(MongodbUtil.getInteger(obj, "updator"));
			cuv.setUpdatorDate(MongodbUtil.getLong(obj, "updatorDate"));
			cuv.setRootAdmin(MongodbUtil.getString(obj, "rootAdmin"));
			
			if(MongodbUtil.getInteger(obj, "doctorId") != null){
				docIdArray.add(MongodbUtil.getInteger(obj, "doctorId"));
			}
			
			cuList.add(cuv);
		}
		//查询user信息
		BasicDBObject in = new BasicDBObject();
		in.put("$in", docIdArray);
		DBCursor usrsor = dsForRW.getDB().getCollection("user").find(new BasicDBObject("_id", in));
		//将user中的部分信息 写入groupUser列表
		while (usrsor.hasNext()) {
			DBObject obj = usrsor.next();
			for (GroupUserVO guser : cuList) {
				if(guser.getDoctorId().equals(Integer.valueOf(obj.get("_id").toString()))) {
					DoctorBasicInfo doctor = new DoctorBasicInfo();
					doctor.setDoctorId(Integer.valueOf(obj.get("_id").toString()));
					if(obj.get("name") == null || "".equals(MongodbUtil.getString(obj, "name"))) {
						if(doctor.getDoctorId().equals(guser.getCreator())) {
							doctor.setName("创建者");
						}
					} else {
						doctor.setName(MongodbUtil.getString(obj, "name"));
					}
					doctor.setSex(MongodbUtil.getInteger(obj, "sex"));
					doctor.setTelephone(MongodbUtil.getString(obj, "telephone"));
					doctor.setUserType(MongodbUtil.getInteger(obj, "userType"));
					if(obj.get("headPicFileName") == null) {
						doctor.setHeadPicFileName("");
						doctor.setHeadPicFilePath("");
					} else {
						doctor.setHeadPicFileName(MongodbUtil.getString(obj, "headPicFileName"));
						doctor.setHeadPicFilePath(UserHelper.buildHeaderPicPath(doctor.getHeadPicFileName(), guser.getDoctorId()));
					}
					DBObject doc = (BasicDBObject) obj.get("doctor");
					if(null != doc) {
						doctor.setDoctorNum(MongodbUtil.getString(doc, "doctorNum"));
						doctor.setIntroduction(MongodbUtil.getString(doc, "introduction"));
						doctor.setPosition(MongodbUtil.getString(doc, "title"));
						doctor.setSkill(MongodbUtil.getString(doc, "skill"));
						doctor.setHospital(MongodbUtil.getString(doc, "hospital"));
						doctor.setDepartments(MongodbUtil.getString(doc, "departments"));
					}
					guser.setDoctor(doctor);
				}
			}
		}
		//返回分页信息
		PageVO page = new PageVO();
        page.setPageData(cuList);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(dsForRW.getDB().getCollection("c_group_user").count(query));
        
        return page;
	}


	@Override
	@Deprecated
	public GroupUser getGroupUserById(GroupUser cuser) {
		Query<GroupUser> query = dsForRW.createQuery(GroupUser.class);
		if(!StringUtil.isEmpty(cuser.getId())) {
			query.field("_id").equal(new ObjectId(cuser.getId()));
		}
		if(null != cuser.getDoctorId()) {
			query.field("doctorId").equal(cuser.getDoctorId());
		}
		if(null != cuser.getType()) {
			query.field("type").equal(cuser.getType());
		}
		if(!StringUtil.isEmpty(cuser.getObjectId())) {
			query.field("objectId").equal(cuser.getObjectId());
		}
		if (StringUtil.isNotBlank(cuser.getStatus())) {
			query.field("status").equal(cuser.getStatus());
		}
		return query.get();
	}

//	@Override
//	public Query<GroupUser> getQueryByGroupIds(String... groupIds) {
//		BasicDBList idList = new BasicDBList();
//		for (String groupId : groupIds) {
//			idList.add(groupId);
//		}
//		return dsForRW.createQuery(GroupUser.class).filter("objectId in", idList).filter("type", 2);
//	}
	

	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyUserDao#getGroupUserById(java.lang.String)
	 */
//	@Override
//	public GroupUser getGroupUserById(String id) {
//		
//		return getGroupUserByIdAndStatus(id,null,null,null,null);	
//		
//	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyUserDao#getGroupUserByIdAndStatus(java.lang.Integer, java.lang.String, java.lang.String)
	 */
//	@Override
//	public List<GroupUser> getGroupUserListByIdAndStatus(Integer doctorId,Integer type,String status) {
//		
//		return getGroupUserListByIdAndStatus(null,doctorId,null,type,status);
//		
//	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyUserDao#getGroupUserByIdAndStatus(java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String)
	 */
//	public List<GroupUser> getGroupUserListByIdAndStatus(Integer doctorId,Integer type,String status,String groupId) {
//
//		return getGroupUserListByIdAndStatus(null,doctorId,groupId,type,status);
//		
//	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyUserDao#getGroupUserByDoctorIdAndGroupId(java.lang.Integer, java.lang.String)
	 */
//	@Override
//	public GroupUser getGroupUserByIdAndStatus(Integer doctorId,String groupId,String status){
//
//		return getGroupUserByIdAndStatus(null,doctorId,groupId,null,status);		
//	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyUserDao#getGroupUserByIdAndStatus(java.lang.String, java.lang.Integer, java.lang.String, java.lang.Integer, java.lang.String)
	 */
	@Override
	public GroupUser getGroupUserByIdAndStatus(String id,Integer doctorId,String groupId,Integer type,String status){
		Query<GroupUser> query = dsForRW.createQuery(GroupUser.class);

		//参数非空校验
		if(!StringUtil.isEmpty(id)) {
			try {
				query.field("_id").equal(new ObjectId(id));
			} catch (Exception e) {
				throw new ServiceException("参数id不能转换成ObjectId");
			}
		}
		if(null != doctorId && doctorId != 0) {
			query.field("doctorId").equal(doctorId);
		}
		if(null != type && type != 0) {
			query.field("type").equal(type);
		}
		if (!StringUtil.isEmpty(status)) {
			query.field("status").equal(status);
		}
		if (!StringUtil.isEmpty(groupId)) {
			query.field("objectId").equal(groupId);
		}
		
		return query.get();
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyUserDao#getGroupUserListByIdAndStatus(java.lang.String, java.lang.Integer, java.lang.String, java.lang.Integer, java.lang.String)
	 */
	@Override
	public List<GroupUser> getGroupUserListByIdAndStatus(String id,Integer doctorId,String groupId,Integer type,String status){
		Query<GroupUser> query = dsForRW.createQuery(GroupUser.class);

		//参数非空校验
		if(!StringUtil.isEmpty(id)) {
			try {
				query.field("_id").equal(new ObjectId(id));
			} catch (Exception e) {
				throw new ServiceException("参数id不能转换成ObjectId");
			}
		}
		if(null != doctorId && doctorId != 0) {
			query.field("doctorId").equal(doctorId);
		}
		if(null != type && type != 0) {
			query.field("type").equal(type);
		}
		if (!StringUtil.isEmpty(status)) {
			query.field("status").equal(status);
		}
		if (!StringUtil.isEmpty(groupId)) {
			query.field("objectId").equal(groupId);
		}
		
		return query.asList();
	}
	
	/* (non-Javadoc)
	 * @see com.dachen.health.group.company.dao.ICompanyUserDao#getRootAdminByGroupId(java.lang.String)
	 */
	@Override
	public GroupUser getRootAdminByGroupId(String groupId){
		//参数校验
		if (StringUtil.isEmpty(groupId)) {
			throw new ServiceException("参数groupId不能为空");
		}
		//查询超级管理员
		Query<GroupUser> query = dsForRW.createQuery(GroupUser.class);
		query.field("objectId").equal(groupId);
		query.field("status").equal(GroupUserStatus.正常使用.getIndex());
		query.field("rootAdmin").equal(GroupRootAdmin.root.getIndex());
		
		return query.get();
		
	}

    @Override
    public GroupUser getRootGroupManage(String groupId) {
        return this.getRootAdminByGroupId(groupId);
    }

}
