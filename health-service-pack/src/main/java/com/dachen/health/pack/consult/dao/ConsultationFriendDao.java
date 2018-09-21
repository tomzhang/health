package com.dachen.health.pack.consult.dao;

import java.util.List;

import com.dachen.health.pack.consult.entity.po.ConsultationApplyFriend;
import com.dachen.health.pack.consult.entity.po.ConsultationFriend;

public interface ConsultationFriendDao {

	void insertApplyFriend(ConsultationApplyFriend consultationApplyFriend);

	ConsultationApplyFriend getApplyFriendById(String consultationApplyFriendId);

	ConsultationFriend getDoctorFriendByDoctorIdAndRoleType(Integer doctorId, Integer friendType);

	void insertDoctorFriend(ConsultationFriend cf);

	void addFriends(Integer queryDoctorId, Integer pushDoctorId, Integer friendType);

	void updateFriendsApply(String consultationApplyFriendId, Integer applyStatus);
	
	Integer getFriendCount(Integer doctorId, int doctorRole);

	Integer getFriendApplyCount(Integer doctorId, Integer applyType);

	ConsultationApplyFriend getApplyFriendByDoctorId(Integer consultationDoctorId, int unionDoctorId);

	void removeFriendsApply(String consultationApplyFriendId);

	long getApplyFriendCountByRoleType(Integer doctorId, Integer roleType);

	List<ConsultationApplyFriend> getApplyFriendByRoleType(Integer doctorId, Integer roleType, Integer pageIndex,
			Integer pageSize);

	void collectFriend(Integer unionDoctorId, Integer consultationDoctorId);

	void cancelCollectFriend(Integer unionDoctorId, Integer consultationDoctorId);

	boolean isCollect(Integer currDoctorId, Integer friendDoctorId, Integer currentDoctorRole);

	List<Integer> findAllDoctorFriendIdByDoctorId(int userId);

}
