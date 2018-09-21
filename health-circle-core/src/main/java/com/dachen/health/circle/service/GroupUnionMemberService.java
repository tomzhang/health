package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.Group2;
import com.dachen.health.circle.entity.GroupUnion;
import com.dachen.health.circle.entity.GroupUnionApply;
import com.dachen.health.circle.entity.GroupUnionMember;
import com.dachen.health.circle.vo.MobileGroupUnionMemberVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.page.Pagination;

import java.util.List;

public interface GroupUnionMemberService extends ServiceBase{

    GroupUnionMember addByCreateUnion(Integer currentUserId, GroupUnion groupUnion, Group2 group);
    GroupUnionMember addByApply(Integer currentUserId, String applyId);

    void closeApplyAndInviteByMemberAsync(String id);

    GroupUnionMember addByInvite(Integer currentUserId, String inviteId);

    GroupUnionMember findByUK(String unionId, String groupId);

    void checkByUK(String unionId, String groupId);

    void checkIfMember(GroupUnion groupUnion, Group2 group);

    List<String> findIdListByUnionAndDoctor(String unionId, Integer doctorId);

    boolean remove(Integer currentUserId, String unionId, String groupId);
    boolean remove(Integer currentUserId, String id);

    Pagination<MobileGroupUnionMemberVO> findPageAndVO(Integer currentUserId, String unionId, Integer pageIndex, Integer pageSize);
    Pagination<MobileGroupUnionMemberVO> findPageByGroup(Integer currentUserId, String groupId, Integer pageIndex, Integer pageSize);

    List<String> findGroupIdByUnion(String unionId);

    List<String> findUnionIdByGroup(String groupId);

    List<String> findUnionIdsByGroups(List<String> groupIdList);
    List<String> findGroupIdsByUnions(List<String> unionIdList);

    List<String> findGroupIdsByUnion(String unionId);

    List<GroupUnionMember> findByUnion(String unionId);

    List<GroupUnionMember> findByGroups(List<String> groupIdList);
    List<GroupUnionMember> findFullByGroups(List<String> groupIdList);

    List<MobileGroupUnionMemberVO> findByGroupsAndVO(List<String> groupIdList);

    int deleteByGroupUnion(Integer currentUserId, String unionId);

    int deleteByGroup(Integer currentUserId, String groupId);

    int countUnionMemberByGroup(String groupId);

    @Deprecated
    boolean quit(Integer currentUserId, String id);

    List<MobileGroupUnionMemberVO> findQuitList(Integer currentUserId, String unionId);

    boolean quit(Integer currentUserId, String id,String memberId);
}