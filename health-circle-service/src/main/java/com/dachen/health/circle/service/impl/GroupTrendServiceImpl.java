package com.dachen.health.circle.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.circle.CircleEnum;
import com.dachen.health.circle.CircleImBizTypeEnum;
import com.dachen.health.circle.CirleErrorCodeEnum;
import com.dachen.health.circle.entity.*;
import com.dachen.health.circle.form.GroupTrendAddForm;
import com.dachen.health.circle.form.GroupTrendUpdateForm;
import com.dachen.health.circle.service.*;
import com.dachen.health.circle.vo.MobileGroupTrendVO;
import com.dachen.health.commons.vo.User;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.pub.model.param.PubMsgParam;
import com.dachen.pub.util.PubUtils;
import com.dachen.pub.util.PubaccHelper;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkJsonUtils;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.StringUtils;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Model(GroupTrend.class)
@Service
public class GroupTrendServiceImpl extends BaseServiceImpl implements GroupTrendService {

    @Autowired
    protected Group2Service group2Service;


    @Autowired
    protected GroupTrendLikeService groupTrendLikeService;

    @Autowired
    private GroupTrendCreditService groupTrendCreditService;
    @Override
    public GroupTrend findFullById(Integer currentUserId,String id) {
        GroupTrend dbItem = this.findById(id);
        this.wrapAll(dbItem);
        this.wrapUserCredit(currentUserId, dbItem);
        return dbItem;
    }

    @Override
    public GroupTrend findNormalById(String id) {
        GroupTrend trend = this.findById(id);
        this.checkNormal(trend);
        return trend;
    }

    @Override
    public void checkNormal(GroupTrend trend) {
        if (CircleEnum.GroupTrendStatusEnum.eval(trend.getStatusId()) != CircleEnum.GroupTrendStatusEnum.Passed) {
            throw new ServiceException(CirleErrorCodeEnum.GroupTrendNoExistent.getId(),"动态不存在");
        }
    }

    @Override
    public Pagination<GroupTrend> findPage(Integer currentUserId, String groupId, Integer pageIndex, Integer pageSize) {
        Group2 group = group2Service.findAndCheckDept(groupId);

        Query<GroupTrend> query = this.createQuery();
        query.field("groupId").equal(groupId).field("statusId").equal(CircleEnum.GroupTrendStatusEnum.Passed.getId());
        Long total = query.countAll();

        query.order("-statusTime");
        query.offset(pageIndex*pageSize).limit(pageSize);

        List<GroupTrend> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        this.wrapUser(list);

        Pagination<GroupTrend> pagination = new Pagination<>(list, total, pageIndex, pageSize);
        return pagination;
    }

    @Override
    public Pagination<MobileGroupTrendVO> findPageAndVO(Integer currentUserId, String groupId, Integer pageIndex, Integer pageSize) {
        Group2 group = group2Service.findAndCheckDept(groupId);

        Query<GroupTrend> query = this.createQuery();
        query.field("groupId").equal(groupId).field("statusId").equal(CircleEnum.GroupTrendStatusEnum.Passed.getId());
        Long total = query.countAll();

        query.order("-statusTime");
        query.offset(pageIndex*pageSize).limit(pageSize);
        query.retrievedFields(false, "content");    // 过滤掉富文本字段

        List<GroupTrend> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        this.wrapAll(list);
        this.wrapUserLike(currentUserId, list);

        this.wrapUserCredit(currentUserId, list);

        List<MobileGroupTrendVO> voList = this.convertToMobile(list);
        Pagination<MobileGroupTrendVO> pagination1 = new Pagination<>(voList, total, pageIndex, pageSize);
        return pagination1;
    }

    protected void wrapAll(GroupTrend groupTrend) {
        if (null == groupTrend) {
            return;
        }

        this.wrapUser(groupTrend);
        this.wrapRecentLikeList(groupTrend);
    }

    protected void wrapGroup(GroupTrend groupTrend) {
        if (null == groupTrend) {
            return;
        }

      Group2 group2=group2Service.findAndCheckById(groupTrend.getGroupId());
      groupTrend.setGroup2(group2);
    }

    protected void wrapAll(List<GroupTrend> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        this.wrapUser(list);
    }

    protected void wrapRecentLikeList(GroupTrend groupTrend) {
        if (null == groupTrend) {
            return;
        }
        if (SdkUtils.isEmpty(groupTrend.getRecentLikeIdList())) {
            return;
        }
        List<GroupTrendLike> likeList =  groupTrendLikeService.findFullByIds(groupTrend.getRecentLikeIdList());
        groupTrend.setRecentLikeList(likeList);
    }

