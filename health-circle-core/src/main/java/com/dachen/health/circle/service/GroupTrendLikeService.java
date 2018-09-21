package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.GroupTrendLike;
import com.dachen.health.circle.vo.MobileGroupTrendLikeVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.page.Pagination;

import java.util.List;

public interface GroupTrendLikeService extends ServiceBase {

    List<GroupTrendLike> findFullByIds(List<String> idList);

    List<GroupTrendLike> findByUserAndTrends(Integer userId, List<String> trendIdList);

    GroupTrendLike add(Integer currentUserId, String trendId);
    GroupTrendLike findByUK(Integer userId, String trendId);

    List<String> findIdListPage(String trendId, Integer pageIndex, Integer pageSize);

    Pagination<MobileGroupTrendLikeVO> findPageAndVO(Integer currentUserId, String trendId, Integer pageIndex, Integer pageSize);

    void wrapAll(List<GroupTrendLike> list);

    boolean remove(Integer currentUserId, String id);
    boolean removeByTrend(Integer currentUserId, String trendId);

}

