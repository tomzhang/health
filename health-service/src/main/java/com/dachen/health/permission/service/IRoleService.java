package com.dachen.health.permission.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.permission.entity.param.RoleParam;
import com.dachen.health.permission.entity.po.Role;
import java.util.List;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/13 14:48 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public interface IRoleService {
    void insert(RoleParam param);
    void update(RoleParam param);
    void enable(String id);
    void disable(String id);
    List<Role> getAll();
    PageVO getAllPaging(Integer pageIndex, Integer pageSize);
    List<Role> getByIds(List<String> ids);
    Role getById(String id);
}
