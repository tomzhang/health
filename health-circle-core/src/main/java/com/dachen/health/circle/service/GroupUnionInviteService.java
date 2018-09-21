package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.GroupUnionInvite;
import com.dachen.health.circle.vo.MobileGroupUnionInviteVO;
import com.dachen.sdk.page.Pagination;

public interface GroupUnionInviteService extends BaseGroupUnionApplyOrInviteService {
    int closeByGroupDismiss(String groupId);

    int closeByGroupUnionDismiss(String unionId);

    GroupUnionInvite findLatestHandlingByUnionAndGroup(String unionId, String groupId);

    Pagination<MobileGroupUnionInviteVO> findPageByUnionAndVO(Integer currentUserId, String unionId, String kw, Integer pageIndex, Integer pageSize);

    GroupUnionInvite create(Integer currentUserId, String unionId, String groupId);

    GroupUnionInvite findFullById(String id);

    boolean accept(Integer currentUserId, String id);

    boolean refuse(Integer currentUserId, String id);

    MobileGroupUnionInviteVO findDetailByIdAndVO(Integer currentUserId, String id);

    int closeByMember(String memberId);
}
