package com.dachen.health.permission.service;

import com.dachen.health.permission.entity.po.Permission;
import java.util.List;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/13 14:48 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public interface IPermissionService {
    List<Permission> getAll();
    List<Permission> getByIds(List<String> ids);
}
