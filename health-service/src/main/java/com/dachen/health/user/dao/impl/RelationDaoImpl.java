package com.dachen.health.user.dao.impl;

import java.util.*;

import com.dachen.health.friend.entity.po.DoctorFriend;
import com.dachen.sdk.util.SdkUtils;
import com.google.common.collect.Lists;
import com.mongodb.*;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.dao.IBaseUserDao;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.RelationType;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.vo.User;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.group.doctor.service.ICommonGroupDoctorService;
import com.dachen.health.user.dao.IRelationDao;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.health.user.entity.po.Tag;
import com.dachen.health.user.entity.po.TagUtil;
import com.dachen.health.user.entity.vo.RelationVO;
import com.dachen.health.user.entity.vo.UserDetailVO;
import com.dachen.util.DateUtil;
import com.dachen.util.MongodbUtil;

/**
 * ProjectName： health-service<br>
 * ClassName： DoctorTagDaoImpl<br>
 * Description： 医生标签dao实现类<br>
 * 
 * @author fanp
 * @crateTime 2015年6月30日
 * @version 1.0.0
 */
@Repository
public class RelationDaoImpl extends NoSqlRepository implements IRelationDao {

	@Autowired
    private IBaseUserDao baseUserDao;
	
    /**
     * </p>按标签分组获取好友</p>
     * 
     * @param relationType
     *            集合名 ,userId 用户id
     * @author fanp
     * @return 用户所有标签及标签下关系用户数量及id的数组
     * @date 2015年6月30日
     */
    public List<RelationVO> getGroupTag(RelationType relationType, Integer userId) {

        List<RelationVO> list = new ArrayList<RelationVO>();

        String collName = this.getCollection(relationType);
        DBCollection inputCollection = dsForRW.getDB().getCollection(collName);

        /**
         * 1.构造pipeline,最终pipeline如下 <br>
         * {$match: { doctorId: 45 } }, <br>
         * {$project: {_id: 0, tags: 1,patientId:1 } }, <br>
         * {$unwind: "$tags" }, <br>
         * {$group: { _id:"$tags", count : { $sum : 1 },userIds : { $addToSet : "$patientId" }}} <br>
         * 2.返回结果如下 {"_id":"优质","count":3,"patientId":[1,2,3]}
         */
        // 匹配条件
        DBObject matchFields = new BasicDBObject();
        matchFields.put("userId", userId);
        matchFields.put("status", UserEnum.RelationStatus.normal.getIndex());
        matchFields.put("setting.defriend", 1);
        DBObject match = new BasicDBObject("$match", matchFields);

        // 查询字段
        BasicDBObject fields = new BasicDBObject();
        fields.put("_id", 0);
        fields.put("tags", 1);
        fields.put("toUserId", 1);
        DBObject project = new BasicDBObject("$project", fields);

        // 切割字段，为数组
        DBObject unwind = new BasicDBObject("$unwind", "$tags");

        // 分组条件
        DBObject groupFields = new BasicDBObject("_id", "$tags");
        groupFields.put("count", new BasicDBObject("$sum", 1));
        groupFields.put("userIds", new BasicDBObject("$addToSet", "$toUserId"));
        DBObject group = new BasicDBObject("$group", groupFields);
        
        //排序
        DBObject sortFields = new BasicDBObject();
        sortFields.put("name", 1);
        DBObject sortGroup = new BasicDBObject("$sort", sortFields);

        List<DBObject> pipeline = new ArrayList<DBObject>();
        pipeline.add(match);
        pipeline.add(project);
        pipeline.add(unwind);
        pipeline.add(group);
        pipeline.add(sortGroup);

        AggregationOutput output = inputCollection.aggregate(pipeline);

        Iterator<DBObject> it = output.results().iterator();
        while (it.hasNext()) {
            DBObject tObj = it.next();

            RelationVO vo = new RelationVO();
            vo.setTagName(tObj.get("_id").toString());
            vo.setNum((Integer) tObj.get("count"));

            BasicDBList dbList = (BasicDBList) tObj.get("userIds");
            vo.setUserIds(dbList.toArray(new Integer[dbList.size()]));
            
            list.add(vo);
        }
        return list;
    }
    
