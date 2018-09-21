package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.GroupUnion;
import com.dachen.health.circle.vo.MobileGroupUnionHomePageVO;
import com.dachen.health.circle.vo.MobileGroupUnionVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.page.Pagination;

import java.util.List;

public interface GroupUnionService extends ServiceBase {

    MobileGroupUnionVO createAndVO(Integer currentUserId, String groupId, String name, String intro, String logoPicUrl);

    boolean incrTotalMember(String id, int count);

    void checkAdminPri(Integer doctorId, GroupUnion groupUnion);

    void check(String id);

    GroupUnion findAndCheckById(String id);

    GroupUnion findNormalById(String id);

    void check(GroupUnion groupUnion);

    MobileGroupUnionVO updateLogoAndVO(Integer currentUserId, String id, String logoPicUrl);

    MobileGroupUnionVO updateNameAndVO(Integer currentUserId, String id, String name);

    MobileGroupUnionVO updateIntroAndVO(Integer currentUserId, String id, String intro);

    List<GroupUnion> findFullByIds(List<String> idList);

    MobileGroupUnionHomePageVO findGroupHomePagAndVO(Integer currentUserId, String id, String groupId);
    @Deprecated
    boolean quit(Integer currentUserId, String id);

    boolean dismiss(Integer currentUserId, String id);

    List<MobileGroupUnionVO> findByGroupsAndVO(List<String> groupIdList);

    Pagination<GroupUnion> findPage(String kw, List<String> exceptIdList, Integer pageIndex, Integer pageSize);

    int countByMain(String groupId);

    void wrapTotal(List<GroupUnion> unionList);

    boolean ifCanCreate(Integer doctorId);
}
