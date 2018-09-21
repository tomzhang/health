package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.Group2;

public class MobileGroupHomePageVO extends MobileGroupVO {

    /* 专家数量 */
    private Integer expertNum;

    private Integer ifManager;
    private Integer ifFollow;
    private MobileGroupDoctorVO groupDoctor;
    private MobileGroupFollowVO follow;

    public MobileGroupHomePageVO() {
    }

    public MobileGroupHomePageVO(Group2 group) {
        super(group);
        if (null != group.getGroupUser2()) {
            this.ifManager = 1;
        } else {
            this.ifManager = 0;
        }
        if (null != group.getFollow()) {
            this.ifFollow = 1;
            this.follow = new MobileGroupFollowVO(group.getFollow());
        }else {
            this.ifFollow = 0;
        }
        if (null != group.getGroupDoctor()) {
            this.groupDoctor = new MobileGroupDoctorVO(group.getGroupDoctor());
        }
    }

    public Integer getIfFollow() {
        return ifFollow;
    }

    public void setIfFollow(Integer ifFollow) {
        this.ifFollow = ifFollow;
    }

    public MobileGroupDoctorVO getGroupDoctor() {
        return groupDoctor;
    }

    public void setGroupDoctor(MobileGroupDoctorVO groupDoctor) {
        this.groupDoctor = groupDoctor;
    }

    public Integer getExpertNum() {
        return expertNum;
    }

    public void setExpertNum(Integer expertNum) {
        this.expertNum = expertNum;
    }

    public Integer getIfManager() {
        return ifManager;
    }

    public void setIfManager(Integer ifManager) {
        this.ifManager = ifManager;
    }

    public MobileGroupFollowVO getFollow() {
        return follow;
    }

    public void setFollow(MobileGroupFollowVO follow) {
        this.follow = follow;
    }
}
