package com.dachen.health.activity.invite.controller;

import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.activity.invite.enums.InviteEnum;
import com.dachen.health.activity.invite.form.UserRegisterForm;
import com.dachen.health.activity.invite.service.CircleInviteReportService;
import com.dachen.health.activity.invite.service.DoctorRegisterService;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.constants.UserEnum.InviteWayEnum;
import com.dachen.health.commons.constants.UserEnum.Source;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.IGroupFacadeService;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.manager.RemoteSysManagerUtil;
import com.dachen.mq.producer.BasicProducer;
import com.dachen.sdk.async.task.AsyncTaskPool;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.mobsms.sdk.MobSmsSdk;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activity/invite/doctor")
public class ActivityInviteDoctorController extends ActivityInviteBaseController {
    private static final Logger logger = LoggerFactory.getLogger(ActivityInviteDoctorController.class);

    @Autowired
    protected DoctorRegisterService doctorRegisterService;
    @Resource
    private IGroupFacadeService groupFacadeService;
    @Resource
    protected AsyncTaskPool asyncTaskPool;
    @Autowired
    private UserManager userManager;
    @Autowired
    private IGroupService groupService;
    @Autowired
    private CircleInviteReportService circleInviteReportService;
    
    @Autowired
    protected ShortUrlComponent shortUrlComponent;
    @Autowired
    RemoteSysManagerUtil remoteSysManagerUtil;
    
    /**
     * @api {get} /activity/invite/doctor/register 活动邀请注册医生账号
     * @apiVersion 1.0.0
     * @apiName /activity/invite/doctor/register
     * @apiGroup 医生
     * @apiDescription 活动邀请注册医生账号
     *
     * @apiParam {String}   telephone       手机号
     * @apiParam {String}   name            姓名
     * @apiParam {String}   password        密码
     * @apiParam {String}   inviteActivityId      邀请活动Id
     * @apiParam {String}   registerActivityId      注册活动Id
     * @apiParam {Integer}  inviterId       邀请人id
     * @apiParam {Integer}  subsystem       来源子系统（医生圈-17、药企圈-16）
     * @apiParam {String}   way             邀请方式（短信-sms，微信-wechat，二维码-qrcode）
     *
     * @apiSuccess {String} resultCode      返回状态码
     * @apiSuccess {String} access_token    token
     *
     * @apiAuthor 钟良
     * @date 2017年5月23日
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public JSONMessage register(@Valid UserRegisterForm form) {
        //参数校验
        form.check();

        //1、注册医生
        form.setDeptInvitation(Boolean.FALSE);//非科室邀请注册的医生
        Map<String, Object> result = doctorRegisterService.register(form);
        if (result == null || result.get("doctor_id") == null){
            logger.error("活动邀请注册医生账号失败");
            return JSONMessage.failure("活动邀请注册医生账号失败");
        }
        Integer doctorId = Integer.parseInt(result.get("doctor_id").toString());
        //2、如果子系统来源是药企圈，则往mq中写入注册成功信息，药企圈消费此信息
        if (form.getSubsystem() == Source.drugOrg.getIndex()){
            notifyDrugOrgAsync(form.getInviterId(), doctorId);
        }
        //3、更新邀请报表数据
        circleInviteReportService.incRegisteredCount(form.getInviterId(), form.getInviteActivityId(), form.getSubsystem());
        //发送短信
        try {
        	sendSMS(doctorId);
        } catch (HttpApiException e) {
            logger.error(e.getMessage(), e);
        }
        return JSONMessage.success(null, result);
    }
    
    
    public void sendSMS(int userId) throws HttpApiException {
    	User user = userManager.getUser(userId);
    	String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.arouseDoctor.MedicalCircle"));
        //远程调用参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("tel", user.getTelephone());
        params.put("content", String.format(InviteEnum.INVITIE_REGISTER_NOTICE_CONTENT,generateUrl));
        // 拓展 0=玄关健康 1=康哲，2=IBO，3=博德嘉联 6-医生圈
        params.put("ext", "6");
        Object response = remoteSysManagerUtil.post(InviteEnum.SEND_SMS_URL, params);
        logger.info(response.toString());
    }
    

    private void notifyDrugOrgAsync(Integer inviterId, Integer doctorId) {
        this.asyncTaskPool.getPool().submit(() -> {
            try {
                // 往mq中写入注册成功信息，药企圈消费此信息
                sendMsgToMq(inviterId, doctorId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    private void sendMsgToMq(Integer inviterId, Integer doctorId) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("representUserId", inviterId);
        dataMap.put("doctorUserId", doctorId);
        BasicProducer.sendMessage("EXCHANGE-INVITE-DOCTOR-REGIST", JSONObject.toJSONString(dataMap));
    }

    /**
     * @api {get, post} /activity/invite/doctor/register/integral 活动邀请注册医生账号获取积分数
     * @apiVersion 1.0.0
     * @apiName /activity/invite/doctor/register/integral
     * @apiGroup 医生
     * @apiDescription 活动邀请注册医生账号获取积分数
     *
     * @apiParam {String}   access_token    token
     *
     * @apiSuccess {String} resultCode      返回状态码
     * @apiSuccess {String} integral        积分
     *
     * @apiAuthor 钟良
     * @date 2017年5月26日
     */
    @RequestMapping(value = "/register/integral")
    public JSONMessage registerIntegral() {
        Map<String, String> result = new HashMap<>();
        Long addCredit = 100L;
        String credit = PropertiesUtil.getContextProperty("doctor.checked.add.credit");
        if (StringUtil.isNoneBlank(credit)) {
            addCredit = Long.parseLong(credit);
        }
        result.put("integral", addCredit+"");
        return JSONMessage.success(null, result);
    }

