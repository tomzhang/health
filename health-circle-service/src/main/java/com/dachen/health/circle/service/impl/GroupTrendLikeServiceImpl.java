package com.dachen.health.circle.service.impl;

import com.dachen.health.circle.entity.GroupTrend;
import com.dachen.health.circle.entity.GroupTrendLike;
import com.dachen.health.circle.service.GroupTrendLikeService;
import com.dachen.health.circle.service.GroupTrendService;
import com.dachen.health.circle.service.User2Service;
import com.dachen.health.circle.vo.MobileGroupTrendLikeVO;
import com.dachen.health.commons.vo.User;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Model(GroupTrendLike.class)
@Service
public class GroupTrendLikeServiceImpl extends BaseServiceImpl implements GroupTrendLikeService {

    @Autowired
    protected GroupTrendService groupTrendService;

    @Autowired
    protected User2Service user2Service;

    @Override
    public List<GroupTrendLike> findFullByIds(List<String> idList) {
        List<GroupTrendLike> list = this.findByIds(idList);
        this.wrapAll(list);
        return list;
    }

    @Override
    public List<GroupTrendLike> findByUserAndTrends(Integer userId, List<String> trendIdList) {
        Query<GroupTrendLike> query = this.createQuery();
        query.field("trendId").in(trendIdList);
        query.field("userId").equal(userId);
        return query.asList();
    }

    @Override
    public GroupTrendLike add(Integer currentUserId, String trendId) {
        User user2 = user2Service.findById(currentUserId);
        GroupTrend trend = groupTrendService.findById(trendId);

        GroupTrendLike dbItem = this.findByUK(currentUserId, trendId);
        if (null == dbItem) {
            dbItem = new GroupTrendLike(user2, trend);
            dbItem.setCreateTime(System.currentTimeMillis());
            dbItem = this.saveEntityAndFind(dbItem);
            this.groupTrendService.incrTotalLikeByAdd(dbItem.getTrendId(), dbItem.getId().toString());
        }
        return dbItem;
    }

    @Override
    public GroupTrendLike findByUK(Integer userId, String trendId) {
        Query<GroupTrendLike> query = this.createQuery();
        query.field("userId").equal(userId).field("trendId").equal(trendId);
        return query.get();
    }

    @Override
    public List<String> findIdListPage(String trendId, Integer pageIndex, Integer pageSize) {
        Query<GroupTrendLike> query = this.createQuery();
        query.field("trendId").equal(trendId);

        query.order("-createTime");
        query.offset(pageIndex * pageSize).limit(pageSize);
        query.retrievedFields(true, Mapper.ID_KEY);
        List<GroupTrendLike> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<String> idList = list.stream().map(o->o.getId().toString()).collect(Collectors.toList());
        return idList;
    }

    public Pagination<GroupTrendLike> findPage(String trendId, Integer pageIndex, Integer pageSize) {
        Query<GroupTrendLike> query = this.createQuery();
        query.field("trendId").equal(trendId);

        long total = query.countAll();
        query.order("-createTime");
        query.offset(pageIndex * pageSize).limit(pageSize);
        List<GroupTrendLike> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        Pagination<GroupTrendLike> page = new Pagination<>(list, total, pageIndex, pageSize);
        return page;
    }


    @Override
    public Pagination<MobileGroupTrendLikeVO> findPageAndVO(Integer currentUserId, String trendId, Integer pageIndex, Integer pageSize) {
        Pagination<GroupTrendLike> page = this.findPage(trendId, pageIndex, pageSize);
        if (null == page || SdkUtils.isEmpty(page.getPageData())) {
            return null;
        }
        List<GroupTrendLike> list = page.getPageData();
        this.wrapAll(list);
        List<MobileGroupTrendLikeVO> voList = this.convertToMobile(list);

        Pagination<MobileGroupTrendLikeVO> page2 = new Pagination<>(voList, page.getTotal(), page.getPageIndex(), page.getPageSize());
        return page2;
    }

    protected List<MobileGroupTrendLikeVO> convertToMobile(List<GroupTrendLike> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<MobileGroupTrendLikeVO> voList = new ArrayList<>(list.size());
        for (GroupTrendLike like:list) {
            voList.add(new MobileGroupTrendLikeVO(like));
        }
        return voList;
    }

    @Override
    public void wrapAll(List<GroupTrendLike> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        this.wrapUser(list);
    }

    protected void wrapUser(List<GroupTrendLike> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        Set<Integer> userIdSet = new HashSet<>(list.size());
        for (GroupTrendLike like:list) {
            userIdSet.add(like.getUserId());
        }
        List<User> user2List = user2Service.findDoctorListByIds(new ArrayList<>(userIdSet));
        for (GroupTrendLike like:list) {
            for (User user2:user2List) {
                if (like.getUserId().equals(user2.getUserId())) {
                    like.setUser(user2);
                    break;
                }
            }
        }
    }

    @Override
    public boolean remove(Integer currentUserId, String id) {
        GroupTrendLike dbItem = this.findById(id);
        boolean ret = this.deleteById(id);
        if (ret) {
            this.groupTrendService.decrTotalLikeByRemove(dbItem.getTrendId(), id);
        }
        return ret;
    }

    @Override
    public boolean removeByTrend(Integer currentUserId, String trendId) {
        GroupTrendLike dbItem = this.findByUK(currentUserId, trendId);
        if (null == dbItem) {
            return true;
        }
        String id = dbItem.getId().toString();
        return this.remove(currentUserId, id);
    }
}
