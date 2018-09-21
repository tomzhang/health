package com.dachen.health.user.dao.impl;

import java.util.*;

import com.dachen.health.user.entity.po.Doctor;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.utils.SortByChina;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.TagType;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.vo.User;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.user.dao.ITagDao;
import com.dachen.health.user.entity.param.TagParam;
import com.dachen.health.user.entity.po.Tag;
import com.dachen.health.user.entity.po.TagUtil;
import com.dachen.health.user.entity.vo.RelationVO;
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
public class TagDaoImpl extends NoSqlRepository implements ITagDao {

    /**
     * </p>获取用户某个标签个数，用户判断标签是否重复</p>
     * 
     * @param param
     * @return 标签数量
     * @author fanp
     * @date 2015年7月2日
     */
    public int getTagCount(TagParam param) {
        DBObject query = new BasicDBObject();
        query.put("userId", param.getUserId());
        query.put("tagType", param.getTagType());
        query.put("tagName", param.getTagName().trim());
        return dsForRW.getDB().getCollection("u_tag").find(query).count();
    }

    /**
     * </p>查询用户存在的标签</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年7月9日
     */
    public List<String> getExistTag(TagParam param){
        DBObject query = new BasicDBObject();
        query.put("userId", param.getUserId());
        query.put("tagType", param.getTagType());
        query.put("tagName", new BasicDBObject(QueryOperators.IN,param.getTagNames()));
        DBCursor cursor = dsForRW.getDB().getCollection("u_tag").find(query);
        List<String> list = new ArrayList<String>();
        while(cursor.hasNext()){
            DBObject obj = cursor.next();
            list.add(MongodbUtil.getString(obj, "tagName"));
        }
        return list;
    }
    
    /**
     * </p>获取标签列表</p>
     * 
     * @param userId
     * @param tagType
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public List<String> getTag(Integer userId, TagType tagType) {
        DBObject query = new BasicDBObject();

        query.put("userId", userId);
        query.put("tagType", tagType.getIndex());

        DBCursor cursor = dsForRW.getDB().getCollection("u_tag").find(query);

        List<String> list = new ArrayList<String>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            list.add(MongodbUtil.getString(obj, "tagName"));
        }
        return list;
    }

    /**
     * </p>添加标签</p>
     * 
     * @param tag
     * @author fanp
     * @date 2015年7月3日
     */
    public void addTag(Tag tag) {
        dsForRW.insert(tag);
    }

    /**
     * </p>修改标签</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月3日
     */
    public void updateTag(TagParam param) {
        DBObject query = new BasicDBObject();
        query.put("userId", param.getUserId());
        query.put("tagType", param.getTagType());
        query.put("tagName", param.getOldName().trim());

        DBObject update = new BasicDBObject();
        update.put("$set", new BasicDBObject("tagName", param.getTagName().trim()));

        dsForRW.getDB().getCollection("u_tag").updateMulti(query, update);
    }

    /**
     * </p>删除标签</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月3日
     */
    public void deleteTag(TagParam param) {
        DBObject query = new BasicDBObject();
        query.put("userId", param.getUserId());
        query.put("tagType", param.getTagType());
        query.put("tagName", param.getTagName().trim());

        dsForRW.getDB().getCollection("u_tag").remove(query);
    }
    
    public Tag getTag(Integer userId, String tagName) {
    	return dsForRW.createQuery("u_tag", Tag.class).filter("tagName", tagName).filter("userId", userId).get();
    }
    
    /**
     * 未激活标签组
     * @param userId
     * @return
     */
    public RelationVO getInactiveTag(Integer userId) {
		List<DoctorPatient> docPatList = dsForRW.createQuery("u_doctor_patient", DoctorPatient.class)
                                                .field("userId").equal(userId)
                                                .field("status").equal(UserEnum.RelationStatus.normal.getIndex())
				                                .field("setting.defriend").equal(1)
                                                .asList();
		List<Integer> ids = Lists.newArrayList();
		if (docPatList != null && docPatList.size() > 0) {
            for (DoctorPatient dp : docPatList) {
                ids.add(dp.getToUserId());
            }
        }

		if (ids.isEmpty()){
			ids.add(-1);
        }
		Query<User> q = dsForRW.createQuery("user", User.class).field("status").equal(UserStatus.inactive.getIndex()).filter("_id in", ids);
		List<User> users = q.asList();
		Collections.sort(users, new SortByChina<User>("name"));
		RelationVO vo = new RelationVO();
		vo.setTagName(TagUtil.INACTION);
		vo.setNum((int)q.countAll());
		List<Integer> inactionIds = new ArrayList<Integer>();
		for (User user : users) {
			inactionIds.add(user.getUserId());
		}
		vo.setUserIds(inactionIds.toArray(new Integer[0]));
		return vo;
    }



