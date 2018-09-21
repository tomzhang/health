package com.dachen.health.circle.service.impl;

import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.circle.CircleEnum;
import com.dachen.health.circle.CircleImBizTypeEnum;
import com.dachen.health.circle.entity.*;
import com.dachen.health.circle.entity.GroupDoctorInvite;
import com.dachen.health.circle.service.*;
import com.dachen.health.circle.vo.MobileGroupDoctorVO;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.exception.ServiceException;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.JSONUtil;
import com.dachen.util.PropertiesUtil;
import com.mobsms.sdk.MobSmsSdk;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Model(GroupDoctorInvite.class)
@Service
public class GroupDoctorInviteServiceImpl extends BaseGroupDoctorApplyOrInviteServiceImpl implements GroupDoctorInviteService {

    @Override
    public GroupDoctorInvite create(Integer currentUserId, String groupId, Integer doctorId) {
        Group2 group = group2Service.findAndCheckGroupOrDept(groupId);
        // 检查权限
        groupDoctor2Service.checkInvitePri(group, currentUserId);

        if (currentUserId.equals(doctorId)) {
            throw new ServiceException("不能邀请自己");
        }
        
        User currentUser = user2Service.findAndCheckDoctor(currentUserId);
        User doctorUser = user2Service.findAndCheckDoctor(doctorId);

        GroupDoctorInvite dbItem = this.doAdd(currentUser, doctorUser, group);
        this.wrapAll(dbItem);

        this.sendInviteIMMsgToDoctorAsync(currentUser, dbItem);
        this.sendInviteSmsAsync(dbItem);

        return dbItem;
    }

    @Override
    public boolean accept(Integer currentUserId, String id) throws HttpApiException {
        GroupDoctorInvite dbItem = this.findById(id);
        // check pri
        if (!currentUserId.equals(dbItem.getUserId())) {
            throw new ServiceException("Forbidden");
        }

        CircleEnum.GroupUnionApplyStatus curStatus = CircleEnum.GroupUnionApplyStatus.eval(dbItem.getStatusId());
        if (CircleEnum.GroupUnionApplyStatus.handling != curStatus) {
            throw new ServiceException("邀请" + curStatus.getTitle());
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
        }else {
            this.wrapAll(dbItem);
            groupDoctor2 = groupDoctor2Service.addByInvite(currentUserId, dbItem);
        }
        dbItem.setStatus(CircleEnum.GroupUnionApplyStatus.accepted);
        this.saveEntity(dbItem);

        // 发送通知给管理员
        sendAcceptIMNotifyToManagersAsync(dbItem);
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
        GroupDoctorInvite dbItem = this.findById(id);
        if (!currentUserId.equals(dbItem.getUserId())) {
            throw new ServiceException("Forbidden");
        }

        CircleEnum.GroupUnionApplyStatus curStatus = CircleEnum.GroupUnionApplyStatus.eval(dbItem.getStatusId());
        if (CircleEnum.GroupUnionApplyStatus.handling != curStatus) {
            throw new ServiceException("邀请" + curStatus.getTitle());
        }


        dbItem.setStatus(CircleEnum.GroupUnionApplyStatus.refused);
        this.saveEntity(dbItem);

        this.wrapAll(dbItem);
        // 发送通知给邀请人
        sendRefuseIMMsgAsync(dbItem);

        return true;
    }

    @Override
    public MobileGroupDoctorVO findDetailByIdAndGroupDoctorVO(Integer currentUserId, String id) {
        GroupDoctorInvite dbItem = this.findById(id);
        this.wrapAll(dbItem);

        MobileGroupDoctorVO vo = this.convertToGroupDoctorVO(dbItem);
        // 刘琦邀请您加入大辰中西医结合眼科
        User inviteUser = user2Service.findDoctorById(dbItem.getCreateUserId());
        vo.setMsg(String.format("%s邀请您加入%s", inviteUser.getName(), dbItem.getGroup().getName()));
        return vo;
    }

    @Override
    public boolean findInviteingByGroupAndDoctor(String groupId, Integer doctorId){
        Query<GroupDoctorInvite> query = this.createQuery();
        query.field("userId").equal(doctorId)
                .field("groupId").equal(groupId)
                .field("statusId").equal(CircleEnum.GroupUnionApplyStatus.handling.getIndex());
        List<GroupDoctorInvite> groupDoctorInvites = query.asList();
        if(groupDoctorInvites!=null && groupDoctorInvites.size()>0){
            return true;
        }else {
            return false;
        }
    }

