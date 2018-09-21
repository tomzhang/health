package com.dachen.health.auto.dao.impl;

import com.dachen.health.auto.dao.AutoDiagnoseDao;
import com.dachen.health.auto.entity.po.Body;
import com.dachen.health.auto.entity.po.Disease;
import com.dachen.health.auto.entity.po.Symptoms;
import com.dachen.health.auto.entity.po.SymptomsDisease;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by liming on 2016/11/10.
 */
@Repository
public class AutoDiagnoseDaoImpl extends BaseDaoImpl implements AutoDiagnoseDao {
    @Override
    public List<Body> getBodyParsts() {
       Query<Body> q= dsForRW.createQuery(Body.class);
        q.filter("enable",true);
        q.order("id");
        return q.asList();
    }

    @Override
    public Body getBodyByName(String name) {
        Query<Body> q= dsForRW.createQuery(Body.class);
        q.filter("enable",true);
        q.filter("name",name);
        q.order("id");
        return q.get();
    }

    @Override
    public List<Symptoms> getDiseaseByBodyCode(String bodyCode,String sex) {
        Query<Symptoms> q=dsForRW.createQuery(Symptoms.class);
        q.filter("bodyCode",bodyCode);
        q.or(q.criteria("sex").contains("0"),q.criteria("sex").contains(sex));
        q.order("id");
        return q.asList();
    }

    @Override
    public void saveSymptomes(Symptoms symptoms) {
        dsForRW.insert(symptoms);
    }

    @Override
    public List<SymptomsDisease> getSymptomsDiseaseBySympCode(String sympCode) {
        Query<SymptomsDisease> q=dsForRW.createQuery(SymptomsDisease.class);
        q.filter("symptomsCode",sympCode);
        return q.asList();
    }

    @Override
    public SymptomsDisease getSymptomsDiseaseBySympCodeAndDiseaseCode(String sympCode, String diseaseCode) {
        Query<SymptomsDisease> q=dsForRW.createQuery(SymptomsDisease.class);
        q.filter("symptomsCode",sympCode);
        q.filter("diseaseCode",diseaseCode);
        return q.get();
    }

    @Override
    public List<Disease> getDisease(String symptomsCode) {
        Query<Disease> q=dsForRW.createQuery(Disease.class);
        q.filter("symptomsCode",symptomsCode);
        return q.asList();
    }

    @Override
    public List<Disease> getDiseaseByName(String name) {
        Query<Disease> q=dsForRW.createQuery(Disease.class);
        q.filter("name",name);
        return q.asList();
    }

    @Override
    public void saveDisease(Disease disease) {
        dsForRW.insert(disease);
    }

    @Override
    public void saveSymptomesDisease(SymptomsDisease symptomsDisease) {
        dsForRW.insert(symptomsDisease);
    }

    @Override
    public Symptoms getSymptomesByNameAndSex(String name, String sex) {
        Query<Symptoms> q=dsForRW.createQuery(Symptoms.class);
        q.filter("name",name);
        q.filter("sex",sex);
        return q.get();
    }
}
