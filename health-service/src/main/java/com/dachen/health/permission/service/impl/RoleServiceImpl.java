package com.dachen.health.permission.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.permission.dao.IRoleDao;
import com.dachen.health.permission.entity.param.RoleParam;
import com.dachen.health.permission.entity.po.Role;
import com.dachen.health.permission.enums.PermissionEnum.RoleStatus;
import com.dachen.health.permission.service.IPermissionService;
import com.dachen.health.permission.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/13 14:53 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private IRoleDao roleDao;
    @Autowired
    private IPermissionService permissionService;

    @Override
    public void insert(RoleParam param) {
        checkParam(param);

        Role role = new Role();
        role.setRoleName(param.getRoleName());
        role.setPermissionList(param.getPermissionList());
        role.setStatus(RoleStatus.ENABLED.getIndex());
        roleDao.insert(role);
    }

    private void checkParam(RoleParam param) {
        if (Objects.isNull(param)) {
            throw new ServiceException("参数不能为空");
        }
        if (Objects.isNull(param.getRoleName())) {
            throw new ServiceException("角色名不能为空");
        }
        if (CollectionUtils.isEmpty(param.getPermissionList())) {
            throw new ServiceException("权限不能为空");
        }
    }

    @Override
    public void update(RoleParam param) {
        checkParam(param);

        Map<String, Object> updateFieldMap = new HashMap<>();
        updateFieldMap.put("roleName", param.getRoleName());
        updateFieldMap.put("permissionList", param.getPermissionList());
        roleDao.update(Role.class, param.getId(), updateFieldMap);
    }

    @Override
    public void enable(String id) {
        Map<String, Object> updateFieldMap = new HashMap<>();
        updateFieldMap.put("status", RoleStatus.ENABLED.getIndex());
        roleDao.update(Role.class, id, updateFieldMap);
    }

    @Override
    public void disable(String id) {
        Map<String, Object> updateFieldMap = new HashMap<>();
        updateFieldMap.put("status", RoleStatus.DISABLED.getIndex());
        roleDao.update(Role.class, id, updateFieldMap);
    }

    @Override
    public List<Role> getAll() {
        return roleDao.getAll();
    }

    @Override
    public PageVO getAllPaging(Integer pageIndex, Integer pageSize) {
        return roleDao.getAllPaging(pageIndex, pageSize);
    }

    @Override
    public List<Role> getByIds(List<String> ids) {
        return roleDao.getByIds(ids);
    }

    @Override
    public Role getById(String id) {
        return roleDao.getByPK(Role.class, id);
    }
}
