package com.dachen.health.pack.patient.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.dachen.health.pack.patient.model.PatientDisease;
import com.dachen.health.pack.patient.model.PatientDiseaseExample;
import com.dachen.health.pack.patient.model.PatientDiseaseParam;

public interface PatientDiseaseMapper {
    int countByExample(PatientDiseaseExample example);

    int deleteByExample(PatientDiseaseExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(PatientDisease record);

    int insertSelective(PatientDisease record);

    List<PatientDisease> selectByExample(PatientDiseaseExample example);

    PatientDisease selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") PatientDisease record, @Param("example") PatientDiseaseExample example);

    int updateByExample(@Param("record") PatientDisease record, @Param("example") PatientDiseaseExample example);

    int updateByPrimaryKeySelective(PatientDisease record);

    int updateByPrimaryKey(PatientDisease record);
    
    List<Map<String, Object>> findByParam(PatientDiseaseParam param);
    
    /**
     * 根据医生和集团查询患者id
     * @param param
     * @return
     */
    List<Integer> findPatientIdByDoctorAndGroup(PatientDiseaseParam param);
}