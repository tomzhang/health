package com.dachen.health.circle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.circle.entity.GroupTrendComment;
import com.dachen.health.circle.entity.GroupTrendCommentCredit;
import com.dachen.health.circle.service.GroupTrendCommentCreditService;
import com.dachen.health.circle.service.GroupTrendCommentService;
import com.dachen.health.circle.service.User2Service;
import com.dachen.health.circle.vo.MobileGroupTrendCommentCreditVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.common.util.RemoteSysManager;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 评论打赏学分
 * Created By lim
 * Date: 2017/6/7
 * Time: 16:23
 */
@Model(GroupTrendCommentCredit.class)
@Service
@Deprecated
public class GroupTrendCommentCreditServiceImpl extends BaseServiceImpl implements GroupTrendCommentCreditService {
    @Autowired
    protected User2Service user2Service;
    @Autowired
    protected GroupTrendCommentService groupTrendCommentService;
    @Autowired
    protected RemoteSysManager remoteSysManager;

    private static final String SERVER = "CREDIT";
    private static final String ACTION = "/trans/reward";

    @Override
    public synchronized GroupTrendCommentCredit add(Integer currentUserId, String commentId, Integer credit) {
        if (credit <= 0) {
            throw new ServiceException("打赏学分必须大于0");
        }
        GroupTrendComment groupTrendComment = groupTrendCommentService.findById(commentId);
        if (null == groupTrendComment) {
            throw new ServiceException("打赏的评论为空" + commentId);
        }
        GroupTrendCommentCredit commentCredit = this.findByCommentIdAndUserId(commentId, currentUserId);
        //同一个人对同一个人只能打赏一次
        if (commentCredit != null) {
            throw new ServiceException("同一个人对同一个人只能打赏一次");
        }
        User user = user2Service.findDoctorById(currentUserId);
        if (null == user) {
            throw new ServiceException("认证通过的医生才可以打赏");
        }
        GroupTrendCommentCredit groupTrendCommentCredit = new GroupTrendCommentCredit(groupTrendComment, user);

        //调用外部系统打赏操作
        boolean result = this.GiveCredit(commentId, currentUserId, groupTrendComment.getUserId(), String.valueOf(credit));
        if(!result){
            return null;
        }
        groupTrendCommentCredit.setCredit(credit);
        //groupTrendCommentCredit.setCreditId(creditId);
        groupTrendCommentCredit.setCreateTime(System.currentTimeMillis());
        groupTrendCommentCredit = this.saveEntityAndFind(groupTrendCommentCredit);
        return groupTrendCommentCredit;
    }

    @Override
    public List<GroupTrendCommentCredit> findFullByCommentIds(List<String> commentIds) {
        if (null == commentIds) {
            throw new ServiceException("查询的打赏评论id为空");
        }
        List<GroupTrendCommentCredit> dbItems = this.findByIds(commentIds);
        this.wrapAll(dbItems);
        return dbItems;
    }

    @Override
    public GroupTrendCommentCredit findByCommentIdAndUserId(String commentId, Integer userId) {
        Query<GroupTrendCommentCredit> query = this.createQuery();
        query.field("userId").equal(userId).field("commentId").equal(commentId);
        return query.get();
    }

    @Override
    public Pagination<MobileGroupTrendCommentCreditVO> findPageAndVO(Integer currentUserId, String commentId, Integer pageIndex, Integer pageSize) {
        Pagination<GroupTrendCommentCredit> page = this.findPage(commentId, pageIndex, pageSize);
        if (null == page || SdkUtils.isEmpty(page.getPageData())) {
            return null;
        }
        List<GroupTrendCommentCredit> list = page.getPageData();
        this.wrapAll(list);
        List<MobileGroupTrendCommentCreditVO> voList = this.convertToMobile(list);

        Pagination<MobileGroupTrendCommentCreditVO> page2 = new Pagination<>(voList, page.getTotal(), page.getPageIndex(), page.getPageSize());
        return page2;
    }

    @Override
    public Pagination<GroupTrendCommentCredit> findPage(String commentId, Integer pageIndex, Integer pageSize) {
        Query<GroupTrendCommentCredit> query = this.createQuery();
        query.field("commentId").equal(commentId);

        long total = query.countAll();
        query.order("-createTime");
        query.offset(pageIndex * pageSize).limit(pageSize);
        List<GroupTrendCommentCredit> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        Pagination<GroupTrendCommentCredit> page = new Pagination<>(list, total, pageIndex, pageSize);
        return page;
    }

    protected boolean GiveCredit(String commentId, Integer userId, Integer targerId, String credit) {

        Map<String, String> param = new HashMap<>();
        param.put("source", String.valueOf(userId));
        param.put("sourceType", "1");
        param.put("target", String.valueOf(targerId));
        param.put("targetType", "1");
        param.put("value", credit);
        param.put("orderId", commentId);
        param.put("reason", "评论打赏学分");
        String result = remoteSysManager.send(SERVER, ACTION, param);

        JSONObject jsonObject = JSON.parseObject(result);

        if (jsonObject != null && "1".equals(jsonObject.getInteger("resultCode"))) {
            return true;
        } else {
            return false;
        }

    }


    protected List<MobileGroupTrendCommentCreditVO> convertToMobile(List<GroupTrendCommentCredit> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<MobileGroupTrendCommentCreditVO> voList = new ArrayList<>(list.size());
        for (GroupTrendCommentCredit credit : list) {
            voList.add(new MobileGroupTrendCommentCreditVO(credit));
        }
        return voList;
    }

    public void wrapAll(List<GroupTrendCommentCredit> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        this.wrapUser(list);
    }

    protected void wrapUser(List<GroupTrendCommentCredit> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        Set<Integer> userIdSet = new HashSet<>(list.size());
        for (GroupTrendCommentCredit credit : list) {
            userIdSet.add(credit.getUserId());
        }
        List<User> user2List = user2Service.findDoctorListByIds(new ArrayList<>(userIdSet));
        for (GroupTrendCommentCredit credit : list) {
            for (User user2 : user2List) {
                if (credit.getUserId().equals(user2.getUserId())) {
                    credit.setUser(user2);
                    break;
                }
            }
        }
    }
}
