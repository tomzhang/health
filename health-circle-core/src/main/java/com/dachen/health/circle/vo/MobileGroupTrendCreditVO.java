package com.dachen.health.circle.vo;


import com.dachen.health.circle.entity.GroupTrendCommentCredit;
import com.dachen.health.circle.entity.GroupTrendCredit;

import java.io.Serializable;

public class MobileGroupTrendCreditVO implements Serializable {
    /**
     * 打赏的学分
     */
    private Integer credit;
    private Long createTime;
    private MobileDoctorVO user;

    public MobileGroupTrendCreditVO() {

    }

    public MobileGroupTrendCreditVO(GroupTrendCredit commentCredit) {
        this.credit = commentCredit.getCredit();
        this.createTime = commentCredit.getCreateTime();
        if (null != commentCredit.getUser()) {
            this.user = new MobileDoctorVO(commentCredit.getUser());
        }
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public MobileDoctorVO getUser() {
        return user;
    }

    public void setUser(MobileDoctorVO user) {
        this.user = user;
    }
}
