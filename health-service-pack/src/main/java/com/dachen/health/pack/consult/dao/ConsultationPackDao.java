package com.dachen.health.pack.consult.dao;

import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

import com.dachen.health.pack.consult.entity.po.GroupConsultationPack;
import com.dachen.health.pack.consult.entity.vo.ConsultationPackParams;

public interface ConsultationPackDao {

	long getTotal(ConsultationPackParams consultationPackParams);

	List<GroupConsultationPack> getConsultPackList(ConsultationPackParams consultationPackParams);
	
	
	long getTotal(ConsultationPackParams consultationPackParams, List<ObjectId> consultationIds,boolean isNotIn);
	
	List<GroupConsultationPack> getConsultPackList(ConsultationPackParams consultationPackParams , List<ObjectId> consultationIds,int skipNum,boolean isNotIn);

	GroupConsultationPack getConsultPackDetail(String consultationPackId);

	void updateConsultPack(GroupConsultationPack groupConsultationPack);

	GroupConsultationPack addConsultPack(GroupConsultationPack groupConsultationPack);

	List<Integer> getDoctorIds(String consultationPackId);

	List<Integer> getNotInCurrentPackDoctorIds(String groupId, String consultationPackId);

	void deleteConsultPack(String consultationPackId);

	int getMinFeeByDoctorId(int doctorId);

	Set<Integer> getAllConsultationDoctorIds();

	int getMaxFeeByDoctorId(int doctorId);

	boolean existsConsultationPack(Integer doctorId);

	List<String> getGroupIdsByDoctorId(Integer doctorId);

	void removeDoctor(String groupId, Integer doctorId);

	GroupConsultationPack getConsultationPackByGroupIdAndDoctorId(String groupId, Integer doctorId);

	GroupConsultationPack getById(String consultationPackId);

	Object findOneOkPack(Integer doctorId);
	
	List<GroupConsultationPack> getConsultationPackList(Integer doctorId);

}
