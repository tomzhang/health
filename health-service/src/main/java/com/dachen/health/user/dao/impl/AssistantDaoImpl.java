package com.dachen.health.user.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.user.dao.IAssistantDao;
import com.dachen.health.user.entity.po.DrugVerifyRecord;
import com.dachen.health.user.entity.vo.DoctorDetailVO;
import com.dachen.util.MongodbUtil;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class AssistantDaoImpl extends NoSqlRepository implements IAssistantDao {

    @Override
    public void addDrugVerifyRecord(DrugVerifyRecord verifyRecord) {
        dsForRW.save(verifyRecord);
    }

    /**
     * </p>获取医助存在医生的分管医院</p>
     * 
     * @param doctor
     * @return
     * @author fanp
     * @date 2015年7月8日
     */
    public List<DoctorDetailVO> getHospitals(Integer userId) {
        List<String> hospitalIdList = this.gethospitalIds(userId);// 医助分管医院id
        if (hospitalIdList == null || hospitalIdList.size() == 0) {
            return null;
        }

        DBCollection inputCollection = dsForRW.getDB().getCollection("user");

        /**
         * 1.构造pipeline,最终pipeline如下 <br>
         * {$match: { userType: 3
         * ,'doctor.hospitalId':{$in:["200108223268","200108223269"]}}}, <br>
         * {$project: {_id: 0, doctor:1 } },<br>
         * {$group: {_id:"$doctor.hospitalId", count : { $sum : 1 }, hospital: {
         * $first: "$doctor.hospital" }}}<br>
         * 2.返回结果如下{"_id":"200108223268","count":3,"hospital":"深圳市人民医院"}
         */
        // 匹配条件
        DBObject matchFields = new BasicDBObject();
        matchFields.put("userType", UserEnum.UserType.doctor.getIndex());
        matchFields.put("status", UserEnum.UserStatus.normal.getIndex());
        matchFields.put("doctor.hospitalId", new BasicDBObject("$in", hospitalIdList.toArray()));
        DBObject match = new BasicDBObject("$match", matchFields);

        // 查询字段
        BasicDBObject fields = new BasicDBObject();
        fields.put("_id", 0);
        fields.put("doctor", 1);
        DBObject project = new BasicDBObject("$project", fields);

        // 分组条件
        DBObject groupFields = new BasicDBObject("_id", "$doctor.hospitalId");
        // groupFields.put("count", new BasicDBObject("$sum", 1));
        groupFields.put("hospital", new BasicDBObject("$first", "$doctor.hospital"));
        DBObject group = new BasicDBObject("$group", groupFields);

        List<DBObject> pipeline = new ArrayList<DBObject>();
        pipeline.add(match);
        pipeline.add(project);
        pipeline.add(group);

        AggregationOutput output = inputCollection.aggregate(pipeline);

        List<DoctorDetailVO> list = new ArrayList<DoctorDetailVO>();

        Iterator<DBObject> it = output.results().iterator();
        while (it.hasNext()) {
            DBObject obj = it.next();

            DoctorDetailVO vo = new DoctorDetailVO();
            vo.setHospitalId(MongodbUtil.getString(obj, "_id"));
            vo.setHospital(MongodbUtil.getString(obj, "hospital"));
            list.add(vo);
        }

        return list;
    }

    /**
     * </p>获取医助分管医院的医生</p>
     * 
     * @param doctor
     * @return
     * @author fanp
     * @date 2015年7月8日
     */
    public List<DoctorDetailVO> getHospitalDoctors(Integer userId, String hospitalId) {

        // 查找医生信息
        List<DoctorDetailVO> list = new ArrayList<DoctorDetailVO>();

        DBObject queryDoctor = new BasicDBObject();
        queryDoctor.put("userType", UserEnum.UserType.doctor.getIndex());
        queryDoctor.put("status", UserEnum.UserStatus.normal.getIndex());
        queryDoctor.put("doctor.hospitalId", hospitalId.trim());

        DBObject fieldDoctor = new BasicDBObject();
        fieldDoctor.put("name", 1);
        fieldDoctor.put("nickname", 1);
        fieldDoctor.put("telephone", 1);
        fieldDoctor.put("headPicFileName", 1);
        fieldDoctor.put("doctor.hospital", 1);
        fieldDoctor.put("doctor.departments", 1);
        fieldDoctor.put("doctor.title", 1);
        DBCursor doctorCursor = dsForRW.getDB().getCollection("user").find(queryDoctor, fieldDoctor);

        while (doctorCursor.hasNext()) {
            DBObject obj = doctorCursor.next();

            DoctorDetailVO vo = new DoctorDetailVO();
            vo.setUserId(MongodbUtil.getInteger(obj, "_id"));
            vo.setName(MongodbUtil.getString(obj, "name"));
            vo.setNickname(MongodbUtil.getString(obj, "nickname"));
            vo.setTelephone(MongodbUtil.getString(obj, "telephone"));
            vo.setHeadPicFileName(MongodbUtil.getString(obj, "headPicFileName"));

            DBObject doctor = (BasicDBObject) obj.get("doctor");

            vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
            vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
            vo.setTitle(MongodbUtil.getString(doctor, "title"));

            list.add(vo);
        }

        return list;
    }

    /**
     * </p>获取医助分管医院id列表</p>
     * 
     * @param userId
     * @return
     * @author fanp
     * @date 2015年7月8日
     */
    private List<String> gethospitalIds(Integer userId) {
        // 查找医助号
        DBObject field = new BasicDBObject();
        field.put("_id", 0);
        field.put("assistant.number", 1);
        DBObject userObj = dsForRW.getDB().getCollection("user").findOne(new BasicDBObject("_id", userId), field);
        if (userObj == null) {
            return null;
        }
        DBObject o = (BasicDBObject) userObj.get("assistant");
        if (o == null) {
            return null;
        }
        String assistantNumber = MongodbUtil.getString(o, "number");// 医助编号

        // 查找分管医院
        DBObject fieldHId = new BasicDBObject();
        fieldHId.put("_id", 0);
        fieldHId.put("hospital", 1);
        DBCursor hIdCursor = dsForRW.getDB().getCollection("b_drughospital").find(new BasicDBObject("drugPerson", assistantNumber), fieldHId);

        List<String> hIdList = new ArrayList<String>();
        while (hIdCursor.hasNext()) {
            DBObject obj = hIdCursor.next();
            hIdList.add(obj.get("hospital").toString());
        }

        return hIdList;
    }

}
