package com.dachen.health.pack.consult.Service;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.pack.consult.entity.po.ConsultationApplyFriend;
import com.dachen.health.pack.consult.entity.vo.CategoryFriendsPageVo;
import com.dachen.health.pack.consult.entity.vo.DoctorDetail;
import com.dachen.health.pack.consult.entity.vo.OrderUserInfoVo;
import com.dachen.sdk.exception.HttpApiException;

public interface ConsultationFriendService {

	DoctorDetail searchAssistantDoctors(String number);

	PageVO searchConsultationDoctors(Integer areaCode, String name, String deptId, Integer pageIndex, Integer pageSize);

	DoctorDetail doctorDetail(Integer doctorId,Integer roleType);

	void applyFriends(ConsultationApplyFriend consultationApplyFriend);

	List<CategoryFriendsPageVo> getFriendsByRoleType(Integer doctorId, Integer roleType, Integer pageEnterType);

	void processFriendsApply(String consultationApplyFriendId, Integer applyStatus);

	boolean isSpecialist(Integer doctorId);

	PageVO getPatientIllcaseList(Integer doctorId, Integer pageIndex, Integer pageSize);

	Integer getConsultationDoctorNum(Integer doctorId);

	Integer getUnionDoctorNum(Integer doctorId);

	Integer getDoctorApplyNum(Integer doctorId, Integer applyType);

	PageVO getApplyFriendByRoleType(Integer doctorId, Integer roleType, Integer pageIndex, Integer pageSize);

	void collectOperate(Integer unionDoctorId, Integer consultationDoctorId, Integer operateIndex);

	List<OrderUserInfoVo> getConsultationMember(Integer orderId);

	void notifySpecialist(Integer orderId) throws HttpApiException;

	void sendDirective(Integer orderId) throws HttpApiException;

	PageVO searchConsultationDoctorsByKeyword(String keyword, Integer pageIndex, Integer pageSize);

	PageVO getFriendList(Integer pageIndex, Integer pageSize);

	PageVO searchDoctors(Integer areaCode, String name, String deptId, Integer pageIndex, Integer pageSize);

	Long getFriendsNum(int userId);

	String getMainGroupName(Integer userId);
	
}
