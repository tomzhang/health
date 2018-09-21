package com.dachen.health.permission.dao;

import com.dachen.commons.page.PageVO;
import com.dachen.health.auto.dao.BaseDao;
import com.dachen.health.permission.entity.po.Permission;
import com.dachen.health.permission.entity.po.Role;
import java.util.List;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/13 14:00 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public interface IRoleDao extends BaseDao<Role> {
    void enable(String id);
    void disable(String id);
    List<Role> getAll();
    PageVO getAllPaging(Integer pageIndex, Integer pageSize);
    List<Role> getByIds(List<String> ids);
}
