package com.dachen.health.circle.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.circle.CircleEnum;
import com.dachen.health.circle.CircleImBizTypeEnum;
import com.dachen.health.circle.CirleErrorCodeEnum;
import com.dachen.health.circle.entity.*;
import com.dachen.health.circle.service.*;
import com.dachen.health.circle.vo.MobileGroupTrendCommentVO;
import com.dachen.health.commons.vo.User;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import org.apache.commons.collections.map.HashedMap;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Model(GroupTrendComment.class)
@Service
public class GroupTrendCommentServiceImpl extends BaseServiceImpl implements GroupTrendCommentService {

    @Autowired
    protected GroupTrendService groupTrendService;
    @Autowired
    protected GroupTrendCommentLikeService groupTrendCommentLikeService;
    @Autowired
    private Group2Service group2Service;
    @Autowired
    protected GroupUser2Service groupUser2Service;


    private static final int DEFAULT_SHOW_LIKE_COUNT = 8;

    @Override
    public MobileGroupTrendCommentVO findByIdAndVO(Integer currentUserId, String id) {
        GroupTrendComment dbItem = this.findById(id);
        this.checkNormal(dbItem);// 动态评论内容已删除86000200
        groupTrendService.findNormalById(dbItem.getTrendId());//动态删除错误码86000100
        Group2 group2 = group2Service.findAndCheckById(dbItem.getGroupId());// 科室解散错误码86000000
        this.wrapAll(dbItem);
        this.wrapUserLike(currentUserId,dbItem);
        MobileGroupTrendCommentVO vo = this.convertToMobile(dbItem);
        return vo;
    }

    @Override
    public GroupTrendComment findNormalById(String id) {
        GroupTrendComment dbItem = this.findById(id);
        this.checkNormal(dbItem);
        return dbItem;
    }

    protected void checkNormal(GroupTrendComment dbItem) {
        if (CircleEnum.TrendCommentStatusEnum.eval(dbItem.getStatusId()) != CircleEnum.TrendCommentStatusEnum.Passed) {
            //throw new ServiceException("评论状态不对:" + dbItem.getStatusId());
            throw new ServiceException(CirleErrorCodeEnum.GroupTrendCommentNoExistent.getId(),"动态评论内容已删除");
        }else if(CircleEnum.TrendCommentStatusEnum.eval(dbItem.getStatusId()) == CircleEnum.TrendCommentStatusEnum.Prepared){
            throw new ServiceException("动态评论内容待审核");
        }
    }

    @Override
    public GroupTrendComment add(Integer currentUserId, String trendId, String content,String[] imageList) {
        GroupTrend trend = groupTrendService.findNormalById(trendId);
        group2Service.findAndCheckById(trend.getGroupId());
        GroupTrendComment tmp = new GroupTrendComment(trendId, currentUserId, content);
        // 冗余值
        tmp.setGroupId(trend.getGroupId());
        // 默认值
        tmp.setCreateTime(System.currentTimeMillis());
        tmp.setStatus(CircleEnum.TrendCommentStatusEnum.Passed);   // 默认是通过
        if(imageList!=null && imageList.length>0){
            tmp.setImageList(imageList);
        }

        GroupTrendComment comment = this.saveEntityAndFind(tmp);
        if(null != comment) {
            this.groupTrendService.incrTotalComment(comment.getTrendId(), 1); // 将动态的评论数+1
            //发送评论通知
            this.sendCommentIMMsgToClients(currentUserId,comment,trend.getUserId());
        }
        return comment;
    }


    @Override
    public boolean remove(Integer currentUserId, String id) {
        GroupTrendComment dbItem = this.findById(id);
        dbItem.setStatus(CircleEnum.TrendCommentStatusEnum.Deleted);
        dbItem = this.saveEntityAndFind(dbItem);

        boolean ret = null != dbItem;
        if (ret) {
            this.groupTrendService.incrTotalComment(dbItem.getTrendId(), -1);
        }
        return ret;
    }


