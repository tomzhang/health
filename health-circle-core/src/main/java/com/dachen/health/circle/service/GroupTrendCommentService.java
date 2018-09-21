package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.GroupTrendComment;
import com.dachen.health.circle.entity.GroupTrendCommentLike;
import com.dachen.health.circle.form.TrendCommentAddForm;
import com.dachen.health.circle.vo.MobileGroupTrendCommentVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.page.Pagination;

public interface GroupTrendCommentService extends ServiceBase {

    MobileGroupTrendCommentVO findByIdAndVO(Integer currentUserId, String id);
    GroupTrendComment findNormalById(String id);

    GroupTrendComment add(Integer currentUserId, String trendId, String content,String[] imageList);

    boolean remove(Integer currentUserId, String id);

    Pagination<MobileGroupTrendCommentVO> findPageAndVO(Integer currentUserId, String trendId, Integer pageIndex, Integer pageSize);

    void incrTotalReplyByAdd(String id, String replyId);

    void decrTotalReplyByRemove(String id, String replyId);

    void refreshRecentReplyIdListAsync();

    void incrTotalLikeByAdd(String id, String likeId);

    void decrTotalLikeByRemove(String id, String likeId);
}
