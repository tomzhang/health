package com.dachen.health.circle.service;


import com.dachen.health.circle.entity.Group2;
import com.dachen.health.circle.entity.GroupDoctor2;
import com.dachen.health.circle.entity.GroupDoctorApply;
import com.dachen.health.circle.entity.GroupDoctorInvite;
import com.dachen.health.circle.vo.MobileGroupDoctorVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.page.Pagination;

import java.util.List;
import java.util.Map;

public interface GroupDoctor2Service extends ServiceBase {
    List<String> findGroupIdListByDoctor(Integer doctorId);

    List<String> findGroupIdListExceptByDoctor(Integer doctorId);

    List<Integer> findDoctorIdListByGroupAndUnions(List<String> groupIdList, List<String> unionIdList);

    List<Integer> findDoctorIdListByGroup(String groupId);

    List<String> findDeptIdListByDoctor(List<Integer> doctorIdList);

    List<GroupDoctor2> findDeptListByDoctor(List<Integer> doctorIdList);

    Map<Integer, Group2> findDeptMapByDoctor(List<Integer> doctorIdList);

    String findDeptIdByDoctor(Integer doctorId);

    void checkCurDeptIdWhenJoinDept(Integer doctorId);
    boolean isJoinDept(Integer currentUserId);
    boolean infoIsDept(Integer currentUserId,String groupId);
    List<GroupDoctor2> findByDoctor(Integer doctorId);
    List<GroupDoctor2> findFullByDoctor(Integer doctorId);

    GroupDoctor2 findFullById(String id);

    GroupDoctor2 findFullAndCheckById(String id);

    List<MobileGroupDoctorVO> findByDoctorAndVO(Integer doctorId);

    List<String> findGroupIdListByDoctorExcept(Integer doctorId, List<String> exceptGroupIdList);

    Pagination<MobileGroupDoctorVO> findPage(Integer currentUserId, String groupId, Integer pageIndex, Integer pageSize);
    List<MobileGroupDoctorVO> findList(Integer currentUserId, String groupId);
    Pagination<MobileGroupDoctorVO> findListPage(Integer currentUserId, String groupId, String kw ,Integer pageIndex, Integer pageSize);
    List<MobileGroupDoctorVO> findNoManagerListAndVO(Integer currentUserId, String groupId);
    
    List<MobileGroupDoctorVO> findRecInviteList(Integer currentUserId, String groupId);

    int deleteByDept(String groupId);

    Map<String,Object> ifFriendAndInfo(Integer userId, Integer toUserId);

    GroupDoctor2 findByUK(String groupId, Integer doctorId);
    GroupDoctor2 findNormalByUK(String groupId, Integer doctorId);
    boolean remove(Integer currentUserId, String id);

    boolean quitDept(Integer currentUserId, String id) throws HttpApiException;

    void checkNormal(GroupDoctor2 groupDoctor);

    void checkInvitePri(Group2 group, Integer doctorId);

    GroupDoctor2 addByCreateDept(Integer currentUserId, Group2 group2, Integer doctorId);

    GroupDoctor2 addByApply(Integer currentUserId, GroupDoctorApply apply);

    GroupDoctor2 addByInvite(Integer currentUserId, GroupDoctorInvite invite);

    Map<String,Integer> countByGroupList(List<String> groupIdList);
    Map<String,Integer> countTotalExpertByGroupList(List<String> groupIdList);
    Integer countByGroup(String groupId);


    int deleteByGroup(String groupId);

    @Deprecated
    MobileGroupDoctorVO findApplyDetailByIdAndVO(Integer currentUserId, String id);
    @Deprecated
    MobileGroupDoctorVO findInviteDetailByIdAndVO(Integer currentUserId, String id);

    void checkNormalByUK(String groupId, Integer doctorId);

    Pagination<Integer> findDoctorIdListByGroupAndUnionsPage(List<String> groupIdList, List<String> unionIdList, Integer pageIndex, Integer pageSize);

    void closeApplyAndInviteAsync(GroupDoctor2 groupDoctor);

    int updateNormalById(String id);

    Map<String,String> getBothFriendStatus(Integer userId, Integer toUserId);

}
