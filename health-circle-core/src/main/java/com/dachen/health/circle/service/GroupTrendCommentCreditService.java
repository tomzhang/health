package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.GroupTrendCommentCredit;
import com.dachen.health.circle.vo.MobileGroupTrendCommentCreditVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.page.Pagination;

import java.util.List;

/**
 * 评论打赏学分
 * Created By lim
 * Date: 2017/6/7
 * Time: 16:20
 */
@Deprecated
public interface GroupTrendCommentCreditService extends ServiceBase {
    GroupTrendCommentCredit add(Integer currentUserId, String commentId, Integer credit);

    List<GroupTrendCommentCredit> findFullByCommentIds(List<String> commentIdList);

    Pagination<GroupTrendCommentCredit> findPage(String commentId, Integer pageIndex, Integer pageSize);

    Pagination<MobileGroupTrendCommentCreditVO> findPageAndVO(Integer currentUserId, String commentId, Integer pageIndex, Integer pageSize);

    GroupTrendCommentCredit findByCommentIdAndUserId(String commentId,Integer userId);
}