    /**
     * </p>按标签分组获取好友</p>
     * 
     * @param relationType
     *            集合名 ,userId 用户id
     * @author fanp
     * @return 用户所有标签及标签下关系用户数量及id的数组
     * @date 2015年6月30日
     */
    public List<RelationVO> getGroupTag(RelationType relationType, Integer userId, List<UserDetailVO> userDetailVOs) {

        List<RelationVO> list = new ArrayList<RelationVO>();

        String collName = this.getCollection(relationType);
        DBCollection inputCollection = dsForRW.getDB().getCollection(collName);

        /**
         * 1.构造pipeline,最终pipeline如下 <br>
         * {$match: { doctorId: 45 } }, <br>
         * {$project: {_id: 0, tags: 1,patientId:1 } }, <br>
         * {$unwind: "$tags" }, <br>
         * {$group: { _id:"$tags", count : { $sum : 1 },userIds : { $addToSet : "$patientId" }}} <br>
         * 2.返回结果如下 {"_id":"优质","count":3,"patientId":[1,2,3]}
         */
        // 匹配条件
        DBObject matchFields = new BasicDBObject();
        matchFields.put("userId", userId);
        matchFields.put("status", UserEnum.RelationStatus.normal.getIndex());
        matchFields.put("setting.defriend", 1);
        DBObject match = new BasicDBObject("$match", matchFields);

        // 查询字段
        BasicDBObject fields = new BasicDBObject();
        fields.put("_id", 0);
        fields.put("tags", 1);
        fields.put("toUserId", 1);
        DBObject project = new BasicDBObject("$project", fields);

        // 切割字段，为数组
        DBObject unwind = new BasicDBObject("$unwind", "$tags");

        // 分组条件
        DBObject groupFields = new BasicDBObject("_id", "$tags");
        groupFields.put("count", new BasicDBObject("$sum", 1));
        groupFields.put("userIds", new BasicDBObject("$addToSet", "$toUserId"));
        DBObject group = new BasicDBObject("$group", groupFields);
        
        //排序
        DBObject sortFields = new BasicDBObject();
        sortFields.put("name", 1);
        DBObject sortGroup = new BasicDBObject("$sort", sortFields);

        List<DBObject> pipeline = new ArrayList<DBObject>();
        pipeline.add(match);
        pipeline.add(project);
        pipeline.add(unwind);
        pipeline.add(group);
        pipeline.add(sortGroup);

        AggregationOutput output = inputCollection.aggregate(pipeline);
        List<Integer> userIds = new ArrayList<>();
        if (userDetailVOs != null && userDetailVOs.size() > 0) {
			for (UserDetailVO userDetailVO : userDetailVOs) {
				Integer idTemp = userDetailVO.getUserId();
				userIds.add(idTemp);
			}
		}
        
        Iterator<DBObject> it = output.results().iterator();
        while (it.hasNext()) {
            DBObject tObj = it.next();

            RelationVO vo = new RelationVO();
            vo.setTagName(tObj.get("_id").toString());
            vo.setNum((Integer) tObj.get("count"));

            BasicDBList dbList = (BasicDBList) tObj.get("userIds");
            
            Integer[] userIdArray = new Integer[dbList.size()];
            
            Integer[] userIdArrayTemp = dbList.toArray(new Integer[dbList.size()]);
            
            if(userIds != null && userIds.size() > 0 && userIdArrayTemp != null && userIdArrayTemp.length > 0) {
            	int index = 0;
            	for(Integer userIdTemp : userIds) {
            		for (int i = 0; i < userIdArrayTemp.length; i++) {
						if (userIdTemp.equals(userIdArrayTemp[i])) {
							userIdArray[index] = userIdTemp;
							index ++;
						}
					}
            	}
            }
            
            vo.setUserIds(userIdArray);
            
            list.add(vo);
        }
        return list;
    }

