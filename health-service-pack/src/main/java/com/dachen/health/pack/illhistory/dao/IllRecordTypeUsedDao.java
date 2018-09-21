package com.dachen.health.pack.illhistory.dao;

import com.dachen.health.pack.illhistory.entity.po.IllRecordTypeUsed;

/**
 * Created by fuyongde on 2016/12/5.
 */
public interface IllRecordTypeUsedDao {

    /**
     * 查询医生常用的病程类型
     * @param doctorId 医生id
     * @return
     */
    IllRecordTypeUsed findByDoctorId(Integer doctorId);

    IllRecordTypeUsed insert(IllRecordTypeUsed illRecordTypeUsed);

    void update(Integer doctorId, IllRecordTypeUsed illRecordTypeUsed);
}
