package com.dachen.health.activity.invite.service.impl;

import com.dachen.common.auth.Auth2Helper;
import com.dachen.common.auth.data.AccessToken;
import com.dachen.commons.constants.Constants;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.activity.invite.entity.Activity;
import com.dachen.health.activity.invite.form.UserRegisterForm;
import com.dachen.health.activity.invite.service.ActivityService;
import com.dachen.health.activity.invite.service.DoctorRegisterService;
import com.dachen.health.base.dao.IdxRepository;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.Source;
import com.dachen.health.commons.constants.UserEnum.UserLevel;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.service.impl.SendDoctorTopicService;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.UserSource;
import com.dachen.health.operationLog.constant.OperationLogTypeDesc;
import com.dachen.health.operationLog.mq.OperationLogMqProducer;
import com.dachen.health.user.UserInfoNotify;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.sdk.annotation.Model;
import com.dachen.util.DateUtil;
import com.dachen.util.HeaderInfo;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Maps;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Model(value = User.class)
@Service
public class DoctorRegisterServiceImpl extends IntegerBaseServiceImpl implements
    DoctorRegisterService {

    @Autowired
    protected UserManager userManager;

    @Autowired
    protected Auth2Helper auth2Helper;

    @Autowired
    private ActivityService activityService;

    @Autowired
    protected IdxRepository idxRepository;
    
    @Autowired
	private OperationLogMqProducer operationLogMqProducer;

    @Autowired
    private SendDoctorTopicService sendDoctorTopicService;

    @Override
    public Map<String, Object> register(UserRegisterForm form) {
        /*User inviter = this.findDoctorById(form.getInviterId());
        if (null == inviter) {
            throw new ServiceException("邀请人不存在");
        }*/

        String telephone = form.getTelephone();
        String password = form.getPassword();
        // userType 医生
        int userType = UserEnum.UserType.doctor.getIndex();
        boolean exists = userManager.existsUser(telephone, userType);
        if (exists) {
            throw new ServiceException("该手机号码已注册");
        }

        HeaderInfo headerInfo = ReqUtil.instance.getHeaderInfo();
        String deviceType = headerInfo.getDeviceType();
        String appName = headerInfo.getAppName();
        String deviceId = headerInfo.getDeviceId();
        // 生成userId
        AccessToken tokenVo = auth2Helper.registerAndLogin(telephone, password, userType, null, deviceId, deviceType, appName);

        User user = this.addUser(tokenVo.getUserId(), userType, form);

        Map<String, Object> result = Maps.newHashMap();
        result.put("access_token", tokenVo.getToken());
        result.put("expires_in", Constants.Expire.DAY7);
        result.put("doctor_id", user.getUserId());

        logger.info("ActivityInviteRegistration register userId:{}, inviterId:{}", user.getUserId(), user.getSource().getInviterId());
        /* 异步发送Topic消息队列 */
        sendDoctorTopicService.sendRegisterTopicMes(user);
        /* 用户信息注册通知 */
        user.setOpenId(tokenVo.getOpenId());
        UserInfoNotify.notifyUserRegister(user);
        return result;
    }

    protected String nextDoctorNum() {
        String doctorNum = idxRepository.nextDoctorNum(IdxRepository.idxType.doctorNum);
        return doctorNum;
    }

    public User addUser(int userId, int userType, UserRegisterForm form) {
        User userTemp = new User();
        userTemp.setUserId(userId);
        userTemp.setUserType(userType);
        userTemp.setUsername("");
        userTemp.setTelephone(form.getTelephone());
        userTemp.setName(form.getName());
        userTemp.setSource(new UserSource());
        userTemp.getSource().setTerminal(UserEnum.Terminal.xg.getIndex());
        userTemp.getSource().setInviterId(form.getInviterId());
        userTemp.getSource().setInviteActivityId(form.getInviteActivityId());
        userTemp.getSource().setRegisterActivityId(form.getRegisterActivityId());
        userTemp.getSource().setDeptInvitation(form.getDeptInvitation());
        //注册活动
        if (StringUtil.isNotBlank(form.getRegisterActivityId())) {
            Activity activity = activityService.findById(form.getRegisterActivityId());
            if (!Objects.isNull(activity)) {
                userTemp.getSource().setActivityName(activity.getName());
            }
        }
        userTemp.getSource().setSourceType(form.getSubsystem());
        Map<String, String> opeartorLog = new HashMap<>();
        opeartorLog.put("operationType", OperationLogTypeDesc.DOCOTORLEVELCHANGE);
        if (Objects.equals(form.getSubsystem(), Source.drugOrg.getIndex())) {
            //自己注册的用户初始换用户级别
            if (Objects.equals(userType, UserType.doctor.getIndex())) {
                userTemp.setUserLevel(UserLevel.TemporaryUser.getIndex());
                //userTemp.setLimitedPeriodTime(System.currentTimeMillis()+UserEnum.TEMPUSER_LIMITED_PERIOD);
                userTemp.setLimitedPeriodTime(UserEnum.FOREVER_LIMITED_PERIOD);
                opeartorLog.put("content",
                        String.format("(%1$s)通过医药代表邀请注册为医生用户变更为-%2$s(%3$s)", userTemp.getTelephone(),
                                UserEnum.UserLevel.getName(UserLevel.TemporaryUser.getIndex()),
                                DateUtil.formatDate2Str(UserEnum.FOREVER_LIMITED_PERIOD)));
            }
        } else {
            if (Objects.equals(userType, UserType.doctor.getIndex())) {
                User inviter = userManager.getUser(form.getInviterId());
                if (Objects.nonNull(inviter)) {
                    if (Objects.equals(inviter.getStatus(), UserStatus.normal.getIndex())
                            || Objects.equals(inviter.getBaseUserLevel(), UserLevel.TemporaryUser.getIndex())) {
                        //认证用户和临时用户邀请注册，新注册的用户都是临时用户
                        userTemp.setUserLevel(UserLevel.TemporaryUser.getIndex());
                        // userTemp.setLimitedPeriodTime(System.currentTimeMillis()+UserEnum.TEMPUSER_LIMITED_PERIOD);
                        userTemp.setLimitedPeriodTime(UserEnum.FOREVER_LIMITED_PERIOD);
                        opeartorLog.put("content",
                                String.format("(%1$s)通过认证用户和临时用户邀请注册为医生身份变更为-%2$s(%3$s)", userTemp.getTelephone(),
                                        UserEnum.UserLevel.getName(UserLevel.TemporaryUser.getIndex()),
                                        DateUtil.formatDate2Str(UserEnum.FOREVER_LIMITED_PERIOD)));
                    } else {
//            			userTemp.setUserLevel(UserLevel.Tourist.getIndex());
//            			userTemp.setLimitedPeriodTime(UserEnum.GUEST_LIMITED_PERIOD);
//            			opeartorLog.put("content",String.format("(%1$s)通过活动邀请注册为医生身份变更为-%2$s(%3$s)",
//                				userTemp.getTelephone(),UserEnum.UserLevel.getName(UserLevel.Tourist.getIndex()),DateUtil.formatDate2Str(UserEnum.GUEST_LIMITED_PERIOD, null)));
                    }
                } else {
                    userTemp.setUserLevel(UserLevel.TemporaryUser.getIndex());
                    userTemp.setLimitedPeriodTime(UserEnum.FOREVER_LIMITED_PERIOD);
                    opeartorLog.put("content", String.format("(%1$s)通过活动邀请注册为医生身份变更为-%2$s(%3$s)",
                            userTemp.getTelephone(), UserEnum.UserLevel.getName(UserLevel.TemporaryUser.getIndex()), DateUtil.formatDate2Str(UserEnum.FOREVER_LIMITED_PERIOD, null)));

                }
            }
        }
        
        userTemp.getSource().setInvateWay(form.getWay());

        userTemp.setCreateTime(System.currentTimeMillis());
        userTemp.setModifyTime(userTemp.getCreateTime());
//        jo.put("isAuth", 0);
        userTemp.setStatus(UserEnum.UserStatus.Unautherized.getIndex());    // 医生默认是未认证

        // 初始化登录日志
//        jo.put("loginLog", User.LoginLog.init(example, true));
        // 初始化用户设置
        userTemp.setSettings(User.UserSettings.getDefaultSetting());

        userTemp.setDoctor(new Doctor());
        userTemp.getDoctor().setDoctorNum(this.nextDoctorNum());
        userTemp.getDoctor().setServiceStatus(UserEnum.ServiceStatus.close.getIndex());

        userTemp.setSuspend(0);
        User user = this.saveEntityAndFind(userTemp);

        notifyUserUpdateAsync(user);
        
        /*if(!CollectionUtils.isEmpty(opeartorLog)&&Objects.equals(opeartorLog.size(),2)){
        	operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),opeartorLog.get("operationType"),opeartorLog.get("content"));
        }*/
        return user;
    }

    protected void notifyUserUpdateAsync(User user) {
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    // 用户信息修改通知
                    UserInfoNotify.notifyUserUpdate(user.getUserId());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });

        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    // 用户头像修改通知:1、生成头像缩略图;2：生成用户二维码；3、如果是医生，则更改医生公共号头像
                    UserInfoNotify.notifyUserPicUpdate(user, "avatar");
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }
}