    /**
     * </p>按标签分组获取好友</p>
     *
     * @param relationType
     *            集合名 ,userId 用户id
     * @author fanp
     * @return 用户所有标签及标签下关系用户数量及id的数组
     * @date 2015年6月30日
     */
    public List<RelationVO> getGroupTag2(RelationType relationType, Integer userId, List<Integer> patientIds) {

        List<RelationVO> list = new ArrayList<RelationVO>();

        String collName = this.getCollection(relationType);
        DBCollection inputCollection = dsForRW.getDB().getCollection(collName);

        /**
         * 1.构造pipeline,最终pipeline如下 <br>
         * {$match: { doctorId: 45 } }, <br>
         * {$project: {_id: 0, tags: 1,patientId:1 } }, <br>
         * {$unwind: "$tags" }, <br>
         * {$group: { _id:"$tags", count : { $sum : 1 },userIds : { $addToSet : "$patientId" }}} <br>
         * 2.返回结果如下 {"_id":"优质","count":3,"patientId":[1,2,3]}
         */
        // 匹配条件
        DBObject matchFields = new BasicDBObject();
        matchFields.put("userId", userId);
        matchFields.put("status", UserEnum.RelationStatus.normal.getIndex());
        matchFields.put("setting.defriend", 1);
        DBObject match = new BasicDBObject("$match", matchFields);

        // 查询字段
        BasicDBObject fields = new BasicDBObject();
        fields.put("_id", 0);
        fields.put("tags", 1);
        fields.put("patientId", 1);
        DBObject project = new BasicDBObject("$project", fields);

        // 切割字段，为数组
        DBObject unwind = new BasicDBObject("$unwind", "$tags");

        // 分组条件
        DBObject groupFields = new BasicDBObject("_id", "$tags");
        groupFields.put("count", new BasicDBObject("$sum", 1));
        groupFields.put("patientIds", new BasicDBObject("$addToSet", "$patientId"));
        DBObject group = new BasicDBObject("$group", groupFields);

        //排序
        DBObject sortFields = new BasicDBObject();
        sortFields.put("name", 1);
        DBObject sortGroup = new BasicDBObject("$sort", sortFields);

        List<DBObject> pipeline = Lists.newArrayList();
        pipeline.add(match);
        pipeline.add(project);
        pipeline.add(unwind);
        pipeline.add(group);
        pipeline.add(sortGroup);

        AggregationOutput output = inputCollection.aggregate(pipeline);

        Iterator<DBObject> it = output.results().iterator();
        while (it.hasNext()) {
            DBObject tObj = it.next();
            RelationVO vo = new RelationVO();
            vo.setTagName(tObj.get("_id").toString());
            vo.setNum((Integer) tObj.get("count"));

            BasicDBList dbList = (BasicDBList) tObj.get("patientIds");

            List<Integer> patientIdList = Lists.newArrayList();
            patientIdList.addAll(patientIds);

            List<Integer> patientIdListTemp = Lists.newArrayList();
            if (dbList != null && dbList.size() > 0) {
                Iterator iterator = dbList.iterator();
                while (iterator.hasNext()) {
                    Object o = iterator.next();
                    Integer userIdTemp = (Integer)o;
                    patientIdListTemp.add(userIdTemp);
                }
            }

            //取交集
            if(patientIdList != null && patientIdList.size() > 0 && patientIdListTemp != null && patientIdListTemp.size() > 0) {
                patientIdList.retainAll(patientIdListTemp);
            }

            vo.setPatientIds(patientIdList);

            list.add(vo);
        }
        return list;
    }

    @Override
    public List<DoctorPatient> getAll() {
        return dsForRW.createQuery(DoctorPatient.class).asList();
    }

    @Override
    public void fixDoctorPatient(ObjectId id, Integer patientId) {
        Query<DoctorPatient> query = dsForRW.createQuery(DoctorPatient.class).field("_id").equal(id);
        UpdateOperations<DoctorPatient> ops = dsForRW.createUpdateOperations(DoctorPatient.class);
        ops.set("patientId", patientId);
        dsForRW.update(query, ops);
    }

