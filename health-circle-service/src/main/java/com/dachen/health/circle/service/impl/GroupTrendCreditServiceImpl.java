package com.dachen.health.circle.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.circle.CircleImBizTypeEnum;
import com.dachen.health.circle.entity.GroupTrend;
import com.dachen.health.circle.entity.GroupTrendCredit;
import com.dachen.health.circle.service.*;
import com.dachen.health.circle.vo.MobileGroupTrendCreditVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.common.util.RemoteSysManager;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import org.apache.commons.collections.map.HashedMap;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 科室动态打赏学分
 * Created By lim
 * Date: 2017/6/7
 * Time: 16:23
 */
@Model(GroupTrendCredit.class)
@Service
public class GroupTrendCreditServiceImpl extends BaseServiceImpl implements GroupTrendCreditService {
    @Autowired
    protected User2Service user2Service;
    @Autowired
    protected GroupTrendService groupTrendService;
    @Autowired
    private RemoteSysManager remoteSysManager;
    @Autowired
    private Group2Service group2Service;

    private static final String SERVER = "CREDIT";
    private static final String ACTION = "trans/reward";

    @Override
    public synchronized GroupTrendCredit add(Integer currentUserId, String TrendId, Integer credit) {
        if (credit <= 0) {
            throw new ServiceException("打赏学分必须大于0");
        }
        GroupTrend trend = groupTrendService.findById(TrendId);
        if (null == trend) {
            throw new ServiceException("打赏动态不存在");
        }

        group2Service.findAndCheckById(trend.getGroupId());

        if(trend.getCreateUserId().equals(currentUserId)){
            throw new ServiceException("不能给自己打赏");
        }
        GroupTrendCredit trendCredit = this.findByTrendIdAndUserId(TrendId, currentUserId);
        //同一个人对同一个人只能打赏一次
        if (trendCredit != null) {
            throw new ServiceException("同一个人对同一个动态只能打赏一次");
        }
        User user = user2Service.findDoctorById(currentUserId);
        if (null == user) {
            throw new ServiceException("认证通过的医生才可以打赏");
        }
        GroupTrendCredit groupTrendCredit = new GroupTrendCredit(trend, user);

        //调用外部系统打赏操作
        boolean result = this.GiveCredit(TrendId, currentUserId, trend.getGroupId(), credit ,user.getName());
        if(!result){
            throw new ServiceException("打赏失败");
        }
        groupTrendCredit.setCredit(credit);
        //打賞通知
        this.sendCreditIMMsgToClients(currentUserId,credit,TrendId,trend.getGroupId());
        //groupTrendCommentCredit.setCreditId(creditId);
        groupTrendCredit.setCreateTime(System.currentTimeMillis());
        groupTrendCredit = this.saveEntityAndFind(groupTrendCredit);
        groupTrendService.incrTotalCreditByAdd(TrendId,groupTrendCredit.getId().toString());
        return groupTrendCredit;
    }

    @Override
    public List<GroupTrendCredit> findFullByIds(List<String> idList) {
        List<GroupTrendCredit> list = this.findByIds(idList);
        this.wrapAll(list);
        return list;
    }

    @Override
    public List<GroupTrendCredit> findFullByTrendIds(List<String> TrendIds) {
        if (null == TrendIds) {
            throw new ServiceException("查询的打赏评论id为空");
        }
        List<GroupTrendCredit> dbItems = this.findByIds(TrendIds);
        this.wrapAll(dbItems);
        return dbItems;
    }

    @Override
    public GroupTrendCredit findByUK(Integer userId, String trendId) {
        Query<GroupTrendCredit> query = this.createQuery();
        query.field("userId").equal(userId).field("trendId").equal(trendId);
        return query.get();
    }

    @Override
    public List<GroupTrendCredit> findByUserAndTrends(Integer userId, List<String> trendIdList) {
        Query<GroupTrendCredit> query = this.createQuery();
        query.field("trendId").in(trendIdList);
        query.field("userId").equal(userId);
        List<GroupTrendCredit> dbItems = query.asList();
        this.wrapAll(dbItems);
        return dbItems;
    }

