package com.dachen.health.permission.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.auto.dao.impl.BaseDaoImpl;
import com.dachen.health.permission.dao.IRoleDao;
import com.dachen.health.permission.entity.po.Role;
import com.dachen.health.permission.enums.PermissionEnum.RoleStatus;
import com.dachen.util.MongodbUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/13 14:01 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Repository
public class RoleDaoImpl extends BaseDaoImpl<Role> implements IRoleDao {

    @Override
    public void enable(String id) {
        if (StringUtils.isBlank(id)) {
            throw new ServiceException("id is blank!!!");
        }
        Query<Role> query = dsForRW.createQuery(Role.class);
        query.field(Mapper.ID_KEY).equal(new ObjectId(id));

        UpdateOperations<Role> ops = dsForRW.createUpdateOperations(Role.class);
        ops.set("status", RoleStatus.ENABLED.getIndex());
        dsForRW.update(query, ops);
    }

    @Override
    public void disable(String id) {
        if (StringUtils.isBlank(id)) {
            throw new ServiceException("id is blank!!!");
        }
        Query<Role> query = dsForRW.createQuery(Role.class);
        query.field(Mapper.ID_KEY).equal(new ObjectId(id));

        UpdateOperations<Role> ops = dsForRW.createUpdateOperations(Role.class);
        ops.set("status", RoleStatus.DISABLED.getIndex());
        dsForRW.update(query, ops);
    }

    @Override
    public List<Role> getAll() {
        Query<Role> query = dsForRW.createQuery(Role.class);
        return query.asList();
    }

    @Override
    public PageVO getAllPaging(Integer pageIndex, Integer pageSize) {

        DBObject userQuery = new BasicDBObject();
        DBCursor cursor = dsForRW.getDB().getCollection("t_role").find(userQuery);

        //排序
        DBObject sortDBObject = new BasicDBObject();
        sortDBObject.put("roleName",1);
        cursor.sort(sortDBObject);
        //分页查询
        cursor.skip(pageIndex * pageSize).limit(pageSize);

        //总数
        Integer count = cursor.count();

        List<Role> roleList = new ArrayList<>();
        //循环指针
        while (cursor.hasNext()) {
            DBObject object = cursor.next();

            Role role = JSONObject.parseObject(JSON.toJSONString(object), Role.class);
            role.setId(MongodbUtil.getString(object, "_id"));
            roleList.add(role);
        }

        PageVO pageVO = new PageVO();
        pageVO.setPageIndex(pageIndex);
        pageVO.setPageSize(pageSize);
//        Query<Role> query = dsForRW.createQuery(Role.class);
//        long total = query.countAll();
//        query.offset(pageIndex * pageSize);
//        query.limit(pageSize);
        pageVO.setTotal(count.longValue());
        pageVO.setPageData(roleList);
        return pageVO;
    }

    @Override
    public List<Role> getByIds(List<String> ids) {
        Query<Role> query = dsForRW.createQuery(Role.class);
        List<ObjectId> fParams = new ArrayList<>();
        for (String id : ids) {
            fParams.add(new ObjectId(id));
        }
        query.field(Mapper.ID_KEY).in(fParams);
        return query.asList();
    }
}
