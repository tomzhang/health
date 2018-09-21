package com.dachen.health.user.dao.impl;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.vo.OperationPassword;
import com.dachen.health.user.dao.IValidationDao;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ValidationDaoImpl extends NoSqlRepository implements IValidationDao {
    @Override
    public OperationPassword getValidation(String type) {
        Query<OperationPassword> query = dsForRW.createQuery(OperationPassword.class).field("type").equal(type);
        return query.get();

    }
}