    /**
     * </p>给关系打标签</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月3日
     */
    public void addRelationTag(TagParam param) {
        if (param.getUserIds() == null || param.getUserIds().length == 0) {
            // 未选择关系用户
            return;
        }

        // 根据标签分类获取存储表
        String collName = "";
        if (param.getTagType() == UserEnum.TagType.doctorPatient.getIndex()) {
            collName = "u_doctor_patient";
        } else if (param.getTagType() == UserEnum.TagType.doctorFriend.getIndex()) {
            collName = "u_doctor_friend";
        } else if (param.getTagType() == UserEnum.TagType.patientFriend.getIndex()) {
            collName = "u_patient_friend";
        }

        DBObject query = new BasicDBObject();
        query.put("userId", param.getUserId());
        query.put("toUserId", new BasicDBObject("$in", param.getUserIds()));
        query.put("status", UserEnum.RelationStatus.normal.getIndex());

        DBObject update = new BasicDBObject();
        update.put("$addToSet", new BasicDBObject("tags", param.getTagName().trim()));

        dsForRW.getDB().getCollection(collName).updateMulti(query, update);
    }

    /**
     * </p>给关系打标签</p>
     *
     * @param param
     * @author fanp
     * @date 2015年7月3日
     */
    public void addRelationTag2(TagParam param) {
        if (param.getUserIds() == null || param.getUserIds().length == 0) {
            // 未选择关系用户
            return;
        }

        // 根据标签分类获取存储表
        String collName = "";
        if (param.getTagType() == UserEnum.TagType.doctorPatient.getIndex()) {
            collName = "u_doctor_patient";
        } else if (param.getTagType() == UserEnum.TagType.doctorFriend.getIndex()) {
            collName = "u_doctor_friend";
        } else if (param.getTagType() == UserEnum.TagType.patientFriend.getIndex()) {
            collName = "u_patient_friend";
        }

        DBObject query = new BasicDBObject();
        query.put("userId", param.getUserId());
        query.put("patientId", param.getPatientId());
        query.put("status", UserEnum.RelationStatus.normal.getIndex());

        DBObject update = new BasicDBObject();
        update.put("$addToSet", new BasicDBObject("tags", param.getTagName().trim()));

        dsForRW.getDB().getCollection(collName).updateMulti(query, update);
    }

    /**
     * </p>删除关系标签</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月3日
     */
    public void deleteRelationTag(TagParam param) {
        // 根据标签分类获取存储表
        String collName = "";
        if (param.getTagType() == UserEnum.TagType.doctorPatient.getIndex()) {
            collName = "u_doctor_patient";
        } else if (param.getTagType() == UserEnum.TagType.doctorFriend.getIndex()) {
            collName = "u_doctor_friend";
        } else if (param.getTagType() == UserEnum.TagType.patientFriend.getIndex()) {
            collName = "u_patient_friend";
        }

        DBObject query = new BasicDBObject();
        query.put("userId", param.getUserId());
        query.put("status", UserEnum.RelationStatus.normal.getIndex());

        DBObject update = new BasicDBObject();
        update.put("$pull", new BasicDBObject("tags", param.getOldName().trim()));

        dsForRW.getDB().getCollection(collName).updateMulti(query, update);
    }

    /**
     * </p>给关系打标签(多个标签)</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月3日
     */
    public void addRelationTags(TagParam param){

        // 根据标签分类获取存储表
        String collName = "";
        if (param.getTagType() == UserEnum.TagType.doctorPatient.getIndex()) {
            collName = "u_doctor_patient";
        } else if (param.getTagType() == UserEnum.TagType.doctorFriend.getIndex()) {
            collName = "u_doctor_friend";
        } else if (param.getTagType() == UserEnum.TagType.patientFriend.getIndex()) {
            collName = "u_patient_friend";
        }

        DBObject query = new BasicDBObject();
        query.put("userId", param.getUserId());
        query.put("toUserId",param.getId());
        query.put("status", UserEnum.RelationStatus.normal.getIndex());

        DBObject update = new BasicDBObject();
        update.put("$addToSet", new BasicDBObject("tags", new BasicDBObject("$each",param.getTagNames())));

        dsForRW.getDB().getCollection(collName).update(query, update);
    }

