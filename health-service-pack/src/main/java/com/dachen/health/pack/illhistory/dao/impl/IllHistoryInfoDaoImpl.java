package com.dachen.health.pack.illhistory.dao.impl;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.pack.illhistory.dao.IllHistoryInfoDao;
import com.dachen.health.pack.illhistory.entity.po.Diagnosis;
import com.dachen.health.pack.illhistory.entity.po.IllContentInfo;
import com.dachen.health.pack.illhistory.entity.po.IllHistoryInfo;
import com.dachen.health.pack.illhistory.entity.po.IllHistoryRecord;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
public class IllHistoryInfoDaoImpl extends NoSqlRepository implements IllHistoryInfoDao {
	
	@Autowired
	private DiseaseTypeRepository diseaseTypeRepository;

    @Override
    public IllHistoryInfo createIllHistoryInfo(Integer doctorId, Integer userId, Integer patientId, String patientInfoId, Boolean isFirstTreat) {
        Long now = System.currentTimeMillis();
        IllHistoryInfo illHistoryInfo = new IllHistoryInfo();
        illHistoryInfo.setCreateTime(now);
        illHistoryInfo.setUpdateTime(now);
        illHistoryInfo.setDoctorId(doctorId);
        illHistoryInfo.setPatientId(patientId);
        illHistoryInfo.setUserId(userId);
        illHistoryInfo.setPatientInfoId(patientInfoId);
        illHistoryInfo.setFirstTreat(isFirstTreat);
        List<Integer> doctorIds = Lists.newArrayList();
        doctorIds.add(doctorId);
        illHistoryInfo.setDoctorIds(doctorIds);
        dsForRW.save(illHistoryInfo);

        return illHistoryInfo;
    }

    @Override
    public IllHistoryInfo insert(IllHistoryInfo illHistoryInfo) {
        Long now = System.currentTimeMillis();
        illHistoryInfo.setCreateTime(now);
        illHistoryInfo.setUpdateTime(now);
        dsForRW.save(illHistoryInfo);
        return illHistoryInfo;
    }

    @Override
    public void addIllContentInfo(IllHistoryInfo illHistoryInfo, String illDesc, String treatCase, List<String> pics) {
    	
    	Query<IllHistoryInfo> query = dsForRW.createQuery(IllHistoryInfo.class).field("_id").equal(new ObjectId(illHistoryInfo.getId()));
    	UpdateOperations<IllHistoryInfo> ops = dsForRW.createUpdateOperations(IllHistoryInfo.class);
    	
    	Long now = System.currentTimeMillis();
    	
    	IllContentInfo illContentInfo = new IllContentInfo();
    	illContentInfo.setIllDesc(illDesc);
    	illContentInfo.setPics(pics);
    	illContentInfo.setTreatment(treatCase);

    	if(StringUtils.isNotBlank(illDesc) || StringUtils.isNotBlank(treatCase) || pics != null) {
            illContentInfo.setUpdater(ReqUtil.instance.getUserId());
            illContentInfo.setUpdateTime(now);
        }
        ops.set("illContentInfo", illContentInfo);
    	ops.set("updateTime", now);
    	
    	dsForRW.update(query, ops);
    	
    }

    @Override
    public void addDiagnosis(String illHistoryInfoId, String content, String diseaseId, Integer doctorId, Integer orderId) {
        Query<IllHistoryInfo> query = dsForRW.createQuery(IllHistoryInfo.class).field("_id").equal(new ObjectId(illHistoryInfoId));
        Long now = System.currentTimeMillis();

        UpdateOperations<IllHistoryInfo> ops = dsForRW.createUpdateOperations(IllHistoryInfo.class);

        IllHistoryInfo illHistoryInfo = query.get();
        List<Diagnosis> diagnosis = illHistoryInfo.getDiagnosis();

        Set<Diagnosis> set = Sets.newHashSet();
        if (!CollectionUtils.isEmpty(diagnosis)) {
            set.addAll(diagnosis);
        }

        Diagnosis diagnosisThis = new Diagnosis();
        diagnosisThis.setContent(content);

        if (StringUtils.isBlank(content) && StringUtils.isBlank(diseaseId)) {
            return;
        }

        diagnosisThis.setCreateTime(now);
        diagnosisThis.setUpdateTime(now);

        if (null != doctorId) {
			diagnosisThis.setFromDoctorId(doctorId);
		}

		if (!Objects.isNull(orderId)) {
            diagnosisThis.setOrderId(orderId);
        }

        if (StringUtil.isNotEmpty(diseaseId)) {
        	diagnosisThis.setDiseaseId(diseaseId);

        	if (diseaseId.contains(",")) {
                List<String> diseaseNames = Lists.newArrayList();
                String[] diseaseIdArray = diseaseId.split(",");
                for (String dis : diseaseIdArray) {
                    DiseaseType diseaseType = diseaseTypeRepository.findByDiseaseId(dis);
                    diseaseNames.add(diseaseType.getName());
                }

                String s = StringUtils.join(diseaseNames, ",");
                diagnosisThis.setDiseaseName(s);
            } else {
                DiseaseType diseaseType = diseaseTypeRepository.findByDiseaseId(diseaseId);
                if (null != diseaseType) {
                    diagnosisThis.setDiseaseName(diseaseType.getName());
                }
            }
		}

        set.add(diagnosisThis);

        if (set != null && set.size() > 0) {
            ops.set("diagnosis", set);
        }

        dsForRW.update(query, ops);
    }

