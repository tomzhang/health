package com.dachen.health.circle.service.impl;

import com.dachen.health.circle.CircleEnum;
import com.dachen.health.circle.CircleImBizTypeEnum;
import com.dachen.health.circle.entity.GroupTrend;
import com.dachen.health.circle.entity.GroupTrendComment;
import com.dachen.health.circle.entity.GroupTrendCommentReply;
import com.dachen.health.circle.service.*;
import com.dachen.health.circle.vo.MobileGroupTrendCommentReplyVO;
import com.dachen.health.commons.vo.User;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.StringUtils;
import org.apache.commons.collections.map.HashedMap;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Model(GroupTrendCommentReply.class)
@Service
public class GroupTrendCommentReplyServiceImpl extends BaseServiceImpl implements GroupTrendCommentReplyService {

    @Autowired
    protected GroupTrendService groupTrendService;
    @Autowired
    protected GroupUser2Service groupUser2Service;
    @Autowired
    protected ImService imService;
    @Autowired
    private Group2Service group2Service;

    @Override
    public List<GroupTrendCommentReply> findFullByIds(List<String> idList) {
        List<GroupTrendCommentReply> list = this.findByIds(idList);
        this.wrapAll(list);
        return list;
    }

    @Override
    public GroupTrendCommentReply addByComment(Integer currentUserId, String commentId, String content) {
        GroupTrendComment comment = groupTrendCommentService.findNormalById(commentId);
        group2Service.findAndCheckById(comment.getGroupId());
        GroupTrendCommentReply tmp = new GroupTrendCommentReply(commentId, content, currentUserId, null);
        // 设置默认值
        tmp.setStatus(CircleEnum.TrendCommentStatusEnum.Passed);
        // 设置冗余值
        tmp.setTrendId(comment.getTrendId());

        GroupTrendCommentReply reply = this.saveEntityAndFind(tmp);
        // 当为回复时需要维护冗余字段recentReplyIdList
        this.groupTrendCommentService.incrTotalReplyByAdd(reply.getCommentId(), reply.getId().toString());   // 刷新最近回复列表

        //回复通知
        this.sendCommentIMMsgToClients(currentUserId,reply,commentId);
        return reply;
    }

    @Override
    public GroupTrendCommentReply addByReply(Integer currentUserId, String replyToId, String content) {
        GroupTrendCommentReply dbItem = this.findById(replyToId);
        String commentId = dbItem.getCommentId();

        GroupTrendCommentReply tmp = new GroupTrendCommentReply(commentId, content, currentUserId, replyToId);

        // 设置默认值
        tmp.setStatus(CircleEnum.TrendCommentStatusEnum.Passed);
        // 设置冗余值
        tmp.setTrendId(dbItem.getTrendId());
        tmp.setReplyToUserId(dbItem.getUserId());

        // TODO: 与产品确认
        if (StringUtils.isNotBlank(dbItem.getReplyToSourceId())) {
            tmp.setReplyToSourceId(dbItem.getReplyToSourceId());
            tmp.setReplyToSourceUserId(dbItem.getReplyToSourceUserId());
        } else {
            tmp.setReplyToSourceId(dbItem.getId().toString());
            tmp.setReplyToSourceUserId(dbItem.getUserId());
        }

        GroupTrendCommentReply reply = this.saveEntityAndFind(tmp);
        if(null!=reply) {
            // 当为回复时需要维护冗余字段recentReplyIdList
            this.groupTrendCommentService.incrTotalReplyByAdd(reply.getCommentId(), reply.getId().toString());   // 刷新最近回复列表
            //回复通知
            this.sendCommentIMMsgToClients(currentUserId,reply,tmp.getReplyToUserId());
        }
        return reply;
    }


    @Override
    public boolean remove(Integer currentUserId, String id) {
        GroupTrendCommentReply dbItem = this.findById(id);
        dbItem.setStatus(CircleEnum.TrendCommentStatusEnum.Deleted);
        dbItem = this.saveEntityAndFind(dbItem);

        boolean ret = CircleEnum.TrendCommentStatusEnum.eval(dbItem.getStatusId()) == CircleEnum.TrendCommentStatusEnum.Deleted;
        if (ret) {
            // 处理冗余字段, 将recentReplyIdList, totalReply
            this.groupTrendCommentService.decrTotalReplyByRemove(dbItem.getCommentId(), id);
        }
        return ret;
    }

    @Override
    public List<String> findIdListByComment(String commentId, Integer pageIndex, Integer pageSize) {
        Query<GroupTrendCommentReply> query = this.createQuery();
        query.field("commentId").equal(commentId).field("statusId").equal(CircleEnum.TrendCommentStatusEnum.Passed.getId());

        query.order("statusTime");
        query.offset(pageIndex*pageSize).limit(pageSize);
        query.retrievedFields(true, Mapper.ID_KEY);

        List<GroupTrendCommentReply> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<String> idList = list.stream().map(o->o.getId().toString()).collect(Collectors.toList());
        return idList;
    }

    public Pagination<GroupTrendCommentReply> findPage(String commentId, Integer pageIndex, Integer pageSize) {
        Query<GroupTrendCommentReply> query = this.createQuery();
        query.field("commentId").equal(commentId).field("statusId").equal(CircleEnum.TrendCommentStatusEnum.Passed.getId());

        Long total = query.countAll();

        query.order("statusTime");
        query.offset(pageIndex*pageSize).limit(pageSize);

        List<GroupTrendCommentReply> list = query.asList();

        Pagination<GroupTrendCommentReply> page = new Pagination<>(list, total, pageIndex, pageSize);
        return page;
    }