    protected void wrapUser(GroupTrend groupTrend) {
        if (null == groupTrend) {
            return;
        }
        User user = user2Service.findById(groupTrend.getUserId());
        groupTrend.setUser(user);
    }

    protected void wrapUser(List<GroupTrend> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        Set<Integer> userIdSet = new HashSet<>(list.size());
        for (GroupTrend groupTrend:list) {
            userIdSet.add(groupTrend.getUserId());
        }

        List<User> userList = user2Service.findByIds(new ArrayList<>(userIdSet));
        for (GroupTrend groupTrend:list) {
            for (User user:userList) {
                if (groupTrend.getUserId().equals(user.getUserId())) {
                    groupTrend.setUser(user);
                    break;
                }
            }
        }
    }

    protected void wrapUserLike(Integer userId, List<GroupTrend> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        Set<String> trendIdSet = new HashSet<>(list.size());
        for (GroupTrend groupTrend:list) {
            trendIdSet.add(groupTrend.getId().toString());
        }

        List<GroupTrendLike> likeList = groupTrendLikeService.findByUserAndTrends(userId, new ArrayList<>(trendIdSet));
        groupTrendLikeService.wrapAll(likeList);
        for (GroupTrend groupTrend:list) {
            List<String> recentLikeIdList = groupTrend.getRecentLikeIdList();
            if(null!=recentLikeIdList) {
                List<GroupTrendLike> groupTrendLikes = groupTrendLikeService.findFullByIds(recentLikeIdList);
                groupTrend.setRecentLikeList(groupTrendLikes);
            }
            for (GroupTrendLike like:likeList) {
                if (groupTrend.getId().toString().equals(like.getTrendId())) {
                    groupTrend.setLike(like);
                    break;
                }
            }
        }
    }


    protected void wrapUserCredit(Integer userId, List<GroupTrend> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        Set<String> trendIdSet = new HashSet<>(list.size());
        for (GroupTrend groupTrend:list) {
            trendIdSet.add(groupTrend.getId().toString());
        }

        List<GroupTrendCredit> creditList = groupTrendCreditService.findByUserAndTrends(userId, new ArrayList<>(trendIdSet));

        for (GroupTrend groupTrend:list) {
            List<String> recentCreditList = groupTrend.getRecentCreditIdList();
            if(null!=recentCreditList) {
                List<GroupTrendCredit> groupTrendCredits = groupTrendCreditService.findFullByIds(recentCreditList);
                groupTrend.setRecentCreditList(groupTrendCredits);
            }
            for (GroupTrendCredit credit:creditList) {
                if (groupTrend.getId().toString().equals(credit.getTrendId())) {
                    groupTrend.setCredit(credit);
                    break;
                }
            }
        }
    }
    protected void wrapUserLike(Integer userId, GroupTrend groupTrend) {
        if (null == groupTrend) {
            return;
        }

        GroupTrendLike like = groupTrendLikeService.findByUK(userId, groupTrend.getId().toString());
        groupTrend.setLike(like);
    }
    protected void wrapUserCredit(Integer userId, GroupTrend groupTrend) {
        if (null == groupTrend) {
            return;
        }

        GroupTrendCredit credit = groupTrendCreditService.findByUK(userId, groupTrend.getId().toString());
        groupTrend.setCredit(credit);

        List<String> recentCreditIdList = groupTrend.getRecentCreditIdList();
        if(null!=recentCreditIdList){
            List<GroupTrendCredit> groupTrendCredits = groupTrendCreditService.findFullByIds(recentCreditIdList);
            groupTrend.setRecentCreditList(groupTrendCredits);
        }
    }
    protected MobileGroupTrendVO convertToMobile(GroupTrend groupTrend) {
        if (null == groupTrend) {
            return null;
        }
        MobileGroupTrendVO vo = new MobileGroupTrendVO(groupTrend);
        return vo;
    }

    protected List<MobileGroupTrendVO> convertToMobile(List<GroupTrend> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<MobileGroupTrendVO> voList = new ArrayList<>(list.size());
        for (GroupTrend groupTrend:list) {
            voList.add(new MobileGroupTrendVO(groupTrend));
        }
        return voList;
    }

    @Autowired
    protected User2Service user2Service;

    @Autowired
    protected GroupUser2Service groupUser2Service;

