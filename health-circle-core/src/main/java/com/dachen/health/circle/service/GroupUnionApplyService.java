package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.GroupUnionApply;
import com.dachen.health.circle.vo.MobileGroupUnionApplyVO;
import com.dachen.sdk.page.Pagination;

public interface GroupUnionApplyService extends BaseGroupUnionApplyOrInviteService {
    GroupUnionApply create(Integer currentUserId, String groupId, String unionId, String msg);

    MobileGroupUnionApplyVO findDetailByIdAndVO(Integer currentUserId, String applyId);

    int closeByGroupDismiss(String groupId);

    int closeByGroupUnionDismiss(String unionId);

    Pagination<MobileGroupUnionApplyVO> findPageByGroupAndVO(Integer currentUserId, String groupId, String kw, Integer pageIndex, Integer pageSize);

    GroupUnionApply findFullAndCheckById(String id);

    boolean accept(Integer currentUserId, String id);

    boolean refuse(Integer currentUserId, String id);

    GroupUnionApply findLatestHandlingByGroupAndUnion(String groupId, String unionId);

    int closeByMember(String memberId);
}
