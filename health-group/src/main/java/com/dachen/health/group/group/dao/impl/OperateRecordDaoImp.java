package com.dachen.health.group.group.dao.impl;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.group.group.dao.IOperateRecordDao;
import com.dachen.health.user.entity.po.OperationRecord;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2016/11/29.
 */
@Repository
public class OperateRecordDaoImp extends NoSqlRepository implements IOperateRecordDao {
    @Override
    public void save(OperationRecord record) {
        if (record != null) {
            dsForRW.save(record);
        }
    }
}
