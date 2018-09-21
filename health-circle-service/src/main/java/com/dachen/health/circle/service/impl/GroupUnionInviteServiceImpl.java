package com.dachen.health.circle.service.impl;

import com.dachen.health.circle.CircleEnum;
import com.dachen.health.circle.CircleImBizTypeEnum;
import com.dachen.health.circle.entity.*;
import com.dachen.health.circle.service.GroupUnionInviteService;
import com.dachen.health.circle.vo.MobileGroupUnionInviteVO;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.exception.ServiceException;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.HeaderInfo;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import org.apache.commons.collections.map.HashedMap;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@Model(GroupUnionInvite.class)
@Service
public class GroupUnionInviteServiceImpl extends BaseGroupUnionApplyOrInviteServiceImpl implements GroupUnionInviteService {

    @Override
    public GroupUnionInvite create(Integer currentUserId, String unionId, String groupId) {
        GroupUnion groupUnion = groupUnionService.findAndCheckById(unionId);
        Group2 group = group2Service.findAndCheckGroupOrDept(groupId);

        this.groupUnionMemberService.checkByUK(unionId, groupId);

        GroupUnionInvite dbItem = this.findLatestHandlingByUnionAndGroup(unionId, groupId);

        if (null == dbItem) {
            GroupUnionInvite invite = this.doAdd(currentUserId, groupUnion, group);
            HeaderInfo headerInfo = ReqUtil.instance.getHeaderInfo();
            this.asyncTaskPool.getPool().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        sendInviteJoinUnionIMMsgToClients(currentUserId, groupUnion, group, invite,headerInfo);
                    } catch (HttpApiException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            });

