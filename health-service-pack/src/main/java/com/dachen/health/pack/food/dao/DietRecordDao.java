package com.dachen.health.pack.food.dao;

import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.food.entity.po.DietRecord;

/**
 * Created by fuyongde on 2017/2/23.
 */
public interface DietRecordDao {

    /**
     * 患者新增一条进食记录
     * @param dietRecord
     */
    void save(DietRecord dietRecord);

    PageVO findByPatientId(Integer patientId, Integer pageIndex, Integer pageSize);
}
