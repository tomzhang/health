package com.dachen.health.recommand.dao;

import com.dachen.health.commons.entity.DiseaseLaber;
import com.dachen.health.commons.entity.UserDiseaseLaber;

import java.util.List;
import java.util.Map;

/**
 * Created by liangcs on 2016/12/5.
 */
public interface IDiseaseLaberDao {

    void save(DiseaseLaber diseaseLaber);
    
    void delAllLaber();

    DiseaseLaber findByDiseaseId(String diseaseId);

    DiseaseLaber updateLaber(String id, Map<String,Object> updateFieldMap);

    List<String> getDiseaseIdsByStatus(Integer status);
    public List<DiseaseLaber> getDiseaseLaber();
    
    List<UserDiseaseLaber> findByUserId(Integer userId);
    
    List<UserDiseaseLaber> sortByCreateTime(Integer userId);
     
    UserDiseaseLaber findByUserIdAndDiseaseId(Integer userId, String diseaseId);
    
    void saveUserLaber(UserDiseaseLaber laber);
    
    void delUserLaber(List<String> diseaseIds);
    
    UserDiseaseLaber updateUserLaber(String id,Map<String, Object> updateFieldMap);
    public Long getUserLaberByDiseaseId(String diseaseId);
    
}