    public void updateDiagnosis(String illHistoryInfoId, String content, String diseaseId, Integer orderId) {
        Query<IllHistoryInfo> query = dsForRW.createQuery(IllHistoryInfo.class).field("_id").equal(new ObjectId(illHistoryInfoId));

        UpdateOperations<IllHistoryInfo> ops = dsForRW.createUpdateOperations(IllHistoryInfo.class);

        IllHistoryInfo illHistoryInfo = query.get();
        List<Diagnosis> diagnoses = illHistoryInfo.getDiagnosis();
        Diagnosis temp = null;
        Set<Diagnosis> set = Sets.newHashSet();
        if (diagnoses != null && diagnoses.size() > 0) {
            for (Diagnosis diagnosis : diagnoses) {
                if (diagnosis.getOrderId() != null && diagnosis.getOrderId().intValue() == orderId.intValue()) {
                    temp = diagnosis;
                }
                set.add(diagnosis);
            }
            temp.setContent(content);
            temp.setDiseaseId(diseaseId);

            if (StringUtil.isNotEmpty(diseaseId)) {
                temp.setDiseaseId(diseaseId);
                if (diseaseId.contains(",")) {
                    List<String> diseaseNames = Lists.newArrayList();
                    String[] diseaseIdArray = diseaseId.split(",");
                    for (String dis : diseaseIdArray) {
                        DiseaseType diseaseType = diseaseTypeRepository.findByDiseaseId(dis);
                        diseaseNames.add(diseaseType.getName());
                    }

                    String s = StringUtils.join(diseaseNames, ",");
                    temp.setDiseaseName(s);
                } else {
                    DiseaseType diseaseType = diseaseTypeRepository.findByDiseaseId(diseaseId);
                    if (null != diseaseType) {
                        temp.setDiseaseName(diseaseType.getName());
                    }
                }
            }

            temp.setUpdateTime(System.currentTimeMillis());
            set.add(temp);
        }

        if (set != null && set.size() > 0) {
            ops.set("diagnosis", set);
        } else {
            ops.unset("diagnosis");
        }

        dsForRW.update(query, ops);
    }

    @Override
    public IllHistoryInfo getByDoctorIdAndPatientId(Integer doctorId, Integer patientId) {
        return dsForRW.createQuery(IllHistoryInfo.class).field("doctorId").equal(doctorId).field("patientId").equal(patientId).get();
    }

    @Override
    public List<IllHistoryInfo> getByPatientId(Integer patientId) {
        return dsForRW.createQuery(IllHistoryInfo.class).field("patientId").equal(patientId).asList();
    }


    @Override
    public IllHistoryInfo findById(String illHistoryInfoId) {
        return dsForRW.createQuery(IllHistoryInfo.class).field("_id").equal(new ObjectId(illHistoryInfoId)).get();
    }

    @Override
    public void updateIllContentInfo(String illHistoryInfoId, IllContentInfo illContentInfo) {
        Query<IllHistoryInfo> query = dsForRW.createQuery(IllHistoryInfo.class).field("_id").equal(new ObjectId(illHistoryInfoId));
        Long now = System.currentTimeMillis();
        UpdateOperations<IllHistoryInfo> ops = dsForRW.createUpdateOperations(IllHistoryInfo.class);
        if (StringUtils.isNotEmpty(illContentInfo.getIllDesc())) {
            ops.set("illContentInfo.illDesc", illContentInfo.getIllDesc());
        } else {
            ops.unset("illContentInfo.illDesc");
        }
        if (illContentInfo.getPics() != null) {
            ops.set("illContentInfo.pics", illContentInfo.getPics());
        } else {
            ops.unset("illContentInfo.pics");
        }
        if (StringUtils.isNotEmpty(illContentInfo.getTreatment())) {
            ops.set("illContentInfo.treatment", illContentInfo.getTreatment());
        } else {
            ops.unset("illContentInfo.treatment");
        }
        ops.set("illContentInfo.updateTime", now);
        ops.set("illContentInfo.updater", ReqUtil.instance.getUserId());
        ops.set("updateTime", now);
        dsForRW.update(query, ops);
    }