    protected  MobileGroupDoctorVO convertToGroupDoctorVO(GroupDoctorInvite dbItem) {
        if (null == dbItem) {
            return null;
        }
        this.wrapGroupDoctorInviteStatus(dbItem);
        MobileGroupDoctorVO vo = new MobileGroupDoctorVO(dbItem);
        return vo;
    }

    @Override
    public void wrapGroupDoctorInviteStatus(GroupDoctorInvite dbItem) {
        if (null == dbItem) {
            return;
        }
        CircleEnum.GroupUnionApplyStatus curStatus = CircleEnum.GroupUnionApplyStatus.eval(dbItem.getStatusId());
        switch (curStatus) {
            case handling:
                dbItem.setGroupDoctorStatus(GroupEnum.GroupDoctorStatus.邀请待确认.getIndex());
                break;
            case accepted:
                dbItem.setGroupDoctorStatus(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
                break;
            case refused:
                logger.info("wrapGroupDoctorInviteStatus method {}", ToStringBuilder.reflectionToString(dbItem));
                dbItem.setGroupDoctorStatus(GroupEnum.GroupDoctorStatus.邀请拒绝.getIndex());
                break;
            case deleted:
                // 关闭时把数据删掉
                break;
            case closed:
                // 关闭时把数据删掉
                break;
        }
    }

    protected void sendRefuseIMMsgAsync(GroupDoctorInvite invite) {
        if (null == invite) {
            return;
        }
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sendRefuseIMMsg(invite);
                } catch (HttpApiException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    protected void sendRefuseIMMsg(GroupDoctorInvite invite) throws HttpApiException {
        String tag = "inviteRefuseSendIMMsg";
        logger.info("{}. groupDoctor={}", tag, invite);
        if (null == invite) {
            return;
        }
        this.wrapAll(invite);

        Group2 group = invite.getGroup();
        User user = invite.getUser();

        String content = user.getName() + "医生已拒绝您的邀请，未加入" + group.getName();

        // 初始化发送的消息文本
        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>(1);
        ImgTextMsg textMsg = new ImgTextMsg();
        textMsg.setTime(System.currentTimeMillis());
        textMsg.setStyle(7);
        textMsg.setContent(content);
        textMsg.setTitle("加入" + GroupEnum.GroupType.eval(group.getType()).getTitle());
        //textMsg.setFooter("查看详情");

        // 设置业务类型和参数
        Map<String, Object> imParam = new HashMap<String, Object>();
        imParam.put("bizType", CircleImBizTypeEnum.GroupResult.getId());
        imParam.put("bizId", invite.getId().toString());
        textMsg.setParam(imParam);

        mpt.add(textMsg);

        businessServiceMsg.sendTextMsg(invite.getCreateUserId() + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
    }

    protected void sendAcceptIMNotifyToManagersAsync(GroupDoctorInvite dbItem) {
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sendAcceptIMNotifyToManagers(dbItem);
                } catch (HttpApiException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    protected void sendAcceptIMNotifyToManagers(GroupDoctorInvite dbItem) throws HttpApiException {
        String tag = "sendAcceptIMNotifyToManagers";
        logger.info("{}. dbItem={}", tag, dbItem);

        this.wrapAll(dbItem);

        // 查询集团管理员列表
        List<GroupUser2> groupUserList = groupUser2Service.findList(dbItem.getGroupId());
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
        textMsg.setTitle("加入科室");
        //设置业务类型和参数
        Map<String, Object> imParam = new HashMap<String, Object>();
        imParam.put("bizType", CircleImBizTypeEnum.GroupResult.getId());
        imParam.put("bizId", dbItem.getId().toString());
        textMsg.setParam(imParam);

        mpt.add(textMsg);

        String managerContent = "";
        String inviteManagerContent = "";
        User user = dbItem.getUser();
        Group2 group = dbItem.getGroup();
        if (StringUtils.isNotEmpty(user.getName())) {
            managerContent = user.getName() + "医生已加入" + group.getName();
            inviteManagerContent = user.getName() + "医生已接受您的邀请，加入" + group.getName();
        } else {
            managerContent = user.getTelephone() + "医生已加入" + group.getName();
            inviteManagerContent = user.getTelephone() + "医生已接受您的邀请，加入" + group.getName();
        }

        for (GroupUser2 guser : groupUserList) {
            if (guser.getDoctorId().equals(dbItem.getCreateUserId())) {
                textMsg.setContent(inviteManagerContent);
            } else {
                textMsg.setContent(managerContent);
            }
            logger.info("{}. userId: {} textMsg: {}", tag, guser.getDoctorId(), JSONUtil.toJSONString(textMsg));
            businessServiceMsg.sendTextMsg(guser.getDoctorId() + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
        }
    }

    protected void sendInviteSmsAsync(GroupDoctorInvite dbItem) {
        if (null == dbItem) {
            return;
        }
       this.asyncTaskPool.getPool().submit(new Runnable() {
           @Override
           public void run() {
               try {
                   sendInviteSms(dbItem);
               } catch (HttpApiException e) {
                   logger.error(e.getMessage(), e);
               }
           }
       });
    }

    protected void sendInviteSms(GroupDoctorInvite dbItem) throws HttpApiException {
        if (null == dbItem) {
            return;
        }
        this.wrapAll(dbItem);

        String sms = this.getInviteTpl(3, dbItem.getId().toString(), dbItem.getGroup(), dbItem.getUser());
        mobSmsSdk.send(dbItem.getUser().getTelephone(), sms, BaseConstants.XG_YSQ_APP);
    }

    /**
     * 获取邀请短链
     */
    protected String getInviteTpl(Integer type, String id, Group2 group, User doctorUser) throws HttpApiException {
        String tpl = null;
        String url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.server");

        if (GroupEnum.GroupType.hospital.getIndex().equals(group.getType())) {
            url = PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.hospitalJoin");
        }

        final String doctorName = doctorUser.getName() == null ? "" : doctorUser.getName();
        url = url + "?id=" + id + "&doctorName=" + doctorName;

        String unitName = "";
        String opName = "";
        String doc = BaseConstants.XG_YSQ_APP;
        if (2 == type) {
            url = url + "&type=2" + "&name=" + group.getName();
            unitName = group.getName();
            opName = String.format("成为%s管理员", GroupEnum.GroupType.eval(group.getType()).getTitle());
        } else if (3 == type) {
            url = url + "&type=3" + "&name=" + group.getName();
            unitName = group.getName();
            opName = "加入科室";
        }
        tpl = baseDataService.toContent("0002", doctorName, unitName, opName, shortUrlComponent.generateShortUrl(url), doc);

        return tpl;
    }

    protected void sendInviteIMMsgToDoctorAsync(User currentUser, GroupDoctorInvite dbItem) {
        if (null == currentUser || null == dbItem) {
            return;
        }
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                sendInviteIMMsgToDoctor(currentUser, dbItem);
            }
        });
    }
    protected void sendInviteIMMsgToDoctor(User currentUser, GroupDoctorInvite dbItem) {
        if (null == dbItem) {
            return;
        }

        this.wrapAll(dbItem);

        Group2 group = dbItem.getGroup();

        String title = String.format("加入%s", GroupEnum.GroupType.eval(group.getType()).getTitle());
        String content = String.format("%s邀请您加入%s", currentUser.getName(), group.getName());
        Map<String, Object> params = new HashedMap(2);
        params.put("bizType", CircleImBizTypeEnum.GroupInvite.getId());
        params.put("bizId", dbItem.getId().toString());
        ImgTextMsg imgTextMsg=new ImgTextMsg();
        imgTextMsg.setStyle(6);
        imgTextMsg.setPic(getDefaultGroupInviteLogoPicUrl());
        imService.sendTodoNotifyMsg(dbItem.getUserId(), title, content, null, params, imgTextMsg);
    }
    protected String getDefaultGroupInviteLogoPicUrl() {
        String pic = String.format("%s/default/%s", PropertiesUtil.getHeaderPrefix(),  PropertiesUtil.getContextProperty("group.invite.pic"));
        return pic;
    }

    private GroupDoctorInvite doAdd(User currentUser, User doctorUser, Group2 group) {
        long now = System.currentTimeMillis();
        GroupDoctorInvite tmp = new GroupDoctorInvite(doctorUser, group);

        tmp.setStatus(CircleEnum.GroupUnionApplyStatus.handling);

        tmp.setCreateTime(now);
        tmp.setCreateUserId(currentUser.getUserId());

        GroupDoctorInvite dbItem = this.saveEntityAndFind(tmp);

        return dbItem;
    }


}
