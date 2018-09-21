package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.Group2;
import com.dachen.health.circle.entity.GroupDoctor2;
import com.dachen.health.circle.entity.GroupUser2;
import com.dachen.health.circle.vo.MobileGroupUserVO;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.page.Pagination;

import java.util.List;
import java.util.Map;

public interface GroupUser2Service extends ServiceBase {
    List<GroupUser2> findList(String groupId);

    List<Integer> findDoctorIdList(String groupId);
    List<String> findGroupIdByDoctor(Integer doctorId);
    List<MobileGroupUserVO> findMyGroupList(Integer doctorId);

    Pagination<MobileGroupUserVO> findPage(String groupId, Integer pageIndex, Integer pageSize);

    void checkRootOrAdminPri(String groupId, Integer doctorId);

    GroupUser2 findByUK(String groupId, Integer doctorId);
    boolean ifMyGroup(Integer doctorId, String groupId);

    GroupUser2 add(Integer currentUserId, GroupDoctor2 groupDoctor);

    GroupUser2 addByCreateDept(Integer currentUserId, Group2 group2, Integer doctorId);

    boolean removeBySelf(Integer currentUserId, String id);

    boolean removeByGroupDoctor(GroupDoctor2 groupDoctor);

    boolean quitGroup(GroupDoctor2 groupDoctor);

    int deleteByGroup(String groupId);

    Integer countByGroup(String groupId);
    Map<String,Integer> countByGroupList(List<String> groupIdList);

    List<GroupUser2> findAdminUserByGroupId(String groupId);

    List<String> findGroupIdListByDoctor(Integer doctorId);
}
