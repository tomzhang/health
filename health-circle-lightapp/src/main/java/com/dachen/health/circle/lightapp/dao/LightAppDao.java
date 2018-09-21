package com.dachen.health.circle.lightapp.dao;

import com.dachen.health.circle.lightapp.entity.LightApp;
import com.dachen.health.circle.lightapp.entity.OrgLightApp;
import com.dachen.health.circle.lightapp.entity.UserLightApp;
import java.util.List;
import javax.annotation.Resource;
import org.bson.types.ObjectId;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

/**
 * @author sharp
 * @desc
 * @date:2017/6/1211:25 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Repository
public class LightAppDao {

    @Resource(name = "dsForRW")
    protected AdvancedDatastore dsForRW;

    public LightApp findOne(String id) {
        return dsForRW.createQuery(LightApp.class).filter("_id", new ObjectId(id)).get();
    }

    public LightApp findByAppId(String appId) {
        return dsForRW.createQuery(LightApp.class).filter("appId", appId).get();
    }

    public List<LightApp> findAll() {
        return dsForRW.createQuery(LightApp.class).asList();
    }

    public List<LightApp> findByStatus(Integer status) {
        return dsForRW.createQuery(LightApp.class).filter("status", status).asList();
    }

    public <T> String insert(T entity) {
        Key<T> key = dsForRW.insert(entity);
        return key.getId().toString();
    }

    @Deprecated
    public List<UserLightApp> getPersonApps(Integer userId) {
        return dsForRW.createQuery(UserLightApp.class).filter("userId", userId).asList();
    }

    @Deprecated
    public List<OrgLightApp> getOrgApps(String orgType, List<String> orgIdList) {
        Query<OrgLightApp> q = dsForRW.createQuery(OrgLightApp.class).filter("orgType", orgType);
        if (orgIdList != null && !orgIdList.isEmpty()) {
            q.filter("orgId in ", orgIdList);
        }
        return q.asList();
    }

}
