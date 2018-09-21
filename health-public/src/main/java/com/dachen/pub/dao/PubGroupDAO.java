package com.dachen.pub.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class PubGroupDAO extends NoSqlRepository{

	/**
	 * 暂时放这 
	 * @param userType
	 * @param client
	 * @return
	 */
	public List<String> getAllUser(Integer userType,String client)
	{
		DBObject query = new BasicDBObject();
		DBCollection dbUser = dsForRW.getDB().getCollection("user");
//		DBObject condition = new BasicDBObject("userType", userType);
		query.put("userType", userType);
		query.put("status", 1);
		//  客户端来源
//		query.put("source.terminal", PubUtils.getTerminal(client));
			
		 // 查询字段
        BasicDBObject fields = new BasicDBObject();
        fields.put("_id", 1);
		DBCursor cursor =dbUser.find(query, fields);
		List<String>userIdSet = new ArrayList<String>();	
		while(cursor.hasNext())
		{
			userIdSet.add(cursor.next().get("_id").toString());
		}
		return userIdSet;
	}
	
	
    /**
     * </p>查找集团下所有医生</p>
     * 
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年9月1日
     */
    public List<String> getDoctorIdByGroup(String groupId) {
    	List<Integer> doctorIds= getDoctorIntIdByGroup(groupId);
    	List<String> list = null;
    	if(doctorIds!=null && doctorIds.size()>0)
    	{
    		list = new ArrayList<String>(doctorIds.size());
    		for(Integer id:doctorIds)
    		{
    			list.add(String.valueOf(id));
    		}
    	}
        return list;
    }
    
    /**
     * </p>查找集团下所有患者</p>
     * 
     * @param groupId
     * @return
     * @author qujunli
     * @date 2015年9月1日
     */
    public List<String> getUserPatientIdByGroup(String groupId,String type) {
    	List<Integer> doctorIds = new ArrayList<Integer>();
    	if(StringUtil.isEmpty(type)){
    		doctorIds = getDoctorIntIdByGroup(groupId);
    	}else{
    		doctorIds = getDoctorIntIdByGroupId(groupId);
    	}
        DBObject query = new BasicDBObject();
        query.put("userId", new BasicDBObject("$in",doctorIds));
        
        DBObject project = new BasicDBObject();
        project.put("toUserId", 1);
        
        DBCursor cursor = dsForRW.getDB().getCollection("u_doctor_patient").find(query,project);
        List<String> list = new ArrayList<String>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            list.add(MongodbUtil.getString(obj, "toUserId"));
        }
        return list;
    }
    
    /**
     * </p>查找医生的所有患者</p>
     * 
     * @param doctorId
     * @return
     * @author qujunli
     * @date 2015年9月1日
     */
    public List<String> getUserPatientIdByDoctorId(Integer doctorId) {
        
        DBObject query = new BasicDBObject();
        query.put("userId",doctorId);
        
        DBObject project = new BasicDBObject();
        project.put("toUserId", 1);
        
        DBCursor cursor = dsForRW.getDB().getCollection("u_doctor_patient").find(query,project);
        List<String> list = new ArrayList<String>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            list.add(MongodbUtil.getString(obj, "toUserId"));
        }
        return list;
    }
    
    private List<Integer> getDoctorIntIdByGroup(String groupId) {
        DBObject query = new BasicDBObject();
        query.put("groupId", groupId);
        query.put("status", "C");

        DBObject project = new BasicDBObject();
        project.put("doctorId", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query, project);

        List<Integer> list = new ArrayList<Integer>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            list.add(MongodbUtil.getInteger(obj, "doctorId"));
        }
        return list;
    }
    /**
     * 发送患者之声的时候与医生的是否离职无关
     * @param groupId
     * @return
     */
    private List<Integer> getDoctorIntIdByGroupId(String groupId) {
        DBObject query = new BasicDBObject();
        query.put("groupId", groupId);
        DBObject project = new BasicDBObject();
        project.put("doctorId", 1);
        DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query, project);
        List<Integer> list = new ArrayList<Integer>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            list.add(MongodbUtil.getInteger(obj, "doctorId"));
        }
        return list;
    }
    
    public boolean isCreator(String mid,String userId) {
    	
        DBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(mid));

        DBObject project = new BasicDBObject();
        project.put("creator", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("c_group").find(query, project);
        String creator = null;
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            creator= MongodbUtil.getString(obj, "creator");
            if(StringUtil.equals(userId, creator))
            {
            	return true;
            }
        }
        return false;
    }
    
    /*public String getGroupLogoURL(String groupId)
    {
    	 DBObject query = new BasicDBObject();
         query.put("_id", new ObjectId(groupId));

         DBObject project = new BasicDBObject();
         project.put("logoUrl", 1);

         DBCursor cursor = dsForRW.getDB().getCollection("c_group").find(query, project);
         String logoUrl = null;
         while (cursor.hasNext()) {
             DBObject obj = cursor.next();
             logoUrl= MongodbUtil.getString(obj, "logoUrl");
         }
    	return logoUrl;
    }*/
}
