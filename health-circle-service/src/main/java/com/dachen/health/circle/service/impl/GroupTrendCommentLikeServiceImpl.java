package com.dachen.health.circle.service.impl;

import com.dachen.health.circle.entity.Group2;
import com.dachen.health.circle.entity.GroupTrendComment;
import com.dachen.health.circle.entity.GroupTrendCommentLike;
import com.dachen.health.circle.service.GroupDoctor2Service;
import com.dachen.health.circle.service.GroupTrendCommentLikeService;
import com.dachen.health.circle.service.GroupTrendCommentService;
import com.dachen.health.circle.service.User2Service;
import com.dachen.health.circle.vo.MobileGroupTrendCommentLikeVO;
import com.dachen.health.commons.vo.User;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 动态评论点赞
 * Created By lim
 * Date: 2017/6/2
 * Time: 11:16
 */
@Model(GroupTrendCommentLike.class)
@Service
public class GroupTrendCommentLikeServiceImpl extends BaseServiceImpl implements GroupTrendCommentLikeService {

    @Autowired
    protected User2Service user2Service;
    @Autowired
    protected GroupTrendCommentService groupTrendCommentService;

    @Override
    public List<GroupTrendCommentLike> findFullByIds(List<String> idList) {
        List<GroupTrendCommentLike> list = this.findByIds(idList);
        this.wrapAll(list);
        return list;
    }

    @Override
    public GroupTrendCommentLike add(Integer currentUserId, String commentId) {
        User user2 = user2Service.findById(currentUserId);
        GroupTrendComment groupTrendComment = groupTrendCommentService.findById(commentId);
        GroupTrendCommentLike groupTrendCommentLike = this.findByUK(currentUserId, commentId);
        //如果没有点赞过，则需要更新动态评论点赞数据 并 新增点赞数据
        if (null == groupTrendCommentLike) {
            groupTrendCommentLike = new GroupTrendCommentLike(groupTrendComment, user2);
            groupTrendCommentLike.setCreateTime(System.currentTimeMillis());
            //新增点赞数据库
            groupTrendCommentLike = this.saveEntityAndFind(groupTrendCommentLike);
            //维护评论点赞数据
            groupTrendCommentService.incrTotalLikeByAdd(commentId, groupTrendCommentLike.getId().toString());
        }

        return groupTrendCommentLike;
    }

    @Override
    public boolean remove(Integer currentUserId, String id) {
        GroupTrendCommentLike groupTrendCommentLike = this.findById(id);
        boolean ret = this.deleteById(id);
        if (ret) {
            this.groupTrendCommentService.decrTotalLikeByRemove(groupTrendCommentLike.getCommentId(), id);
        }
        return ret;
    }

    @Override
    public boolean removeByCommentId(Integer currentUserId, String commentId) {
        GroupTrendCommentLike dbItem = this.findByUK(currentUserId, commentId);
        if (null == dbItem) {
            return true;
        }
        String id = dbItem.getId().toString();
        return this.remove(currentUserId, id);
    }

    @Override
    public GroupTrendCommentLike findByUK(Integer userId, String commentId) {
        Query<GroupTrendCommentLike> query = this.createQuery();
        query.field("userId").equal(userId).field("commentId").equal(commentId);
        return query.get();
    }

    @Override
    public List<String> findIdListPage(String commentId, Integer pageIndex, Integer pageSize) {
        Query<GroupTrendCommentLike> query = this.createQuery();
        query.field("commentId").equal(commentId);

        query.order("-createTime");
        query.offset(pageIndex * pageSize).limit(pageSize);
        query.retrievedFields(true, Mapper.ID_KEY);
        List<GroupTrendCommentLike> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<String> idList = list.stream().map(o -> o.getId().toString()).collect(Collectors.toList());
        return idList;
    }

    @Override
    public Pagination<GroupTrendCommentLike> findPage(String commentId, Integer pageIndex, Integer pageSize) {
        Query<GroupTrendCommentLike> query = this.createQuery();
        query.field("commentId").equal(commentId);

        long total = query.countAll();
        query.order("-createTime");
        query.offset(pageIndex * pageSize).limit(pageSize);
        List<GroupTrendCommentLike> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        Pagination<GroupTrendCommentLike> page = new Pagination<>(list, total, pageIndex, pageSize);
        return page;
    }

    @Override
    public List<GroupTrendCommentLike> findByUserAndCommentId(Integer userId, List<String> commentIdList) {
        Query<GroupTrendCommentLike> query = this.createQuery();
        query.field("commentId").in(commentIdList);
        query.field("userId").equal(userId);
        return query.asList();
    }

    @Override
    public Pagination<MobileGroupTrendCommentLikeVO> findPageAndVO(Integer currentUserId, String commentId, Integer pageIndex, Integer pageSize) {
        Pagination<GroupTrendCommentLike> page = this.findPage(commentId, pageIndex, pageSize);
        if (null == page || SdkUtils.isEmpty(page.getPageData())) {
            return null;
        }
        List<GroupTrendCommentLike> list = page.getPageData();
        this.wrapAll(list);
        List<MobileGroupTrendCommentLikeVO> voList = this.convertToMobile(list);

        Pagination<MobileGroupTrendCommentLikeVO> page2 = new Pagination<>(voList, page.getTotal(), page.getPageIndex(), page.getPageSize());
        return page2;
    }

    protected List<MobileGroupTrendCommentLikeVO> convertToMobile(List<GroupTrendCommentLike> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<MobileGroupTrendCommentLikeVO> voList = new ArrayList<>(list.size());
        for (GroupTrendCommentLike like : list) {
            voList.add(new MobileGroupTrendCommentLikeVO(like));
        }
        return voList;
    }

    @Override
    public void wrapAll(List<GroupTrendCommentLike> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        this.wrapUser(list);
    }

    @Autowired
    protected GroupDoctor2Service groupDoctor2Service;

    protected void wrapUser(List<GroupTrendCommentLike> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        Set<Integer> userIdSet = new HashSet<>(list.size());
        for (GroupTrendCommentLike like : list) {
            userIdSet.add(like.getUserId());
        }
        List<User> user2List = user2Service.findDoctorListByIds(new ArrayList<>(userIdSet));
        Map<Integer, Group2> deptMap = groupDoctor2Service.findDeptMapByDoctor(new ArrayList<>(userIdSet));
        for (GroupTrendCommentLike like : list) {
            for (User user2 : user2List) {
                if (like.getUserId().equals(user2.getUserId())) {
                    like.setUser(user2);
                    like.setUserDept(deptMap.get(like.getUserId()));
                    break;
                }
            }
        }
    }
}