    @Override
    public List<IllHistoryInfo> getByMyPatientIds(Integer doctorId, List<Integer> patientIds) {
        return dsForRW.createQuery(IllHistoryInfo.class).field("doctorId").equal(doctorId).field("patientId").in(patientIds).asList();
    }

    @Override
    public void updatePatientInfoId(String illHistoryInfoId, String patientInfoId) {
        Query<IllHistoryInfo> query = dsForRW.createQuery(IllHistoryInfo.class).field("_id").equal(new ObjectId(illHistoryInfoId));
        Long now = System.currentTimeMillis();
        UpdateOperations<IllHistoryInfo> ops = dsForRW.createUpdateOperations(IllHistoryInfo.class);
        if (StringUtils.isNotEmpty(patientInfoId)) {
            ops.set("patientInfoId", patientInfoId);
        }
        ops.set("updateTime", now);
        dsForRW.update(query, ops);
    }

    @Override
    public List<IllHistoryInfo> getInfosByDoctorIdAndPatientId(Integer doctorId, Integer patientId) {
        Query<IllHistoryInfo> query=dsForRW.createQuery(IllHistoryInfo.class);
        if(null!=doctorId){
            List<Integer> doctorIds= Lists.newArrayList();
            doctorIds.add(doctorId);
            query.or(
                    query.criteria("doctorId").equal(doctorId.intValue()),
                    query.criteria("doctorIds").in(doctorIds)
            );
        }
        if(null!=patientId){
            query.filter("patientId",patientId.intValue());
        }
        query.order("-updateTime");

        return query.asList();
    }



    @Override
	public void updateIllHistoryInfo(IllHistoryInfo illHistoryInfo) {
		if (StringUtils.isEmpty(illHistoryInfo.getId())) {
			return;
		}
        Query<IllHistoryInfo> query = dsForRW.createQuery(IllHistoryInfo.class).field("_id").equal(new ObjectId(illHistoryInfo.getId()));
        Long now = System.currentTimeMillis();
        UpdateOperations<IllHistoryInfo> ops = dsForRW.createUpdateOperations(IllHistoryInfo.class);
        
        if (StringUtils.isNotEmpty(illHistoryInfo.getBriefHistroy())) {
        	ops.set("briefHistroy", illHistoryInfo.getBriefHistroy());
		} else {
            ops.unset("briefHistroy");
        }
        
        ops.set("updateTime", now);
        
        if (!CollectionUtils.isEmpty(illHistoryInfo.getDoctorIds())) {
        	ops.set("doctorIds",illHistoryInfo.getDoctorIds());
		}
        
        dsForRW.update(query, ops);
	}

	@Override
	public IllHistoryRecord findCareByOrderId(Integer orderId) {
		Query<IllHistoryRecord> q = 
				dsForRW.createQuery(IllHistoryRecord.class).field("recordCare.orderId").equal(orderId);
		return q.get();
	}

    @Override
    public IllHistoryInfo getById(String id) {
        return dsForRW.createQuery(IllHistoryInfo.class).field("_id").equal(new ObjectId(id)).get();
    }

    @Override
    public List<IllHistoryInfo> findAll() {
        return dsForRW.createQuery(IllHistoryInfo.class).asList();
    }

    @Override
    public void fixStartWithNullData(String illHistoryInfoId, String birefHistory) {
        Query<IllHistoryInfo> query = dsForRW.createQuery(IllHistoryInfo.class).field("_id").equal(new ObjectId(illHistoryInfoId));
        UpdateOperations<IllHistoryInfo> ops = dsForRW.createUpdateOperations(IllHistoryInfo.class);
        if (StringUtils.isNotBlank(birefHistory)) {
            ops.set("briefHistroy", birefHistory);
        } else {
            ops.unset("briefHistroy");
        }
        dsForRW.update(query, ops);
    }

    @Override
    public void removeNullDoctorId(String illHistoryInfoId) {
        Query<IllHistoryInfo> query = dsForRW.createQuery(IllHistoryInfo.class).field("_id").equal(new ObjectId(illHistoryInfoId));
        dsForRW.delete(query);
    }

}
