package com.dachen.health.group.group.entity.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author 钟良
 * @desc
 * @date:2017/6/13 22:19 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class GroupDeptVO implements Serializable {
    private String id;
    private String name;
    private List<DeptVO> deptList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DeptVO> getDeptList() {
        return deptList;
    }

    public void setDeptList(
        List<DeptVO> deptList) {
        this.deptList = deptList;
    }

}
