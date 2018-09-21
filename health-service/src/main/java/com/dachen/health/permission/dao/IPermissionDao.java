package com.dachen.health.permission.dao;

import com.dachen.health.auto.dao.BaseDao;
import com.dachen.health.permission.entity.po.Permission;
import java.util.List;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/13 13:55 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public interface IPermissionDao extends BaseDao<Permission>{
    List<Permission> getAll();
    List<Permission> getByIds(List<String> ids);
}
