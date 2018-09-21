package com.dachen.health.circle.vo;

public class UserGroupAndUnionHomeMapVO extends UserGroupAndUnionMapVO {

    private Integer ifUnionCanCreate;

    public UserGroupAndUnionHomeMapVO() {
    }

    public UserGroupAndUnionHomeMapVO(UserGroupAndUnionHomeMap map) {
        super(map);
        this.ifUnionCanCreate = map.getIfUnionCanCreate();
    }

    public Integer getIfUnionCanCreate() {
        return ifUnionCanCreate;
    }

    public void setIfUnionCanCreate(Integer ifUnionCanCreate) {
        this.ifUnionCanCreate = ifUnionCanCreate;
    }

}
