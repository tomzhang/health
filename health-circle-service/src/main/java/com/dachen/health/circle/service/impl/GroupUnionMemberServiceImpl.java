package com.dachen.health.circle.service.impl;

import com.dachen.health.circle.CircleEnum;
import com.dachen.health.circle.CircleImBizTypeEnum;
import com.dachen.health.circle.entity.*;
import com.dachen.health.circle.service.*;
import com.dachen.health.circle.vo.MobileGroupUnionMemberVO;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.exception.ServiceException;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import org.apache.commons.collections.map.HashedMap;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Model(GroupUnionMember.class)
@Service
public class GroupUnionMemberServiceImpl extends BaseServiceImpl implements GroupUnionMemberService {

    @Autowired
    protected Group2Service group2Service;

    @Autowired
    protected GroupUser2Service groupUser2Service;

    @Autowired
    protected GroupUnionService groupUnionService;

    @Autowired
    protected GroupDoctor2Service groupDoctor2Service;

    @Autowired
    protected IBusinessServiceMsg businessServiceMsg;

    @Autowired
    protected ImService imService;

    @Autowired
    protected User2Service user2Service;

    protected void checkPri(Integer currentUserId, GroupUnion groupUnion) {
        GroupUser2 groupUser = groupUser2Service.findByUK(groupUnion.getGroupId(), currentUserId);
        if (null == groupUser) {
            throw new ServiceException("Forbidden");
        }
    }

    @Autowired
    protected GroupUnionApplyService groupUnionApplyService;

    public GroupUnionMember doAdd(Integer currentUserId, GroupUnion groupUnion, Group2 group, boolean ifMain, CircleEnum.GroupUnionMemberFromTypeEnum fromType, String fromId) {
        GroupUnionMember tmp = new GroupUnionMember(groupUnion, group, ifMain, fromType, fromId);
        tmp.setCreateTime(System.currentTimeMillis());
        tmp.setCreateUserId(currentUserId);

        GroupUnionMember member = this.saveEntityAndFind(tmp);
        return member;
    }

    @Override
    public GroupUnionMember addByCreateUnion(Integer currentUserId, GroupUnion groupUnion, Group2 group) {
        GroupUnionMember member = this.doAdd(currentUserId, groupUnion, group, true, CircleEnum.GroupUnionMemberFromTypeEnum.groupUnion, groupUnion.getId().toString());
        if (null != member) {
            groupUnionService.incrTotalMember(groupUnion.getId().toString(), 1);
        }
        return member;
    }

    @Override
    public GroupUnionMember addByApply(Integer currentUserId, String applyId) {
        Integer doctorId = currentUserId;

        GroupUnionApply apply = groupUnionApplyService.findFullAndCheckById(applyId);
        groupUser2Service.checkRootOrAdminPri(apply.getGroupId(), apply.getUserId());

        GroupUnionMember member = this.findByUK(apply.getUnionId(), apply.getGroupId());
        if (null != member) {
            throw new ServiceException(String.format("该%s已经是成员", GroupEnum.GroupType.eval(member.getGroupType()).getTitle()));
        }

        member = this.doAdd(currentUserId, apply.getUnion(), apply.getGroup(), false, CircleEnum.GroupUnionMemberFromTypeEnum.apply, applyId);

        if (null != member) {
            groupUnionService.incrTotalMember(apply.getUnionId(), 1);
        }

        return member;
    }

    public void closeApplyAndInviteByMember(String id) {
        groupUnionApplyService.closeByMember(id);
        groupUnionInviteService.closeByMember(id);
    }

