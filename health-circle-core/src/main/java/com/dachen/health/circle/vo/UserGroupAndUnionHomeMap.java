package com.dachen.health.circle.vo;

public class UserGroupAndUnionHomeMap extends UserGroupAndUnionMap {

    private Integer ifUnionCanCreate;

    public UserGroupAndUnionHomeMap() {
    }

    public UserGroupAndUnionHomeMap(UserGroupAndUnionMap map) {
        super(map.getUserId(), map.getGroupDoctors(), map.getUnionMembers());
    }


    public Integer getIfUnionCanCreate() {
        return ifUnionCanCreate;
    }

    public void setIfUnionCanCreate(Integer ifUnionCanCreate) {
        this.ifUnionCanCreate = ifUnionCanCreate;
    }

}
