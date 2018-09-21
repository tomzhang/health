package com.dachen.health.friend.dao.mongo;

import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.constant.FriendReqStatus;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.friend.dao.IFriendReqDao;
import com.dachen.health.friend.entity.param.FriendReqQuery;
import com.dachen.health.friend.entity.po.FriendReq;
import com.dachen.health.user.entity.po.Doctor;
import com.google.common.collect.Lists;
import com.mongodb.*;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FriendReqDaoImpl extends NoSqlRepository implements IFriendReqDao{

 
	 @Autowired
	  private UserManager userManager;
	
	@Override
	public void save(FriendReq friendReq) {
		dsForRW.save(friendReq);
	}

    public void update(FriendReq friendReq) {
    	try {
    		 DBObject query = new BasicDBObject();
    	        query.put("_id", friendReq.getId());

    	        DBObject update = new BasicDBObject();
    	        update.put("status", friendReq.getStatus());
    	        update.put("updateTime", friendReq.getUpdateTime());
    	        update.put("createTime", friendReq.getCreateTime());
    	        update.put("applyContent", friendReq.getApplyContent());
    	        update.put("userReqType", friendReq.getUserReqType());
//    	        update.put("saleGoodFileList", friendReq.getSaleGoodFileList());
    	        dsForRW.getDB().getCollection("u_friend_req").update(query, new BasicDBObject("$set", update));
		} catch (Exception e) {
			e.printStackTrace();
		}
       
    }
    
	public FriendReq getFriendReqById(String id) {
		Query<FriendReq> query = dsForRW.createQuery(FriendReq.class).field("_id").equal(new ObjectId(id));
		FriendReq friendReq= query.get();
		return friendReq;
	}
	
	public PageVO queryFriendReq(FriendReqQuery friendReqQuery) {
		DBCollection dbCollection = dsForRW.getDB().getCollection("u_friend_req");
		BasicDBObject q1 = new BasicDBObject();
		q1.put("fromUserId", friendReqQuery.getUserId());
		if (friendReqQuery.getUserReqType() != null) {
			q1.put("userReqType", friendReqQuery.getUserReqType());
		}
		BasicDBObject q2 = new BasicDBObject();
		q2.put("toUserId", friendReqQuery.getUserId());
		if (friendReqQuery.getUserReqType() != null) {
			q2.put("userReqType", friendReqQuery.getUserReqType());
		}
		
		BasicDBList values = new BasicDBList(); 
		values.add(q1);
		values.add(q2);
		
	    DBObject queryCondition = new BasicDBObject();     
	    queryCondition.put(QueryOperators.OR, values);

		long total = dbCollection.count(queryCondition);
		java.util.List<DBObject> pageData = Lists.newArrayList();
		DBCursor cursor;
		if (friendReqQuery.getPageIndex() != null) {
			cursor = dbCollection.find(queryCondition).sort(new BasicDBObject("createTime", -1))
					.skip(friendReqQuery.getPageIndex() * friendReqQuery.getPageSize())
					.limit(friendReqQuery.getPageSize());
		} else {
			cursor = dbCollection.find(queryCondition).sort(new BasicDBObject("createTime", -1));
			friendReqQuery.setPageIndex(0);
			friendReqQuery.setPageSize(friendReqQuery.getPageSize());
		}
		while (cursor.hasNext()) {
			BasicDBObject dbObj = (BasicDBObject) cursor.next();
			
			Object  reqType = dbObj.get("userReqType");
			if (null != reqType) {
				dbObj.put("userReqType", dbObj.getInt("userReqType"));// 区分医药代表
				setDoctorInfoForEnterpriseUser(dbObj.getInt("userReqType"),dbObj.getInt("toUserId"),dbObj);
			} else {
				dbObj.put("userReqType", 0);// 默认值
			}
			
			if(dbObj.containsField("fromUserId")&&userManager.getUserNoException(dbObj.getInt("fromUserId"))!=null)
			{
				if(dbObj.getInt("userReqType") == 0){
					User fromUser = userManager.getUserNoException(dbObj.getInt("fromUserId"));
					dbObj.put("fromUserName",fromUser.getName());
					dbObj.put("fromHeadPicFileName",fromUser.getHeadPicFileName());
				}
			}
			if(dbObj.containsField("toUserId")&&userManager.getUserNoException(dbObj.getInt("toUserId"))!=null)
			{
				User toUser = userManager.getUserNoException(dbObj.getInt("toUserId"));
				dbObj.put("toUserName",userManager.getUserNoException(dbObj.getInt("toUserId")).getName());
				dbObj.put("toHeadPicFileName",toUser.getHeadPicFileName());
			}
			pageData.add(dbObj);
		}
		return new PageVO(pageData, total, friendReqQuery.getPageIndex(),friendReqQuery.getPageSize());
	}
	
	
	
	/**
	 * 两个用户之间是否存在未处理的验证请求
	 * @param userId
	 * @param toUserId
	 * @return
	 */
	public FriendReq getUnTreatedFriendReq(Integer userId,Integer toUserId) {
		FriendReq friendReq=null;
		DBCollection dbCollection = dsForRW.getDB().getCollection("u_friend_req");
		BasicDBObject q = new BasicDBObject();
		q.put("fromUserId", userId);
		q.put("toUserId", toUserId);
		q.put("status", FriendReqStatus.WAIT_ACCEPT.getValue());
		DBCursor cursor  = dbCollection.find(q);
		while (cursor.hasNext()) {
			BasicDBObject dbObj = (BasicDBObject) cursor.next();
			friendReq =new FriendReq();
			friendReq.setId(dbObj.getObjectId("_id"));
		}
		
		BasicDBObject q2 = new BasicDBObject();
		q2.put("fromUserId", toUserId);
		q2.put("toUserId", userId);
		q2.put("status", FriendReqStatus.WAIT_ACCEPT.getValue());
		DBCursor cursor2  = dbCollection.find(q);
		while (cursor2.hasNext()) {
			BasicDBObject dbObj = (BasicDBObject) cursor2.next();
			friendReq =new FriendReq();
			friendReq.setId(dbObj.getObjectId("_id"));
		}
		return friendReq;
	}
	
	public FriendReq getFriendReq(Integer fromUserId, Integer toUserId, int status) {
		return dsForRW.createQuery("u_friend_req", FriendReq.class).field("fromUserId").equal(fromUserId)
				.field("toUserId").equal(toUserId).field("status").equal(status).get();
	}
	@Override
	public FriendReq getWaitAcceptFriendReq(Integer fromUserId, Integer toUserId) {
		return dsForRW.createQuery("u_friend_req", FriendReq.class).field("fromUserId").equal(fromUserId)
				.field("toUserId").equal(toUserId).field("status").equal(FriendReqStatus.WAIT_ACCEPT.getValue()).order("-createTime").get();
	}

	//设置医生的科室和医院信息
	private void setDoctorInfoForEnterpriseUser(Integer userReqType,
			Integer doctorId, BasicDBObject dbObj) {
		if (userReqType.intValue() == 1) {
			if (null != doctorId) {
				User user = userManager.getUserNoException(doctorId);
				if (null != user && user.getUserType() == UserEnum.UserType.doctor.getIndex()) {
					Doctor doctor = user.getDoctor();
					if (null != doctor) {
						dbObj.put("toUserTitle", doctor.getTitle());
						dbObj.put("toUserHospital", doctor.getHospital());
						dbObj.put("toUserDepartment", doctor.getDepartments());
						dbObj.put("toUserStatus", user.getStatus());
					}
				}

			}
		}
	}
	

	@Override
	public void deleteFriendReqById(ObjectId id) {
		// TODO Auto-generated method stub
		 DBObject queryCondition = new BasicDBObject();     
		 queryCondition.put("_id", id);
		dsForRW.getDB().getCollection("u_friend_req").remove(queryCondition);
	}
	@Override
	public FriendReq getFriendReq(Integer fromUserId, Integer toUserId) {
		return dsForRW.createQuery("u_friend_req", FriendReq.class).field("fromUserId").equal(fromUserId)
				.field("toUserId").equal(toUserId).get();
	}
	@Override
	public List<FriendReq> getFriendReqListByUserId(Integer userId,String column) {
		
		List<FriendReq> result = new ArrayList<FriendReq>();
		if(null==userId ||userId.intValue()==0)
		{	
			return result;
		}
		Query<FriendReq>  list =dsForRW.createQuery("u_friend_req", FriendReq.class);
		list.filter("status", FriendReqStatus.ACCEPTED.getValue());
		result =list.filter(column, userId).asList();
		return result;
	}
}
