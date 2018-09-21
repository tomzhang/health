package com.dachen.health.pack.consult.Service;

import java.util.List;
import java.util.Map;
import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.consult.entity.po.GroupConsultationPack;
import com.dachen.health.pack.consult.entity.vo.ConsultationPackPageVo;
import com.dachen.health.pack.consult.entity.vo.ConsultationPackParams;
import com.dachen.health.user.entity.vo.UserInfoVO;
import com.dachen.sdk.exception.HttpApiException;

public interface ConsultationPackService {

	PageVO getConsultPackList(ConsultationPackParams consultationPackParams);

	ConsultationPackPageVo getConsultPackDetail(String consultationPackId);

	void updateConsultPack(ConsultationPackParams param) throws HttpApiException;

	GroupConsultationPack addConsultPack(ConsultationPackParams consultationPackParams) throws HttpApiException;

	List<UserInfoVO> getDoctorList(String consultationPackId);

	List<Integer> getNotInCurrentPackDoctorIds(String groupId, String consultationPackId);

	int deleteConsultPack(String consultationPackId);

	void openService(String groupId);

	Map<String, Integer> getConsultationPrice(int userId);

	List<Map<String, Object>> getGroupListByDoctorId(Integer doctorId, Integer orderId);
	
	String getConsultationOrderGroupByDoctorId(Integer doctorId);

	Map<String,String> getConsultationPackByGroupIdAndDoctorId(String groupId, Integer conDoctorId, Long price);

	Integer getOpenConsultation(String groupId);

	Map<String, String> getConsultationPackById(String consultationPackId, Long price);

	PageVO getPlatformSelectedDoctors(String groupId, String consultationPackId, String keyWord, Integer pageIndex, Integer pageSize);
	
	PageVO getConsultationPackList(ConsultationPackParams consultationPackParams);
	
	PageVO getMyConsultationPackList(ConsultationPackParams consultationPackParams);

	void syncData() throws HttpApiException;

}
