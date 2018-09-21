package com.dachen.health.recommand.entity.vo;

/**
 * Created by liming on 2017/2/13.
 */
public class DiseaseLaberCountVo {
    //疾病id
    private String diseaseId;
    //疾病名称
    private String diseaseName;
    //标签下面的人数
    private Long count;

    public String getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(String diseaseId) {
        this.diseaseId = diseaseId;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
