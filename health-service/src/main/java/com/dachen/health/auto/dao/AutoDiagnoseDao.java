package com.dachen.health.auto.dao;

import com.dachen.health.auto.entity.po.Body;
import com.dachen.health.auto.entity.po.Disease;
import com.dachen.health.auto.entity.po.Symptoms;
import com.dachen.health.auto.entity.po.SymptomsDisease;

import java.util.List;

/**
 * Created by liming on 2016/11/10.
 */
public interface AutoDiagnoseDao extends BaseDao{
    /**
     * 获取身体部位列表
     * @return
     */
    public List<Body> getBodyParsts();

    public Body getBodyByName(String name);

    /**
     * 通过身体部位列表，获取对应的症状列表
     * @param bodyCode
     * @return
     */
    public List<Symptoms> getDiseaseByBodyCode(String bodyCode,String sex);

    public void saveSymptomes(Symptoms symptoms);


    /**
     * 获取病症和疾病库之间的中间表
     * @param sympCode
     * @return
     */
    public List<SymptomsDisease> getSymptomsDiseaseBySympCode(String sympCode);

    public SymptomsDisease getSymptomsDiseaseBySympCodeAndDiseaseCode(String sympCode,String diseaseCode);

   /**
     * 通过症状列表，获取对应的疾病列表
     * @param symptomsCode
     * @return
     */
    public List<Disease> getDisease(String symptomsCode);

    public List<Disease> getDiseaseByName(String name);

    public void saveDisease(Disease disease);

    public void saveSymptomesDisease(SymptomsDisease symptomsDisease);

    public Symptoms getSymptomesByNameAndSex(String name,String sex);



}
