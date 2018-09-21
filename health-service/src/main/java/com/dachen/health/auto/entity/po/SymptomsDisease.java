package com.dachen.health.auto.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by liming on 2016/11/17.
 */
@Entity(value = "b_auto_symptoms_disease",noClassnameStored = true)
public class SymptomsDisease {
    @Id
    private String id;
    private String symptomsCode;
    private String diseaseCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymptomsCode() {
        return symptomsCode;
    }

    public void setSymptomsCode(String symptomsCode) {
        this.symptomsCode = symptomsCode;
    }

    public String getDiseaseCode() {
        return diseaseCode;
    }

    public void setDiseaseCode(String diseaseCode) {
        this.diseaseCode = diseaseCode;
    }
}
