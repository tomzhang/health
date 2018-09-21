package com.dachen.health.auto.entity.vo;

import com.dachen.health.auto.entity.po.Symptoms;

import java.util.List;

/**
 * Created by liming on 2016/11/10.
 */
public class BodyDisease {
    /**
     * 身体部位code
     *
     */
    private String bodyCode;
    /**
     * 身体部位名称
     */
    private String name;
    /**
     * 疑似病症
     */
    private List<Symptoms> diseases;

    public String getBodyCode() {
        return bodyCode;
    }

    public void setBodyCode(String bodyCode) {
        this.bodyCode = bodyCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Symptoms> getDiseases() {
        return diseases;
    }

    public void setDiseases(List<Symptoms> diseases) {
        this.diseases = diseases;
    }
}
