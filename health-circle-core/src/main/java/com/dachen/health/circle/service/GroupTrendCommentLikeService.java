package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.GroupTrendCommentLike;
import com.dachen.health.circle.entity.GroupTrendLike;
import com.dachen.health.circle.vo.MobileGroupTrendCommentLikeVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.page.Pagination;

import java.util.List;

public interface GroupTrendCommentLikeService extends ServiceBase {

    List<GroupTrendCommentLike> findFullByIds(List<String> idList);

    GroupTrendCommentLike add(Integer currentUserId, String commentId);

    GroupTrendCommentLike findByUK(Integer userId, String commentId);

    List<String> findIdListPage(String commentId, Integer pageIndex, Integer pageSize);

    Pagination<MobileGroupTrendCommentLikeVO> findPageAndVO(Integer currentUserId, String commentId, Integer pageIndex, Integer pageSize);

    boolean removeByCommentId(Integer currentUserId, String commentId);

    List<GroupTrendCommentLike> findByUserAndCommentId(Integer userId, List<String> commentIdList);

    Pagination<GroupTrendCommentLike> findPage(String commentId, Integer pageIndex, Integer pageSize);

    boolean remove(Integer currentUserId, String id);

    void wrapAll(List<GroupTrendCommentLike> list);
}

