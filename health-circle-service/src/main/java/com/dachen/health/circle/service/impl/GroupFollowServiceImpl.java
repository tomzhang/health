package com.dachen.health.circle.service.impl;

import com.dachen.health.circle.entity.Group2;
import com.dachen.health.circle.entity.GroupDoctor2;
import com.dachen.health.circle.entity.GroupFollow;
import com.dachen.health.circle.service.*;
import com.dachen.health.circle.vo.MobileGroupFollowVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.pub.service.PubGroupService;
import com.dachen.pub.util.PubUtils;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.exception.ServiceException;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.StringUtil;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Model(GroupFollow.class)
@Service
public class GroupFollowServiceImpl extends BaseServiceImpl implements GroupFollowService {

    @Autowired
    protected PubGroupService pubGroupService;

    @Autowired
    protected User2Service user2Service;

    @Autowired
    protected ImService imService;

    @Override
    public GroupFollow add(Integer currentUserId, String groupId) throws HttpApiException {
        Integer doctorId = currentUserId;
        User user = user2Service.findAndCheckDoctor(doctorId);
        Integer userId = doctorId;
        Group2 group = group2Service.findAndCheckDept(groupId);

        GroupFollow groupFollow = this.findByUK(groupId, userId);
        if (null == groupFollow) {
            groupFollow = new GroupFollow(user, group);

            /*
            15:48:01.183 [http-nio-8101-exec-6] INFO  com.dachen.sdk.component.RemoteInvokeComponent - executePostMethod. http://pubacc/inner/pub/subscribe execute spent 4 ms, ret={"resultCode":1}
             */
            // 关注公众号
            pubGroupService.addSubUser(userId, groupId, true);

            // 保存数据
            groupFollow = this.saveEntityAndFind(groupFollow);

            // 不能往公众号发送消息
//            String gid = PubUtils.PUB_DEPT + groupId;
//            imService.sendRemind(doctorId, gid, String.format("感谢你关注%s", group.getName()));
        }
        return groupFollow;
    }

    @Override
    public int dismissGroup(Integer currentUserId, String groupId) throws HttpApiException {
        Query<GroupFollow> query = this.createQuery();
        query.field("groupId").equal(groupId);

        List<GroupFollow> list = query.asList();
        if (SdkUtils.isNotEmpty(list)) {
            for (GroupFollow groupFollow:list) {
                logger.info("取消关注 发送im userId:{}"+groupFollow.getUserId()+" groupId: "+groupId);
                pubGroupService.delSubUser(groupFollow.getUserId(), groupId);
                iBusinessServiceMsg.refreshCircleTab(String.valueOf(groupFollow.getUserId()));
            }
        }
        return this.deleteByQuery(query);
    }

    /**
     * 我加入的科室，默认关注，不能取消关注。退出科室后，默认不关注该科室。
     *
     * @param currentUserId
     * @param groupId
     * @return
     * @throws HttpApiException
     */
    @Override
    @Deprecated
    public boolean removeByGroup(Integer currentUserId, String groupId) throws HttpApiException {
        GroupFollow groupFollow = this.findByUK(groupId, currentUserId);
        if (null == groupFollow) {
            return true;
        }
        iBusinessServiceMsg.refreshCircleTab(String.valueOf(groupFollow.getUserId()));
        return this.remove(currentUserId, groupFollow.getId().toString());
    }

    /**
     * 我加入的科室，默认关注，不能取消关注。退出科室后，默认不关注该科室。
     *
     * @return
     * @throws HttpApiException
     */
    @Override
    public boolean remove(Integer currentUserId, String id) throws HttpApiException {
        GroupFollow dbItem = this.findById(id);

        Group2 group = group2Service.findAndCheckDept(dbItem.getGroupId());

        GroupDoctor2 groupDoctor2 = groupDoctor2Service.findNormalByUK(dbItem.getGroupId(), currentUserId);
        if (null != groupDoctor2) {
            throw new ServiceException("科室未退出，不能取消关注");
        }
        // 取关公众号
        pubGroupService.delSubUser(currentUserId, dbItem.getGroupId());
        // 删除数据
        boolean ret = this.deleteById(id);
        return ret;
    }
    @Override
    public Set<Integer> getDeptUserByUserId(Integer userId){
        List<String> myGroupList = this.findGroupIdListByUser(userId);
        if (SdkUtils.isEmpty(myGroupList)){
            return null;
        }
        List<Integer> deptUserId = groupDoctor2Service.findDoctorIdListByGroupAndUnions(myGroupList, null);
        if(SdkUtils.isEmpty(deptUserId)){
            return null;
        }
        return new HashSet(deptUserId);
    }

