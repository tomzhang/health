package com.dachen.health.group.group.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.group.company.entity.po.GroupUser;
import com.dachen.health.group.group.dao.IGroupUserDao;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

@Repository
public class GroupUserDaoImpl extends NoSqlRepository implements IGroupUserDao {

	@Override
	public GroupUser findGroupRootAdmin(String groupId) {	
		
		return dsForRW.createQuery(GroupUser.class)
				.field("objectId").equal(groupId)
				.field("status").equal("C")
				.field("rootAdmin").equal("root")				
				.get();
	}

	@Override
	public List<GroupUser> findGroupRootAdmins(List<String> groupId) {
		return dsForRW.createQuery(GroupUser.class)
			.field("objectId").in(groupId)
			.field("status").equal("C")
			.field("rootAdmin").equal("root")
			.asList();
	}

	@Override
	public boolean findUserIsAdmin(Integer userId) {
		BasicDBObject query = new BasicDBObject();
		query.put("doctorId", userId);
		query.put("status", "C");
		
		BasicDBList in = new BasicDBList();
		in.add("root");
		in.add("admin");
		query.put("rootAdmin", new BasicDBObject("$in", in));
		
		//已屏蔽的集团不进行过滤
		
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_user").find(query);
			   
		return cursor.hasNext();
	}

	public boolean isAdmin(Integer userId, String groupId) {
		BasicDBObject query = new BasicDBObject();
		query.put("doctorId", userId);
		query.put("status", "C");
		query.put("objectId", groupId);

		BasicDBList in = new BasicDBList();
		in.add("root");
		in.add("admin");
		query.put("rootAdmin", new BasicDBObject("$in", in));

		//已屏蔽的集团不进行过滤
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_user").find(query);

		return cursor.hasNext();
	}
	
	@Override
	public List<GroupUser> findGroupRoots(String groupId) {	
		
		return dsForRW.createQuery(GroupUser.class)
				.field("objectId").equal(groupId)
				.field("status").equal("C")
				.field("rootAdmin").equal("root")				
				.asList();
	}

	@Override
	public List<GroupUser> findGroupAdminWithOutRoot(String groupId) {
		return dsForRW.createQuery(GroupUser.class)
				.field("objectId").equal(groupId)
				.field("status").equal("C")		
				.asList();
	}

	@Override
	public List<GroupUser> findByDoctorId(Integer doctorId) {
		return dsForRW.createQuery(GroupUser.class)
			.field("doctorId").equal(doctorId)
			.field("status").equal("C")
			.asList();
	}

}