    /**
     * @api {post} /activity/invite/doctor/deptInviteRegister 科室邀请注册医生
     * @apiVersion 1.0.0
     * @apiName /activity/invite/doctor/deptInviteRegister
     * @apiGroup 医生
     * @apiDescription 科室邀请注册医生
     *
     * @apiParam {String}   telephone       手机号
     * @apiParam {String}   name            姓名
     * @apiParam {String}   password        密码
     * @apiParam {Integer}  inviterId       邀请人id
     * @apiParam {Integer}  subsystem       来源子系统（医生圈-17、药企圈-16）
     * @apiParam {String}   way             邀请方式（短信-sms，微信-wechat，二维码-qrcode）
     * @apiParam {String}   groupId         科室Id
     *
     * @apiSuccess {String} resultCode      返回状态码
     * @apiSuccess {String} access_token    token
     *
     * @apiAuthor 钟良
     * @date 2017年6月9日
     */
    @RequestMapping(value = "/deptInviteRegister", method = RequestMethod.POST)
    public JSONMessage deptInviteRegister(UserRegisterForm form) {
        //参数校验
        form.check();

        //1、注册医生
        form.setDeptInvitation(Boolean.TRUE);//科室邀请注册的医生
        Map<String, Object> data = doctorRegisterService.register(form);
        if (data == null || data.get("doctor_id") == null){
            logger.error("活动邀请注册医生账号失败");
            return JSONMessage.failure("活动邀请注册医生账号失败");
        }
        Integer doctorId = Integer.parseInt(data.get("doctor_id").toString());
        //2、如果子系统来源是药企圈，则往mq中写入注册成功信息，药企圈消费此信息
        if (form.getSubsystem() == Source.drugOrg.getIndex()){
            notifyDrugOrgAsync(form.getInviterId(), doctorId);
        }

        return JSONMessage.success("");
    }