    @Override
    public void addDoctorPatient(DoctorPatient doctorPatient) {
        dsForRW.insert(doctorPatient);
    }

    /**
     * </p>查找有关系的用户信息</p>
     * 
     * @param relationType
     *            集合名 ,userId 用户id
     * @return 用户信息列表
     * @author fanp
     * @date 2015年7月1日
     */
    public List<UserDetailVO> getRelations(RelationType relationType,Integer userId) {
        //查询条件
        DBObject query = new BasicDBObject();
        query.put("userId", userId);
        query.put("status", UserEnum.RelationStatus.normal.getIndex());
        query.put("setting.defriend", 1);
        
        //返回字段
        DBObject fields = new BasicDBObject();
        fields.put("toUserId", 1);

        String collName = this.getCollection(relationType);
        DBCursor cursor = dsForRW.getDB().getCollection(collName).find(query, fields);

        List<Integer> relationId = new ArrayList<Integer>();//关系id列表
        Map<Integer,Object> objMap = new HashMap<Integer,Object>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            Integer toUserId = (Integer) obj.get("toUserId");
            relationId.add(toUserId);
            
        }

        List<UserDetailVO> list = new ArrayList<UserDetailVO>();
        if (relationId.size() > 0) {
            // 查找用户信息
            DBObject uquery = new BasicDBObject();
            uquery.put("_id", new BasicDBObject(QueryOperators.IN, relationId));
//            uquery.put("status", UserEnum.UserStatus.normal.getIndex());

            DBObject ufield = new BasicDBObject();
            ufield.put("name", 1);
            ufield.put("telephone", 1);
            ufield.put("sex", 1);
//            ufield.put("birthday", 1);
            ufield.put("area", 1);
            ufield.put("age", 1);
            ufield.put("headPicFileName", 1);
            ufield.put("doctor", 1);
            ufield.put("status", 1);
            ufield.put("userType", 1);
            DBObject orderBy = new BasicDBObject();
			orderBy.put("name",1);
            DBCursor userCursor = dsForRW.getDB().getCollection("user").find(uquery,ufield).sort(orderBy);
            while(userCursor.hasNext()){
                DBObject obj = userCursor.next();
                UserDetailVO vo = new UserDetailVO();
                vo.setUserId(MongodbUtil.getInteger(obj, "_id"));
                vo.setName(MongodbUtil.getString(obj, "name"));
                vo.setTelephone(MongodbUtil.getString(obj, "telephone"));
                vo.setSex(MongodbUtil.getInteger(obj, "sex"));
                vo.setHeadPicFileName(MongodbUtil.getString(obj, "headPicFileName"));
                vo.setArea(MongodbUtil.getString(obj, "area"));
//                vo.setAge(MongodbUtil.getInteger(obj, "age"));
                vo.setStatus(MongodbUtil.getInteger(obj, "status"));
                vo.setUserType(MongodbUtil.getInteger(obj, "userType"));

                Long birthday = MongodbUtil.getLong(obj, "birthday");
                if(birthday!=null){
                    vo.setAge(DateUtil.calcAge(birthday));
                }
                
                //医生信息
                DBObject doctor = (BasicDBObject)obj.get("doctor");
                if(doctor!=null) {
                    vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
                    vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
                    vo.setTitle(MongodbUtil.getString(doctor, "title"));
                    vo.setDoctorNum(MongodbUtil.getString(doctor, "doctorNum"));
                    vo.setIntroduction(MongodbUtil.getString(doctor, "introduction"));
//                    /**
//                     * 添加医生集团及所处科室
//                     */
//                    vo = commongdService.getGroupListByUserId(vo);
                  /*  BaseUserVO baseUserVO = baseUserDao.getGroupByUserId(vo.getUserId());
                    if(baseUserVO!=null){
                        vo.setGroupId(baseUserVO.getGroupId());
                        vo.setGroupName(baseUserVO.getGroupName());
                    }*/
                }
                //好友设置
                Object setting = objMap.get(vo.getUserId());
                if(setting!=null){
                    vo.setSetting(setting);
                }
                list.add(vo);
            }
        }

