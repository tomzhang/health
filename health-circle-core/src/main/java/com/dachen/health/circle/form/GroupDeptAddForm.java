package com.dachen.health.circle.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
public class GroupDeptAddForm {

    @NotEmpty(message = "hospitalId不能为空")
    private String hospitalId;

    @NotEmpty(message = "deptId不能为空")
    private String deptId;

    /**
     * 子科室名称，可以不填
     */
    @Length(min = 0, max = 30, message = "childName长度为30以内")
    private String childName;

    private String intro;

    private String logoPicUrl;

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getLogoPicUrl() {
        return logoPicUrl;
    }

    public void setLogoPicUrl(String logoPicUrl) {
        this.logoPicUrl = logoPicUrl;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }
}
