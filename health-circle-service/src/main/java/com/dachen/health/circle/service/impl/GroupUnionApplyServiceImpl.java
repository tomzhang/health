package com.dachen.health.circle.service.impl;

import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.circle.CircleEnum;
import com.dachen.health.circle.CircleImBizTypeEnum;
import com.dachen.health.circle.entity.Group2;
import com.dachen.health.circle.entity.GroupUnion;
import com.dachen.health.circle.entity.GroupUnionApply;
import com.dachen.health.circle.entity.GroupUnionMember;
import com.dachen.health.circle.service.GroupUnionApplyService;
import com.dachen.health.circle.vo.MobileGroupUnionApplyVO;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.exception.ServiceException;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.PropertiesUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Model(GroupUnionApply.class)
@Service
public class GroupUnionApplyServiceImpl extends BaseGroupUnionApplyOrInviteServiceImpl implements GroupUnionApplyService {

    @Override
    public GroupUnionApply create(Integer currentUserId, String groupId, String unionId, String msg) {
        GroupUnion groupUnion = groupUnionService.findAndCheckById(unionId);
        Group2 group = group2Service.findAndCheckGroupOrDept(groupId);
        this.groupUnionMemberService.checkIfMember(groupUnion, group);

        GroupUnionApply dbItem = this.findLatestHandlingByGroupAndUnion(groupId, unionId);

        if (null == dbItem) {
            GroupUnionApply apply = this.doAdd(currentUserId, groupUnion, group, msg);
            this.asyncTaskPool.getPool().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        sendApplyJoinUnionIMMsgAndShortMsgToClients(currentUserId, apply, group.getName(), groupUnion.getName());
                    } catch (HttpApiException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            });
            return apply;
        }

        return dbItem;
    }

    @Override
    public GroupUnionApply findFullAndCheckById(String id) {
        GroupUnionApply apply = this.findById(id);
        this.wrapAll(apply);
        return apply;
    }

    @Override
    public boolean accept(Integer currentUserId, String id) {
        Integer doctorId = currentUserId;

        GroupUnionApply apply = this.findFullAndCheckById(id);

        GroupUnion groupUnion = apply.getUnion();

        // 判断当前用户是否是医联体的管理员
        groupUser2Service.checkRootOrAdminPri(groupUnion.getGroupId(), doctorId);

        CircleEnum.GroupUnionApplyStatus curStatus = CircleEnum.GroupUnionApplyStatus.eval(apply.getStatusId());
        if (CircleEnum.GroupUnionApplyStatus.handling != curStatus) {
            throw new ServiceException("申请" + curStatus.getTitle());
        }

        GroupUnionMember member = this.groupUnionMemberService.findByUK(apply.getUnionId(), apply.getGroupId());
        if (null != member) {
            apply.setStatus(CircleEnum.GroupUnionApplyStatus.closed);
            this.saveEntity(apply);

            throw new ServiceException(String.format("该%s已经是成员", GroupEnum.GroupType.eval(member.getGroupType()).getTitle()));
        }

        member = this.groupUnionMemberService.addByApply(currentUserId, id);
        if (null != member) {
            apply.setStatus(CircleEnum.GroupUnionApplyStatus.accepted);
            this.saveEntity(apply);
            businessServiceMsg.refreshCircleTab(String.valueOf(apply.getUserId()));
            groupUnionMemberService.closeApplyAndInviteByMemberAsync(member.getId().toString());
        }
        return true;
    }

    @Override
    public boolean refuse(Integer currentUserId, String id) {
        Integer doctorId = currentUserId;

        GroupUnionApply apply = this.findById(id);
        GroupUnion groupUnion = this.groupUnionService.findAndCheckById(apply.getUnionId());
        // 判断当前用户是否是医联体的管理员
        groupUser2Service.checkRootOrAdminPri(groupUnion.getGroupId(), doctorId);

        CircleEnum.GroupUnionApplyStatus curStatus = CircleEnum.GroupUnionApplyStatus.eval(apply.getStatusId());
        if (CircleEnum.GroupUnionApplyStatus.handling != curStatus) {
            throw new ServiceException("申请" + curStatus.getTitle());
        }

        GroupUnionMember member = this.groupUnionMemberService.findByUK(apply.getUnionId(), apply.getGroupId());
        if (null != member) {
            apply.setStatus(CircleEnum.GroupUnionApplyStatus.closed);
            this.saveEntity(apply);

            this.groupUnionMemberService.checkByUK(apply.getUnionId(), apply.getGroupId());
            return false;
        }

        apply.setStatus(CircleEnum.GroupUnionApplyStatus.refused);
        this.saveEntity(apply);

        return true;
    }

    @Override
    public MobileGroupUnionApplyVO findDetailByIdAndVO(Integer currentUserId, String applyId) {
        GroupUnionApply dbItem = this.findFullAndCheckById(applyId);
        groupUnionService.check(dbItem.getUnionId());
        MobileGroupUnionApplyVO vo = this.convertToMobile(dbItem);

        // 我是{医院名称}{科室名称}，申请加入
        //String notifyText = String.format();
        vo.setMsg(dbItem.getMsg());

        return vo;
    }

    protected MobileGroupUnionApplyVO convertToMobile(GroupUnionApply apply) {
        if (null == apply) {
            return null;
        }
        MobileGroupUnionApplyVO vo = new MobileGroupUnionApplyVO(apply);
        return vo;
    }

    protected List<MobileGroupUnionApplyVO> convertToMobile(List<GroupUnionApply> list) {
        if (SdkUtils.isEmpty(list)) {
            return null;
        }
        List<MobileGroupUnionApplyVO> ret = new ArrayList<>(list.size());
        for (GroupUnionApply apply:list) {
            MobileGroupUnionApplyVO vo = new MobileGroupUnionApplyVO(apply);
            ret.add(vo);
        }
        return ret;
    }

    protected void sendApplyJoinUnionIMMsgAndShortMsgToClients(Integer currentUserId, GroupUnionApply apply, String groupName, String unionName) throws HttpApiException {
        GroupUnion groupUnion = groupUnionService.findAndCheckById(apply.getUnionId());
        logger.info("科室申请加入联盟 通知 findAndCheckById {}", ToStringBuilder.reflectionToString(groupUnion));
        List<Integer> managerIdList = groupUser2Service.findDoctorIdList(groupUnion.getGroupId());
        logger.info("接收通知所有管理员 {}",ToStringBuilder.reflectionToString(managerIdList));
        if (SdkUtils.isEmpty(managerIdList)) {
            return;
        }
        for (Integer mangerId : managerIdList) {  // 通知所有的管理员
            String title = String.format("加入科室联盟");
            String content = String.format("%s申请加入%s：%s", groupName, unionName, apply.getMsg());
            Map<String, Object> params = new HashedMap(2);
            params.put("bizType", CircleImBizTypeEnum.GroupUnionApply.getId());
            params.put("bizId", apply.getId().toString());
            ImgTextMsg imgTextMsg = new ImgTextMsg();
            imgTextMsg.setPic(getDefaultGroupCheckLogoPicUrl());
            imService.sendTodoNotifyMsg(mangerId, title, content, null, params, imgTextMsg);
            logger.info("发送通知userId:{}",mangerId);
            try {
                //{科室名称}申请加入{科室联盟名称}，点击立即查看{打开医生圈APP链接}。
                /*String generateUrl = shortUrlComponent
                        .generateShortUrl(PropertiesUtil.getContextProperty("invite.url") +
                                PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));*/
                /**修改成从应用宝获取应用**/
                String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));
                final String smsContent = baseDataService.toContent("1054", groupName, unionName, generateUrl);
                User doctor = user2Service.findDoctorById(mangerId);
                if (null != doctor && doctor.getTelephone() != null) {
                    mobSmsSdk.send(doctor.getTelephone(), smsContent, BaseConstants.XG_YSQ_APP);
                }
            }catch (Exception e){
                logger.error("科室申请加入联盟 通知 发送短信失败",e);
            }
        }
    }

    protected String getDefaultGroupCheckLogoPicUrl() {
        String pic = String.format("%s/default/%s", PropertiesUtil.getHeaderPrefix(),  PropertiesUtil.getContextProperty("group.check.pic"));
        return pic;
    }

    protected GroupUnionApply doAdd(Integer currentUserId, GroupUnion groupUnion, Group2 group, String msg) {
        long now = System.currentTimeMillis();
        GroupUnionApply tmp = new GroupUnionApply(groupUnion, group, msg);

        tmp.setStatus( CircleEnum.GroupUnionApplyStatus.handling);

        tmp.setUserId(currentUserId);
        tmp.setCreateTime(now);
        tmp.setCreateUserId(currentUserId);

        GroupUnionApply apply = this.saveEntityAndFind(tmp);
        return apply;
    }

    @Override
    public GroupUnionApply findLatestHandlingByGroupAndUnion(String groupId, String unionId) {
        Query<GroupUnionApply> query = this.createQuery();
        query.field("unionId").equal(unionId).field("groupId").equal(groupId);
        query.field("statusId").equal(CircleEnum.GroupUnionApplyStatus.handling.getIndex());
        query.order("-createTime");
        query.limit(1);
        return query.get();
    }

    @Override
    public int closeByMember(String memberId) {
        GroupUnionMember member = groupUnionMemberService.findById(memberId);

        Query<GroupUnionApply> query = this.createQuery();
        query.field("unionId").equal(member.getUnionId()).field("groupId").equal(member.getGroupId());
        query.field("statusId").equal(CircleEnum.GroupUnionApplyStatus.handling.getIndex());

        UpdateOperations<GroupUnionApply> ops = this.createUpdateOperations();
        ops.set("statusId", CircleEnum.GroupUnionApplyStatus.closed.getIndex());
        ops.set("statusTime", System.currentTimeMillis());

        return this.update(query, ops);
    }

    @Override
    public int closeByGroupDismiss(String groupId) {
        Query<GroupUnionApply> query = this.createQuery();
        query.field("groupId").equal(groupId);
        query.field("statusId").equal(CircleEnum.GroupUnionApplyStatus.handling.getIndex());

        UpdateOperations<GroupUnionApply> ops = this.createUpdateOperations();
        ops.set("statusId", CircleEnum.GroupUnionApplyStatus.closed.getIndex());
        ops.set("statusTime", System.currentTimeMillis());

        return this.update(query, ops);
    }

    @Override
    public int closeByGroupUnionDismiss(String unionId) {
        Query<GroupUnionApply> query = this.createQuery();
        query.field("unionId").equal(unionId);
        query.field("statusId").equal(CircleEnum.GroupUnionApplyStatus.handling.getIndex());

        UpdateOperations<GroupUnionApply> ops = this.createUpdateOperations();
        ops.set("statusId", CircleEnum.GroupUnionApplyStatus.closed.getIndex());
        ops.set("statusTime", System.currentTimeMillis());

        return this.update(query, ops);
    }

    public List<GroupUnionApply> findHandlingListByGroup(String groupId) {
        Query<GroupUnionApply> query = this.createQuery();
        query.field("groupId").equal(groupId).field("statusId").equal(CircleEnum.GroupUnionApplyStatus.handling.getIndex());
        return query.asList();
    }


    /**
     * 1、分页显示平台所有科室联盟，不显示已经加入的科室联盟
     *
     * @param currentUserId
     * @param kw
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Override
    public Pagination<MobileGroupUnionApplyVO> findPageByGroupAndVO(Integer currentUserId, String groupId, String kw, Integer pageIndex, Integer pageSize) {
        String tag = "findPageByGroupAndVO";
        logger.info("{}. currentUserId={}, groupId={}, kw={}, pageIndex={}, pageSize={}", tag, currentUserId, groupId, kw, pageIndex, pageSize);

        Integer doctorId = currentUserId;
        List<String> myGroup = groupDoctor2Service.findGroupIdListByDoctor(doctorId);  //我加入的科室和圈子
        List<String> unionIdList = groupUnionMemberService.findUnionIdsByGroups(myGroup);   // 我目前加入的医联体列表
        logger.info("{}. unionIdList={}", tag, unionIdList);

        Pagination<GroupUnion> page = groupUnionService.findPage(kw, unionIdList, pageIndex, pageSize);
        logger.info("{}. page={}", tag, page);
        if (null == page) {
            return null;
        }

        List<GroupUnion> unionList = page.getPageData();
        if (SdkUtils.isEmpty(unionList)) {
            return null;
        }

        List<GroupUnionApply> applyList = this.findHandlingListByGroup(groupId); // Group申请的记录列表
        List<GroupUnionApply> applyListTmp = this.convertToApplyByGroup(groupId, unionList, applyList);

        this.wrapAll(applyListTmp);
        List<MobileGroupUnionApplyVO> ret = this.convertToMobile(applyListTmp);
        Pagination<MobileGroupUnionApplyVO> page2 = new Pagination<>(ret, page.getTotal(), pageIndex, pageSize);
        return page2;
    }

    protected List<GroupUnionApply> convertToApplyByGroup(String groupId, List<GroupUnion> list, List<GroupUnionApply> applyList) {
        List<GroupUnionApply> tmpList = new ArrayList<>(list.size());
        for (GroupUnion union:list) {
            boolean found = false;
            if (SdkUtils.isNotEmpty(applyList)) {
                for (GroupUnionApply apply:applyList){
                    if (apply.getUnionId().equals(union.getId().toString())) {
                        found = true;
                        tmpList.add(apply);
                        break;
                    }
                }
            }
            if (found) {
                continue;
            }
            GroupUnionApply groupUnionApply = new GroupUnionApply(union, groupId);
            tmpList.add(groupUnionApply);
        }
        return tmpList;
    }

}
