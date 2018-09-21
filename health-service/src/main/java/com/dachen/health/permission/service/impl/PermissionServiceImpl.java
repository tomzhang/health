package com.dachen.health.permission.service.impl;

import com.dachen.health.permission.dao.IPermissionDao;
import com.dachen.health.permission.entity.po.Permission;
import com.dachen.health.permission.service.IPermissionService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/13 14:51 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Service
public class PermissionServiceImpl implements IPermissionService {
    @Autowired
    private IPermissionDao permissionDao;

    @Override
    public List<Permission> getAll() {
        List<Permission> permissionList = permissionDao.getAll();
        Map<String, List<Permission>> permissionMap = permissionList.stream().collect(Collectors.groupingBy(Permission::getPid));
        List<Permission> roots = permissionMap.get("0");
        setChildren(roots, permissionMap);
        return roots;
    }

    private void setChildren(List<Permission> roots, Map<String, List<Permission>> map){
        if(CollectionUtils.isEmpty(roots))
            return;
        for(Permission root : roots){
            String id = root.getId();
            List<Permission> children = map.get(id);
            if (Objects.isNull(children)) {
                children = new ArrayList<>();
            }
            root.setChildren(children);
            setChildren(children, map);
        }
    }

    @Override
    public List<Permission> getByIds(List<String> ids) {
        return permissionDao.getByIds(ids);
    }

}