    /**
     * </p>删除关系标签(多个标签)</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月3日
     */
    public void deleteRelationTags(TagParam param){
     // 根据标签分类获取存储表
        String collName = "";
        if (param.getTagType() == UserEnum.TagType.doctorPatient.getIndex()) {
            collName = "u_doctor_patient";
        } else if (param.getTagType() == UserEnum.TagType.doctorFriend.getIndex()) {
            collName = "u_doctor_friend";
        } else if (param.getTagType() == UserEnum.TagType.patientFriend.getIndex()) {
            collName = "u_patient_friend";
        }

        DBObject query = new BasicDBObject();
        query.put("userId", param.getUserId());
        query.put("toUserId", param.getId());
        query.put("status", UserEnum.RelationStatus.normal.getIndex());

        DBObject update = new BasicDBObject();
        //update.put("$unset", new BasicDBObject("tags", ""));
        BasicDBList dblist = new BasicDBList();
        dblist.addAll(TagUtil.SYS_TAG);
        DBObject ninQuery = new BasicDBObject();
        ninQuery.put("$nin", dblist);
        update.put("$pull", new BasicDBObject("tags", ninQuery));

        dsForRW.getDB().getCollection(collName).updateMulti(query, update);
    }

	@Override
	public String[] getDoctorPatientTag(Integer doctorId, Integer userId) {
		Query<DoctorPatient> q = dsForRW.createQuery(DoctorPatient.class)
					.field("userId").equal(doctorId)
					.field("toUserId").equal(userId)
					.field("status").equal(UserEnum.RelationStatus.normal.getIndex());
		q.retrievedFields(true, "tags");
		DoctorPatient dp = q.get();
		if(dp != null){
			return dp.getTags();
		}
		return null;
	}

    @Override
    public DoctorPatient findByDoctorIdAndPatientId(Integer doctorId, Integer patientId) {
        return dsForRW.createQuery(DoctorPatient.class).field("userId").equal(doctorId).field("patientId").equal(patientId).get();
    }

    public void updateDoctorPatient(ObjectId id, String[] tags, String remarkName, String remark) {
        Query<DoctorPatient> query = dsForRW.createQuery(DoctorPatient.class).field("_id").equal(id);
        UpdateOperations<DoctorPatient> ops = dsForRW.createUpdateOperations(DoctorPatient.class);
        Set<String> set = Sets.newHashSet();

        DoctorPatient doctorPatient = query.get();
        if (doctorPatient != null && doctorPatient.getTags() != null && doctorPatient.getTags().length > 0) {
            for (String tag : doctorPatient.getTags()) {
                if(TagUtil.SYS_TAG.contains(StringUtils.trim(tag))) {
                    set.add(tag);
                }
            }
        }

        if (StringUtils.isNotBlank(remarkName)) {
            ops.set("remarkName", remarkName);
        } else {
            ops.unset("remarkName");
        }

        if (StringUtils.isNotBlank(remark)) {
            ops.set("remark", remark);
        } else {
            ops.unset("remark");
        }

        if (tags!= null && tags.length > 0) {
            set.addAll(Arrays.asList(tags));
        }
        if (set != null && set.size() > 0) {
            ops.set("tags", set);
        } else {
            ops.unset("tags");
        }
        dsForRW.update(query, ops);
    }

    @Override
    public boolean existTag(Tag tag) {
        DBObject query = new BasicDBObject();
        query.put("userId", tag.getUserId());
        query.put("tagType", tag.getTagType());
        query.put("tagName", tag.getTagName().trim());
        return dsForRW.getDB().getCollection("u_tag").find(query).count() > 0;
    }

    public List<DoctorPatient> getAllMyPatient(Integer doctorId) {
        return dsForRW.createQuery(DoctorPatient.class).field("userId").equal(doctorId).asList();
    }

}