            return invite;
        }
        throw new ServiceException("Unknown");
    }

    @Override
    public GroupUnionInvite findFullById(String id) {
        GroupUnionInvite apply = this.findById(id);
        this.wrapAll(apply);
        return apply;
    }

    @Override
    public boolean accept(Integer currentUserId, String id) {
        Integer doctorId = currentUserId;

        GroupUnionInvite apply = this.findById(id);
        groupUser2Service.checkRootOrAdminPri(apply.getGroupId(), doctorId);    // 判断当前用户是否是成员的管理员

        CircleEnum.GroupUnionApplyStatus curStatus = CircleEnum.GroupUnionApplyStatus.eval(apply.getStatusId());
        if (CircleEnum.GroupUnionApplyStatus.handling != curStatus) {
            throw new ServiceException("邀请" + curStatus.getTitle());
        }

        GroupUnionMember member = this.groupUnionMemberService.findByUK(apply.getUnionId(), apply.getGroupId());
        if (null != member) {
            apply.setStatus(CircleEnum.GroupUnionApplyStatus.closed);
            this.saveEntity(apply);

            throw new ServiceException(String.format("该%s已经是成员", GroupEnum.GroupType.eval(member.getGroupType()).getTitle()));
        }

        member = this.groupUnionMemberService.addByInvite(currentUserId, id);
        if (null != member) {
            apply.setStatus(CircleEnum.GroupUnionApplyStatus.accepted);
            this.saveEntity(apply);

            businessServiceMsg.refreshCircleTab(String.valueOf(currentUserId));

            groupUnionMemberService.closeApplyAndInviteByMemberAsync(member.getId().toString());

            return true;
        }

        return false;
    }

    @Override
    public boolean refuse(Integer currentUserId, String id) {
        Integer doctorId = currentUserId;

        GroupUnionInvite apply = this.findById(id);
        groupUser2Service.checkRootOrAdminPri(apply.getGroupId(), doctorId);    // 判断当前用户是否是成员的管理员

        CircleEnum.GroupUnionApplyStatus curStatus = CircleEnum.GroupUnionApplyStatus.eval(apply.getStatusId());
        if (CircleEnum.GroupUnionApplyStatus.handling != curStatus) {
            throw new ServiceException("邀请" + curStatus.getTitle());
        }

        apply.setStatus(CircleEnum.GroupUnionApplyStatus.refused);
        this.saveEntity(apply);

        return true;
    }

    @Override
    public MobileGroupUnionInviteVO findDetailByIdAndVO(Integer currentUserId, String id) {
        GroupUnionInvite dbItem = this.findFullById(id);
        groupUnionService.check(dbItem.getUnionId());
        MobileGroupUnionInviteVO vo = this.convertToMobile(dbItem);

        User user = user2Service.findDoctorById(dbItem.getUserId());
        // 我是{科室联盟名称}管理员{医生名称}，诚挚的希望{被邀请的科室或圈子名称}加入{科室联盟名称}
        String notifyText = String.format("我是%s管理员%s，诚挚的希望%s加入%s。",
                dbItem.getUnion().getName(), user.getName(), dbItem.getGroup().getName(), dbItem.getUnion().getName());
        vo.setMsg(notifyText);

        return vo;
    }

    @Override
    public int closeByMember(String memberId) {
        GroupUnionMember member = groupUnionMemberService.findById(memberId);

        Query<GroupUnionInvite> query = this.createQuery();
        query.field("unionId").equal(member.getUnionId()).field("groupId").equal(member.getGroupId());
        query.field("statusId").equal(CircleEnum.GroupUnionApplyStatus.handling.getIndex());

        UpdateOperations<GroupUnionInvite> ops = this.createUpdateOperations();
        ops.set("statusId", CircleEnum.GroupUnionApplyStatus.closed.getIndex());
        ops.set("statusTime", System.currentTimeMillis());

        return this.update(query, ops);
    }

    @Override
    public int closeByGroupDismiss(String groupId) {
        Query<GroupUnionInvite> query = this.createQuery();
        query.field("groupId").equal(groupId);
        query.field("statusId").equal(CircleEnum.GroupUnionApplyStatus.handling.getIndex());

        UpdateOperations<GroupUnionInvite> ops = this.createUpdateOperations();
        ops.set("statusId", CircleEnum.GroupUnionApplyStatus.closed.getIndex());
        ops.set("statusTime", System.currentTimeMillis());

        return this.update(query, ops);
    }

    @Override
    public int closeByGroupUnionDismiss(String unionId) {
        Query<GroupUnionInvite> query = this.createQuery();
        query.field("unionId").equal(unionId);
        query.field("statusId").equal(CircleEnum.GroupUnionApplyStatus.handling.getIndex());

        UpdateOperations<GroupUnionInvite> ops = this.createUpdateOperations();
        ops.set("statusId", CircleEnum.GroupUnionApplyStatus.closed.getIndex());
        ops.set("statusTime", System.currentTimeMillis());

        return this.update(query, ops);
    }

    protected MobileGroupUnionInviteVO convertToMobile(GroupUnionInvite apply) {
        if (null == apply) {
            return null;
        }
        MobileGroupUnionInviteVO vo = new MobileGroupUnionInviteVO(apply);
        return vo;
    }

    protected List<MobileGroupUnionInviteVO> convertToMobile(List<GroupUnionInvite> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<MobileGroupUnionInviteVO> ret = new ArrayList<>(list.size());
        for (GroupUnionInvite apply:list) {
            MobileGroupUnionInviteVO vo = new MobileGroupUnionInviteVO(apply);
            ret.add(vo);
        }
        return ret;
    }

    protected void sendInviteJoinUnionIMMsgToClients(Integer currentUserId, GroupUnion groupUnion, Group2 group, GroupUnionInvite invite,HeaderInfo headerInfo) throws HttpApiException {
        List<GroupUser2> managerIdList = groupUser2Service.findAdminUserByGroupId(group.getId().toString());
        if (SdkUtils.isEmpty(managerIdList)) {
            return;
        }

        for (GroupUser2 groupUser2 : managerIdList) {  // 通知所有的管理员
            String url = "";
            String title = String.format("加入科室联盟邀请函");
            String content = String.format("%s邀请%s加入", groupUnion.getName(), group.getName());
            Map<String, Object> params = new HashedMap();
            ImgTextMsg imgTextMsg = new ImgTextMsg();
            imgTextMsg.setPic(getDefaultGroupInviteLogoPicUrl());
            params.put("bizType", CircleImBizTypeEnum.GroupUnionInvite.getId());
            params.put("bizId", invite.getId().toString());
            imService.sendTodoNotifyMsg(groupUser2.getDoctorId(), title, content, null, params,imgTextMsg);

            /**
             * 发送短信
             * {科室联盟名称}邀请{科室或圈子名称}加入，点击立即查看{打开医生圈APP链接}。
             */
            /*String generateUrl = shortUrlComponent
                .generateShortUrl(PropertiesUtil.getContextProperty("invite.url") +
                    PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));*/
            /**修改成从应用宝获取应用**/
            String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));
            final String smsContent = baseDataService.toContent("1055", groupUnion.getName(), group.getName(), generateUrl);
            User user = user2Service.findDoctorById(groupUser2.getDoctorId());
            if(null!=user && user.getTelephone()!=null) {
                mobSmsSdk.send(user.getTelephone(), smsContent,headerInfo);
            }
        }
    }

    protected String getDefaultGroupInviteLogoPicUrl() {
        String pic = String.format("%s/default/%s", PropertiesUtil.getHeaderPrefix(),  PropertiesUtil.getContextProperty("group.invite.pic"));
        return pic;
    }

    protected GroupUnionInvite doAdd(Integer currentUserId, GroupUnion groupUnion, Group2 group) {
        long now = System.currentTimeMillis();
        GroupUnionInvite tmp = new GroupUnionInvite(groupUnion, group);

        tmp.setStatus(CircleEnum.GroupUnionApplyStatus.handling);

        tmp.setUserId(currentUserId);
        tmp.setCreateTime(now);
        tmp.setCreateUserId(currentUserId);

        GroupUnionInvite apply = this.saveEntityAndFind(tmp);
        return apply;
    }

    @Override
    public GroupUnionInvite findLatestHandlingByUnionAndGroup(String unionId, String groupId) {
        Query<GroupUnionInvite> query = this.createQuery();
        query.field("unionId").equal(unionId).field("groupId").equal(groupId);
        query.field("statusId").equal(CircleEnum.GroupUnionApplyStatus.handling.getIndex());
        query.order("-createTime");
        query.limit(1);
        return query.get();
    }

    public List<GroupUnionInvite> findHandlingListByUnion(String unionId) {
        Query<GroupUnionInvite> query = this.createQuery();
        query.field("unionId").equal(unionId);
        query.field("statusId").equal(CircleEnum.GroupUnionApplyStatus.handling.getIndex());
        List<GroupUnionInvite> groupUnionInvites = query.asList();
        //去除 已经解散的组织
        if(SdkUtils.isNotEmpty(groupUnionInvites)){
            Iterator<GroupUnionInvite> iterator = groupUnionInvites.iterator();
            while (iterator.hasNext()){
                try {
                    GroupUnionInvite groupUnionInvite = iterator.next();
                    Group2 group2 = group2Service.findAndCheckGroupOrDept(groupUnionInvite.getGroupId());
                }catch (Exception e){
                    iterator.remove();
                }

            }
        }
        return groupUnionInvites;
    }

    /**
     * 1、列表显示平台所有科室和圈子。不显示创建这个科室联盟的科室或圈子
     * 2、排序：我加入的科室和圈子排在最前面
     *
     * @param currentUserId
     * @param unionId
     * @param kw
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Override
    public Pagination<MobileGroupUnionInviteVO> findPageByUnionAndVO(Integer currentUserId, String unionId, String kw, Integer pageIndex, Integer pageSize) {
        GroupUnion groupUnion = groupUnionService.findAndCheckById(unionId);

        Integer doctorId = currentUserId;
        List<String> unionGroupIdList = groupUnionMemberService.findGroupIdByUnion(unionId);   // 医联体目前的成员
        List<String> myGroupIdList = groupDoctor2Service.findGroupIdListByDoctorExcept(doctorId, unionGroupIdList);     // 我加入的组织（排除掉已加入医联体的）

        List<GroupUnionInvite> applyList = this.findHandlingListByUnion(unionId); // 医联体邀请的记录列表

        long total = 0;
        List<Group2> groupList = new ArrayList<>();
        Set<String> groupIdSet = new HashSet<>();
        if (0 == pageIndex && SdkUtils.isNotEmpty(myGroupIdList)) {   // 当查询第一页时，获取我加入的所有组织列表
            groupList = this.group2Service.findNormalByIdsAndKw(myGroupIdList, kw);
            total = groupList.size();
        }

        for (Group2 group2:groupList) {
            groupIdSet.add(group2.getId().toString());
        }
        groupIdSet.addAll(unionGroupIdList);

        if (groupList.size() < pageSize) {
            int pageSize2 = pageSize - groupList.size();
            Pagination<Group2> page = this.group2Service.findPage(kw, new ArrayList<>(groupIdSet), pageIndex, pageSize2);
            total += page.getTotal();
            if (SdkUtils.isNotEmpty(page.getPageData())) {
                groupList.addAll(page.getPageData());
            }
        }

        List<GroupUnionInvite> applyListTmp = this.convertToInvite(unionId, groupList, applyList);
        this.wrapAll(applyListTmp);
        List<MobileGroupUnionInviteVO> ret = this.convertToMobile(applyListTmp);
        Pagination<MobileGroupUnionInviteVO> page2 = new Pagination<>(ret, total, pageIndex, pageSize);
        return page2;
    }

    protected List<GroupUnionInvite> convertToInvite(String unionId, List<Group2> list, List<GroupUnionInvite> inviteList) {
        List<GroupUnionInvite> tmpList = new ArrayList<>(list.size());
        for (Group2 group2:list) {
            boolean found = false;
            if (SdkUtils.isNotEmpty(inviteList)) {
                for (GroupUnionInvite apply:inviteList){
                    if (apply.getGroupId().equals(group2.getId().toString())) {
                        found = true;
                        tmpList.add(apply);
                        break;
                    }
                }
            }
            if (found) {
                continue;
            }
            GroupUnionInvite GroupUnionInvite = new GroupUnionInvite(unionId, group2);
            tmpList.add(GroupUnionInvite);
        }
        return tmpList;
    }
}
