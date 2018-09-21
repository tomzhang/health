package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.Group2;

import java.io.Serializable;

public class MobileGroupVO implements Serializable {

    private String groupId;
    private String type;
    private String name;
    private String hospitalName;
    private String deptName;
    private String childName;
    private String intro;
    private String logoPicUrl;

    private Integer totalMember;
    private Integer totalManager;

    private MobileDoctorVO creator;
    /**
     * 发帖数
     */
    private Long postedNumber;

    public MobileGroupVO() {
    }

    public MobileGroupVO(Group2 group) {
        this.groupId = group.getId().toString();
        this.type = group.getType();
        this.name = group.getName();
        this.hospitalName = group.getHospitalName();
        this.deptName = group.getDeptName();
        this.childName = group.getChildName();
        this.intro = group.getIntroduction();
        this.logoPicUrl = group.getLogoUrl();

        if (null != group.getCreatorUser()) {
            this.creator = new MobileDoctorVO(group.getCreatorUser());
        }

        this.totalMember = group.getTotalMember();
        this.totalManager = group.getTotalManager();
    }

    public Long getPostedNumber() {
        return postedNumber;
    }

    public void setPostedNumber(Long postedNumber) {
        this.postedNumber = postedNumber;
    }

    public MobileGroupVO(String deptName) {
        this.deptName = deptName;
    }
    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MobileDoctorVO getCreator() {
        return creator;
    }

    public void setCreator(MobileDoctorVO creator) {
        this.creator = creator;
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

    public Integer getTotalMember() {
        return totalMember;
    }

    public void setTotalMember(Integer totalMember) {
        this.totalMember = totalMember;
    }

    public Integer getTotalManager() {
        return totalManager;
    }

    public void setTotalManager(Integer totalManager) {
        this.totalManager = totalManager;
    }
}