    public Pagination<GroupTrendComment> findPage(String trendId, Integer pageIndex, Integer pageSize) {
        Query<GroupTrendComment> query = this.createQuery();
        query.field("trendId").equal(trendId).field("statusId").equal(CircleEnum.TrendCommentStatusEnum.Passed.getId());

        Long total = query.countAll();

        query.order("statusTime"); // 只查询Passed，所以按passed时间升序
        query.offset(pageIndex*pageSize).limit(pageSize);

        List<GroupTrendComment> list = query.asList();
        Pagination<GroupTrendComment> page = new Pagination<>(list, total, pageIndex, pageSize);
        return page;
    }

    @Override
    public Pagination<MobileGroupTrendCommentVO> findPageAndVO(Integer currentUserId, String trendId, Integer pageIndex, Integer pageSize) {
        Pagination<GroupTrendComment> page = this.findPage(trendId, pageIndex, pageSize);

        List<GroupTrendComment> list = page.getPageData();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        this.wrapAll(list);
        //重新包装点赞
        this.wrapUserLike(currentUserId, list);
        List<MobileGroupTrendCommentVO> voList = this.convertToMobile(list);

        Pagination<MobileGroupTrendCommentVO> page2 = new Pagination<>(voList, page.getTotal(), page.getPageIndex(), page.getPageSize());
        return page2;
    }

    @Autowired
    protected ImService imService;

    protected void sendCommentIMMsgToClients(Integer currentUserId, GroupTrendComment comment,Integer userId) {
        this.wrapUser(comment);
        GroupTrend groupTrend = groupTrendService.findFullById(currentUserId, comment.getTrendId());
        if (userId == null) {
            return;
        }
        String title = String.format("系统通知");
        String content = String.format("%s评论了您的%s动态\"%s\"", comment.getUser().getName(), groupTrend.getTitle(), comment.getContent());
        Map<String, Object> params = new HashedMap(2);
        params.put("bizType", CircleImBizTypeEnum.GroupTrendComment.getId());
        params.put("bizId", comment.getId().toString());
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setStyle(7);
        imgTextMsg.setFooter("查看详情");
        imService.sendTodoNotifyMsg(userId, title, content, null, params, imgTextMsg);
    }
    private static final Integer COMMENT_REPLY_COUNT_DEFAULT = 3;

    @Override
    public void incrTotalReplyByAdd(String id, String replyId) {
        GroupTrendComment dbItem = this.findById(id);
        dbItem.setTotalReply(dbItem.getTotalReply()+1);

        if (dbItem.getTotalReply()>COMMENT_REPLY_COUNT_DEFAULT) {
            this.saveEntity(dbItem);
            return;
        }

        List<String> recentReplyIdList = dbItem.getRecentReplyIdList();
        if (SdkUtils.isEmpty(recentReplyIdList)) {
            recentReplyIdList = new ArrayList<>(1);
        }
        recentReplyIdList.add(replyId);
        dbItem.setRecentReplyIdList(recentReplyIdList);

        this.saveEntity(dbItem);
    }

    @Override
    public void decrTotalLikeByRemove(String id, String likeId) {
        GroupTrendComment groupTrendComment = this.findById(id);
        groupTrendComment.setTotalLike(groupTrendComment.getTotalLike()-1);
        if (0 == groupTrendComment.getTotalLike()) {
            groupTrendComment.setRecentLikeIdList(null);
            this.saveEntity(groupTrendComment);
            return;
        }

        if (groupTrendComment.getRecentLikeIdList().contains(likeId)) {
            List<String> likeIdList = groupTrendCommentLikeService.findIdListPage(id, 0, DEFAULT_SHOW_LIKE_COUNT);
            groupTrendComment.setRecentLikeIdList(likeIdList);
        }
        this.saveEntity(groupTrendComment);
    }

    @Override
    public void decrTotalReplyByRemove(String id, String replyId) {
        GroupTrendComment dbItem = this.findById(id);
        dbItem.setTotalReply(dbItem.getTotalReply()-1);

        if (0 == dbItem.getTotalReply()) {
            dbItem.setRecentReplyIdList(null);
            this.saveEntity(dbItem);
            return;
        }

        List<String> recentReplyIdList = dbItem.getRecentReplyIdList();
        if (recentReplyIdList.contains(replyId)) {
            List<String> replyIdList = groupTrendCommentReplyService.findIdListByComment(id, 0, COMMENT_REPLY_COUNT_DEFAULT);
            dbItem.setRecentReplyIdList(replyIdList);
        }
        this.saveEntity(dbItem);
    }