    @Override
    public void closeApplyAndInviteByMemberAsync(String id) {
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                closeApplyAndInviteByMember(id);
            }
        });
    }

    @Autowired
    protected GroupUnionInviteService groupUnionInviteService;

    @Override
    public GroupUnionMember addByInvite(Integer currentUserId, String inviteId) {
        Integer doctorId = currentUserId;

        GroupUnionInvite invite = groupUnionInviteService.findFullById(inviteId);
        groupUser2Service.checkRootOrAdminPri(invite.getGroupId(), doctorId);

        GroupUnionMember member = this.findByUK(invite.getUnionId(), invite.getGroupId());
        if (null != member) {
            throw new ServiceException(String.format("该%s已经是成员", GroupEnum.GroupType.eval(member.getGroupType()).getTitle()));
        }

        member = this.doAdd(currentUserId, invite.getUnion(), invite.getGroup(), false, CircleEnum.GroupUnionMemberFromTypeEnum.invite, inviteId);

        if (null != member) {
            groupUnionService.incrTotalMember(invite.getUnionId(), 1);
        }

        return member;
    }


    @Override
    public GroupUnionMember findByUK(String unionId, String groupId) {
        Query<GroupUnionMember> query = this.createQuery();
        query.field("unionId").equal(unionId).field("groupId").equal(groupId);
        return query.get();
    }

    @Override
    public void checkByUK(String unionId, String groupId) {
        GroupUnionMember member = this.findByUK(unionId, groupId);

        if (null != member) {
            throw new ServiceException(String.format("该%s已经是成员", GroupEnum.GroupType.eval(member.getGroupType()).getTitle()));
        }
    }

    @Override
    public void checkIfMember(GroupUnion groupUnion, Group2 group) {
        GroupUnionMember member = this.findByUK(groupUnion.getId().toString(), group.getId().toString());
        if (null != member) {
            throw new ServiceException(String.format("该%s已经是成员", GroupEnum.GroupType.eval(member.getGroupType()).getTitle()));
        }
    }

    @Override
    public List<String> findIdListByUnionAndDoctor(String unionId, Integer doctorId) {
        List<String> groupIdList = groupUser2Service.findGroupIdByDoctor(doctorId); // 医生管理的组织列表

        Query<GroupUnionMember> query = this.createQuery();
        query.field("unionId").equal(unionId);

        Criteria[] criterias = new Criteria[groupIdList.size()];
        for (int i=0; i<groupIdList.size(); i++) {
            String groupId = groupIdList.get(i);
            criterias[i] = query.criteria("groupId").equal(groupId);
        }
        query.or(criterias);

        query.retrievedFields(true, Mapper.ID_KEY);

        List<GroupUnionMember> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<String> idList = list.stream().map(o->o.getId().toString()).collect(Collectors.toList());
        return idList;
    }


    @Override
    public boolean remove(Integer currentUserId, String unionId, String groupId) {
        GroupUnion groupUnion = groupUnionService.findAndCheckById(unionId);
        Group2 group = group2Service.findAndCheckGroupOrDept(groupId);
        this.checkPri(currentUserId, groupUnion);

        GroupUnionMember dbItem = this.findByUK(unionId, groupId);
        if (null == dbItem) {
            return true;
        }
        boolean ret = this.deleteById(dbItem.getId().toString());
        if (ret) {
            groupUnionService.incrTotalMember(unionId, -1);
        }
        return ret;
    }

    @Override
    public boolean remove(Integer currentUserId, String id) {
        GroupUnionMember dbItem = this.findById(id);
        if (null == dbItem) {
            return true;
        }

        GroupUnion groupUnion = groupUnionService.findAndCheckById(dbItem.getUnionId());
        if (dbItem.getGroupId().equals(groupUnion.getGroupId())) {
            throw new ServiceException("不能移除主体成员");
        }

        String unionId = groupUnion.getId().toString();
        boolean ret = this.deleteById(dbItem.getId().toString());
        if (ret) {
            groupUnionService.incrTotalMember(unionId, -1);
            //发送医联体成员移除通知
            this.sendRemoveDeptIMMsgToClients(currentUserId,groupUnion,dbItem.getGroupId());
        }
        return ret;
    }
    protected void sendRemoveDeptIMMsgToClients(Integer currentUserId, GroupUnion groupUnion,String groupId) {
        //User user = user2Service.findDoctorById(currentUserId);
        logger.info("科室移除联盟通知发送开始");
        Group2 group = group2Service.findById(groupId);
        List<GroupUser2> adminUser = groupUser2Service.findAdminUserByGroupId(groupId); //获取groupId所有管理员
        if(adminUser==null && adminUser.size()==0){
            logger.info("科室移除联盟adminUser为空");
            return;
        }
        // 通知科室或者圈子所有管理员
        for (GroupUser2 groupUser2:adminUser) {
            String title = String.format("系统通知");
            String content = String.format("%s被%s移除出组织", group.getName(), groupUnion.getName());
            Map<String, Object> params = new HashedMap(2);
            params.put("bizType", CircleImBizTypeEnum.GroupRemove.getId());
            params.put("bizId", group.getId());
            ImgTextMsg imgTextMsg=new ImgTextMsg();
            imgTextMsg.setStyle(7);
            imService.sendTodoNotifyMsg(groupUser2.getDoctorId(), title, content, null, params,imgTextMsg);
            logger.info("科室移除联盟通知发送完毕{} ,人数 {}"+groupUser2.getDoctorId() ,adminUser.size());
        }
    }
    public Pagination<GroupUnionMember> findPage(Integer currentUserId, String unionId, Integer pageIndex, Integer pageSize) {
        Query<GroupUnionMember> query = this.createQuery();
        query.field("unionId").equal(unionId);
        long total = query.countAll();

        query.offset(pageIndex*pageSize).limit(pageSize);

        List<GroupUnionMember> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        Pagination<GroupUnionMember> page = new Pagination<>(list, total, pageIndex, pageSize);
        return page;
    }

    public Pagination<GroupUnionMember> findPageByGroup(String groupId, Integer pageIndex, Integer pageSize) {
        Query<GroupUnionMember> query = this.createQuery();
        query.field("groupId").equal(groupId);
        long total = query.countAll();

        query.offset(pageIndex*pageSize).limit(pageSize);

        List<GroupUnionMember> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        Pagination<GroupUnionMember> page = new Pagination<>(list, total, pageIndex, pageSize);
        return page;
    }

    @Override
    public Pagination<MobileGroupUnionMemberVO> findPageAndVO(Integer currentUserId, String unionId, Integer pageIndex, Integer pageSize) {
        GroupUnion union = groupUnionService.findAndCheckById(unionId);

        Pagination<GroupUnionMember> page = this.findPage(currentUserId, unionId, pageIndex, pageSize);
        if (null == page) {
            return null;
        }
        List<GroupUnionMember> list = page.getPageData();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

//        this.wrapAll(list);
        this.wrapGroup(list);
        this.wrapIfCanRemove(currentUserId, union, list);

        List<MobileGroupUnionMemberVO> groupUnionMemberVOList = this.convertToMobile(list);

        Pagination<MobileGroupUnionMemberVO> voPage = new Pagination<>(groupUnionMemberVOList, page.getTotal(), page.getPageIndex(), page.getPageSize());
        return voPage;
    }

    protected void wrapIfCanRemove(Integer currentUserId, GroupUnion union, List<GroupUnionMember> list) {
        GroupUser2 groupUser2 = groupUser2Service.findByUK(union.getGroupId(), currentUserId);
        boolean ifCanRemove = false;
        if (null != groupUser2) {
            ifCanRemove = true;
        }

        for (GroupUnionMember member:list) {
            member.setIfCanRemove(ifCanRemove);
            if (union.getGroupId().equals(member.getGroupId())) {
                member.setIfCanRemove(false);
            }
        }
    }

    @Override
    public Pagination<MobileGroupUnionMemberVO> findPageByGroup(Integer currentUserId, String groupId, Integer pageIndex, Integer pageSize) {
        Pagination<GroupUnionMember> pagination = this.findPageByGroup(groupId, pageIndex, pageSize);
        if (null == pagination) {
            return null;
        }

        List<GroupUnionMember> list = pagination.getPageData();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

//        this.wrapAll(memberList);
        this.wrapGroupUnion(list);

        List<MobileGroupUnionMemberVO> groupUnionMemberVOList = this.convertToMobile(list);

        Pagination<MobileGroupUnionMemberVO> voPage = new Pagination<>(groupUnionMemberVOList, pagination.getTotal(), pagination.getPageIndex(), pagination.getPageSize());
        return voPage;
    }

    @Override
    @Deprecated
    public List<String> findGroupIdByUnion(String unionId) {
        Query<GroupUnionMember> query = this.createQuery();
        query.field("unionId").equal(unionId);
        query.retrievedFields(true, "groupId");

        List<GroupUnionMember> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }

        List<String> ret = list.stream().map(o->o.getGroupId()).collect(Collectors.toList());
        return ret;
    }

    @Override
    public List<String> findUnionIdByGroup(String groupId) {
        Query<GroupUnionMember> query = this.createQuery();
        query.field("groupId").equal(groupId);
        query.retrievedFields(true, "unionId");

        List<GroupUnionMember> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<String> ret = list.stream().map(o->o.getUnionId()).collect(Collectors.toList());
        return ret;
    }

    @Override
    public List<String> findUnionIdsByGroups(List<String> groupIdList) {
        Query<GroupUnionMember> query = this.createQuery();
        query.field("groupId").in(groupIdList);
        query.retrievedFields(true, "unionId");

        List<GroupUnionMember> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }

        List<String> ret = list.stream().map(o->o.getUnionId()).collect(Collectors.toList());
        return ret;
    }

    @Override
    public List<String> findGroupIdsByUnions(List<String> unionIdList) {
        Query<GroupUnionMember> query = this.createQuery();
        query.field("unionId").in(unionIdList);
        query.retrievedFields(true, "groupId");

        List<GroupUnionMember> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<String> ret = list.stream().map(o->o.getGroupId()).collect(Collectors.toList());
        return ret;
    }

    @Override
    public List<String> findGroupIdsByUnion(String unionId) {
        Query<GroupUnionMember> query = this.createQuery();
        query.field("unionId").equal(unionId);
        query.retrievedFields(true, "groupId");

        List<GroupUnionMember> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<String> ret = list.stream().map(o->o.getGroupId()).collect(Collectors.toList());
        return ret;
    }

    @Override
    public List<GroupUnionMember> findByUnion(String unionId) {
        Query<GroupUnionMember> query = this.createQuery();
        query.field("unionId").equal(unionId);

        List<GroupUnionMember> list = query.asList();
        return list;
    }

    @Override
    public List<GroupUnionMember> findByGroups(List<String> groupIdList) {
        Query<GroupUnionMember> query = this.createQuery();
        query.field("groupId").in(groupIdList);

        List<GroupUnionMember> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }
        return list;
    }

    @Override
    public List<GroupUnionMember> findFullByGroups(List<String> groupIdList) {
        List<GroupUnionMember> list = this.findByGroups(groupIdList);
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        this.wrapAll(list);
        return list;
    }

    @Override
    public List<MobileGroupUnionMemberVO> findByGroupsAndVO(List<String> groupIdList) {
        List<GroupUnionMember> list = findByGroups(groupIdList);
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        this.wrapAll(list);
        List<MobileGroupUnionMemberVO> voList = this.convertToMobile(list);
        return voList;
    }

    @Override
    public int deleteByGroupUnion(Integer currentUserId, String unionId) {
        Query<GroupUnionMember> query = this.createQuery();
        query.field("unionId").equal(unionId);
        return this.deleteByQuery(query);
    }
    public void refreshCircleTab(String unionId) {
        Query<GroupUnionMember> query = this.createQuery();
        query.field("unionId").equal(unionId);
        List<GroupUnionMember> unionMembers = query.asList();
        if(SdkUtils.isNotEmpty(unionMembers)){
            for (GroupUnionMember groupUnionMember:unionMembers){
                List<Integer> doctorIdListByGroup = groupDoctor2Service.findDoctorIdListByGroup(groupUnionMember.getGroupId());
                businessServiceMsg.refreshCircleTab(doctorIdListByGroup);
            }
        }
    }

    @Override
    public int deleteByGroup(Integer currentUserId, String groupId) {

        Query<GroupUnionMember> query = this.createQuery();
        query.field("groupId").equal(groupId);

        List<GroupUnionMember> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return 0;
        }

        for (GroupUnionMember member:list) {
            this.remove(currentUserId, member.getId().toString());
        }

        return list.size();
    }
    @Override
    public int countUnionMemberByGroup(String groupId) {
        Query<GroupUnionMember> query = this.createQuery();
        query.field("groupId").equal(groupId);
        long count = query.countAll();
        return (int) count;
    }

    @Override
    public boolean quit(Integer currentUserId, String id) {
        GroupUnionMember dbItem = this.findById(id);
        boolean ret = this.deleteById(id);
        if (ret) {
            groupUnionService.incrTotalMember(dbItem.getUnionId(), -1);
        }
        return ret;
    }

    @Override
    public boolean quit(Integer currentUserId,String unionId, String id) {
        Integer doctorId = currentUserId;
        GroupUnion dbItem = groupUnionService.findAndCheckById(unionId);
        GroupUnionMember member = this.findById(id);
        // check pri
        groupUnionService.checkAdminPri(currentUserId, dbItem); // admin can quit
        boolean b = this.deleteById(id);
        if (b) {
            this.asyncTaskPool.getPool().submit(new Runnable() {
                @Override
                public void run() {
                    // totalMember
                    groupUnionService.incrTotalMember(unionId, -1);

                    businessServiceMsg.refreshCircleTab(String.valueOf(currentUserId));
                    // 发送退出医联体通知
                    sendQuitUnionIMMsgToClients(currentUserId, dbItem.getName(), member.getGroupId(),id);
                }
            });
        }
        return b;
    }

    @Override
    public List<MobileGroupUnionMemberVO> findQuitList(Integer currentUserId, String unionId) {
        List<String> myGroupIds = groupUser2Service.findGroupIdListByDoctor(currentUserId);
        if (SdkUtils.isEmpty(myGroupIds)) {
            return null;
        }

        List<GroupUnionMember> memberList = this.findByUnion(unionId);
        if(SdkUtils.isNotEmpty(memberList)){
            Iterator<GroupUnionMember> memberIterator = memberList.iterator();
            while (memberIterator.hasNext()){
                GroupUnionMember member = memberIterator.next();
                if (!myGroupIds.contains(member.getGroupId())) {
                    memberIterator.remove();
                }
            }
        }

        if (SdkUtils.isEmpty(memberList)) {
            return null;
        }

        this.wrapGroup(memberList);
        List<MobileGroupUnionMemberVO> voList = this.convertToMobile(memberList);
        return voList;
    }

    protected void sendQuitUnionIMMsgToClients(Integer currentUserId, String unionName, String groupId,String memberId) {
        String title = String.format("系统通知");
        Group2 group=group2Service.findById(groupId);
        List<Integer> userIds = groupDoctor2Service.findDoctorIdListByGroup(groupId); //获取groupId所有成员
        if(userIds==null && userIds.size()==0){
            return;
        }
        // 通知科室或者圈子所有管理员
        for (Integer userId:userIds) {
            String content = String.format("%s退出了%s组织", group.getName(), unionName);
            Map<String, Object> params = new HashedMap(2);
            params.put("bizType", CircleImBizTypeEnum.GroupQuit.getId());
            params.put("bizId", memberId);
            ImgTextMsg imgTextMsg=new ImgTextMsg();
            imgTextMsg.setStyle(7);
            imService.sendTodoNotifyMsg(userId, title, content, null, params, imgTextMsg);
        }
    }
    protected void wrapAll(List<GroupUnionMember> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        this.wrapGroup(list);
        this.wrapGroupUnion(list);
    }

    protected void wrapGroup(List<GroupUnionMember> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        Set<String> groupIdSet = new HashSet<>(list.size());
        for (GroupUnionMember groupUnionMember:list) {
            groupIdSet.add(groupUnionMember.getGroupId());
        }

        List<Group2> group2List = group2Service.findFullByIds(new ArrayList<>(groupIdSet));
        for (GroupUnionMember groupUnionMember:list) {
            for (Group2 group2:group2List) {
                if (groupUnionMember.getGroupId().equals(group2.getId().toString())) {
                    groupUnionMember.setGroup(group2);
                    break;
                }
            }
        }
    }

    protected void wrapGroupUnion(List<GroupUnionMember> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }

        Set<String> unionIdSet = new HashSet<>(list.size());
        for (GroupUnionMember groupUnionMember:list) {
            unionIdSet.add(groupUnionMember.getUnionId());
        }

        List<GroupUnion> group2List = groupUnionService.findFullByIds(new ArrayList<>(unionIdSet));
        for (GroupUnionMember groupUnionMember:list) {
            for (GroupUnion groupUnion:group2List) {
                if (groupUnionMember.getUnionId().equals(groupUnion.getId().toString())) {
                    groupUnionMember.setUnion(groupUnion);
                    break;
                }
            }
        }
    }

    protected List<MobileGroupUnionMemberVO> convertToMobile(List<GroupUnionMember> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<MobileGroupUnionMemberVO> voList = new ArrayList<>(list.size());
        for (GroupUnionMember groupUnionMember:list) {
            voList.add(new MobileGroupUnionMemberVO(groupUnionMember));
        }
        return voList;
    }
}
