package com.dachen.health.circle.service.impl;

import com.dachen.health.circle.CircleEnum;
import com.dachen.health.circle.CircleImBizTypeEnum;
import com.dachen.health.circle.entity.*;
import com.dachen.health.circle.service.GroupDoctorApplyService;
import com.dachen.health.circle.vo.MobileGroupDoctorVO;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.exception.ServiceException;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.JSONUtil;
import com.dachen.util.PropertiesUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Model(GroupDoctorApply.class)
@Service
public class GroupDoctorApplyServiceImpl extends BaseGroupDoctorApplyOrInviteServiceImpl implements GroupDoctorApplyService {

    @Override
    public GroupDoctorApply create(Integer currentUserId, String groupId, String msg) throws HttpApiException {
        Integer doctorId = currentUserId;
        User doctorUser = user2Service.findDoctorById(doctorId);
        Group2 group2 = group2Service.findAndCheckGroupOrDept(groupId);

        this.groupDoctor2Service.checkCurDeptIdWhenJoinDept(doctorId);

        this.groupDoctor2Service.checkNormalByUK(groupId, doctorId);

        GroupDoctorApply apply = this.doAdd(currentUserId, doctorUser, group2, msg);

        sendCreateIMMsgToManagersAsync(apply);

        return apply;
    }

    @Override
    public boolean accept(Integer currentUserId, String id) throws HttpApiException {
        GroupDoctorApply dbItem = this.findById(id);
        // 权限检查：是否是科室管理员
        this.groupUser2Service.checkRootOrAdminPri(dbItem.getGroupId(), currentUserId);

        CircleEnum.GroupUnionApplyStatus curStatus = CircleEnum.GroupUnionApplyStatus.eval(dbItem.getStatusId());
        if (CircleEnum.GroupUnionApplyStatus.handling != curStatus) {
            throw new ServiceException("申请" + curStatus.getTitle());
        }

        GroupDoctor2 groupDoctor2 = groupDoctor2Service.findByUK(dbItem.getGroupId(), dbItem.getUserId());
        if (null != groupDoctor2) {
            if(GroupEnum.GroupDoctorStatus.正在使用.getIndex().equals(groupDoctor2.getStatus())){
                dbItem.setStatus(CircleEnum.GroupUnionApplyStatus.closed);
                this.saveEntity(dbItem);

                groupDoctor2Service.checkNormalByUK(dbItem.getGroupId(), dbItem.getUserId());
                return false;
            }
            groupDoctor2Service.updateNormalById(groupDoctor2.getId().toString());
        } else {
            this.wrapAll(dbItem);
            groupDoctor2 = groupDoctor2Service.addByApply(currentUserId, dbItem);
        }

        dbItem.setStatus(CircleEnum.GroupUnionApplyStatus.accepted);
        this.saveEntity(dbItem);
        // 发送通知给管理员
        sendAcceptIMMsgToManagersAsync(dbItem);
        sendAcceptSmsAsync(dbItem);
        businessServiceMsg.refreshCircleTab(String.valueOf(dbItem.getUserId()));
        groupDoctor2Service.closeApplyAndInviteAsync(groupDoctor2);

        return true;
    }

    @Override
    public int closeByGroupDoctor(GroupDoctor2 groupDoctor) {
        Query<GroupDoctorInvite> query = this.createQuery();
        query.field("userId").equal(groupDoctor.getDoctorId()).field("groupId").equal(groupDoctor.getGroupId());
        query.field("statusId").equal(CircleEnum.GroupUnionApplyStatus.handling.getIndex());

        UpdateOperations<GroupDoctorInvite> ops = this.createUpdateOperations();
        ops.set("statusId", CircleEnum.GroupUnionApplyStatus.closed.getIndex());
        ops.set("statusTime", System.currentTimeMillis());

        return this.update(query, ops);
    }

