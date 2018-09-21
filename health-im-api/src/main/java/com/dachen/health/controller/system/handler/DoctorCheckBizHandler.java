package com.dachen.health.controller.system.handler;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.activity.invite.api.credit.CreditApiProxy;
import com.dachen.health.activity.invite.service.CircleInviteReportService;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.UserSource;
import com.dachen.health.group.IGroupFacadeService;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.operationLog.constant.OperationLogTypeDesc;
import com.dachen.health.operationLog.mq.OperationLogMqProducer;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.system.entity.param.DoctorCheckParam;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.manager.RemoteSysManagerUtil;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by sharp on 2018/3/1.
 */
@Service
public class DoctorCheckBizHandler {

    static Logger logger = LoggerFactory.getLogger(DoctorCheckBizHandler.class);

    @Autowired
    private UserManager userManager;
    @Autowired
    private IGroupDao groupDao;
    @Autowired
    private IGroupDoctorService groupDoctorService;
    @Resource
    private IGroupFacadeService groupFacadeService;
    @Autowired
    private IPackService packService;
    @Autowired
    private CircleInviteReportService circleInviteReportService;
    @Autowired
    private CreditApiProxy creditApiProxy;
    @Autowired
    private IBusinessServiceMsg businessServiceMsg;
    @Autowired
    private RemoteSysManagerUtil remoteSysManagerUtil;

    @Async
    public void handleRelationBiz(User user) throws HttpApiException {
        if (user.getUserType() != UserEnum.UserType.doctor.getIndex()) {
            return;
        }
        if (user.getGiveCoin() == null) {
            this.doctorCheckedAddCredit(user);
            logger.info("审核医生学币赠送完成。。。");
            this.inviterCheckedAddCredit(user);
            logger.info("邀请人学币赠送完成。。。");
            userManager.updateGiveCoin(user.getUserId(), 1);
            logger.info("。。。学币赠送完成。。。");
        }
        userManager.userInfoChangeNotify(user.getUserId());
        this.inviteConfirm(user);
        logger.info("邀请确认完成。。。");
        this.autoJoinDept(user);
        logger.info("加入科室完成。。。");
        this.updateActivityInviteReportData(user);
        logger.info("更新认证数量完成。。。");
        //医生审核通过时候判断是否需要激活对应的集团
        groupDoctorService.activeAllUserGroup(user.getUserId());
        //陈洁说医生审核通过的时候默认添加图文套餐 2016-10-24 16:06:02
        packService.addPackIfMessageNull(user.getUserId());

        logger.info("医生审核通过异步处理相关业务完成。。。");
    }

