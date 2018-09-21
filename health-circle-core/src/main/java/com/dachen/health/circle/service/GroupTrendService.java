package com.dachen.health.circle.service;

import com.dachen.health.circle.entity.GroupTrend;
import com.dachen.health.circle.form.GroupTrendAddForm;
import com.dachen.health.circle.form.GroupTrendUpdateForm;
import com.dachen.health.circle.vo.MobileGroupTrendVO;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.page.Pagination;

public interface GroupTrendService extends ServiceBase {

    GroupTrend findFullById(Integer currentUserId,String id);

    GroupTrend findNormalById(String id);

    void checkNormal(GroupTrend trend);

    Pagination<GroupTrend> findPage(Integer currentUserId, String groupId, Integer pageIndex, Integer pageSize);

    Pagination<MobileGroupTrendVO> findPageAndVO(Integer currentUserId, String groupId, Integer pageIndex, Integer pageSize);

    GroupTrend update(Integer currentUserId, String id, GroupTrendUpdateForm form);

    GroupTrend add(Integer currentUserId, GroupTrendAddForm form) throws HttpApiException;

    boolean remove(Integer currentUserId, String id);

    void incrTotalComment(String id, int count);

    void incrTotalLike(String id, int count);

    void incrTotalCreditByAdd(String id, String creditId);

    void incrTotalLikeByAdd(String id, String likeId);

    void decrTotalLikeByRemove(String id, String likeId);

    MobileGroupTrendVO findByIdAndVO(Integer currentUserId, String id);
}