    @Autowired
    protected GroupDoctor2Service groupDoctor2Service;
    @Autowired
    protected IBusinessServiceMsg iBusinessServiceMsg;


    @Override
    public GroupFollow findByUK(String groupId, Integer userId) {
        Query<GroupFollow> query = this.createQuery();
        query.field("userId").equal(userId).field("groupId").equal(groupId);
//        query.order("-createTime");
        return query.get();
    }

    @Override
    public List<GroupFollow> findList(Integer userId) {
        Query<GroupFollow> query = this.createQuery();
        query.field("userId").equal(userId);
        query.order("-createTime");
        return query.asList();
    }
    public List<String> findGroupIdListByUser(Integer userId) {
        Query<GroupFollow> query = this.createQuery();
        query.field("userId").equal(userId);
        query.retrievedFields(true, "groupId");
        List<GroupFollow> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<String> groupIdList = list.stream().map(o->o.getGroupId()).collect(Collectors.toList());
        return groupIdList;
    }

    @Override
    public List<MobileGroupFollowVO> findListAndVO(Integer userId) {
        List<GroupFollow> list = this.findList(userId);
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        this.wrapAll(list);
        List<MobileGroupFollowVO> voList = this.convertToMobile(list);
        return voList;
    }

    @Override
    public Pagination<MobileGroupFollowVO> findMoreAndVO(Integer currentUserId, String kw, Integer pageIndex, Integer pageSize) {
        String tag = "findMoreAndVO";
        logger.debug("{}. currentUserId={}, kw={}, pageIndex={}, pageSize={}", tag, currentUserId, kw, pageIndex, pageSize);
        List<String> groupIdList = this.findGroupIdListByUser(currentUserId);//已经关注的科室
        String myGroupId = groupDoctor2Service.findDeptIdByDoctor(currentUserId);//已经加入的科室
        if(SdkUtils.isNotEmpty(groupIdList) && StringUtil.isNotEmpty(myGroupId)) {
            groupIdList.add(myGroupId);
        }
        logger.debug("{}. groupIdList={}", tag, groupIdList);

        Pagination<Group2> page = group2Service.findDeptPage(kw, groupIdList, pageIndex, pageSize);
        logger.debug("{}. page={}", tag, page);
        if (null == page) {
            return null;
        }

        List<Group2> group2List = page.getPageData();
        if (SdkUtils.isEmpty(group2List)) {
            return null;
        }

        List<GroupFollow> list = this.convertToGroupFollow(currentUserId, group2List);

        this.wrapAll(list);
        List<MobileGroupFollowVO> voList = this.convertToMobile(list);

        Pagination<MobileGroupFollowVO> page2 = new Pagination<>(voList, page.getTotal(), pageIndex, pageSize);
        logger.debug("{}. page2={}", tag, page2);
        return page2;
    }

    protected List<GroupFollow> convertToGroupFollow(Integer currentUserId, List<Group2> group2List) {
        List<GroupFollow> list = new ArrayList<>(group2List.size());
        for (Group2 group2:group2List) {
            list.add(new GroupFollow(currentUserId, group2));
        }
        return list;
    }

    private List<MobileGroupFollowVO> convertToMobile(List<GroupFollow> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<MobileGroupFollowVO> voList = new ArrayList<>(list.size());
        for (GroupFollow groupFollow:list) {
            voList.add(new MobileGroupFollowVO(groupFollow));
        }
        return voList;
    }

    @Autowired
    protected Group2Service group2Service;

    protected void wrapAll(List<GroupFollow> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        this.wrapGroup(list);
    }

    protected void wrapGroup(List<GroupFollow> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        Set<String> groupIdSet = new HashSet<>(list.size());
        for (GroupFollow groupFollow:list) {
            groupIdSet.add(groupFollow.getGroupId());
        }

        List<Group2> group2List = group2Service.findFullByIds(new ArrayList<>(groupIdSet));
        for (GroupFollow groupFollow:list) {
            for (Group2 group2:group2List) {
                if (groupFollow.getGroupId().equals(group2.getId().toString())) {
                    groupFollow.setGroup(group2);
                    break;
                }
            }
        }
    }
}