    @Autowired
    protected GroupTrendCommentService groupTrendCommentService;

    @Override
    public Pagination<MobileGroupTrendCommentReplyVO> findPageAndVO(Integer currentUserId, String commentId, Integer pageIndex, Integer pageSize) {
        GroupTrendComment comment = this.groupTrendCommentService.findById(commentId);

        Pagination<GroupTrendCommentReply> page = this.findPage(commentId, pageIndex, pageSize);

        List<GroupTrendCommentReply> list = page.getPageData();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        this.wrapAll(list);

        List<MobileGroupTrendCommentReplyVO> voList = this.convertToMobile(list);

        Pagination<MobileGroupTrendCommentReplyVO> page2 = new Pagination<>(voList, page.getTotal(), page.getPageIndex(), page.getPageSize());
        return page2;
    }


    protected MobileGroupTrendCommentReplyVO convertToMobile(GroupTrendCommentReply comment) {
        if (null == comment) {
            return null;
        }
        MobileGroupTrendCommentReplyVO vo = new MobileGroupTrendCommentReplyVO(comment);
        return vo;
    }

    protected List<MobileGroupTrendCommentReplyVO> convertToMobile(List<GroupTrendCommentReply> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<MobileGroupTrendCommentReplyVO> voList = new ArrayList<>(list.size());
        for (GroupTrendCommentReply groupTrendComment :list) {
            voList.add(new MobileGroupTrendCommentReplyVO(groupTrendComment));
        }
        return voList;
    }


    protected void wrapAll(List<GroupTrendCommentReply> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        this.wrapUser(list);
    }

    protected void wrapAll(GroupTrendCommentReply reply) {
        if (null == reply) {
            return;
        }
        this.wrapUser(reply);
    }

    @Autowired
    protected User2Service user2Service;

    protected void wrapUser(List<GroupTrendCommentReply> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        Set<Integer> userIdSet = new HashSet<>(list.size());
        for (GroupTrendCommentReply comment:list) {
            userIdSet.add(comment.getUserId());
            if (null != comment.getReplyToUserId()) {
                userIdSet.add(comment.getReplyToUserId());
            }
        }

        List<User> userList = user2Service.findDoctorListByIds(new ArrayList<>(userIdSet));
        for (GroupTrendCommentReply comment:list) {
            for (User user:userList) {
                if (user.getUserId().equals(comment.getUserId())) {
                    comment.setUser(user);
                }
                if (user.getUserId().equals(comment.getReplyToUserId())) {
                    comment.setReplyToUser(user);
                }
            }
        }
    }

    protected void wrapUser(GroupTrendCommentReply reply) {
        if (null == reply) {
            return;
        }

        Set<Integer> userIdSet = new HashSet<>(2);
        userIdSet.add(reply.getUserId());
        if (null != reply.getReplyToUserId()) {
            userIdSet.add(reply.getReplyToUserId());
        }

        List<User> userList = user2Service.findDoctorListByIds(new ArrayList<>(userIdSet));
        for (User user:userList) {
            if (user.getUserId().equals(reply.getUserId())) {
                reply.setUser(user);
            }
            if (user.getUserId().equals(reply.getReplyToUserId())) {
                reply.setReplyToUser(user);
            }
        }
    }

    protected void sendCommentIMMsgToClients(Integer currentUserId, GroupTrendCommentReply commentReply,Integer toUserId) {
        this.wrapUser(commentReply);
        GroupTrend groupTrend = groupTrendService.findFullById(currentUserId, commentReply.getTrendId());
        String title = String.format("系统通知");
        String content = String.format("%s在%s中回复了您\"%s\"", commentReply.getUser().getName(), groupTrend.getTitle(), commentReply.getContent());
        Map<String, Object> params = new HashedMap(2);
        params.put("bizType", CircleImBizTypeEnum.GroupTrendCommentReply.getId());
        params.put("bizId", commentReply.getCommentId());
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setStyle(7);
        imgTextMsg.setFooter("查看详情");
        imService.sendTodoNotifyMsg(toUserId, title, content, null, params, imgTextMsg);
    }

    protected void sendCommentIMMsgToClients(Integer currentUserId, GroupTrendCommentReply commentReply,String commentId) {
        this.wrapUser(commentReply);
        GroupTrend groupTrend = groupTrendService.findFullById(currentUserId, commentReply.getTrendId());
        GroupTrendComment groupTrendComment = groupTrendCommentService.findById(commentId);
        String title = String.format("系统通知");
        String content = String.format("%s在%s中回复了您\"%s\"", commentReply.getUser().getName(), groupTrend.getTitle(), commentReply.getContent());
        Map<String, Object> params = new HashedMap(2);
        params.put("bizType", CircleImBizTypeEnum.GroupTrendCommentReply.getId());
        params.put("bizId", commentReply.getCommentId());
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setStyle(7);
        imgTextMsg.setFooter("查看详情");
        imService.sendTodoNotifyMsg(groupTrendComment.getUserId(), title, content, null, params, imgTextMsg);
    }

}
