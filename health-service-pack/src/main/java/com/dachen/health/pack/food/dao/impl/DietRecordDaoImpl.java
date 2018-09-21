package com.dachen.health.pack.food.dao.impl;

import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.pack.food.dao.DietRecordDao;
import com.dachen.health.pack.food.entity.po.DietRecord;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.util.List;
import java.util.Objects;

/**
 * Created by fuyongde on 2017/2/23.
 */
@Repository
public class DietRecordDaoImpl extends NoSqlRepository implements DietRecordDao {

    @Override
    public void save(DietRecord dietRecord) {
        Long now = Clock.systemUTC().millis();
        dietRecord.setCreateTime(now);
        dietRecord.setUpdateTime(now);
        dsForRW.insert(dietRecord);
    }

    @Override
    public PageVO findByPatientId(Integer patientId, Integer pageIndex, Integer pageSize) {

        if (Objects.isNull(pageIndex)) {
            pageIndex = 0;
        }

        if (Objects.isNull(pageSize)) {
            pageSize = 10;
        }

        Query<DietRecord> query = dsForRW.createQuery(DietRecord.class);
        query.field("patientId").equal(patientId);

        long count = query.countAll();

        int skip = pageIndex * pageSize;
        skip = skip < 0 ? 0 : skip;

        List<DietRecord> dietRecords = query.order("-dietTime").offset(skip).limit(pageSize).asList();

        PageVO pageVO = new PageVO();
        pageVO.setPageIndex(pageIndex);
        pageVO.setPageSize(pageSize);
        pageVO.setTotal(count);
        pageVO.setPageData(dietRecords);

        return pageVO;
    }
}