    /**
     * @api {post} /activity/invite/doctor/registerJoinDept 注册并加入科室
     * @apiVersion 1.0.0
     * @apiName /activity/invite/doctor/registerJoinDept
     * @apiGroup 医生
     * @apiDescription 注册并加入科室
     *
     * @apiParam {String}   telephone       手机号
     * @apiParam {String}   name            姓名
     * @apiParam {String}   password        密码
     * @apiParam {Integer}  inviterId       邀请人id
     * @apiParam {Integer}  subsystem       来源子系统（医生圈-17、药企圈-16）
     * @apiParam {String}   way             邀请方式（短信-sms，微信-wechat，二维码-qrcode）
     * @apiParam {String}   groupId         科室Id
     *
     * @apiSuccess {String} resultCode      返回状态码
     * @apiSuccess {String} access_token    token
     *
     * @apiAuthor 钟良
     * @date 2017年5月31日
     */
    @RequestMapping(value = "/registerJoinDept", method = RequestMethod.POST)
    public JSONMessage registerJoinDept(UserRegisterForm form) {
        //参数校验
        form.check();

        //1、注册医生
        form.setDeptInvitation(Boolean.TRUE);//科室邀请注册的医生
        Map<String, Object> data = doctorRegisterService.register(form);
        if (data == null || data.get("doctor_id") == null){
            logger.error("活动邀请注册医生账号失败");
            return JSONMessage.failure("活动邀请注册医生账号失败");
        }
        Integer doctorId = Integer.parseInt(data.get("doctor_id").toString());
        //2、如果子系统来源是药企圈，则往mq中写入注册成功信息，药企圈消费此信息
        if (form.getSubsystem() == Source.drugOrg.getIndex()){
            notifyDrugOrgAsync(form.getInviterId(), doctorId);
        }
        //3、加入科室
        Map<String, Object>  result;
        try {
            result = groupFacadeService.saveCompleteGroupDoctor(form.getGroupId(), doctorId, form.getTelephone(), form.getInviterId());
        } catch (HttpApiException e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("加入科室出错");
        }

        return JSONMessage.success(null, result);
    }

    /**
     * @api {post} /activity/invite/doctor/joinDept 已注册用户加入科室
     * @apiVersion 1.0.0
     * @apiName /activity/invite/doctor/joinDept
     * @apiGroup 医生
     * @apiDescription 已注册用户加入科室
     *
     * @apiParam {String}   telephone       手机号
     * @apiParam {Integer}  inviterId       邀请人id
     * @apiParam {Integer}  subsystem       来源子系统（医生圈-17、药企圈-16）
     * @apiParam {String}   way             邀请方式（短信-sms，微信-wechat，二维码-qrcode）
     * @apiParam {String}   groupId         科室Id
     *
     * @apiSuccess {String} resultCode      返回状态码
     * @apiSuccess {String} access_token    token
     *
     * @apiAuthor 钟良
     * @date 2017年5月31日
     */
    @RequestMapping(value = "/joinDept", method = RequestMethod.POST)
    public JSONMessage joinDept(UserRegisterForm form) {
        //参数校验
        form.checkForJoinDept();

        //1、查询注册医生
        User doctor = userManager.getUser(form.getTelephone(), UserType.doctor.getIndex());
        if (doctor == null) {
            throw new ServiceException("该手机号未注册");
        }
        //2、加入科室
        Integer doctorId = doctor.getUserId();
        String resultMsg = "恭喜您加入%s";
        try {
            Map<String, Object> data = groupFacadeService.saveCompleteGroupDoctor(form.getGroupId(), doctorId, form.getTelephone(), form.getInviterId());
            if (data.get("msg") != null && StringUtil.isNotBlank(data.get("msg").toString())){
                return JSONMessage.success(null, data);
            }
            Group group = groupService.getGroupById(form.getGroupId());
            resultMsg = String.format(resultMsg, group.getName());
        } catch (HttpApiException e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("加入科室出错");
        }

        return JSONMessage.success(null, resultMsg);
    }
}
