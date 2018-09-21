package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.GroupTrendCommentReply;
import com.dachen.health.circle.vo.MobileGroupTrendCommentReplyVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.page.Pagination;

import java.util.List;

public interface GroupTrendCommentReplyService extends ServiceBase {
    List<GroupTrendCommentReply> findFullByIds(List<String> idList);

    GroupTrendCommentReply addByComment(Integer currentUserId, String commentId, String content);
    GroupTrendCommentReply addByReply(Integer currentUserId, String replyToId, String content);

    boolean remove(Integer currentUserId, String id);

    List<String> findIdListByComment(String commentId, Integer pageIndex, Integer pageSize);
    Pagination<MobileGroupTrendCommentReplyVO> findPageAndVO(Integer currentUserId, String commentId, Integer pageIndex, Integer pageSize);
}
