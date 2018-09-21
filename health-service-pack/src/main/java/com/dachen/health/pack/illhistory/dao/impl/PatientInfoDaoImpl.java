package com.dachen.health.pack.illhistory.dao.impl;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.pack.illhistory.dao.PatientInfoDao;
import com.dachen.health.pack.illhistory.entity.po.PatientInfo;
import com.dachen.health.pack.patient.mapper.PatientMapper;
import com.dachen.health.user.entity.po.TagUtil;
import com.dachen.health.user.entity.vo.RelationVO;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PatientInfoDaoImpl extends NoSqlRepository implements PatientInfoDao {

    @Autowired
    private PatientMapper patientMapper;

    @Override
    public PatientInfo insert(PatientInfo patientInfo) {
        Long now = System.currentTimeMillis();
        patientInfo.setCreateTime(now);
        patientInfo.setUpdateTime(now);
        dsForRW.save(patientInfo);
        return patientInfo;
    }

    @Override
    public PatientInfo findByDoctorIdAndPatientId(Integer doctorId, Integer patientId) {
        return dsForRW.createQuery(PatientInfo.class).field("doctorId").equal(doctorId).field("patientId").equal(patientId).get();
    }

    @Override
    public PatientInfo findById(String patientInfoId) {
        return dsForRW.createQuery(PatientInfo.class).field("_id").equal(new ObjectId(patientInfoId)).get();
    }

    @Override
    public void update(String patientInfoId, PatientInfo patientInfo) {
        Query<PatientInfo> query = dsForRW.createQuery(PatientInfo.class).field("_id").equal(new ObjectId(patientInfoId));

        Long now = System.currentTimeMillis();

        UpdateOperations<PatientInfo> ops = dsForRW.createUpdateOperations(PatientInfo.class);
        if (StringUtils.isNotEmpty(patientInfo.getHeight())) {
            ops.set("height", patientInfo.getHeight());
        } else {
            ops.unset("height");
        }
        if (StringUtils.isNotEmpty(patientInfo.getWeight())) {
            ops.set("weight", patientInfo.getWeight());
        } else {
            ops.unset("weight");
        }
        if (StringUtils.isNotEmpty(patientInfo.getMarriage())) {
            ops.set("marriage", patientInfo.getMarriage());
        } else {
            ops.unset("marriage");
        }
        if (StringUtils.isNotEmpty(patientInfo.getJob())) {
            ops.set("job", patientInfo.getJob());
        } else {
            ops.unset("job");
        }
        ops.set("updateTime", now);

        dsForRW.update(query, ops);
    }

    @Override
    public List<PatientInfo> findAllMyPatientInfos(Integer doctorId, List<Integer> patientIds) {
        return dsForRW.createQuery(PatientInfo.class).field("doctorId").equal(doctorId).field("patientId").in(patientIds).asList();
    }


    /**
     * 未激活标签的患者
     *
     * @param doctorId
     * @return
     */
    public RelationVO getInactiveTag(Integer doctorId, List<Integer> patientIds) {
        List<DoctorPatient> docPatList = dsForRW.createQuery("u_doctor_patient", DoctorPatient.class)
                .field("userId").equal(doctorId)
                .field("status").equal(UserEnum.RelationStatus.normal.getIndex())
                .field("setting.defriend").equal(1)
                .asList();
        List<Integer> ids = Lists.newArrayList();
        if (docPatList != null && docPatList.size() > 0) {
            for (DoctorPatient dp : docPatList) {
                ids.add(dp.getToUserId());
            }
        }

        if (ids.isEmpty()) {
            ids.add(-1);
        }

        //获取未激活的用户
        Query<User> q = dsForRW.createQuery("user", User.class).field("status").equal(UserEnum.UserStatus.inactive.getIndex()).filter("_id in", ids);
        List<User> users = q.asList();

        List<Integer> inactiveUserIds = Lists.newArrayList();
        if (users != null && users.size() > 0) {
            for (User user : users) {
                inactiveUserIds.add(user.getUserId());
            }
        }
        RelationVO vo = new RelationVO();
        vo.setTagName(TagUtil.INACTION);
        if (inactiveUserIds != null && inactiveUserIds.size() > 0) {
            //获取未激活的用户下的患者
            List<Integer> allPatientIds = patientMapper.getPatientIdsByUserIds(inactiveUserIds);
            if (allPatientIds != null && allPatientIds.size() > 0) {
                allPatientIds.retainAll(patientIds);
                vo.setNum(allPatientIds.size());
                vo.setPatientIds(allPatientIds);
            }
        } else {
            vo.setNum(0);
            vo.setPatientIds(new ArrayList<>());
        }
        return vo;
    }


}