    /**
     * 审核通过后，设为已同意邀请
     * @param user
     */
    private void inviteConfirm(User user) {
        try {
            //通过h5邀请的用户，账号通过审核后，自动设为已同意邀请 李敏 2017年9月7
            remoteSysManagerUtil.postForObject("http://CIRCLE/inner/inviteConfirmForH5/{userId}", user.getUserId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 认证通过给医生增加100学币，并且发通知
     */
    private void doctorCheckedAddCredit(User user) throws HttpApiException {
        Long addCredit = 100L;
        //与侯桐确认 如果提交审核时间为2018-01-03以前的，还是按照100学币来赠送；
        //2018-01-03以后的则按50学币赠送
        Calendar cal = Calendar.getInstance();
        cal.set(2018, Calendar.JANUARY, 3, 0, 0, 0);
        if (Objects.isNull(user.getSubmitTime()) || user.getSubmitTime() >= cal.getTimeInMillis()) {
            String credit = PropertiesUtil.getContextProperty("doctor.checked.add.credit");
            if (StringUtil.isNoneBlank(credit)) {
                addCredit = Long.parseLong(credit);
            }
        }
        creditApiProxy.doctorChecked(user.getUserId() + "", user.getName(), addCredit, "认证通过", "认证通过");
        sendIMNoticeToDoctorCircle(user.getUserId() + "", user.getUserId());
    }

    /**
     * 认证通过给邀请人增加20学币，并且发通知
     */
    private void inviterCheckedAddCredit(User user) throws HttpApiException {
        UserSource userSource = user.getSource();
        if (userSource == null) {
            return;
        }
        logger.info("userId={}, userSource={}", user.getUserId(), userSource);
        if (UserEnum.Source.doctorCircle.getIndex() == userSource.getSourceType()) {
            User inviter = userManager.getUser(userSource.getInviterId());
            if (Objects.nonNull(inviter)) {
                String reason = String.format("%s邀请%s注册,%s得的奖励", inviter.getName(), user.getName(), inviter.getName());
                String toUserReason = String.format("邀请%s医生认证通过", user.getName());
                creditApiProxy.userIntegralTransfer(userSource.getInviterId() + "", 20L, reason, reason, toUserReason);
                sendIMInviterToDoctorCircle(userSource.getInviterId() + "", userSource.getInviterId());
            }
        } else if (UserEnum.Source.doctorCircleInviteJoin.getIndex() == userSource.getSourceType()) {
            User inviter = userManager.getUser(userSource.getInviterId());
            if (Objects.nonNull(inviter)) {
                String reason = String.format("%s邀请%s加入圈子,%s得的奖励", inviter.getName(), user.getName(), inviter.getName());
                String toUserReason = String.format("邀请%s医生认证通过", user.getName());
                creditApiProxy.userIntegralTransfer(userSource.getInviterId() + "", 20L, reason, reason, toUserReason);
                sendIMInviterToDoctorCircle(userSource.getInviterId() + "", userSource.getInviterId());
            }
        }
    }

    /**
     * 审核医生通过，要给医生发送IM通知，医生圈app系统通知
     *
     * @param toUserId IM通知发送给谁
     * @param doctorId 医生
     */
    private void sendIMNoticeToDoctorCircle(String toUserId, Integer doctorId) throws HttpApiException {
        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setTime(System.currentTimeMillis());
        imgTextMsg.setStyle(7);
        imgTextMsg.setTitle("系统通知");
        imgTextMsg.setFooter("查看详情");
        String content = "恭喜您认证通过，已获得学币奖励";
        imgTextMsg.setContent(content);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("bizType", 34);
        param.put("bizId", doctorId);
        imgTextMsg.setParam(param);
        mpt.add(imgTextMsg);
        businessServiceMsg.sendTextMsg(toUserId, SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
    }


    /**
     * 审核医生通过，给邀请人医生发送IM通知，医生圈app系统通知
     *
     * @param toUserId IM通知发送给谁
     * @param doctorId 医生
     */
    private void sendIMInviterToDoctorCircle(String toUserId, Integer doctorId) throws HttpApiException {
        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setTime(System.currentTimeMillis());
        imgTextMsg.setStyle(7);
        imgTextMsg.setTitle("系统通知");
        imgTextMsg.setFooter("查看详情");
        String content = "您邀请的医生认证通过，获得20学币";
        imgTextMsg.setContent(content);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("bizType", 34);
        param.put("bizId", doctorId);
        imgTextMsg.setParam(param);
        mpt.add(imgTextMsg);
        businessServiceMsg.sendTextMsg(toUserId, SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
    }

    /**
     * 更新活动邀请人认证数量
     * @param user
     */
    private void updateActivityInviteReportData(User user) {
        //被活动邀请注册的医生，审核通过，需要更新活动管理-邀请报表数据
        if (user.getSource() != null && StringUtil.isNotBlank(user.getSource().getInviteActivityId())){
            circleInviteReportService.incAutherizedCount(user.getSource().getInviterId(), user.getSource().getInviteActivityId(), user.getSource().getSourceType());
        }
    }

    /**
     * 科室邀请注册的医生自动加入科室
     */
    private void autoJoinDept(User user) throws HttpApiException {
        //必须是由科室邀请注册的医生才有自动加入科室的流程
        if (user.getSource() != null && user.getSource().getDeptInvitation() != null && user.getSource().getDeptInvitation()){
            //邀请人
            User inviter = userManager.getUser(user.getSource().getInviterId());
            GroupDoctor checkGroup = groupDao.checkDept(inviter.getUserId());
            if (checkGroup != null) {
                Group joinGroup = groupDao.getById(checkGroup.getGroupId());

                String groupId = joinGroup.getId();
                //判断医生的执业信息中的科室与待加入的科室是否一致
                if (user.getDoctor() != null && StringUtil.isNoneBlank(user.getDoctor().getDeptId())) {
                    if (!user.getDoctor().getDeptId().equals(inviter.getDoctor().getDeptId())) {
                        // 不一致，发通知提示
                        sendIMNoticeCanNotJoinDept(user.getUserId()+"", joinGroup.getName());
                    } else {
                        //一致，加入科室后发通知
                        groupFacadeService.saveCompleteGroupDoctor(groupId, user.getUserId(), user.getTelephone(), inviter.getUserId());
                        sendIMNoticeJoinDept(user.getUserId()+"", joinGroup.getName());
                    }
                }
            }
        }
    }

    /**
     * 医生执业信息与科室不一致，通知提示不能加入科室
     *
     * @param toUserId IM通知发送给谁
     * @param deptName 目标科室
     */
    private void sendIMNoticeCanNotJoinDept(String toUserId, String deptName) throws HttpApiException {
        List<ImgTextMsg> list = new ArrayList<ImgTextMsg>();
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setStyle(7);
        //  通知内容需要放到资源配置文件中，方便统一更新
        imgTextMsg.setTitle("系统通知");
        imgTextMsg.setTime(System.currentTimeMillis());
        imgTextMsg.setContent(String.format("您的执业医院科室与%s不一致，无法加入科室。您可以在圈子页面-我的科室里加入其它科室，或者创建新科室。", deptName));
        list.add(imgTextMsg);
        //imgTextMsg.setFooter("查看详情");
        businessServiceMsg.sendTextMsg(toUserId, SysGroupEnum.TODO_NOTIFY_DOC, list, null);
    }

    /**
     * 医生执业信息与科室不一致，通知提示不能加入科室
     *
     * @param toUserId IM通知发送给谁
     * @param deptName 目标科室
     */
    private void sendIMNoticeJoinDept(String toUserId, String deptName) throws HttpApiException {
        List<ImgTextMsg> list = new ArrayList<ImgTextMsg>();
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setStyle(7);
        //  通知内容需要放到资源配置文件中，方便统一更新
        imgTextMsg.setTitle("系统通知");
        imgTextMsg.setTime(System.currentTimeMillis());
        imgTextMsg.setContent(String.format("恭喜您加入%s", deptName));
        list.add(imgTextMsg);
        //imgTextMsg.setFooter("查看详情");
        businessServiceMsg.sendTextMsg(toUserId, SysGroupEnum.TODO_NOTIFY_DOC, list, null);
    }

}
