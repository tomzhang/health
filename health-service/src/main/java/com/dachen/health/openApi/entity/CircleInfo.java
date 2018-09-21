package com.dachen.health.openApi.entity;

/**
 * Author: xuhuanjie
 * Date: 2018-04-26
 * Time: 15:59
 * Description:
 */
public class CircleInfo {

    private Long id;
    private String name;
    private String sortName;
    private String logo;
    private Integer type;
    private String introduction;
    private Integer memberTotal;
    private Float charge;
    private String arrearageStatus;
    private String deptIds;
    private String deptNames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Integer getMemberTotal() {
        return memberTotal;
    }

    public void setMemberTotal(Integer memberTotal) {
        this.memberTotal = memberTotal;
    }

    public Float getCharge() {
        return charge;
    }

    public void setCharge(Float charge) {
        this.charge = charge;
    }

    public String getArrearageStatus() {
        return arrearageStatus;
    }

    public void setArrearageStatus(String arrearageStatus) {
        this.arrearageStatus = arrearageStatus;
    }

    public String getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(String deptIds) {
        this.deptIds = deptIds;
    }

    public String getDeptNames() {
        return deptNames;
    }

    public void setDeptNames(String deptNames) {
        this.deptNames = deptNames;
    }
}
