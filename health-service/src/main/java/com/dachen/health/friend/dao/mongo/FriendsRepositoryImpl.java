package com.dachen.health.friend.dao.mongo;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.friend.dao.FriendsRepository;
import com.dachen.health.friend.entity.po.DoctorFriend;
import com.dachen.health.user.entity.vo.UserDetailVO;
import com.dachen.util.DateUtil;
import com.dachen.util.MongodbUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class FriendsRepositoryImpl extends NoSqlRepository implements FriendsRepository {

    @Override
    public List<Object> queryBlacklist(int userId) {
        //Query<Friends> q = dsForRW.createQuery(Friends.class).field("userId").equal(userId).field("blacklist").equal(1);
        //List<DoctorAssistant> d1=dsForRW.createQuery(DoctorAssistant.class).field("userId").equal(userId).field("setting.defriend").equal(2).field("status").equal(1).asList();
        //List<DoctorFriend> d2=dsForRW.createQuery(DoctorFriend.class).field("userId").equal(userId).field("setting.defriend").equal(2).field("status").equal(1).asList();
        //List<DoctorPatient> d3=dsForRW.createQuery(DoctorPatient.class).field("userId").equal(userId).field("setting.defriend").equal(2).field("status").equal(1).asList();
        //List<PatientFriend> d4=dsForRW.createQuery(PatientFriend.class).field("userId").equal(userId).field("setting.defriend").equal(2).field("status").equal(1).asList();

        //查询条件
        DBObject query = new BasicDBObject();
        query.put("userId", userId);
        query.put("status", UserEnum.RelationStatus.normal.getIndex());
        query.put("setting.defriend", 2);

        //返回字段
        DBObject fields = new BasicDBObject();
        fields.put("toUserId", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("u_doctor_assistant").find(query, fields);

        List<Integer> relationId = new ArrayList<Integer>();//关系id列表
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            relationId.add((Integer) obj.get("toUserId"));
        }

        cursor = dsForRW.getDB().getCollection("u_doctor_friend").find(query, fields);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            relationId.add((Integer) obj.get("toUserId"));
        }

        cursor = dsForRW.getDB().getCollection("u_doctor_patient").find(query, fields);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            relationId.add((Integer) obj.get("toUserId"));
        }

        cursor = dsForRW.getDB().getCollection("u_patient_friend").find(query, fields);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            relationId.add((Integer) obj.get("toUserId"));
        }

        //List<UserDetailVO> list = new ArrayList<UserDetailVO>();
        List<Object> list = new ArrayList<Object>();
        if (relationId.size() > 0) {
            // 查找用户信息
            DBObject uquery = new BasicDBObject();
            uquery.put("_id", new BasicDBObject("$in", relationId));
            // uquery.put("status", UserEnum.UserStatus.normal.getIndex());

            DBObject ufield = new BasicDBObject();
            ufield.put("_id", 1);
            ufield.put("name", 1);
            ufield.put("telephone", 1);
            ufield.put("sex", 1);
            ufield.put("birthday", 1);
            ufield.put("doctor", 1);
            ufield.put("headPicFileName", 1);
            ufield.put("userType", 1);

            DBCursor userCursor = dsForRW.getDB().getCollection("user").find(uquery, ufield);
            while (userCursor.hasNext()) {
                DBObject obj = userCursor.next();
                UserDetailVO vo = new UserDetailVO();
                vo.setUserId(MongodbUtil.getInteger(obj, "_id"));
                vo.setName(MongodbUtil.getString(obj, "name"));
                vo.setTelephone(MongodbUtil.getString(obj, "telephone"));
                vo.setSex(MongodbUtil.getInteger(obj, "sex"));
                vo.setHeadPicFileName(MongodbUtil.getString(obj, "headPicFileName"));
                vo.setUserType(MongodbUtil.getInteger(obj, "userType"));

                Long birthday = MongodbUtil.getLong(obj, "birthday");
                if (birthday != null) {
                    vo.setAge(DateUtil.yearDiff(new Date(), new Date(birthday)));
                }

                DBObject doctor = (BasicDBObject) obj.get("doctor");
                if (doctor != null) {
                    vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
                    vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
                    vo.setTitle(MongodbUtil.getString(doctor, "title"));
                }

                list.add(vo);
            }
        }

        return list;

        //return datas;
    }

    @Override
    public List<Integer> getToUserIdList(Integer userId) {
        if (Objects.isNull(userId)) {
            return null;
        }
        Query<DoctorFriend> query = dsForRW.createQuery(DoctorFriend.class);
        query.field("userId").equal(userId);
        query.filter("status", UserEnum.RelationStatus.normal.getIndex());
        query.filter("setting.defriend", 1);//拉黑【1:否/2:是】
        query.retrievedFields(true, "toUserId");
        List<Integer> toUserIdList;
        List<DoctorFriend> friends = query.asList();
        toUserIdList = friends.stream().map(DoctorFriend::getToUserId).collect(Collectors.toList());
        return toUserIdList;
    }

}