        return list;
    }

    public List<Integer> getAllMyPatientIds(Integer doctorId) {
        List<Integer> patientIds = Lists.newArrayList();
        //查询条件
        DBObject query = new BasicDBObject();
        query.put("userId", doctorId);
        query.put("status", UserEnum.RelationStatus.normal.getIndex());
        query.put("setting.defriend", 1);

        //返回字段
        DBObject fields = new BasicDBObject();
        fields.put("patientId", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("u_doctor_patient").find(query, fields);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            Integer patientId = (Integer) obj.get("patientId");
            patientIds.add(patientId);
        }

        return patientIds;
    }
    public boolean ifDoctorFriend(Integer userId,Integer toUserId) {
        //查询条件
        DBObject query = new BasicDBObject();
        query.put("userId", userId);
        query.put("status", UserEnum.RelationStatus.normal.getIndex());
        query.put("setting.defriend", 1);
        query.put("toUserId", toUserId);
        //返回字段
        DBObject fields = new BasicDBObject();
        fields.put("toUserId", 1);
        fields.put("setting", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("u_doctor_friend").find(query, fields);
        if(cursor.hasNext()){
            return true;
        }
        return false;
    }

        public List<User> getRelation(RelationType relationType,Integer userId){
    	  //查询条件
        DBObject query = new BasicDBObject();
        query.put("userId", userId);
        query.put("status", UserEnum.RelationStatus.normal.getIndex());
        query.put("setting.defriend", 1);
        
        //返回字段
        DBObject fields = new BasicDBObject();
        fields.put("toUserId", 1);
        fields.put("setting", 1);

        String collName = this.getCollection(relationType);
        DBCursor cursor = dsForRW.getDB().getCollection(collName).find(query, fields);

        List<Integer> relationId = new ArrayList<Integer>();//关系id列表
        Map<Integer,Object> objMap = new HashMap<Integer,Object>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            Integer toUserId = (Integer) obj.get("toUserId");
            relationId.add(toUserId);
            
            objMap.put(toUserId, (BasicDBObject)obj.get("setting"));
        }
        cursor.close();
        
        
        List<User> list = new ArrayList<User>();
        if (relationId.size() > 0) {
            // 查找用户信息
            DBObject uquery = new BasicDBObject();
            uquery.put("_id", new BasicDBObject("$in",relationId));
            uquery.put("doctor", new BasicDBObject("$ne", null));
            uquery.put("doctor.doctorNum", new BasicDBObject("$exists", true));
            uquery.put("doctor", new BasicDBObject("$exists",true));
            
            DBObject ufield = new BasicDBObject();
            ufield.put("name", 1);
            ufield.put("telephone", 1);
            ufield.put("sex", 1);
//            ufield.put("birthday", 1);
            ufield.put("area", 1);
            ufield.put("age", 1);
            ufield.put("headPicFileName", 1);
            ufield.put("doctor", 1);
            ufield.put("status", 1);
            ufield.put("userType", 1);
            
            DBCursor userCursor = dsForRW.getDB().getCollection("user").find(uquery,ufield);
            while(userCursor.hasNext()){
                DBObject obj = userCursor.next();
                
                //医生信息
                DBObject doctor = (BasicDBObject)obj.get("doctor");
//                if(doctor.toString().equals("{ }")){
//                	continue;
//                }
                
                User vo = new User();
                vo.setUserId(MongodbUtil.getInteger(obj, "_id"));
                vo.setName(MongodbUtil.getString(obj, "name"));
                vo.setTelephone(MongodbUtil.getString(obj, "telephone"));
                vo.setSex(MongodbUtil.getInteger(obj, "sex"));
                vo.setHeadPicFileName(MongodbUtil.getString(obj, "headPicFileName"));
                vo.setArea(MongodbUtil.getString(obj, "area"));
                vo.setStatus(MongodbUtil.getInteger(obj, "status"));
                vo.setUserType(MongodbUtil.getInteger(obj, "userType"));
                
                vo.setRemarks("MY_DOCTOR");//是否问诊过
                
                Long birthday = MongodbUtil.getLong(obj, "birthday");
                if(birthday!=null){
                    vo.setAge(DateUtil.calcAge(birthday));
                }
                
              
                Doctor userDoctor = new Doctor();
                if(doctor!=null) {
                	userDoctor.setHospital(MongodbUtil.getString(doctor, "hospital"));
                	userDoctor.setDepartments(MongodbUtil.getString(doctor, "departments"));
                	userDoctor.setTitle(MongodbUtil.getString(doctor, "title"));
                	userDoctor.setDoctorNum(MongodbUtil.getString(doctor, "doctorNum"));
                	userDoctor.setCureNum(MongodbUtil.getInteger(doctor, "cureNum"));
                	userDoctor.setHospitalId(MongodbUtil.getString(doctor, "hospitalId"));
                	userDoctor.setSkill(MongodbUtil.getString(doctor, "skill"));
                	List<String> udlist = new ArrayList<String>();
                	Object eList = doctor.get("expertise");
        			if (eList != null) {
        				List<Object> oList = (BasicDBList) eList;
        				for (Object temp : oList) {
        					udlist.add(temp.toString());
        				}
        			}
        			userDoctor.setExpertise(udlist);
        			vo.setDoctor(userDoctor);
                }
                list.add(vo);
            }
            userCursor.close();
        }
        
        return list;
    }

    
    /**
     * </p>查找个人收藏</p>
     * @param relationType
     * @param userId
     * @return
     * @author fanp
     * @date 2015年7月10日
     */
    public RelationVO getCollection(RelationType relationType, Integer userId){
        DBObject query = new BasicDBObject();
        query.put("userId", userId);
        query.put("status", UserEnum.RelationStatus.normal.getIndex());
        query.put("setting.defriend", 1);
        query.put("setting.collection", 2);
        
        //返回字段
        DBObject fields = new BasicDBObject();
        fields.put("toUserId", 1);

        String collName = this.getCollection(relationType);
        DBCursor cursor = dsForRW.getDB().getCollection(collName).find(query, fields);

        List<Integer> relationId = new ArrayList<Integer>();//关系id列表
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            relationId.add((Integer) obj.get("toUserId"));
        }
        if(relationId.size()>0){
            RelationVO vo = new RelationVO();
            vo.setTagName("个人收藏");
            vo.setUserIds(relationId.toArray(new Integer[0]));
            return vo;
        }
        
        return null;
    }
    
    /**
     * </p>未打标签的好友</p>
     * @param relationType
     * @param userId
     * @return
     * @author fanp
     * @date 2015年7月10日
     */
    public RelationVO getWithoutTag(RelationType relationType, Integer userId){
        DBObject query = new BasicDBObject();
        query.put("userId", userId);
        query.put("status", UserEnum.RelationStatus.normal.getIndex());
        query.put("setting.defriend", 1);
        query.put("setting.collection", 1);
        query.put("tags.0", new BasicDBObject("$exists",false));
        
        //返回字段
        DBObject fields = new BasicDBObject();
        fields.put("toUserId", 1);

        String collName = this.getCollection(relationType);
        DBCursor cursor = dsForRW.getDB().getCollection(collName).find(query, fields);

        List<Integer> relationId = new ArrayList<Integer>();//关系id列表
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            relationId.add((Integer) obj.get("toUserId"));
        }
        if(relationId.size()>0){
            RelationVO vo = new RelationVO();
            vo.setUserIds(relationId.toArray(new Integer[0]));
            return vo;
        }
        
        return null;
    }

    
    /**
     * </p>获取关系存放的表</p>
     * @param relationType
     * @return
     * @author fanp
     * @date 2015年7月31日
     */
    private String getCollection(RelationType relationType){
        String collName = "";
        switch (relationType) {
            case doctorPatient:
                collName = "u_doctor_patient";
                break;
            case doctorFriend:
                collName = "u_doctor_friend";
                break;
            case doctorAssistant:
                collName = "u_doctor_assistant";
                break;
            case patientFriend:
                collName = "u_patient_friend";
                break;
            default:
                break;
        }
        return collName;
    }
}