    @Override
    public boolean refuse(Integer currentUserId, String id) throws HttpApiException {
        GroupDoctorApply dbItem = this.findById(id);
        // 权限检查：是否是科室管理员
        this.groupUser2Service.checkRootOrAdminPri(dbItem.getGroupId(), currentUserId);
        User currentUser = user2Service.findDoctorById(currentUserId);

        CircleEnum.GroupUnionApplyStatus curStatus = CircleEnum.GroupUnionApplyStatus.eval(dbItem.getStatusId());
        if (CircleEnum.GroupUnionApplyStatus.handling != curStatus) {
            throw new ServiceException("申请" + curStatus.getTitle());
        }

      /*  GroupDoctor2 groupDoctor2 = groupDoctor2Service.findNormalByUK(dbItem.getGroupId(), dbItem.getUserId());
        if (null != groupDoctor2) {
            dbItem.setStatus(CircleEnum.GroupUnionApplyStatus.closed);
            this.saveEntity(dbItem);

            groupDoctor2Service.checkNormalByUK(dbItem.getGroupId(), dbItem.getUserId());
            return false;
        }*/

        dbItem.setStatus(CircleEnum.GroupUnionApplyStatus.refused);
        this.saveEntity(dbItem);

        this.wrapAll(dbItem);
        //发送通知给邀请人
        sendRefuseIMMsgAsync(currentUser, dbItem);

        return true;
    }
    @Override
    public boolean findApplyingByGroupAndDoctor(String groupId, Integer doctorId){
        Query<GroupDoctorApply> query = this.createQuery();
        query.field("userId").equal(doctorId)
                .field("groupId").equal(groupId)
                .field("statusId").equal(CircleEnum.GroupUnionApplyStatus.handling.getIndex());
        List<GroupDoctorApply> groupDoctorApplies = query.asList();
        if(groupDoctorApplies!=null && groupDoctorApplies.size()>0){
            return true;
        }else {
            return false;
        }
    }

    public GroupDoctorApply findLastestApplyingByGroupAndDoctor(String groupId, Integer doctorId){
        Query<GroupDoctorApply> query = this.createQuery();
        query.field("userId").equal(doctorId)
                .field("groupId").equal(groupId)
                .field("statusId").equal(CircleEnum.GroupUnionApplyStatus.handling.getIndex());
        query.order("-createTime");
        query.limit(1);
        return query.get();
    }


    @Override
    public MobileGroupDoctorVO findDetailByIdAndGroupDoctorVO(Integer currentUserId, String id) {
        GroupDoctorApply dbItem = this.findById(id);
        this.wrapAll(dbItem);

        MobileGroupDoctorVO vo = this.convertToGroupDoctorVO(dbItem);
        vo.setMsg(dbItem.getMsg());
        return vo;
    }
    protected  MobileGroupDoctorVO convertToGroupDoctorVO(GroupDoctorApply dbItem) {
        if (null == dbItem) {
            return null;
        }
        this.wrapGroupDoctorApplyStatus(dbItem);
        MobileGroupDoctorVO vo = new MobileGroupDoctorVO(dbItem);
        return vo;
    }

