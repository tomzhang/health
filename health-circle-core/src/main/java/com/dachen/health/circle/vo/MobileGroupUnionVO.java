package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.GroupUnion;

import java.io.Serializable;

public class MobileGroupUnionVO implements Serializable {

    private String unionId;
    private String groupId;
    private String name;
    private String intro;
    private String logoPicUrl;
    private Integer totalMember;
    private Integer totalDoctor;

    private MobileGroupVO group;

    public MobileGroupUnionVO() {
    }

    public MobileGroupUnionVO(GroupUnion union) {
        this.unionId = union.getId().toString();
        this.name = union.getName();
        this.intro = union.getIntro();
        this.logoPicUrl = union.getLogoPicUrl();
        this.groupId = union.getGroupId();
        this.totalMember = union.getTotalMember();
        this.totalDoctor = union.getTotalDoctor();
        if (null != union.getGroup()) {
            this.group = new MobileGroupVO(union.getGroup());
        }
    }

    public Integer getTotalDoctor() {
        return totalDoctor;
    }

    public void setTotalDoctor(Integer totalDoctor) {
        this.totalDoctor = totalDoctor;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getLogoPicUrl() {
        return logoPicUrl;
    }

    public void setLogoPicUrl(String logoPicUrl) {
        this.logoPicUrl = logoPicUrl;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getTotalMember() {
        return totalMember;
    }

    public void setTotalMember(Integer totalMember) {
        this.totalMember = totalMember;
    }

    public MobileGroupVO getGroup() {
        return group;
    }

    public void setGroup(MobileGroupVO group) {
        this.group = group;
    }
}
