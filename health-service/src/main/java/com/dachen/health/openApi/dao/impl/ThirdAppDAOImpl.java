package com.dachen.health.openApi.dao.impl;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.openApi.dao.IThirdAppDAO;
import com.dachen.health.openApi.entity.ThirdApp;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

/**
 * @author liangcs
 * @desc
 * @date:2017/5/3 9:50
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Repository
public class ThirdAppDAOImpl extends NoSqlRepository implements IThirdAppDAO {

    @Override
    public ThirdApp findByAppId(String appId) {

        Query<ThirdApp> query = dsForRW.createQuery(ThirdApp.class);
        query.field("appId").equal(appId);

        return query.get();
    }

    @Override
    public ThirdApp save(ThirdApp thirdApp) {
        dsForRW.save(thirdApp);

        return thirdApp;
    }
}
