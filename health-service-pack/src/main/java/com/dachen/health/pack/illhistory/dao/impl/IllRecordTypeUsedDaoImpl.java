package com.dachen.health.pack.illhistory.dao.impl;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.pack.illhistory.dao.IllRecordTypeUsedDao;
import com.dachen.health.pack.illhistory.entity.po.IllRecordTypeUsed;
import com.dachen.im.server.constant.SysConstant;
import com.dachen.util.SystemUtils;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

/**
 * Created by fuyongde on 2016/12/5.
 */
@Repository
public class IllRecordTypeUsedDaoImpl extends NoSqlRepository implements IllRecordTypeUsedDao {

    @Override
    public IllRecordTypeUsed findByDoctorId(Integer doctorId) {
        return dsForRW.createQuery(IllRecordTypeUsed.class).field("doctorId").equal(doctorId).get();
    }

    @Override
    public IllRecordTypeUsed insert(IllRecordTypeUsed illRecordTypeUsed) {
        Long now = System.currentTimeMillis();
        illRecordTypeUsed.setCreateTime(now);
        illRecordTypeUsed.setUpdateTime(now);
        dsForRW.insert(illRecordTypeUsed);
        return illRecordTypeUsed;
    }

    @Override
    public void update(Integer doctorId, IllRecordTypeUsed illRecordTypeUsed) {
        Query<IllRecordTypeUsed> query = dsForRW.createQuery(IllRecordTypeUsed.class).field("doctorId").equal(doctorId);

        UpdateOperations<IllRecordTypeUsed> ops = dsForRW.createUpdateOperations(IllRecordTypeUsed.class);
        ops.set("updateTime", System.currentTimeMillis());
        if (illRecordTypeUsed.getRecordTypeVos() != null && illRecordTypeUsed.getRecordTypeVos().size() > 0) {
            ops.set("recordTypeVos", illRecordTypeUsed.getRecordTypeVos());
        }

        dsForRW.update(query, ops);
    }
}
