package com.dachen.health.permission.dao.impl;

import com.dachen.health.auto.dao.impl.BaseDaoImpl;
import com.dachen.health.permission.dao.IPermissionDao;
import com.dachen.health.permission.entity.po.Permission;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/13 13:59 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Repository
public class PermissionDaoImpl extends BaseDaoImpl<Permission> implements IPermissionDao {

    @Override
    public List<Permission> getAll() {
        Query<Permission> query = dsForRW.createQuery(Permission.class);
        return query.asList();
    }

    @Override
    public List<Permission> getByIds(List<String> ids) {
        Query<Permission> query = dsForRW.createQuery(Permission.class);
        query.field(Mapper.ID_KEY).in(ids);
        return query.asList();
    }
}