    @Override
    public void refreshRecentReplyIdListAsync() {
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                refreshRecentReplyIdList();
            }
        });
    }

    public void refreshRecentReplyIdList() {
        Integer pageIndex = 0;
        Integer pageSize = 30;
        List<String> idList = this.findIdList(pageIndex, pageSize);
        while (SdkUtils.isNotEmpty(idList)) {
            for (String id:idList) {
                refreshRecentReplyId(id);
            }

            pageIndex ++;
            idList = this.findIdList(pageIndex, pageSize);
        }
    }

    private void refreshRecentReplyId(String id) {
        GroupTrendComment dbItem = this.findById(id);
        List<String> replyIdList = groupTrendCommentReplyService.findIdListByComment(id, 0, COMMENT_REPLY_COUNT_DEFAULT);
        dbItem.setRecentReplyIdList(replyIdList);
        this.saveEntity(dbItem);
    }

    public List<String> findIdList(Integer pageIndex, Integer pageSize) {
        Query<GroupTrendComment> query = this.createQuery();
        query.offset(pageIndex*pageSize).limit(pageSize);
        query.retrievedFields(true, Mapper.ID_KEY);
        List<GroupTrendComment> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<String> idList = list.stream().map(o->o.getId().toString()).collect(Collectors.toList());
        return idList;
    }

    protected MobileGroupTrendCommentVO convertToMobile(GroupTrendComment comment) {
        if (null == comment) {
            return null;
        }
        MobileGroupTrendCommentVO vo = new MobileGroupTrendCommentVO(comment);
        return vo;
    }

    protected List<MobileGroupTrendCommentVO> convertToMobile(List<GroupTrendComment> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<MobileGroupTrendCommentVO> voList = new ArrayList<>(list.size());
        for (GroupTrendComment groupTrendComment :list) {
            voList.add(new MobileGroupTrendCommentVO(groupTrendComment));
        }
        return voList;
    }

    protected void wrapUserLike(Integer userId, GroupTrendComment groupTrendComment) {
        if (null==groupTrendComment) {
            return;
        }

        GroupTrendCommentLike like = groupTrendCommentLikeService.findByUK(userId, groupTrendComment.getId().toString());
        if(null!=like) {
            groupTrendComment.setLike(like);
        }

    }

    protected void wrapUserLike(Integer userId, List<GroupTrendComment> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        Set<String> commentIdSet = new HashSet<>(list.size());
        for (GroupTrendComment groupTrendComment:list) {
            commentIdSet.add(groupTrendComment.getId().toString());
        }

        List<GroupTrendCommentLike> likeList = groupTrendCommentLikeService.findByUserAndCommentId(userId, new ArrayList<>(commentIdSet));
        groupTrendCommentLikeService.wrapAll(likeList);
        for (GroupTrendComment groupTrendComment:list) {
            List<String> recentLikeIdList = groupTrendComment.getRecentLikeIdList();
            if(null!=recentLikeIdList) {
                List<GroupTrendCommentLike> groupTrendCommentLikes = groupTrendCommentLikeService.findFullByIds(recentLikeIdList);
                this.wrapUserLike(groupTrendCommentLikes);
                groupTrendComment.setRecentLikeList(groupTrendCommentLikes);
            }
            for (GroupTrendCommentLike groupTrendCommentLike:likeList) {
                if (groupTrendComment.getId().toString().equals(groupTrendCommentLike.getCommentId())) {
                    groupTrendComment.setLike(groupTrendCommentLike);
                    break;
                }
            }
        }
    }

    protected void wrapAll(List<GroupTrendComment> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        this.wrapRecentReplyList(list);

        this.wrapUser(list);
    }

    protected void wrapAll(GroupTrendComment comment) {
        if (null == comment) {
            return;
        }
//        this.wrapRecentReplyList(comment);
        this.wrapUser(comment);
    }

    protected void wrapRecentReplyList(GroupTrendComment comment) {
        if (null == comment) {
            return;
        }

        if (SdkUtils.isEmpty(comment.getRecentReplyIdList())) {
            return;
        }

        List<GroupTrendCommentReply> replyList = this.groupTrendCommentReplyService.findByIds(comment.getRecentReplyIdList());
        comment.setRecentReplyList(replyList);
    }

    @Autowired
    protected GroupTrendCommentReplyService groupTrendCommentReplyService;

    protected void wrapRecentReplyList(List<GroupTrendComment> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        Set<String> replyIdSet = new HashSet<>();
        for (GroupTrendComment comment:list) {
            if (SdkUtils.isEmpty(comment.getRecentReplyIdList())) {
                continue;
            }
            replyIdSet.addAll(comment.getRecentReplyIdList());
        }

        if (SdkUtils.isEmpty(replyIdSet)) {
            return;
        }

        List<GroupTrendCommentReply> replyList = this.groupTrendCommentReplyService.findFullByIds(new ArrayList<>(replyIdSet));
        for (GroupTrendComment comment:list) {
            List<String> recentReplyIdList = comment.getRecentReplyIdList();
            if (SdkUtils.isEmpty(recentReplyIdList)) {
                continue;
            }

            List<GroupTrendCommentReply> recentReplyList = new ArrayList<>(recentReplyIdList.size());
            for (String recentReplyId:recentReplyIdList) {
                for (GroupTrendCommentReply reply:replyList) {
                    if (recentReplyId.equals(reply.getId().toString())) {
                        recentReplyList.add(reply);
                        break;
                    }
                }
            }
            comment.setRecentReplyList(recentReplyList);
        }
    }

    @Autowired
    protected User2Service user2Service;

    protected void wrapUser(List<GroupTrendComment> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        Set<Integer> userIdSet = new HashSet<>(list.size());
        for (GroupTrendComment comment:list) {
            userIdSet.add(comment.getUserId());
        }

        List<User> userList = user2Service.findDoctorListByIds(new ArrayList<>(userIdSet));
        for (GroupTrendComment comment:list) {
            for (User user:userList) {
                if (user.getUserId().equals(comment.getUserId())) {
                    comment.setUser(user);
                    break;
                }
            }
        }
    }

    protected void wrapUserLike(List<GroupTrendCommentLike> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        Set<Integer> userIdSet = new HashSet<>(list.size());
        for (GroupTrendCommentLike groupTrendCommentLike:list) {
            userIdSet.add(groupTrendCommentLike.getUserId());
        }

        List<User> userList = user2Service.findDoctorListByIds(new ArrayList<>(userIdSet));
        for (GroupTrendCommentLike groupTrendCommentLike:list) {
            for (User user:userList) {
                if (user.getUserId().equals(groupTrendCommentLike.getUserId())) {
                    groupTrendCommentLike.setUser(user);
                    break;
                }
            }
        }
    }

    protected void wrapUser(GroupTrendComment comment) {
        if (null == comment) {
            return;
        }

        Set<Integer> userIdSet = new HashSet<>(2);
        userIdSet.add(comment.getUserId());

        List<User> userList = user2Service.findDoctorListByIds(new ArrayList<>(userIdSet));
        for (User user:userList) {
            if (user.getUserId().equals(comment.getUserId())) {
                comment.setUser(user);
                break;
            }
        }
    }

    @Override
    public void incrTotalLikeByAdd(String id, String likeId) {
        GroupTrendComment groupTrendComment = this.findById(id);

        groupTrendComment.setTotalLike(groupTrendComment.getTotalLike()+1);

        if (null == groupTrendComment.getRecentLikeIdList()) {
            groupTrendComment.setRecentLikeIdList(new ArrayList<>());
        }
        groupTrendComment.getRecentLikeIdList().add(0, likeId);
        if (groupTrendComment.getRecentLikeIdList().size()>DEFAULT_SHOW_LIKE_COUNT) {
            groupTrendComment.getRecentLikeIdList().remove(groupTrendComment.getRecentLikeIdList().size()-1);
        }
        this.saveEntity(groupTrendComment);
    }
}
