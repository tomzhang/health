package com.dachen.health.pack.illhistory.dao;

import com.dachen.health.pack.illhistory.entity.po.PatientInfo;
import com.dachen.health.user.entity.vo.RelationVO;

import java.util.List;

/**
 * 患者信息的dao层
 * @author fuyongde
 *
 */
public interface PatientInfoDao {
	
	/**
	 * 创建一个患者信息
	 */
	PatientInfo insert(PatientInfo patientInfo);

	PatientInfo findByDoctorIdAndPatientId(Integer doctorId, Integer patientId);

    PatientInfo findById(String patientInfoId);

	void update(String patientInfoId, PatientInfo patientInfo);

	List<PatientInfo> findAllMyPatientInfos(Integer doctorId, List<Integer> patientIds);

	RelationVO getInactiveTag(Integer doctorId, List<Integer> patientId);
}

