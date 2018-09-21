package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.Group2;
import com.dachen.health.commons.vo.User;

public class MobileDoctorVO extends MobileUserVO {
    private String title;
    private String intro;
    private String skill;
    private Integer cureNum;

    /**
     * 医生加入的科室详情
     */
    private MobileGroupVO dept;

    public MobileDoctorVO() {
    }

    public MobileDoctorVO(User user) {
        super(user);

        if (null != user.getDoctor()) {
            this.title = user.getDoctor().getTitle();
            this.intro = user.getDoctor().getIntroduction();
            this.skill = user.getDoctor().getSkill();
        }
    }

    public MobileDoctorVO(User user, Group2 group2) {
        super(user);

        if (null != user.getDoctor()) {
            this.title = user.getDoctor().getTitle();
            this.intro = user.getDoctor().getIntroduction();
            this.skill = user.getDoctor().getSkill();

            if (null != group2) {
                dept = new MobileGroupVO(group2);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public Integer getCureNum() {
        return cureNum;
    }

    public void setCureNum(Integer cureNum) {
        this.cureNum = cureNum;
    }

    public MobileGroupVO getDept() {
        return dept;
    }

    public void setDept(MobileGroupVO dept) {
        this.dept = dept;
    }
}
