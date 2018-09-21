package com.dachen.health.commons.vo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

/**
 * Created by sharp on 2017/11/28.
 * 用户关注的科室
 */
@Entity(value = "user_concern_dept")
public class ConcernDept {

    @Id
    private Integer userId;

    private List<String> deptIds;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<String> getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(List<String> deptIds) {
        this.deptIds = deptIds;
    }

}
