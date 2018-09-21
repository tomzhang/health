package com.dachen.health.circle.service;


import com.dachen.health.circle.entity.GroupFollow;
import com.dachen.health.circle.vo.MobileGroupFollowVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.page.Pagination;

import java.util.List;
import java.util.Set;

public interface GroupFollowService extends ServiceBase {

    GroupFollow add(Integer currentUserId, String groupId) throws HttpApiException;

    int dismissGroup(Integer currentUserId, String groupId) throws HttpApiException;
    boolean removeByGroup(Integer currentUserId, String groupId) throws HttpApiException;
    boolean remove(Integer currentUserId, String id) throws HttpApiException;

    Set<Integer> getDeptUserByUserId(Integer userId);

    GroupFollow findByUK(String groupId, Integer userId);

    List<GroupFollow> findList(Integer userId);

    List<MobileGroupFollowVO> findListAndVO(Integer userId);

    Pagination<MobileGroupFollowVO> findMoreAndVO(Integer currentUserId, String kw, Integer pageIndex, Integer pageSize);
}
