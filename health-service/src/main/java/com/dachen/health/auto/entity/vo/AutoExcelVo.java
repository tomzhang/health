package com.dachen.health.auto.entity.vo;

/**
 * Created by liming on 2016/11/17.
 */
public class AutoExcelVo {
    /**
     * 性别
     */
    private String sex;
    /**
     * 部位名称
     */
    private String bodyName;
    /**
     * 病症名称
     */
    private String symptomsName;
    /**
     * 疾病名称
     */
    private String diseaseName;
    /**
     * 疾病描述
     */
    private String diseaseContent;
    /**
     * 疾病顺序
     */
    private int num;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBodyName() {
        return bodyName;
    }

    public void setBodyName(String bodyName) {
        this.bodyName = bodyName;
    }

    public String getSymptomsName() {
        return symptomsName;
    }

    public void setSymptomsName(String symptomsName) {
        this.symptomsName = symptomsName;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseContent() {
        return diseaseContent;
    }

    public void setDiseaseContent(String diseaseContent) {
        this.diseaseContent = diseaseContent;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
