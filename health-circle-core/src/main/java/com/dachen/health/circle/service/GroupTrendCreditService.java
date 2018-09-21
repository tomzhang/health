package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.GroupTrendCommentCredit;
import com.dachen.health.circle.entity.GroupTrendCredit;
import com.dachen.health.circle.vo.MobileGroupTrendCommentCreditVO;
import com.dachen.health.circle.vo.MobileGroupTrendCreditVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.page.Pagination;

import java.util.List;

/**
 * 科室动态打赏学分
 * Created By lim
 * Date: 2017/6/7
 * Time: 16:20
 */
public interface GroupTrendCreditService extends ServiceBase {
    GroupTrendCredit add(Integer currentUserId, String trendId, Integer credit);

    List<GroupTrendCredit> findFullByIds(List<String> idList);

    List<GroupTrendCredit> findFullByTrendIds(List<String> TrendIdList);

    GroupTrendCredit findByUK(Integer userId, String trendId);

    List<GroupTrendCredit> findByUserAndTrends(Integer userId, List<String> trendIdList);

    Pagination<GroupTrendCredit> findPage(String trendId, Integer pageIndex, Integer pageSize);

    Pagination<MobileGroupTrendCreditVO> findPageAndVO(Integer currentUserId, String trendId, Integer pageIndex, Integer pageSize);

    GroupTrendCredit findByTrendIdAndUserId(String trendId, Integer userId);

    void wrapUser(List<GroupTrendCredit> list);
}