    @Override
    public GroupTrend update(Integer currentUserId, String id, GroupTrendUpdateForm form) {
        GroupTrend dbItem = this.findById(id);
        if (StringUtils.isNotBlank(form.getTitle())) {
            dbItem.setTitle(form.getTitle());
        }
        if (StringUtils.isNotBlank(form.getSummary())) {
            dbItem.setSummary(form.getSummary());
        }
        if (StringUtils.isNotBlank(form.getContent())) {
            dbItem.setContent(form.getContent());
        }
        if (StringUtils.isNotBlank(form.getPicUrl())) {
            dbItem.setPicUrl(form.getPicUrl());
        }
        if (StringUtils.isNotBlank(form.getVideosJson())) {
            List<Video> videoList = SdkJsonUtils.parseList(Video.class, form.getVideosJson());
            dbItem.setVideos(videoList);
        }
        if (StringUtils.isNotBlank(form.getAttachmentsJson())) {
            List<Attachment> attachmentList = SdkJsonUtils.parseList(Attachment.class, form.getAttachmentsJson());
            dbItem.setAttachments(attachmentList);
        }
        dbItem.setUpdateUserId(currentUserId);
        dbItem.setUpdateTime(System.currentTimeMillis());
        dbItem = this.saveEntityAndFind(dbItem);
        return dbItem;
    }

    @Override
    public GroupTrend add(Integer currentUserId, GroupTrendAddForm form) throws HttpApiException {
        GroupTrend groupTrendTemp = form.toGroupTrend();

        Group2 group = group2Service.findAndCheckDept(groupTrendTemp.getGroupId());
        groupTrendTemp.setGroupType(group.getType());
        User currentUser = user2Service.findAndCheckDoctor(currentUserId);
//        groupUser2Service.checkRootOrAdminPri(groupTrendTemp.getGroupId(), currentUserId);

        groupTrendTemp.setUserId(currentUserId);    // 医生作者信息
        groupTrendTemp.setUser(currentUser);

        groupTrendTemp.setCreateUserId(currentUserId);
        groupTrendTemp.setCreateTime(System.currentTimeMillis());
        groupTrendTemp.setStatus(CircleEnum.GroupTrendStatusEnum.Passed);   // 默认是通过

        GroupTrend groupTrend = this.saveEntityAndFind(groupTrendTemp);

        // 发送IM消息
        sendIMMsg(groupTrend);

        return groupTrend;
    }

    @Autowired
    protected PubaccHelper pubaccHelper;

    protected void sendIMMsg(GroupTrend groupTrend) throws HttpApiException {
        String tag = "sendIMMsg";
        logger.info("{}. groupTrend={}", tag, groupTrend);
        PubMsgParam pubMsg = new PubMsgParam();
        pubMsg.setModel(2);//1:文本消息；2：单图文（新闻消息）；3：多图文 4、分享新闻
        pubMsg.setPubId(PubUtils.PUB_DEPT + groupTrend.getGroupId());//公共号Id
        pubMsg.setSendType(1);//sendType=1：表示广播(消息只发送给订阅者)
        pubMsg.setToAll(true);//发送给所有人
        pubMsg.setToNews(false);//不往健康动态公共号转发
        pubMsg.setMpt(buildIMMsg(groupTrend));
        pubMsg.setPush(false);//不推送
//        pubMsg.setSource("system");
        pubaccHelper.sendMsgToPub(pubMsg);
    }

    public static List<ImgTextMsg> buildIMMsg(GroupTrend groupTrend){
//        Map<String, Object> params = new HashedMap(2);
//        params.put("bizType", CircleImBizTypeEnum.GroupTrend.getId());
//        params.put("bizId", groupTrend.getId().toString());
        String url = String.format("app://DeptDynamic?bizType=%s&bizId=%s", CircleImBizTypeEnum.GroupTrend.getId(), groupTrend.getId().toString());

        List<ImgTextMsg>mpt = new ArrayList<ImgTextMsg>(1);
        ImgTextMsg msg = new ImgTextMsg();
        msg.setTitle(groupTrend.getTitle());
        msg.setPic(groupTrend.getPicUrl());
        msg.setDigest(groupTrend.getSummary());
//        msg.setParam(params);
        msg.setUrl(url);
        mpt.add(msg);
        return mpt;
    }