    @Override
    public GroupTrendCredit findByTrendIdAndUserId(String trendId, Integer userId) {
        Query<GroupTrendCredit> query = this.createQuery();
        query.field("userId").equal(userId).field("trendId").equal(trendId);
        return query.get();
    }

    @Override
    public Pagination<MobileGroupTrendCreditVO> findPageAndVO(Integer currentUserId, String trendId, Integer pageIndex, Integer pageSize) {
        Pagination<GroupTrendCredit> page = this.findPage(trendId, pageIndex, pageSize);
        if (null == page || SdkUtils.isEmpty(page.getPageData())) {
            return null;
        }
        List<GroupTrendCredit> list = page.getPageData();
        this.wrapAll(list);
        List<MobileGroupTrendCreditVO> voList = this.convertToMobile(list);

        Pagination<MobileGroupTrendCreditVO> page2 = new Pagination<>(voList, page.getTotal(), page.getPageIndex(), page.getPageSize());
        return page2;
    }

    @Override
    public Pagination<GroupTrendCredit> findPage(String trendId, Integer pageIndex, Integer pageSize) {
        Query<GroupTrendCredit> query = this.createQuery();
        query.field("trendId").equal(trendId);

        long total = query.countAll();
        query.order("-credit");
        query.offset(pageIndex * pageSize).limit(pageSize);
        List<GroupTrendCredit> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        Pagination<GroupTrendCredit> page = new Pagination<>(list, total, pageIndex, pageSize);
        return page;
    }
    @Autowired
    protected ImService imService;

    protected void sendCreditIMMsgToClients(Integer currentUserId, Integer credit ,String trendId ,String groupId) {
        User user = user2Service.findDoctorById(currentUserId);
        GroupTrend groupTrend = groupTrendService.findFullById(currentUserId, trendId);
        if (user==null || groupTrend==null) {
            return;
        }
           // 通知发布该动态的医生
            String title = String.format("系统通知");
            String content = String.format("%s打赏科室动态\"%s\"%s学币", user.getName(), groupTrend.getTitle(),credit);
            Map<String, Object> params = new HashedMap(2);
            params.put("bizType", CircleImBizTypeEnum.GroupTrendCredit.getId());
            params.put("bizId", groupId);
            ImgTextMsg imgTextMsg=new ImgTextMsg();
            imgTextMsg.setStyle(7);
            imgTextMsg.setFooter("查看详情");
            imService.sendTodoNotifyMsg(groupTrend.getCreateUserId(), title, content, null, params,imgTextMsg);

    }
    protected boolean GiveCredit(String trendId, Integer userId, String groupId, Integer credit,String userName) {
        GroupTrend groupTrend=groupTrendService.findById(trendId);
        Map<String, String> param = new HashMap<>();
        param.put("source", String.valueOf(userId));
        param.put("sourceType", "1");
        param.put("target", groupId);
        param.put("targetType", "3");
        param.put("value", String.valueOf(credit));
        param.put("orderId", trendId);
        param.put("reason", "动态打赏学币");
        param.put("toReason", userName+"打赏科室动态"+groupTrend.getTitle());
        try {
            remoteSysManager.send(SERVER, ACTION, param);
        }catch (Exception e){
            logger.error("打赏远程调用失败 {}",e);
            return false;
        }
        return true;

    }


    protected List<MobileGroupTrendCreditVO> convertToMobile(List<GroupTrendCredit> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<MobileGroupTrendCreditVO> voList = new ArrayList<>(list.size());
        for (GroupTrendCredit credit : list) {
            voList.add(new MobileGroupTrendCreditVO(credit));
        }
        return voList;
    }

    public void wrapAll(List<GroupTrendCredit> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        this.wrapUser(list);
    }

    @Override
    public void wrapUser(List<GroupTrendCredit> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        Set<Integer> userIdSet = new HashSet<>(list.size());
        for (GroupTrendCredit credit : list) {
            userIdSet.add(credit.getUserId());
        }
        List<User> user2List = user2Service.findDoctorListByIds(new ArrayList<>(userIdSet));
        for (GroupTrendCredit credit : list) {
            for (User user2 : user2List) {
                if (credit.getUserId().equals(user2.getUserId())) {
                    credit.setUser(user2);
                    break;
                }
            }
        }
    }
}