    protected void sendRefuseIMMsgAsync(User currentUser, GroupDoctorApply apply) {
        if (null == currentUser || null == apply) {
            return;
        }

        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sendRefuseIMMsg(currentUser, apply);
                } catch (HttpApiException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    protected void sendRefuseIMMsg(User currentUser, GroupDoctorApply apply) throws HttpApiException {
        String tag = "sendRefuseIMMsg";
        logger.info("{}. currentUser={}, apply={}", tag, currentUser, apply);

        this.wrapAll(apply);

        Group2 group = apply.getGroup();
        User user = apply.getUser();

        String content = currentUser.getName() + "医生已拒绝您的申请，未加入" + group.getName();

        // 初始化发送的消息文本
        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>(1);
        ImgTextMsg textMsg = new ImgTextMsg();
        textMsg.setTime(System.currentTimeMillis());
        textMsg.setStyle(7);
        textMsg.setContent(content);
        textMsg.setTitle("加入" + GroupEnum.GroupType.eval(group.getType()).getTitle());

        // 设置业务类型和参数
        Map<String, Object> imParam = new HashMap<String, Object>();
        imParam.put("bizType", CircleImBizTypeEnum.GroupResult.getId());
        imParam.put("bizId", apply.getId().toString());
        textMsg.setParam(imParam);
        mpt.add(textMsg);

        businessServiceMsg.sendTextMsg(apply.getUserId() + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
    }

    @Autowired
    protected IBusinessServiceMsg businessServiceMsg;

    protected void sendAcceptIMMsgToManagersAsync(GroupDoctorApply apply)  {
        if (null == apply) {
            return;
        }
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sendAcceptIMMsgToManagers(apply);
                } catch (HttpApiException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    protected void sendAcceptIMMsgToManagers(GroupDoctorApply apply) throws HttpApiException {
        String tag = "sendAcceptIMMsgToManagers";
        logger.info("{}. apply={}", tag, apply);

        if (null == apply) {
            return;
        }

        this.wrapAll(apply);

        // 查询集团管理员列表
        List<GroupUser2> groupUserList = groupUser2Service.findList(apply.getGroupId());
        if (SdkUtils.isEmpty(groupUserList)) {
            return;
        }

        logger.info("{}. groupUserList.size={}", tag, groupUserList.size());

        //初始化发送的消息文本
        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>(1);
        ImgTextMsg textMsg = new ImgTextMsg();
        textMsg.setTime(System.currentTimeMillis());
        textMsg.setStyle(7);
        //+ GroupEnum.GroupType.eval(group.getType()).getTitle()
        //textMsg.setPic("http://group.dev.file.dachentech.com.cn/o_1bivr073114hl17i6q431vggspdl?100");
        textMsg.setTitle("加入科室");
        //设置业务类型和参数
        Map<String, Object> imParam = new HashMap<String, Object>();
        imParam.put("bizType", CircleImBizTypeEnum.GroupResult.getId());
        imParam.put("bizId", apply.getId().toString());
        textMsg.setParam(imParam);

        mpt.add(textMsg);

        String managerContent = "";
        User user = apply.getUser();
        Group2 group = apply.getGroup();
        if (StringUtils.isNotEmpty(user.getName())) {
            managerContent = user.getName() + "医生已加入" + group.getName();
        } else {
            managerContent = user.getTelephone() + "医生已加入" + group.getName();
        }
        textMsg.setContent(managerContent);

        for (GroupUser2 guser : groupUserList) {
            logger.info("{}. userId: {} textMsg: {}", tag, guser.getDoctorId(), JSONUtil.toJSONString(textMsg));
            businessServiceMsg.sendTextMsg(guser.getDoctorId() + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
        }
    }

    public void sendAcceptSmsAsync(GroupDoctorApply apply) throws HttpApiException {
        if (null == apply) {
            return;
        }
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sendAcceptSms(apply);
                } catch (HttpApiException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    public void sendAcceptSms(GroupDoctorApply apply) throws HttpApiException {
        this.wrapAll(apply);
        /**
         * 发送短信
         * {医生名称}您好，{科室名称}已经同意您的加入科室申请，点击立即查看{打开医生圈APP链接}。
         */
        /*String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.url") +
                PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));*/
        /**修改成从应用宝获取应用**/
        String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));
        final String smsContent = baseDataService.toContent("1053", apply.getUser().getName(), apply.getGroup().getName(), generateUrl);
        mobSmsSdk.send(apply.getUser().getTelephone(), smsContent);
    }

    protected GroupDoctorApply doAdd(Integer currentUserId, User user, Group2 group, String msg) {
        long now = System.currentTimeMillis();
        GroupDoctorApply tmp = new GroupDoctorApply(user, group, msg);

        tmp.setStatus(CircleEnum.GroupUnionApplyStatus.handling);

        tmp.setCreateTime(now);
        tmp.setCreateUserId(currentUserId);

        GroupDoctorApply apply = this.saveEntityAndFind(tmp);

        return apply;
    }

    protected void sendCreateIMMsgToManagersAsync(GroupDoctorApply apply) {
        if (null == apply) {
            return;
        }

        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                sendCreateIMMsgToManagers(apply);
            }
        });
    }

    protected void sendCreateIMMsgToManagers(GroupDoctorApply apply) {
        if (null == apply) {
            return;
        }

        List<Integer> managerIdList = groupUser2Service.findDoctorIdList(apply.getGroupId());
        if (SdkUtils.isEmpty(managerIdList)) {
            return;
        }

        List<User> managerList = this.user2Service.findByIds(managerIdList);

        this.wrapAll(apply);

        sendCreateIMMsgToManagersAsync(apply, managerList);
        sendCreateSmsToManagersAsync(apply, managerList);
    }