    @Override
    public boolean remove(Integer currentUserId, String id) {
        GroupTrend dbItem = this.findById(id);

        groupUser2Service.checkRootOrAdminPri(dbItem.getGroupId(), currentUserId);

        if (CircleEnum.GroupTrendStatusEnum.Deleted == CircleEnum.GroupTrendStatusEnum.eval(dbItem.getStatusId())) {
            return true;
        }
        dbItem.setStatus(CircleEnum.GroupTrendStatusEnum.Deleted);
        dbItem = this.saveEntityAndFind(dbItem);
        return true;
    }

    @Override
    public void incrTotalComment(String id, int count) {
//        DBObject query = new BasicDBObject();
//        query.put(Mapper.ID_KEY, new ObjectId(id));
//
//        DBObject update = new BasicDBObject();
//        update.put("$inc", new BasicDBObject("totalComment", 1));
//        dsForRW.getDB().getCollection("c_group_trend").update(query, update);

        Query<GroupTrend> query = this.createQueryByPK(id);
        UpdateOperations<GroupTrend> ops = this.createUpdateOperations();
        ops.inc("totalComment",count);

        this.update(query, ops);

//        GroupTrend dbItem = this.findById(id);
//        dbItem.setTotalComment(dbItem.getTotalComment()+count);
//        dbItem = this.saveEntityAndFind(dbItem);
//        return dbItem.getTotalComment();
    }

    @Override
    public void incrTotalLike(String id, int count) {
        Query<GroupTrend> query = this.createQueryByPK(id);
        UpdateOperations<GroupTrend> ops = this.createUpdateOperations();
        ops.inc("totalLike");

        this.update(query, ops);

//        GroupTrend dbItem = this.findById(id);
//        dbItem.setTotalLike(dbItem.getTotalLike()+count);
//        dbItem = this.saveEntityAndFind(dbItem);
//        return dbItem.getTotalComment();
    }
    private static final int DEFAULT_SHOW_CREDIT_COUNT = 5;

    @Override
    public void incrTotalCreditByAdd(String id, String creditId) {
        GroupTrend dbItem = this.findById(id);
        dbItem.setTotalCredit(dbItem.getTotalCredit()+1);

        if (null == dbItem.getRecentCreditIdList()) {
            dbItem.setRecentCreditIdList(new ArrayList<>());
        }
        dbItem.getRecentCreditIdList().add(0, creditId);
        if (dbItem.getRecentCreditIdList().size()>DEFAULT_SHOW_CREDIT_COUNT) {
            dbItem.getRecentCreditIdList().remove(dbItem.getRecentCreditIdList().size()-1);
        }
        this.saveEntity(dbItem);
    }

    private static final int DEFAULT_SHOW_LIKE_COUNT = 8;
    @Override
    public void incrTotalLikeByAdd(String id, String likeId) {
        GroupTrend dbItem = this.findById(id);
        dbItem.setTotalLike(dbItem.getTotalLike()+1);

        if (null == dbItem.getRecentLikeIdList()) {
            dbItem.setRecentLikeIdList(new ArrayList<>());
        }
        dbItem.getRecentLikeIdList().add(0, likeId);
        if (dbItem.getRecentLikeIdList().size()>DEFAULT_SHOW_LIKE_COUNT) {
            dbItem.getRecentLikeIdList().remove(dbItem.getRecentLikeIdList().size()-1);
        }
        this.saveEntity(dbItem);
    }

    @Override
    public void decrTotalLikeByRemove(String id, String likeId) {
        GroupTrend dbItem = this.findById(id);
        dbItem.setTotalLike(dbItem.getTotalLike()-1);
        if (0 == dbItem.getTotalLike()) {
            dbItem.setRecentLikeIdList(null);
            this.saveEntity(dbItem);
            return;
        }

        if (dbItem.getRecentLikeIdList().contains(likeId)) {
            List<String> likeIdList = groupTrendLikeService.findIdListPage(id, 0, DEFAULT_SHOW_LIKE_COUNT);
            dbItem.setRecentLikeIdList(likeIdList);
        }
        this.saveEntity(dbItem);
    }

    @Override
    public MobileGroupTrendVO findByIdAndVO(Integer currentUserId, String id) {
        GroupTrend groupTrend = this.findById(id);
        this.wrapGroup(groupTrend);
        this.wrapAll(groupTrend);
        this.wrapUserLike(currentUserId, groupTrend);
        this.wrapUserCredit(currentUserId, groupTrend);
        MobileGroupTrendVO vo = this.convertToMobile(groupTrend);
        return vo;
    }


}
