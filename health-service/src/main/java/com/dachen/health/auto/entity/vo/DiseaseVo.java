package com.dachen.health.auto.entity.vo;

import com.dachen.health.disease.entity.DiseaseType;

import java.util.List;

/**
 * Created by liming on 2016/11/17.
 */
public class DiseaseVo {
    private List<String> diseaseIds;
    private List<DiseaseType> disease;

    public List<String> getDiseaseIds() {
        return diseaseIds;
    }

    public void setDiseaseIds(List<String> diseaseIds) {
        this.diseaseIds = diseaseIds;
    }

    public List<DiseaseType> getDisease() {
        return disease;
    }

    public void setDisease(List<DiseaseType> disease) {
        this.disease = disease;
    }
}
