package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.Group2;
import com.dachen.health.circle.form.GroupDeptAddForm;
import com.dachen.health.circle.vo.*;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.page.Pagination;

import java.util.List;

public interface Group2Service extends ServiceBase {

    List<Group2> findFullByIds(List<String> idList);

    Group2 createDept(Integer currentUserId, String hospitalId, String deptId, String childName, String introduction, String logoPicUrl) throws HttpApiException;

    List<Group2> findDeptListByDoctor(List<Integer> doctorIdList);

    MobileGroupVO createDeptAndVO(Integer currentUserId, String hospitalId, String deptId, String childName, String introduction, String logoPicUrl) throws HttpApiException;

    Group2 findDeptByUser(Integer userId);

    Group2 findAndCheckById(String id);

    Group2 findAndCheckDept(String id);

    Group2 findAndCheckGroupOrDept(String id);

    List<String> filterGroupOrDeptIds(List<String> idList);

    void checkDept(Group2 group2);

    void checkDeptOrGroup(Group2 group2);

    List<String> getSkipGroupIds();

    boolean dismissDept(Integer currentUserId, String id);

    Group2 findGroupHomePage(Integer currentUserId, String id);
    MobileGroupHomePageVO findGroupHomePageAndVO(Integer currentUserId, String id);

    MobileGroupVO updateLogoAndVO(Integer currentUserId, String id, String logoPicUrl);
    MobileGroupVO updateNameAndVO(Integer currentUserId, String id, String childName);
    MobileGroupVO updateIntroAndVO(Integer currentUserId, String id, String intro);
    Group2 updateDept(Integer currentUserId, String id, String childName, String logoPicUrl, String intro);

    List<Group2> findNormalExceptDept(List<String> idList);

    Pagination<Group2> findPage(String kw, Integer pageIndex, Integer pageSize);
    Pagination<Group2> findPage(String kw, List<String> exceptIdList, Integer pageIndex, Integer pageSize);

    Pagination<Group2> findDeptPage(String kw, List<String> exceptIdList, Integer pageIndex, Integer pageSize);
    Pagination<MobileGroupVO> findDeptPageAndVO(Integer currentUserId, String kw, Integer pageIndex, Integer pageSize);

    Pagination<MobileGroupVO> findPageAndVO(Integer currentUserId, Integer pageIndex, Integer pageSize);

    Group2 findNormalById(String id);

    List<Group2> findNormalByIdsAndKw(List<String> idList, String kw);

    List<Group2> findRecList(Integer currentUserId);

    List<MobileGroupVO> findRecListAndVO(Integer currentUserId);

    UserGroupAndUnionIdMap findUserGroupAndUnionIdMap(Integer userId);
    UserGroupAndUnionMap findUserGroupAndUnionMap(Integer userId);
    UserGroupAndUnionMapVO findUserGroupAndUnionMapAndVO(Integer userId);
    UserGroupAndUnionHomeMap findUserGroupAndUnionHomeMap(Integer currentUserId);
    UserGroupAndUnionHomeMapVO findUserGroupAndUnionHomeMapAndVO(Integer currentUserId);

    int countTotalCure(String id);




}