    protected void sendCreateSmsToManagersAsync(GroupDoctorApply apply, List<User> managerList) {
        if (SdkUtils.isEmpty(managerList)) {
            return;
        }

       this.asyncTaskPool.getPool().submit(new Runnable() {
           @Override
           public void run() {
               try {
                   sendCreateSmsToManagers(apply, managerList);
               } catch (HttpApiException e) {
                   logger.error(e.getMessage(), e);
               }
           }
       });
    }

    protected void sendCreateSmsToManagers(GroupDoctorApply apply, List<User> managerList) throws HttpApiException {
        if (SdkUtils.isEmpty(managerList)) {
            return;
        }

        Group2 group = apply.getGroup();
        User user = apply.getUser();

        //{医生名称}申请加入{科室名称}，点击立即查看{打开医生圈APP链接}
        /*String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.url") +
                PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));*/
        /**修改成从应用宝获取应用**/
        String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));
        final String smsContent = baseDataService.toContent("1052", user.getName(), group.getName(), generateUrl);

        for (User manager : managerList) {  // 通知所有的管理员
            mobSmsSdk.send(manager.getTelephone(), smsContent);
        }
    }

    protected void sendCreateIMMsgToManagersAsync(GroupDoctorApply apply, List<User> managerList) {
        if (SdkUtils.isEmpty(managerList)) {
            return;
        }
       this.asyncTaskPool.getPool().submit(new Runnable() {
           @Override
           public void run() {
               sendCreateIMMsgToManagers(apply, managerList);
           }
       });
    }

    protected void sendCreateIMMsgToManagers(GroupDoctorApply apply, List<User> managerList){
        if (SdkUtils.isEmpty(managerList)) {
            return;
        }

        Group2 group = apply.getGroup();
        User user = apply.getUser();

        String title = String.format("加入%s", GroupEnum.GroupType.eval(group.getType()).getTitle());
        String msg = "";
        if (StringUtils.isNotBlank(apply.getMsg())) {
            msg = apply.getMsg();
        }
        String content = String.format("%s申请加入%s：%s", user.getName(), group.getName(), msg);
        Map<String, Object> params = new HashedMap(2);
        params.put("bizType", CircleImBizTypeEnum.GroupApply.getId());
        params.put("bizId", apply.getId().toString());
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setStyle(6);
        imgTextMsg.setPic(getDefaultGroupCheckLogoPicUrl());

        for (User manger : managerList) {  // 通知所有的管理员
            imService.sendTodoNotifyMsg(manger.getUserId(), title, content, null, params, imgTextMsg);
        }
    }

    protected String getDefaultGroupCheckLogoPicUrl() {
        String pic = String.format("%s/default/%s", PropertiesUtil.getHeaderPrefix(), PropertiesUtil.getContextProperty("group.check.pic"));
        return pic;
    }

    @Override
    public void wrapGroupDoctorApplyStatus(GroupDoctorApply dbItem) {
        if (null == dbItem) {
            return;
        }
        CircleEnum.GroupUnionApplyStatus curStatus = CircleEnum.GroupUnionApplyStatus.eval(dbItem.getStatusId());
        switch (curStatus) {
            case handling:
                dbItem.setGroupDoctorStatus(GroupEnum.GroupDoctorStatus.申请待确认.getIndex());
                break;
            case accepted:
                dbItem.setGroupDoctorStatus(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
                break;
            case refused:
                dbItem.setGroupDoctorStatus(GroupEnum.GroupDoctorStatus.申请拒绝.getIndex());
                break;
            case deleted:
                // 关闭时把数据删掉
                break;
            case closed:
                // 关闭时把数据删掉
                break;
        }
    }


    @Override
    public GroupDoctor2 findByUKAndGroupDoctor(Integer userId, String groupId) {
        GroupDoctor2 groupDoctor2 = this.groupDoctor2Service.findNormalByUK(groupId, userId);
        if (null != groupDoctor2) {
            return groupDoctor2;
        }

        GroupDoctorApply apply = this.findLastestApplyingByGroupAndDoctor(groupId, userId);
        if(null != apply){
            wrapGroupDoctorApplyStatus(apply);

            GroupDoctor2 gd = new GroupDoctor2();
            gd.setId(new ObjectId());
            gd.setDoctorId(userId);
            gd.setGroupId(groupId);
            gd.setStatus(apply.getGroupDoctorStatus());
            return gd;
        }

        return null;
    }


}
