package com.dachen.health.controller.user;

import com.alibaba.fastjson.JSON;
import com.cloopen.rest.sdk.CCPRestSDK;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.KeyBuilder;
import com.dachen.commons.constants.Constants;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.jedis.JedisCallbackVoid;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.drugorg.api.client.DrugOrgApiClientProxy;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.helper.UserHelper;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.cate.entity.vo.ServiceCategoryVO;
import com.dachen.health.cate.service.IServiceCategoryService;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.commons.constants.ImageDataEnum;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.SysConstants;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.entity.SMSRanCode;
import com.dachen.health.commons.example.BindExample;
import com.dachen.health.commons.example.UserExample;
import com.dachen.health.commons.example.UserExampleV2;
import com.dachen.health.commons.example.UserQueryExample;
import com.dachen.health.commons.service.GuestUserManager;
import com.dachen.health.commons.service.SMSRanCodeService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.service.impl.SendDoctorTopicService;
import com.dachen.health.commons.service.impl.UserOperationLogService;
import com.dachen.health.commons.vo.*;
import com.dachen.health.group.IGroupFacadeService;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.operationLog.constant.OperationLogTypeDesc;
import com.dachen.health.operationLog.mq.OperationLogMqProducer;
import com.dachen.health.pack.guide.service.IGuideService;
import com.dachen.health.pack.order.service.IImageDataService;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.mapper.PackMapper;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.service.IPatientService;
import com.dachen.health.permission.entity.param.UserRoleParam;
import com.dachen.health.permission.entity.vo.UserRoleVO;
import com.dachen.health.user.UserInfoNotify;
import com.dachen.health.user.entity.param.*;
import com.dachen.health.user.entity.po.*;
import com.dachen.health.user.entity.vo.UserInfoVO;
import com.dachen.health.wx.model.WXUserInfo;
import com.dachen.im.server.data.UserSoundVO;
import com.dachen.manager.ISMSManager;
import com.dachen.medice.vo.AccessConfig;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.*;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.mobsms.sdk.MobSmsSdk;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * ProjectName： health-im-api<br>
 * ClassName： UserController<br>
 * Description： 用户<br>
 *
 * @author limiaomiao
 * @version 1.0.0
 * @crateTime 2015年7月27日
 */
@RestController
@RequestMapping("/user")
@Api(value = "/user", description = "用户")
public class UserController extends AbstractController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource(name = "jedisTemplate")
    protected JedisTemplate jedisTemplate;

    @Autowired
    private UserManager userManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IPatientService patientService;

    @Resource
    private SMSRanCodeService smsRanCodeService;

    @Resource
    private ISMSManager smsManager;

    @Resource
    private MobSmsSdk mobSmsSdk;

    @Resource
    private IGroupService groupService;

    @Resource
    private IGroupDoctorDao groupDoctorDao;

    @Resource
    private IGroupFacadeService groupFacadeService;

    @Resource
    private IGroupDoctorService groupDoctorService;

    @Resource
    private IImageDataService imageDataService;

    @Resource
    private IBaseDataService baseDataService;

    @Resource
    private IServiceCategoryService serviceCategoryService;
    @Autowired
    private GuestUserManager guestUserManager;

    @Autowired
    private IPackService packService;

    @Autowired
    private PackMapper packMapper;

    @Autowired
    private IGuideService guideService;

    @Autowired
    private DrugOrgApiClientProxy drugOrgApiClientProxy;

    @Autowired
    private OperationLogMqProducer operationLogMqProducer;

    @Autowired
    private SendDoctorTopicService sendDoctorTopicService;
    @Autowired
    private AccessConfig accessConfig;

    @Autowired
    private UserOperationLogService userOperationLogService;

    @RequestMapping(value = "/bind")
    public JSONMessage bind(@ModelAttribute BindExample example) {
        int userId = ReqUtil.instance.getUserId();
        if ("duapp".equals(example.getPlatformId())) {
            jedisTemplate.execute(new JedisCallbackVoid() {

                @Override
                public void execute(Jedis jedis) {
                    String key = String.format("duapp:channelId:%1$s", example.getChannelId());
                    String value = jedis.get(key);

                    Pipeline pipeline = jedis.pipelined();

                    // 通道已被绑定
                    if (null != value) {
                        // 删除用户绑定的通道
                        pipeline.del(String.format("duapp:userId:%1$s", value));
                    }
                    pipeline.set(key, String.valueOf(userId));
                    pipeline
                            .set(String.format("duapp:userId:%1$s", userId), example.getChannelId());
                    pipeline.sync();
                }
            });
            /*
             * jedisTemplate.executePipelined(new RedisCallback<String>() {
             *
             * @Override public String doInRedis(RedisConnection connection)
             * throws DataAccessException { String key =
             * String.format("duapp:channelId:%1$s", example.getChannelId());
             * String value = new String(connection.get(key.getBytes()));
             *
             * // 通道已被绑定 if (null != value) {
             * connection.del(String.format("duapp:userId:%1$s",
             * value).getBytes()); // 删除用户绑定的通道 //
             * pipeline.del(String.format("duapp:userId:%1$s", value)); }
             * connection.set(key.getBytes(),
             * String.valueOf(userId).getBytes());
             * connection.set(String.format("duapp:userId:%1$s",
             * userId).getBytes(), example.getChannelId().getBytes()); //
             * pipeline.set(key, String.valueOf(userId)); //
             * pipeline.set(String.format("duapp:userId:%1$s", userId), //
             * example.getChannelId()); // pipeline.sync(); return null; } });
             */
            return JSONMessage.success();
        }
        throw new RuntimeException();
    }

    @RequestMapping("/recommend")
    public JSONMessage getRecommendList(@RequestParam String text) {
        List<String> list = JSON.parseArray(text, String.class);
        List<User> data = userRepository.findByTelephone(list);

        return JSONMessage.success(null, data);
    }

    /**
     * @api {[get,post]} /user/get 获取用户好友
     * @apiVersion 1.0.0
     * @apiName get
     * @apiGroup 用户
     * @apiDescription 获取用户好友
     * @apiParam {String} access_token token
     * @apiParam {String} userId 用户Id
     * @apiSuccess {com.dachen.health.commons.vo.User} data 用户对象实体
     * @apiSuccess {String[]} tags 对此用户的分组（只支持医生对患者）
     * @apiSuccess {String} 	is3A 			        			"1":三甲；"0"：非三甲
     * @apiSuccess {String} 	groupList.groupCert 			    "1":认证；"0"：非认证
     * @apiAuthor 李淼淼
     * @date 2015年7月30日
     */
    @RequestMapping(value = "/get")
    public JSONMessage getUser(@RequestParam(defaultValue = "0") int userId) {
        int loginedUserId = ReqUtil.instance.getUserId();
        int toUserId = 0 == userId ? loginedUserId : userId;
        Object data = userManager.getUser(loginedUserId, toUserId);
        return JSONMessage.success(null, data);
    }

    /**
     * @api {[get,post]} /user/getUserById 获取用户对象
     * @apiVersion 1.0.0
     * @apiName getUserById
     * @apiGroup 用户
     * @apiDescription 获取用户对象
     * @apiParam {String} access_token token
     * @apiParam {Integer} userId 用户Id
     * @apiSuccess {com.dachen.health.commons.vo.User} data 用户对象实体
     * @apiAuthor 谢平
     * @date 2015年7月30日
     */
    @RequestMapping(value = "/getUserById")
    public JSONMessage getUserById(@RequestParam(defaultValue = "0") int userId) {
        Object data = userManager.getUser(userId == 0 ? ReqUtil.instance.getUserId() : userId);
        return JSONMessage.success(null, data);
    }

    /**
     * @api {[get,post]} /user/getUserByTel 获取用户对象
     * @apiVersion 1.0.0
     * @apiName getUserById
     * @apiGroup 用户
     * @apiDescription 获取用户对象
     * @apiParam {String} access_token token
     * @apiParam {String} telephone 手机号
     * @apiParam {Integer} userType 用户类型
     * @apiSuccess {com.dachen.health.commons.vo.User} data 用户对象实体
     * @apiAuthor 谢平
     * @date 2015年7月30日
     */
    @RequestMapping(value = "/getUserByTel")
    public JSONMessage getUserByTel(@RequestParam(required = true) String telephone,
                                    @RequestParam(required = true) Integer userType) {
        Object data = userManager.getUser(telephone, userType);
        return JSONMessage.success(null, data);
    }

    /**
     * @api {[get,post]} /user/getUserDetail 获取用户详情
     * @apiVersion 1.0.0
     * @apiName getUserDetail
     * @apiGroup 用户
     * @apiDescription 获取用户详情
     * @apiParam {String} access_token token
     * @apiParam {Integer} userId 用户Id
     * @apiSuccess {com.dachen.health.commons.vo.User} data 用户对象实体
     * @apiAuthor 李淼淼
     * @date 2015年7月30日
     */
    @RequestMapping(value = "/getUserDetail")
    public JSONMessage getUserDetail(Integer userId) {
        if ((userId == null) || (userId == 0)) {
            return JSONMessage.success(null, null);
        }
        return JSONMessage.success(null, userManager.getUser(userId));
    }

    /**
     * @api {[get,post]} /user/login 登录
     * @apiVersion 1.0.1
     * @apiName login
     * @apiGroup 用户
     * @apiDescription 用户登录（原参数不变，这里列出的只是新加的参数）
     * @apiParam {String} serial 客户端设备号,用于推送
     * @apiParam {String} model 客户端类型，值为ios或者android
     * @apiParam {String} loginGroupId 想登录的医生集团id（为空则默认登录主集团）
     * @apiParam {Integer} userType 用户类型：1：患者；2：医助；3：医生；4：客服；5：集团； 6：导医；8：护士；9：店主；10：企业用户；100：游客；
     * @apiSuccess {Map} User 返回参数
     * @apiSuccess {Integer} User.doctor.cureNum 患者人数
     * @apiSuccess {Integer} User.email 患者人数
     * @apiSuccess {Integer} User.doctor.role    角色 1  医生 2 护士 针对医生登录role才起作用
     * @apiSuccess {Integer} User.groupDoctor.taskDuration 在线时长（任务）/秒
     * @apiSuccess {String} User.doctor.troubleFree 免打扰（1：正常，2：免打扰）
     * @apiSuccess {Integer} User.groupDoctor.outpatientPrice 门诊价格（分）
     * @apiSuccess {Integer} User.groupDoctor.dutyDuration 已值班时长（秒）
     * @apiSuccess {String} User.groupDoctor.onLineState 在线状态1，在线，2离线
     * @apiSuccess {String} User.loginGroupId 目前登录的医生集团id
     * @apiSuccess {String} User.groupList[0].skip 集团的屏蔽状态（N表示正常，S表示该集团被屏蔽）
     * @apiSuccess {Integer} User.bdjlGroupStatus （0表示未加入，1表示已加入且为主集团，2表示已加入非主集团，3表示集团邀请用户用户加入）
     * @apiSuccess {String} User.headPicFileName 头像
     * @apiSuccess {String} User.name 姓名
     * @apiSuccess {String} User.telephone 电话
     * @apiSuccess {Integer} User.communityRole 社区角色权限:0-普通管理员，1-社区管理员
     * @apiSuccess {Integer} User.status 1:正常。2：待审核。3：审核未通过。4：暂时禁用。5：永久禁用。6：未激活。7：未认证。8：离职。9：注销
     * @apiSuccess {Integer} resultCode 	1:Success； 1040101：帐号不存在； 1040102：用户名或密码无效； 1040103：请重置密码;
     * 1040104：农牧项目导入医生，请重置手机号以及密码
     * @apiAuthor 傅永德
     * @date 2015年12月26日
     */
    @RequestMapping(value = "/login")
    public JSONMessage login(@RequestHeader("User-Agent") String userAgent, @ModelAttribute UserExample example) {
        Map<String, Object> data = userManager.login(example);
        //异步保存用户登录信息
        userManager.saveUserLoginInfo(example.getSerial(), example.getTelephone(), userAgent);
        if (data.get("user") != null) {
            User user = (User) data.get("user");
            if (user.getUserType() == UserType.patient.getIndex()) {
                Patient p = patientService.findOne(user.getUserId(), SysConstants.ONESELF);
                if (p != null) {
                    data.put("patientId", p.getId());
                }
            } else if (user.getUserType() == UserType.doctor.getIndex()) {
                Integer checkInGive = user.getDoctor().getCheckInGive();
                checkInGive = checkInGive == null ? 1 : checkInGive;
                user.getDoctor().setCheckInGive(checkInGive);
            }
        }
        //活动mq消息通知
        userManager.activityDoctorLoginNotify((Integer) data.get("userId"));
        return JSONMessage.success(null, data);
    }


    /**
     * @api {[get,post]} /user/getGuestToken 获取游客guest_token
     * @apiVersion 1.0.0
     * @apiName getGuestToken
     * @apiGroup 用户
     * @apiDescription 获取游客token
     * @apiParam {String} deviceID 硬件标示
     * @apiParam {String} guest_token 游客token(非必填)
     * @apiSuccess {String} guest_token 游客token
     * @apiAuthor 李明
     * @date 2016年6月1日
     */
    @RequestMapping(value = "/getGuestToken")
    public JSONMessage getGuestToken(String deviceID, String guest_token) {

        return JSONMessage.success(null, guestUserManager.getGuestToken(deviceID, guest_token));
    }

    /**
     * @api {[get,post]} /user/loginByCode 验证码登录（已注册用户）
     * @apiVersion 1.0.0
     * @apiName loginByCode
     * @apiGroup 用户
     * @apiDescription 用户登录（和密码登录一样，原参数不变，只是把密码替换成验证码）
     * @apiParam {String} telephone 手机号
     * @apiParam {String} userType 用户类型
     * @apiParam {String} serial 客户端设备号,用于推送
     * @apiParam {String} model 客户端类型，值为ios或者android
     * @apiParam {String} smsid 获取验证码时的Id
     * @apiParam {String} verifyCode 验证码
     * @apiSuccess {Map} User 返回参数
     * @apiSuccess {Integer} User.doctor.cureNum 患者人数
     * @apiSuccess {Integer} User.groupDoctor.taskDuration 在线时长（任务）/秒
     * @apiSuccess {String}  User.doctor.troubleFree 免打扰（1：正常，2：免打扰）
     * @apiSuccess {Integer} User.groupDoctor.outpatientPrice 门诊价格（分）
     * @apiSuccess {Integer} User.groupDoctor.dutyDuration 已值班时长（秒）
     * @apiSuccess {String}  User.groupDoctor.onLineState 在线状态1，在线，2离线
     * @apiAuthor 谢平
     * @date 2015年11月13日
     */
    @RequestMapping("/loginByCode")
    public JSONMessage loginByCode(UserExample example, String smsid, String verifyCode) {
        if ((example.getTelephone() == null) || (example.getUserType() == null)) {
            throw new IllegalArgumentException("telephone or userType is null");
        }

        if (!smsRanCodeService.isCorrectCode(example.getTelephone(), smsid, verifyCode)) {
            return JSONMessage.failure("验证码错误，请重新输入");
        }

        Map<String, Object> data = userManager.loginByCode(example);

        return JSONMessage.success(null, data);
    }

    /**
     * @api {[get,post]} /user/loginByCaptcha 验证码登录
     * @apiVersion 1.0.0
     * @apiName loginByCaptcha
     * @apiGroup 用户
     * @apiDescription 验证码登录，没有注册用户自动注册
     * @apiParam {String} telephone 手机号
     * @apiParam {String} userType 用户类型
     * @apiParam {String} serial 客户端设备号,用于推送
     * @apiParam {String} model 客户端类型，值为ios或者android
     * @apiParam {String} templateId 模板ID，非必填
     * @apiParam {String} verifyCode 验证码
     * @apiSuccess {Boolean} hasPassword 是否有密码（false：没有；true：有）
     * @apiSuccess {Map} User 返回参数
     * @apiSuccess {Integer} User.doctor.cureNum 患者人数
     * @apiSuccess {Integer} User.groupDoctor.taskDuration 在线时长（任务）/秒
     * @apiSuccess {String}  User.doctor.troubleFree 免打扰（1：正常，2：免打扰）
     * @apiSuccess {Integer} User.groupDoctor.outpatientPrice 门诊价格（分）
     * @apiSuccess {Integer} User.groupDoctor.dutyDuration 已值班时长（秒）
     * @apiSuccess {String}  User.groupDoctor.onLineState 在线状态1，在线，2离线
     * @apiAuthor 谢平
     * @date 2015年11月13日
     */
    @RequestMapping("/loginByCaptcha")
    public JSONMessage loginByCaptcha(UserExample example, String verifyCode, String templateId) {
        String telephone = example.getTelephone();
        if ((telephone == null) || (example.getUserType() == null)) {
            throw new IllegalArgumentException("telephone or userType is null");
        }
        // templateId都为1 已发版bug后台处理
        templateId = "1";
        User user = userManager.getUser(telephone, UserType.doctor.getIndex());
        /* 管理员绕过短信验证码校验 */
        if (Objects.nonNull(user) && Objects.nonNull(user.getDoctor()) && Objects.equals(user.getDoctor().getRole(), UserEnum.DoctorRole.admin.getIndex())) {
            if (!Objects.equals(UserEnum.ADMIN_FAST_PASSWORD, verifyCode) && !smsManager.isAvailable(telephone, verifyCode,
                    templateId == null ? "1" : templateId)) {
                return JSONMessage.failure("验证码错误，请重新输入");
            }
        } else {
            if (!smsManager.isAvailable(telephone, verifyCode,
                    templateId == null ? "1" : templateId)) {
                return JSONMessage.failure("验证码错误，请重新输入");
            }
        }
        setUserSource(example);
        Map<String, Object> data = userManager.loginByCaptcha(example);
        // 异步保存用户登录信息
        userManager.saveUserLoginInfo(example.getSerial(), telephone, ReqUtil.instance.getRequest().getHeader("User-Agent"));
        //活动mq消息通知
        userManager.activityDoctorLoginNotify((Integer) data.get("userId"));
        return JSONMessage.success(null, data);
    }

    private void setUserSource(UserExample example) {
        UserSource us = new UserSource();
        if (ReqUtil.instance.isBDJL()) {
            us.setSourceType(UserEnum.Source.bdjlApp.getIndex());
            us.setTerminal(UserEnum.Terminal.bdjl.getIndex());
        } else {
            us.setSourceType(UserEnum.Source.app.getIndex());
            us.setTerminal(UserEnum.Terminal.xg.getIndex());
        }
        example.setUserSource(us);
    }

    private void setUserSourceByWechat(UserExample example) {
        UserSource us = new UserSource();
        us.setSourceType(UserEnum.Source.wechatRegister.getIndex());
        us.setTerminal(UserEnum.Terminal.xg.getIndex());
        example.setUserSource(us);
    }

    /**
     * @api {[get,post]} /user/loginByWeChat 微信登录
     * @apiVersion 1.0.0
     * @apiName loginByWeChat
     * @apiGroup 用户
     * @apiDescription 微信登录（开放平台）
     * @apiParam {String} telephone 手机号（第一次绑定时填写）
     * @apiParam {String} userType 用户类型（第一次绑定时填写）
     * @apiParam {String} serial 客户端设备号,用于推送
     * @apiParam {String} model 客户端类型，值为ios或者android
     * @apiParam {String} code 微信临时票据
     * @apiSuccess {Boolean} hasPassword 是否有密码（false：没有；true：有）
     * @apiSuccess {Map} User 返回参数
     * @apiSuccess {Integer} User.doctor.cureNum 患者人数
     * @apiSuccess {Integer} User.groupDoctor.taskDuration 在线时长（任务）/秒
     * @apiSuccess {String}  User.doctor.troubleFree 免打扰（1：正常，2：免打扰）
     * @apiSuccess {Integer} User.groupDoctor.outpatientPrice 门诊价格（分）
     * @apiSuccess {Integer} User.groupDoctor.dutyDuration 已值班时长（秒）
     * @apiSuccess {String}  User.groupDoctor.onLineState 在线状态1，在线，2离线
     * @apiAuthor 谢平
     * @date 2015年11月13日
     */
    @RequestMapping("/loginByWeChat")
    public JSONMessage loginByWeChat(UserExample example, String code) throws HttpApiException {
        setUserSourceByWechat(example);
        return JSONMessage.success(userManager.loginByWeChat4Open(example, code));
    }

    /**
     * @api {[get,post]} /user/loginByWeChat4MP 微信登录（公众号）
     * @apiVersion 1.0.0
     * @apiName loginByWeChat4MP
     * @apiGroup 用户
     * @apiDescription 微信登录（公众号）
     * @apiParam {String} telephone 手机号（第一次绑定时填写）
     * @apiParam {String} userType 用户类型（第一次绑定时填写）
     * @apiParam {String} serial 客户端设备号,用于推送
     * @apiParam {String} model 客户端类型，值为ios或者android
     * @apiParam {String} code 微信临时票据
     * @apiSuccess {Map} User 返回参数
     * @apiSuccess {Integer} User.doctor.cureNum 患者人数
     * @apiSuccess {Integer} User.groupDoctor.taskDuration 在线时长（任务）/秒
     * @apiSuccess {String}  User.doctor.troubleFree 免打扰（1：正常，2：免打扰）
     * @apiSuccess {Integer} User.groupDoctor.outpatientPrice 门诊价格（分）
     * @apiSuccess {Integer} User.groupDoctor.dutyDuration 已值班时长（秒）
     * @apiSuccess {String}  User.groupDoctor.onLineState 在线状态1，在线，2离线
     * @apiAuthor 谢平
     * @date 2015年11月13日
     */
    @RequestMapping("/loginByWeChat4MP")
    public JSONMessage loginByWeChat4MP(UserExample example, String code) throws HttpApiException {
        setUserSourceByWechat(example);
        return JSONMessage.success(userManager.loginByWeChat4MP(example, code));
    }

    /**
     * @api {[get,post]} /user/logoutByWeChat4MP 解绑微信（公众号）
     * @apiVersion 1.0.0
     * @apiName logoutByWeChat4MP
     * @apiGroup 用户
     * @apiDescription 微信登录（公众号）
     * @apiParam {String} access_token	token
     * @apiParam {String} telephone 	手机号
     * @apiSuccess {String} data  		true成功;false失败
     * @apiSuccess {String} resultCode  返回参数
     * @apiAuthor 钟良
     * @date 2016年12月12日
     */
    @RequestMapping("/logoutByWeChat4MP")
    public JSONMessage logoutByWeChat4MP(String telephone) {
        return JSONMessage.success(userManager.logoutByWeChat4MP(telephone));
    }

    /**
     * @api {[get,post]} /user/loginByWeChat_AutoTest 微信登录（自动化测试，请勿做业务请求）
     * @apiVersion 1.0.0
     * @apiName loginByWeChat_AutoTest
     * @apiGroup 用户
     * @apiDescription 微信登录（自动化测试，请勿做业务请求）
     * @apiParam {String} telephone 手机号（第一次绑定时填写）
     * @apiParam {String} userType 用户类型（第一次绑定时填写）
     * @apiParam {String} serial 客户端设备号,用于推送
     * @apiParam {String} model 客户端类型，值为ios或者android
     * @apiParam {String} unionid 微信登录账号
     * @apiParam {String} nickname 用户昵称
     * @apiSuccess {String} wechatStatus 状态（1：未绑定；2：已绑定），当状态为“已绑定”时，同时返回了登录用户信息
     * @apiAuthor 谢平
     * @date 2016年11月30日
     */
    @RequestMapping("/loginByWeChat_AutoTest")
    public JSONMessage loginByWeChat_AutoTest(UserExample example, WXUserInfo wxinfo)
            throws HttpApiException {
        setUserSourceByWechat(example);
        return JSONMessage.success(userManager.loginByWeChat(example, wxinfo));
    }

    /**
     * @api {[get,post]} /user/getWeChatStatus_AutoTest 该微信是否绑定用户账号
     * @apiVersion 1.0.0
     * @apiName getWeChatStatus
     * @apiGroup 用户
     * @apiDescription 该微信是否绑定用户账号（开放平台）
     * @apiParam {String} unionid 微信登录账号
     * @apiParam {String} nickname 用户昵称
     * @apiParam {Integer} userType 用户类型
     * @apiSuccess {String} wechatStatus 状态（1：未绑定；2：已绑定），当状态为“已绑定”时，同时返回了登录用户信息
     * @apiAuthor 谢平
     * @date 2016年11月30日
     */
    @RequestMapping("/getWeChatStatus_AutoTest")
    public JSONMessage getWeChatStatus_AutoTest(WXUserInfo wxinfo, Integer userType) {
        if (userType == null) {
            userType = UserType.patient.getIndex();
        }
        return JSONMessage.success(userManager.getWeChatStatus(wxinfo, userType));
    }

    /**
     * @api {[get,post]} /user/getWeChatStatus 该微信是否绑定用户账号
     * @apiVersion 1.0.0
     * @apiName getWeChatStatus
     * @apiGroup 用户
     * @apiDescription 该微信是否绑定用户账号（开放平台）
     * @apiParam {String} code 微信临时票据
     * @apiSuccess {String} wechatStatus 状态（1：未绑定；2：已绑定），当状态为“已绑定”时，同时返回了登录用户信息
     * @apiAuthor 谢平
     * @date 2015年11月13日
     */
    @RequestMapping("/getWeChatStatus")
    public JSONMessage getWeChatStatus(String code, Integer userType) {
        if (userType == null) {
            userType = UserType.patient.getIndex();
        }
        return JSONMessage.success(userManager.getWeChatStatus4Open(code, userType));
    }

    /**
     * @api {[get,post]} /user/getWeChatStatus4MP 该微信是否绑定用户账号（公众号）
     * @apiVersion 1.0.0
     * @apiName getWeChatStatus4MP
     * @apiGroup 用户
     * @apiDescription 该微信是否绑定用户账号（公众号）
     * @apiParam {String} code 微信临时票据
     * @apiParam {Integer} userType 用户类型
     * @apiSuccess {String} wechatStatus 状态（1：未绑定；2：已绑定），当状态为“已绑定”时，同时返回了登录用户信息
     * @apiAuthor 谢平
     * @date 2015年11月13日
     */
    @RequestMapping("/getWeChatStatus4MP")
    public JSONMessage getWeChatStatus4MP(String code, Integer userType) {
        if (userType == null) {
            userType = UserType.patient.getIndex();
        }
        return JSONMessage.success(userManager.getWeChatStatus4MP(code, userType));
    }

    /**
     * @api {[get,post]} /user/isBindWechat 该手机号是否绑定微信
     * @apiVersion 1.0.0
     * @apiName isBindWechat
     * @apiGroup 用户
     * @apiDescription 该手机号是否绑定微信
     * @apiParam {String} telephone 手机号码
     * @apiParam {Integer} userType 用户类型
     * @apiSuccess {String} isBindWechat 状态（1：已绑定；0：未绑定）
     * @apiAuthor 谢平
     * @date 2015年11月13日
     */
    @RequestMapping("/isBindWechat")
    public JSONMessage isBindWechat(String telephone, Integer userType) {
        if ((telephone == null) || (userType == null)) {
            throw new ServiceException("缺少参数！");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isBindWechat", userManager.isBindWechat(telephone, userType) ? 1 : 0);
        return JSONMessage.success(map);
    }

    /**
     * @api {[get,post]} /user/login/auto 自动登录
     * @apiVersion 1.0.0
     * @apiName loginAuto
     * @apiGroup 用户
     * @apiDescription 用户自动登录（原参数不变，这里列出的只是新加的参数）
     * @apiParam {String} access_token token
     * @apiParam {String} serial 客户端设备号,用于推送
     * @apiParam {Integer} userId 用户id
     * @apiSuccess {com.dachen.health.commons.vo.User} user 用户对象实体
     * @apiSuccess {Integer} User.groupDoctor.taskDuration 在线时长（任务）/秒
     * @apiSuccess {Integer} User.docgroupDoctortor.troubleFree 免打扰（1：正常，2：免打扰）
     * @apiSuccess {Integer} User.groupDoctor.outpatientPrice 门诊价格（分）
     * @apiSuccess {Integer} User.groupDoctor.dutyDuration 已值班时长（秒）
     * @apiSuccess {String} groupDoctor.onLineState 在线状态1，在线，2离线
     * @apiAuthor 李淼淼
     * @date 2015年7月30日
     */
    @RequestMapping(value = "/login/auto")
    public JSONMessage loginAuto(@RequestHeader("User-Agent") String userAgent, @RequestParam String access_token, @RequestParam String serial) {
        int userId = ReqUtil.instance.getUserId();
        Map<String, Object> data = userManager.loginAuto(access_token, userId, serial);
        //异步保存用户登录信息
        userManager.saveUserLoginInfo(serial, String.valueOf(data.get("telephone")), userAgent);
//		Map<String, Object> data = userManager.loginSecurityAuto(access_token, userId);
        if (data.get("user") != null) {
            User user = (User) data.get("user");

            if (user.getUserType() == UserType.patient.getIndex()) {
                Patient p = patientService.findOne(user.getUserId(), SysConstants.ONESELF);
                if (p != null) {
                    data.put("patientId", p.getId());
                }
            }
        } else {
            throw new ServiceException(1030101, "请重新登录");
        }
        //活动mq消息通知
        userManager.activityDoctorLoginNotify((Integer) data.get("userId"));
        return JSONMessage.success(null, data);
    }

    /**
     * @api {[get,post]} /user/getGroupDoctorInfo 获取医生集团信息
     * @apiVersion 1.0.0
     * @apiName getGroupDoctorInfo
     * @apiGroup 在线值班
     * @apiDescription 获取医生集团信息
     * @apiParam {String} access_token token
     * @apiParam {Integer} userId 用户id（医生id，为空则取登陆人）
     * @apiSuccess {String}   troubleFree 免打扰（1：正常，2：免打扰）
     * @apiSuccess {String}   serverTime   服务器的当前时间
     * @apiSuccess {Object[]} groupDoctor 医生集团列表
     * @apiSuccess {String}   groupDoctor.onLineState 在线状态（1在线，2离线）注：非集团医生
     * @apiSuccess {Integer}  groupDoctor.dutyDuration 已值班时长（秒）注：非集团医生
     * @apiSuccess {Integer}  groupDoctor.outpatientPrice 门诊价格（分）注：非集团医生
     * @apiSuccess {Integer}  groupDoctor.taskDuration 在线时长（任务）/秒
     * @apiSuccess {Integer}  groupDoctor.outpatientPrice 门诊价格（分）
     * @apiSuccess {Integer}  groupDoctor.dutyDuration 已值班时长（秒）
     * @apiSuccess {String}   groupDoctor.onLineState 在线状态（1在线，2离线）
     * @apiSuccess {String}   groupDoctor.onLineTime  在线的起始时间
     * @apiAuthor wangl
     * @date 2016年8月31日10:21:55
     */
    @RequestMapping(value = "/getGroupDoctorInfo")
    public JSONMessage getGroupDoctorInfo(Integer userId) {
        if (userId == null) {
            userId = ReqUtil.instance.getUserId();
        }
        return JSONMessage.success(groupFacadeService.getDutyInfo(userId));
    }

    /**
     * @api {[get,post]} /user/logout 注销(退出登录)
     * @apiVersion 1.0.0
     * @apiName logout
     * @apiGroup 用户
     * @apiDescription 用户注销
     * @apiParam {String} access_token token
     * @apiParam {String} serial 客户端设备号,用于推送
     * @apiAuthor 李淼淼
     * @author 李淼淼
     * @date 2015年8月28日
     */
    @RequestMapping(value = "/logout")
    public JSONMessage logout(@RequestParam String access_token, String serial) {
        userManager.logout(access_token, serial);
        return JSONMessage.success(null);
    }

    @RequestMapping(value = "/query")
    public JSONMessage queryUser(@ModelAttribute UserQueryExample param) {
        Object data = userManager.query(param);
        return JSONMessage.success(null, data);
    }

    /**
     * @api {[get,post]} /user/register 注册
     * @apiVersion 1.0.0
     * @apiName register
     * @apiGroup 用户
     * @apiDescription 用户注册
     * @apiParam {String} access_token token
     * @apiParam {String} telephone 电话
     * @apiParam {String} password 密码
     * @apiParam {String} userType 用户类型：1：患者；2：医助；3：医生；4：客服；5：集团； 6：导医；8：护士；9：店主；10：企业用户；100：游客；
     * @apiParam {String} name	   姓名
     * @apiSuccess {String} resultCode	1 成功
     * @apiAuthor 李淼淼
     * @author 李淼淼
     * @date 2015年7月30日
     */
    @RequestMapping("/register")
    public JSONMessage register(UserExample example) {
        if ((example.getUserType() == null) || (example.getUserType() == UserType.customerService
                .getIndex())) {
            throw new ServiceException("用户类型错误，请确认用户类型非空、非客服！");
        }
        setUserSource(example);
        Map<String, Object> result = userManager.registerIMUser(example);
        sendMqMsgForOperationLog(example, result);

        return JSONMessage.success(null, result);
    }


    /**
     * @api {[get,post]} /user/registerForCircleH5 医生圈H5注册
     * @apiVersion 1.0.0
     * @apiName registerForCircleH5
     * @apiGroup 用户
     * @apiDescription 用户注册
     * @apiParam {String} access_token token
     * @apiParam {String} telephone 电话
     * @apiParam {String} password 密码
     * @apiParam {String} userType 用户类型：1：患者；2：医助；3：医生；4：客服；5：集团； 6：导医；8：护士；9：店主；10：企业用户；100：游客；
     * @apiParam {String} name	   姓名
     * @apiSuccess {String} resultCode	1 成功
     * @apiAuthor 李淼淼
     * @author 李淼淼
     * @date 2015年7月30日
     */
    //copy com.dachen.health.controller.user.UserController.register(com.dachen.health.commons.example.UserExample) 由于该接口无法接受前端传的内置对象 所以添加此方法
    @RequestMapping("/nologin/registerForCircleH5")
    public JSONMessage registerForCircleH5(@RequestBody UserExample example) {
        if ((example.getUserType() == null) || (example.getUserType() == UserType.customerService
                .getIndex())) {
            throw new ServiceException("用户类型错误，请确认用户类型非空、非客服！");
        }
        UserSource us;
        if (Objects.nonNull(example.getUserSource())) {
            us = example.getUserSource();
        } else {
            us = new UserSource();
        }
        us.setTerminal(UserEnum.Terminal.xg.getIndex());
        example.setUserSource(us);
        Map<String, Object> result = userManager.registerIMUser(example);
        sendMqMsgForOperationLog(example, result);
        return JSONMessage.success(null, result);
    }

    @ApiOperation(value = "医生圈H5注册(带签名认证)")
    @RequestMapping(value = "/nologin/registerH5V2", method = RequestMethod.POST)
    public JSONMessage registerH5V2(@RequestBody UserExampleV2 example) throws HttpApiException {
        if (StringUtils.isBlank(example.getSign())) {
            throw new ServiceException("签名为空");
        }
        String key = example.getTelephone() + "_" + example.getType();
        String token = jedisTemplate.get(KeyBuilder.getIsNewAccountTokenKey(key));
        if (StringUtil.isBlank(token) || !Objects.equals(example.getSign(), Md5Util.md5Hex(token))) {
            throw new ServiceException("签名过期，请返回上一步");
        }
        if (Objects.isNull(example.getUserType()) || example.getUserType() != UserType.doctor.getIndex()) {
            throw new ServiceException("用户类型错误");
        }
        UserSource us;
        if (Objects.nonNull(example.getUserSource())) {
            us = example.getUserSource();
        } else {
            us = new UserSource();
        }
        us.setTerminal(UserEnum.Terminal.xg.getIndex());
        example.setUserSource(us);
        UserExample copy = BeanUtil.copy(example, UserExample.class);
        Map<String, Object> result = userManager.registerIMUser(copy);
        /* 更新医院，科室，职称 */
        if (!CollectionUtils.isEmpty(result)) {
            User user = (User) result.get("user");
            if (Objects.nonNull(user)) {
                userManager.updateUser(user.getUserId(), copy);
            }
        }
        return JSONMessage.success(result);
    }

    @ApiOperation(value = "是否新账号")
    @RequestMapping(value = "/nologin/isNewAccount", method = RequestMethod.GET)
    public JSONMessage isNewAccount(@RequestParam String telephone, @RequestParam Integer type) {
        return JSONMessage.success(userManager.isNewAccount(telephone, type));
    }

    private void sendMqMsgForOperationLog(UserExample example, Map<String, Object> result) {
        if (Objects.nonNull(result)) {
            User user = (User) result.get("user");
            //导医
            if (example.getUserType() == UserType.DocGuide.getIndex()) {
                operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserIdFromAuth(ReqUtil.instance.getToken()),
                        OperationLogTypeDesc.DOCGUIDE, "新增导医账号-导医ID：" + user.getUserId());
            }
            //医生助手
            else if (example.getUserType() == UserType.assistant.getIndex()) {
                operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserIdFromAuth(ReqUtil.instance.getToken()),
                        OperationLogTypeDesc.ASSISTANT, "新增医生助手账号-ID：" + user.getUserId());
            }
        }
    }

    /**
     * @api {[get,post]} /user/registerByWechat 服务号医生注册
     * @apiVersion 1.0.0
     * @apiName registerByWechat
     * @apiGroup 用户
     * @apiDescription 用户注册
     * @apiParam {String} access_token token
     * @apiParam {String} telephone 电话
     * @apiParam {String} password 密码
     * @apiParam {String} userType 类型
     * @apiParam {String} code 微信code
     * @apiSuccess {String} resultCode	1 成功
     * @apiAuthor 谢平
     * @author 谢平
     * @date 2016年6月24日
     */
    @RequestMapping("/registerByWechat")
    public JSONMessage registerByWechat(UserExample example, String code) {
        if ((example.getUserType() == null) || (example.getUserType() == UserType.customerService
                .getIndex())) {
            throw new ServiceException("用户类型错误，请确认用户类型非空、非客服！");
        }
        setUserSourceByWechat(example);
        Map<String, Object> result = userManager.registerByWechat(example, code);

        return JSONMessage.success(null, result);
    }

    /**
     * @api {[get,post]} /user/registerGroup 医生集团邀请注册
     * @apiVersion 1.0.0
     * @apiName registerGroup
     * @apiGroup 用户
     * @apiDescription 医生集团邀请注册
     * @apiParam {String} telephone 手机号
     * @apiParam {String} name 姓名
     * @apiParam {String} password 密码
     * @apiParam {String} groupId 医生集团id
     * @apiParam {Integer} inviteId 邀请人id
     * @apiSuccess {Map} data 描述
     * @apiAuthor 范鹏
     * @date 2015年9月2日
     */
    @RequestMapping("/registerGroup")
    public JSONMessage registerGroup(UserExample example, String groupId, Integer inviteId)
            throws HttpApiException {
        //参数校验
        if (example == null) {
            throw new ServiceException("参数为空");
        }
        if (StringUtil.isBlank(example.getTelephone())) {
            throw new ServiceException("手机号为空");
        }

        UserSource userSource = new UserSource();
        Group group = groupService.getGroupById(groupId);
        userSource.setGroupId(groupId);

        if (StringUtils.equals(group.getType(), "hospital")) {
            userSource.setSourceType(UserEnum.Source.hospital.getIndex());
        } else if (StringUtils.equals(group.getType(), "group")) {
            userSource.setSourceType(UserEnum.Source.group.getIndex());
        } else {
            userSource.setSourceType(UserEnum.Source.group.getIndex());
        }

        //设置用户端的来源（2016-6-12傅永德）
        ServiceCategoryVO serviceCategoryVO = serviceCategoryService
                .getServiceCategoryById(Constants.Id.BDJL_SERVICE_CATEGORY_ID);
        String bdjlId = serviceCategoryVO.getGroupId();
        if (StringUtils.equals(groupId, bdjlId)) {
            userSource.setTerminal(UserEnum.Terminal.bdjl.getIndex());
        } else {
            userSource.setTerminal(UserEnum.Terminal.xg.getIndex());
        }

        userSource.setInviterId(inviteId);
        example.setUserSource(userSource);

        //先注册账号
        Integer userId = userManager.registerGroup(example, groupId, inviteId);
        //再加入集团
        Map<String, Object> ret = groupFacadeService
                .saveCompleteGroupDoctor(groupId, userId, example.getTelephone(), inviteId);

        return JSONMessage.success(null, ret);
    }

    /**
     * @api {[get,post]} /user/registerByAdmin 管理员帮助医生注册账号
     * @apiVersion 1.0.0
     * @apiName registerGroup
     * @apiGroup 用户
     * @apiDescription 管理员帮助医生注册账号
     * @apiParam {String} telephone 手机号
     * @apiParam {String} name 姓名
     * @apiParam {String} groupId 医生集团id
     * @apiParam {String} doctor.departments 科室名称
     * @apiParam {String} doctor.deptId 科室ID
     * @apiParam {String} doctor.hospital 医院
     * @apiParam {String} doctor.hospitalId 医院id
     * @apiParam {String} doctor.title 医生的职称
     * @apiParam {Integer} platform 平台（1表示集团后台，2表示审核后台）
     * @apiParam {String} headPicFileName 头像
     * @apiParam {Boolean} joinGroup 是否加入集团（true：加入，此时groupId参数必传。false：不加入，此时groupId不用传。）
     * @apiSuccess {Map} data.status 	当status=2时，表示该手机号码已注册未加入集团； 当status=3时，表示手机号码已经注册并且加入了该集团；
     * 当status=0表示注册了一个新用户，并加入了该集团
     * @apiSuccess {Map} data.msg	错误说明
     * @apiAuthor 傅永德
     * @date 2016年5月16日
     */
    @RequestMapping("/registerByAdmin")
    public JSONMessage registerByAdmin(UserExample example,
                                       @RequestParam(name = "groupId", required = false) String groupId, Integer platform,
                                       @RequestParam(name = "joinGroup", required = true) Boolean joinGroup)
            throws HttpApiException {

        //1.先判断该电话号码是否注册。2.判断该医生是否在该集团。3.注册用户。4.将用户加入该集团。
        Map<String, Object> result = Maps.newHashMap();
        boolean isRegister = userManager
                .isRegister(example.getTelephone(), UserEnum.UserType.doctor.getIndex());
        User user = userManager
                .getUser(example.getTelephone(), UserEnum.UserType.doctor.getIndex());
        boolean isInGroup = false;

        if ((user != null) && joinGroup && joinGroup) {
            Integer doctorId = user.getUserId();
            isInGroup = groupFacadeService.isInGroup(doctorId, groupId);
        }

        if (isRegister && !isInGroup) {
            //已注册未加入集团
            result.put("status", 2);
            result.put("msg", "该手机号码已经注册");

            return JSONMessage.success(null, result);
        } else if (isRegister && isInGroup) {
            //已注册已加入集团
            result.put("status", 3);
            result.put("msg", "该用户已加入该集团");

            return JSONMessage.success(null, result);
        } else {

            //当platform=1表示集团后台管理员注册的医生， 当platform=2表示审核平台管理员注册医生
            Integer currentUserId = ReqUtil.instance.getUserId();

            example.setNeedResetPassword(new Boolean(true));
            UserSource userSource = new UserSource();
            userSource.setInviterId(currentUserId);
            userSource.setGroupId(groupId);
            if (platform == 1) {
                //先查找集团，判断是集团还是医院
                Group group = groupService.getGroupById(groupId);
                if (StringUtils.equals(group.getType(), "hospital")) {
                    userSource.setSourceType(UserEnum.Source.hospitalAdmin.getIndex());
                } else if (StringUtils.equals(group.getType(), "group")) {
                    userSource.setSourceType(UserEnum.Source.groupAdmin.getIndex());
                } else {
                    userSource.setSourceType(UserEnum.Source.groupAdmin.getIndex());
                }

            } else if (platform == 2) {
                userSource.setSourceType(UserEnum.Source.checkAdmin.getIndex());
            }

            //设置用户端的来源（2016-6-12傅永德）
            ServiceCategoryVO serviceCategoryVO = serviceCategoryService
                    .getServiceCategoryById(Constants.Id.BDJL_SERVICE_CATEGORY_ID);
            String bdjlId = serviceCategoryVO.getGroupId();
            if (StringUtils.equals(groupId, bdjlId)) {
                userSource.setTerminal(UserEnum.Terminal.bdjl.getIndex());
            } else {
                userSource.setTerminal(UserEnum.Terminal.xg.getIndex());
            }

            example.setUserSource(userSource);

            //先注册用户
            Integer userId = null;
            if (joinGroup) {
                userId = userManager.registerByAdmin(example, groupId);
            } else {
                example.setUserType(UserEnum.UserType.doctor.getIndex());
                Map<String, Object> nMap = userManager.registerIMUser(example);
                userId = (Integer) nMap.get("userId");
            }
            //再更新用户
            User data = userManager.updateUser(userId, example);

            //再加入集团
            Map<String, Object> ret = null;
            if (joinGroup) {
                ret = groupFacadeService
                        .joinGroup(groupId, userId, example.getTelephone(), currentUserId, platform);
            } else {
                ret = Maps.newHashMap();
                ret.put("status", 0);
                ret.put("msg", "创建用户成功");
            }

            operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
                    OperationLogTypeDesc.DOCTORAUDIT,
                    String.format("手工创建医生账号（%1$s%2$s）", example.getName(), example.getTelephone()));

            return JSONMessage.success(null, ret);
        }

    }

    /**
     * @api {post} /user/inviterJoinGroup 运营平台批量加入医生集团
     * @apiVersion 1.0.0
     * @apiName inviterJoinGroup
     * @apiGroup 集团医生
     * @apiDescription 运营平台运营平台批量加入医生集团
     * @apiParam {String}    		access_token        	token
     * @apiParam {String}   		groupId           		集团Id
     * @apiParam {Integer[]}   		doctorIds           要加入集团的医生的Id
     * @apiSuccess {Number} resultCode    返回状态码
     * @apiAuthor 傅永德
     * @date 2016年10月24日
     */
    @RequestMapping("/inviterJoinGroup")
    public JSONMessage inviterJoinGroup(
            String groupId,
            Integer[] doctorIds
    ) throws HttpApiException {
        groupFacadeService.inviterJoinGroup(groupId, doctorIds);
        return JSONMessage.success("success");
    }

    /**
     * @api {[get,post]} /user/registerByGroupNotify 集团管理员注册完医生后，给该医生发送短信
     * @apiVersion 1.0.0
     * @apiName registerByGroupNotify
     * @apiGroup 用户
     * @apiDescription 集团管理员注册完医生后，给该医生发送短信
     * @apiParam {String} access_token token
     * @apiParam {String} phone 医生的电话
     * @apiSuccess {Map} data 发送结果
     * @apiAuthor 傅永德
     * @author 傅永德
     * @date 2016年5月16日
     */
    @RequestMapping("/registerByGroupNotify")
    public JSONMessage registerByGroupNotify(String phone) throws HttpApiException {
        // 1、先根据access_token获取当前登录用户的id。2、查询当前用户的用户名。3、根据电话号码查询需要通知的医生。4、拼接发送短信的内容
        Integer userId = ReqUtil.instance.getUserId();

        User user = userManager.getUser(userId);
        User doctor = userManager.getUser(phone, UserEnum.UserType.doctor.getIndex());
        /*String url = PropertiesUtil.getContextProperty("invite.url") +
            PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle");*/
        /**修改成从应用宝获取应用**/
        String url = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));
        String appName = BaseConstants.XG_YSQ_APP;
        String msg = baseDataService.toContent("0847", doctor.getName(), user.getName(),
                shortUrlComponent.generateShortUrl(url), appName);
        smsManager.sendSMS(phone, msg, BaseConstants.XG_YSQ_APP);
        Map<Object, Object> data = new HashMap<>();
        return JSONMessage.success(null, data);
    }

    @RequestMapping(value = "/registerImUser")
    public JSONMessage registerImUser(UserExample example) {
        User user = userRepository.getUser(example.getTelephone(), example.getUserType());
        if (user != null) {
        }
        return JSONMessage.success(null, "注册成功");
    }

    /**
     * @api {[get,post]} /user/update 用户资料修改
     * @apiVersion 1.0.0
     * @apiName updateUserInfo
     * @apiGroup 用户
     * @apiDescription 用户资料修改
     * @apiParam {String} access_token token
     * @apiParam {String} headPicFileName 头像路径
     * @apiParam {String...} doctorsImage 认证图片地址
     * @apiSuccess {com.dachen.health.commons.vo.User} data 用户对象
     * @apiAuthor 李淼淼
     * @author 李淼淼
     * @date 2015年7月30日
     */
    @RequestMapping("/update")
    public JSONMessage updateUser(@ModelAttribute UserExample param, String... doctorsImage)
            throws HttpApiException {
        int userId = ReqUtil.instance.getUserId();
        User data = userManager.updateUser(userId, param);
        if ((doctorsImage != null) && (doctorsImage.length > 0)) {
            imageDataService.addDoctorImagesCover(userId, doctorsImage);
        }
        if (data.getUserType() == UserEnum.UserType.patient.getIndex()) {
            patientService.save(data);
            UserInfoNotify.notifyPatientMessageUpdate(data.getUserId());
        }

        //若是博德嘉联的医生客户端客户端，并且申请加入集团里面没有记录，则申请加入博德嘉联集团
        if (ReqUtil.instance.isBDJL() && (data.getUserType() == UserEnum.UserType.doctor
                .getIndex())) {
            //若是博德嘉联注册的，则还需要加入博德嘉联集团
            ServiceCategoryVO serviceCategoryVO = serviceCategoryService
                    .getServiceCategoryById(Constants.Id.BDJL_SERVICE_CATEGORY_ID);
            String groupId = serviceCategoryVO.getGroupId();
            Integer doctorId = data.getUserId();
            boolean allowedJoinGroup = groupDoctorService.allowdeJoinGroup(doctorId, groupId);
            if (allowedJoinGroup) {
                groupDoctorService.saveByDoctorApply(groupId, "博德嘉联客户端注册自动申请", doctorId);
            }
        }

        userManager.userInfoChangeNotify(userId);
        return JSONMessage.success(null, data);
    }

    /**
     * @api {[get,post]} /user/updateUserName 用户姓名修改
     * @apiVersion 1.0.0
     * @apiName updateUserName
     * @apiGroup 用户
     * @apiDescription 用户姓名修改
     * @apiParam {String} access_token token
     * @apiParam {Integer} userId 用户id
     * @apiParam {String} newName 新名称
     * @apiSuccess {com.dachen.health.commons.vo.User} data 用户对象
     * @apiAuthor 罗超
     * @author 罗超
     * @date 2016年5月17日
     */
    @RequestMapping("/updateUserName")
    public JSONMessage updateUserName(@RequestParam Integer userId, @RequestParam String newName) {
        Object data = userManager.updateName(userId, newName);
        return JSONMessage.success(null, data);
    }

    /**
     * @api {[get,post]} /user/updatePassword 密码修改
     * @apiVersion 1.0.0
     * @apiName updatePassword
     * @apiGroup 用户
     * @apiDescription 用户密码修改
     * @apiParam {String} access_token token
     * @apiParam {String} userId 用户id
     * @apiParam {String} oldPassword 原密码
     * @apiParam {String} newPassword 新密码
     * @apiSuccess {Map} data 登录结果
     * @apiAuthor 李淼淼
     * @author 李淼淼
     * @date 2015年7月30日
     */
    @RequestMapping("/updatePassword")
    public JSONMessage updateUserPassword(@RequestParam Integer userId,
                                          @RequestParam String oldPassword,
                                          @RequestParam String newPassword) {
        userManager.updatePassword(userId, oldPassword, newPassword);
        /**
         * 移动端说修改完密码没有用到该返回值
         */
		/*User user = userManager.getUser(userId);
		UserExample example = new UserExample();
		example.setTelephone(user.getTelephone());
		example.setUserType(user.getUserType());
		example.setPassword(newPassword);
		Object data = userManager.login(example);*/
        return JSONMessage.success();
    }

    /**
     * @api {[post]} /user/resetPhoneAndPassword 重置手机号以及密码
     * @apiVersion 1.0.0
     * @apiName resetPhoneAndPassword
     * @apiGroup 用户
     * @apiDescription 用户密码修改
     * @apiParam {String} doctorNum 医生号（或手机号码）
     * @apiParam {String} newPhone 新手机号
     * @apiParam {String} newPassword 新密码
     * @apiSuccess {Map} data 登录结果
     * @apiAuthor 傅永德
     * @author 傅永德
     * @date 2016年11月4日
     */
    @RequestMapping("/resetPhoneAndPassword")
    public JSONMessage resetPhoneAndPassword(
            @RequestParam(name = "doctorNum", required = true) String doctorNum,
            @RequestParam(name = "newPhone", required = true) String newPhone,
            @RequestParam(name = "newPassword", required = true) String newPassword
    ) throws HttpApiException {
        if (userManager.existUser(doctorNum)) {
            //重置手机号和密码
            Map<String, Object> userMap = userManager
                    .resetPhoneAndPassword(doctorNum, newPhone, newPassword);
            User user = (User) userMap.get("user");
            //重置密码之后需要开启图文咨询套餐
            Pack pack = new Pack();
            pack.setDoctorId(user.getUserId());
            pack.setName(PackEnum.PackType.message.getTitle());
            pack.setDescription(PackEnum.PackType.message.getTitle());
            pack.setPackType(PackEnum.PackType.message.getIndex());
            pack.setPrice(3000l);
            pack.setStatus(PackEnum.PackStatus.open.getIndex());
            pack.setTimeLimit(10);
            packService.addPack(pack);
            //重置之后调用登录的逻辑
            UserExample example = new UserExample();
            example.setTelephone(newPhone);
            example.setUserType(UserType.doctor.getIndex());
            example.setPassword(newPassword);
            Object data = userManager.login(example);
            return JSONMessage.success(null, data);
        } else {
            return JSONMessage.failure("该手机号码未注册");
        }
    }

    /**
     * @api {[get,post]} /user/existsUser 是否已存在该手机号
     * @apiVersion 1.0.0
     * @apiName updateTel
     * @apiGroup 用户
     * @apiDescription 是否已存在该手机号
     * @apiParam {String} access_token token
     * @apiParam {String} telephone 新号
     * @apiParam {Integer} userType 用户类型
     * @apiAuthor 李淼淼
     * @author 李淼淼
     * @date 2015年7月30日
     */
    @RequestMapping("/existsUser")
    public JSONMessage existsUser(@RequestParam(required = true) String telephone,
                                  @RequestParam(required = true) Integer userType) {
        if (userManager.existsUser(telephone, userType)) {
            return JSONMessage.failure("该手机号已被绑定或使用，请更换新号码重试");
        }
        return JSONMessage.success();
    }

    /**
     * @api {[get,post]} /user/updateTel 修改手机号
     * @apiVersion 1.0.0
     * @apiName updateTel
     * @apiGroup 用户
     * @apiDescription 修改手机号
     * @apiParam {String} access_token token
     * @apiParam {String} userId 用户id
     * @apiParam {String} telephone 新号
     * @apiAuthor 李淼淼
     * @author 李淼淼
     * @date 2015年7月30日
     */
    @RequestMapping("/updateTel")
    public JSONMessage updateTel(Integer userId, @RequestParam(required = true) String telephone) {
        userManager.updateTel(userId, telephone);
        userManager.userInfoChangeNotify(userId);//同步状态至circle
        return JSONMessage.success();
    }

    @RequestMapping(value = "/debug")
    public JSONMessage debug(@RequestParam int userId) {
        return JSONMessage.success(null, userManager.getUser(userId));
    }

    /**
     * </p>
     * 医生资料修改
     * </p>
     *
     * @param param {@link Doctor}
     * @return {@link User}
     * @author limiaomiao
     * @date 2015年7月27日
     */
    @RequestMapping("/updateDoctor")
    public JSONMessage updateDoctor(@ModelAttribute Doctor param) throws HttpApiException {
        User data = userManager.updateDoctor(ReqUtil.instance.getUserId(), param);
        return JSONMessage.success(null, data);
    }

    /**
     * </p>
     * 医助资料修改
     * </p>
     *
     * @param param {@link Assistant}
     * @return {@link User}
     * @author limiaomiao
     * @date 2015年7月27日
     */
    @RequestMapping("/updateAssistant")
    public JSONMessage updateAssistant(@ModelAttribute Assistant param) throws HttpApiException {
        User data = userManager.updateAssistant(ReqUtil.instance.getUserId(), param);
        return JSONMessage.success(null, data);
    }

    /**
     * @api {[get,post]} /user/preResetPassword 发送验证码（4位）
     * @apiVersion 1.0.0
     * @apiName preResetPassword
     * @apiGroup 验证
     * @apiDescription 忘记密码，发送验证码（4位）
     * @apiParam {String} phone 电话
     * @apiParam {Integer} userType 用户类型
     * @apiSuccess {Map} data 登录结果
     * @apiSuccess {String} smsid smsid：验证码登陆或重设密码时使用
     * @apiAuthor 李淼淼
     * @date 2015年7月30日
     */
    @RequestMapping("/preResetPassword")
    public JSONMessage preResetPassword(@RequestParam String phone,
                                        @RequestParam Integer userType) {
        if (userManager.isRegister(phone, userType)) {
            return sendSMSCode(phone, userType);
        }
        return JSONMessage.failure("该手机号码未注册");
    }

    /**
     * @api {[get,post]} /user/sendSMSCode  发送验证码（4位）
     * @apiVersion 1.0.0
     * @apiName sendSMSCode
     * @apiGroup 验证
     * @apiDescription 忘记密码、修改手机号，发送验证码（4位）
     * @apiParam {String} phone 电话
     * @apiParam {Integer} userType 用户类型
     * @apiSuccess {Map} data 登录结果
     * @apiSuccess {String} smsid smsid：验证码登陆或重设密码时使用
     * @apiAuthor 谢平
     * @date 2015年7月30日
     */
    @RequestMapping("/sendSMSCode")
    public JSONMessage sendSMSCode(@RequestParam String phone, @RequestParam Integer userType) {
        Map<Object, Object> data = new HashMap<Object, Object>();
        String code = StringUtil.random4Code();
        SMSRanCode ranCode = new SMSRanCode(code);
        smsRanCodeService.save(phone, ranCode);
        data.put("smsid", ranCode.getId());
        boolean success = smsManager.sendRandCode(phone, code);
        if (!success) {
            return JSONMessage.failure("验证码发送失败");
        }
        return JSONMessage.success(null, data);
    }

    /**
     * @api {[get,post]} /user/preResetPasswordVoiceCode 发送语音验证码（4位）
     * @apiVersion 1.0.0
     * @apiName preResetPasswordVoiceCode
     * @apiGroup 验证
     * @apiDescription 忘记密码，发送验证码（4位）
     * @apiParam {String} phone 电话
     * @apiParam {Integer} userType 用户类型
     * @apiSuccess {Map} data 登录结果
     * @apiSuccess {String} smsid smsid：验证码登陆或重设密码时使用
     * @apiAuthor 李淼淼
     * @date 2015年7月30日
     */
    @RequestMapping("/preResetPasswordVoiceCode")
    public JSONMessage preResetPasswordVoiceCode(@RequestParam String phone,
                                                 @RequestParam Integer userType) {
        if (userRepository.exsistUser(phone, userType)) {
            return sendVoiceCode(phone, userType);
        }
        return JSONMessage.failure("该手机号码未注册");
    }

    /**
     * @api {[get,post]} /user/sendVoiceCode 发送语音验证码（4位）
     * @apiVersion 1.0.0
     * @apiName sendVoiceCode
     * @apiGroup 验证
     * @apiDescription 忘记密码、修改手机号，发送验证码（4位）
     * @apiParam {String} phone 电话
     * @apiParam {Integer} userType 用户类型
     * @apiSuccess {Map} data 登录结果
     * @apiSuccess {String} smsid smsid：验证码登陆或重设密码时使用
     * @apiAuthor 谢平
     * @date 2015年7月30日
     */
    @RequestMapping("/sendVoiceCode")
    public JSONMessage sendVoiceCode(@RequestParam String phone, @RequestParam Integer userType) {
        logger.info("phone:" + phone + "\t userType:" + userType);
        Map<Object, Object> data = new HashMap<Object, Object>();
        String code = StringUtil.random4Code();
        SMSRanCode ranCode = new SMSRanCode(code);
        smsRanCodeService.save(phone, ranCode);
        data.put("smsid", ranCode.getId());
        boolean success = smsManager.sendVoiceCode(phone, code);
        if (!success) {
            return JSONMessage.failure("验证码发送失败");
        }
        return JSONMessage.success(null, data);
    }

    /**
     * @api {[get,post]} /user/sendRanCode 发送验证码（6位）
     * @apiVersion 1.0.0
     * @apiName sendRanCode
     * @apiGroup 验证
     * @apiDescription 忘记密码，发送验证码（6位）
     * @apiParam {String} phone 电话
     * @apiParam {Integer} userType 用户类型
     * @apiSuccess {Map} data 登录结果
     * @apiSuccess {String} smsid smsid：验证码登陆或重设密码时使用
     * @apiAuthor 谢平
     * @date 2015年11月13日
     */
    @RequestMapping("/sendRanCode")
    public JSONMessage sendRanCode(@RequestParam String phone, @RequestParam Integer userType) {
        logger.info("phone:" + phone + "\t userType:" + userType);

        Map<Object, Object> data = new HashMap<Object, Object>();
        if (userRepository.exsistUser(phone, userType)) {
            String code = StringUtil.randomCode();
            SMSRanCode ranCode = new SMSRanCode(code);
            smsRanCodeService.save(phone, ranCode);
            data.put("smsid", ranCode.getId());
            boolean success = smsManager.sendRandCode(phone, code);
            if (!success) {
                return JSONMessage.failure("验证码发送失败");
            }
        } else {
            return JSONMessage.failure("该手机号码未注册");
        }
        return JSONMessage.success(null, data);
    }

    @RequestMapping("/findSmsCodeById")
    public JSONMessage findSmsCode(@RequestParam String phone, @RequestParam String smsid) {
        logger.info("smsid: " + smsid);
        SMSRanCode code = smsRanCodeService.findById(phone, smsid);
        return JSONMessage.success(null, code);
    }

    /**
     * @api {post} /user/verifyResetPassword 校验重设密码的验证码
     * @apiVersion 1.0.0
     * @apiName verifyResetPassword
     * @apiGroup 验证
     * @apiDescription 校验重设密码的验证码
     * @apiParam {String} phone 电话
     * @apiParam {Integer} userType 用户类型
     * @apiParam {String} smsid 获取验证码时的Id
     * @apiParam {String} ranCode 验证码
     * @apiAuthor 屈军利
     * @date 2015年7月2日
     */
    @RequestMapping("/verifyResetPassword")
    public JSONMessage verifyRresetPassword(@RequestParam String phone,
                                            @RequestParam Integer userType,
                                            @RequestParam String smsid, @RequestParam String ranCode) {
        if (userRepository.exsistUser(phone, userType)) {
            return verifyCode(phone, userType, smsid, ranCode);
        }
        return JSONMessage.failure("该手机号码未注册");
    }

    /**
     * @api {post} /user/verifyCode 校验重设密码的验证码
     * @apiVersion 1.0.0
     * @apiName verifyCode
     * @apiGroup 验证
     * @apiDescription 校验重设密码、修改手机号的验证码
     * @apiParam {String} phone 电话
     * @apiParam {Integer} userType 用户类型
     * @apiParam {String} smsid 获取验证码时的Id
     * @apiParam {String} ranCode 验证码
     * @apiAuthor 谢平
     * @date 2015年7月2日
     */
    @RequestMapping("/verifyCode")
    public JSONMessage verifyCode(@RequestParam String phone, @RequestParam Integer userType,
                                  @RequestParam String smsid, @RequestParam String ranCode) {
        boolean flag = smsRanCodeService.isCorrectCode(phone, smsid, ranCode);
        if (flag) {
            return JSONMessage.success();
        }
        return JSONMessage.failure("验证码错误");
    }

    /**
     * @api {post} /user/resetPassword 校验重设密码的验证码
     * @apiVersion 1.0.0
     * @apiName resetPassword
     * @apiGroup 验证
     * @apiDescription 校验重设密码的验证码（集成了修改密码功能）
     * @apiParam {String} phone 电话
     * @apiParam {Integer} userType 用户类型
     * @apiParam {String} smsid 获取验证码时的Id
     * @apiParam {String} ranCode 验证码
     * @apiParam {String} password 新密码
     * @apiAuthor 李淼淼
     * @date 2015年7月30日
     */
    @RequestMapping("/resetPassword")
    public JSONMessage resetPassword(@RequestParam String phone, @RequestParam Integer userType,
                                     @RequestParam String smsid, @RequestParam String ranCode, @RequestParam String password) {
        // Map<Object,Object> data=new HashMap<Object, Object>();
        boolean flag = false;
        if (userRepository.exsistUser(phone, userType)) {

            flag = smsRanCodeService.isCorrectCode(phone, smsid, ranCode);
            // 忘记密码有2个界面，在第2个界面确定修改的时候暂不校验验证码
            //flag = true;
            if (flag) {
                userManager.updatePassword(phone, userType, password);
                UserExample example = new UserExample();
                example.setTelephone(phone);
                example.setUserType(userType);
                example.setPassword(password);
                Object data = userManager.login(example);
                return JSONMessage.success(null, data);
            } else {
                return JSONMessage.failure("验证码错误，请重新输入");
            }
        }
        return JSONMessage.failure("该手机号码未注册");
    }

    /**
     * @api {post} /user/setPassword 设置密码
     * @apiVersion 1.0.0
     * @apiName setPassword
     * @apiGroup 验证
     * @apiDescription 设置密码
     * @apiParam {String} phone 电话
     * @apiParam {String} password 密码
     * @apiParam {Integer} userType 用户类型
     * @apiParam {String} smsid 获取验证码时的Id
     * @apiParam {String} ranCode 验证码
     * @apiSuccess {Integer} resultCode 接口返回的状态
     * @apiAuthor 李淼淼
     * @date 2015年7月30日
     */
    @RequestMapping("/setPassword")
    public JSONMessage setPassword(
            @RequestParam(name = "phone", required = true) String phone,
            @RequestParam(name = "password", required = true) String password,
            @RequestParam(name = "userType", required = true) Integer userType,
            @RequestParam(name = "smsid", required = true) String smsid,
            @RequestParam(name = "ranCode", required = true) String ranCode
    ) {
        boolean flag = false;
        if (userRepository.exsistUser(phone, userType)) {
            flag = smsRanCodeService.isCorrectCode(phone, smsid, ranCode);
            if (flag) {
                userManager.updatePassword(phone, userType, password);
                return JSONMessage.success();
            } else {
                return JSONMessage.failure("验证码错误，请重新输入");
            }
        } else {
            return JSONMessage.failure("该手机号码未注册");
        }
    }

    /**
     * @api {post} /user/setPasswordWithoutCode 设置密码（不做验证码校验）
     * @apiVersion 1.0.0
     * @apiName setPasswordWithoutCode
     * @apiGroup 验证
     * @apiDescription 设置密码（不做验证码校验）
     * @apiParam {String} access_token token
     * @apiParam {String} phone 电话
     * @apiParam {String} password 密码
     * @apiParam {Integer} userType 用户类型（1：患者，2：医助 ，3：医生，4：客服，5：集团，6：导医，8：护士，9：店主，10：企业用户，11：药店成员，100：游客）
     * @apiSuccess {Integer} resultCode 接口返回的状态
     * @apiAuthor 傅永德
     * @date 2016年10月18日
     */
    @RequestMapping("/setPasswordWithoutCode")
    public JSONMessage setPasswordWithoutCode(
            @RequestParam(name = "phone", required = true) String phone,
            @RequestParam(name = "password", required = true) String password,
            @RequestParam(name = "userType", required = true) Integer userType
    ) {
        if (userRepository.exsistUser(phone, userType)) {
            userManager.updatePassword(phone, userType, password);
            return JSONMessage.success();
        } else {
            return JSONMessage.failure("该手机号码未注册");
        }
    }

    /**
     * </p>
     * 获取个人资料
     * </p>
     *
     * @return {@link User}
     * @author limiaomiao
     * @date 2015年7月27日
     */
    @RequestMapping("/getSelfProfile")
    public JSONMessage getProfile() {
        Map<String, Object> data = new HashMap<String, Object>();
        int userId = ReqUtil.instance.getUserId();
        User user = userRepository.getUser(userId);
        data.put("user", user);
        return JSONMessage.success(null, data);

    }

    /**
     * </p>
     * 设置用户头像
     * </p>
     *
     * @author limiaomiao
     * @date 2015年7月27日
     */
    @RequestMapping("/setUserHeaderPic")
    public JSONMessage setUserHeaderPic(@RequestParam String headerPicName)
            throws HttpApiException {
        int userId = ReqUtil.instance.getUserId();
        boolean f = userRepository.setHeaderPicName(userId, headerPicName);
        if (f) {
            patientService.updateTopPath(userId, UserHelper.addAvatarPrefix(userId, headerPicName));
        }
        return JSONMessage.success(null, f);

    }

    /**
     * @api {get,post} /user/search 查找用户
     * @apiVersion 1.0.0
     * @apiName search
     * @apiGroup 用户
     * @apiDescription 根据医生号或者手机号查找userType类型用户，如果存在正常返回对应用户和好友关系，否则抛出异常（ ServiceException(11111,
     * "没有找到用户");）
     * @apiParam {String} access_token token
     * @apiParam {String} telephone 电话号码或者手机号
     * @apiParam {Integer} userType 用户类型
     * @apiSuccess {isFriend} true/false 是否为好友
     * @apiSuccess {User} user
     * @apiAuthor 张垠
     * @date 2015年10月17日
     */
    @RequestMapping("/search")
    public JSONMessage search(UserExample example) {
        Map<String, Object> data = new HashMap<String, Object>();
        User user = userRepository
                .getUser(example.getTelephone(), example.getUserType(), UserStatus.normal.getIndex());
        // 查询是否为好友
        if (user != null) {
            data.put("isFriend", userRepository.isFriend(ReqUtil.instance.getUser(), user));
            data.put("user", user);
            return JSONMessage.success(null, data);
        } else {
            throw new ServiceException(11111, "没有找到用户");
        }
    }

    /**
     * </p>
     * 个人voip信息获取
     * </p>
     *
     * @return {@link VOIP}
     * @author limiaomiao
     * @date 2015年7月27日
     */
    // @RequestMapping("/voip")
    // 云通讯的，暂时废弃，改用云之讯
    public JSONMessage getVoipInfo() {
        int userid = ReqUtil.instance.getUserId();
        User user = userRepository.getUser(userid);
        if (user != null) {
            VOIP voip = user.getVoip();
            if (voip == null) {
                CCPRestSDK ccp = new CCPRestSDK();
                String serverIP = PropertiesUtil.getContextProperty("yuntongxun.url");
                String serverPort = PropertiesUtil.getContextProperty("yuntongxun.port");
                String appId = PropertiesUtil.getContextProperty("yuntongxun.app_id");
                String accountSid = PropertiesUtil.getContextProperty("yuntongxun.account_sid");
                String accountToken = PropertiesUtil.getContextProperty("yuntongxun.auth_token");
                ccp.init(serverIP, serverPort);
                ccp.setAccount(accountSid, accountToken);
                ccp.setAppId(appId);

                Map<String, Object> ret = ccp.createSubAccount(userid + "");
                if ("000000".equals(ret.get("statusCode"))) {
                    ret = (Map<String, Object>) ret.get("data");
                    ret = (Map<String, Object>) ret.get("SubAccount");
                    userRepository.updateVoip(userid, ret);
                    return JSONMessage.success(null, ret);
                } else {
                    ret = ccp.querySubAccount(userid + "");
                    if ("000000".equals(ret.get("statusCode"))) {
                        ret = (Map<String, Object>) ret.get("data");
                        ret = (Map<String, Object>) ret.get("SubAccount");
                        userRepository.updateVoip(userid, ret);
                        return JSONMessage.success(null, ret);
                    }

                }
            } else {
                Map<String, Object> ret = new HashMap<String, Object>();
                ret.put("dateCreated", voip.getDateCreated());
                ret.put("subAccountSid", voip.getSubAccountSid());
                ret.put("voipAccount", voip.getVoipAccount());
                ret.put("subToken", voip.getSubToken());
                ret.put("voipPwd", voip.getVoipPwd());
                return JSONMessage.success(null, ret);
            }
            throw new ServiceException("voip通道建立失败");

        } else {
            throw new ServiceException("找不到用户信息");
        }
        // return JSONMessage.success(null,data);

    }

    /**
     * </p>
     * 根据用户获取voip信息
     * </p>
     *
     * @return {@link VOIP}
     * @author limiaomiao
     * @date 2015年7月27日
     */
    // @RequestMapping("/voipByUser")
    // 暂时废弃，改用云之讯
    public JSONMessage getVoipInfo(Integer userid) {
        User user = userRepository.getUser(userid);
        if (user != null) {
            VOIP voip = user.getVoip();
            if (voip == null) {
                CCPRestSDK ccp = new CCPRestSDK();
                String serverIP = PropertiesUtil.getContextProperty("yuntongxun.url");
                String serverPort = PropertiesUtil.getContextProperty("yuntongxun.port");
                String appId = PropertiesUtil.getContextProperty("yuntongxun.app_id");
                String accountSid = PropertiesUtil.getContextProperty("yuntongxun.account_sid");
                String accountToken = PropertiesUtil.getContextProperty("yuntongxun.auth_token");
                ccp.init(serverIP, serverPort);
                ccp.setAccount(accountSid, accountToken);
                ccp.setAppId(appId);

                Map<String, Object> ret = ccp.createSubAccount(userid + "");
                if ("000000".equals(ret.get("statusCode"))) {
                    ret = (Map<String, Object>) ret.get("data");
                    ret = (Map<String, Object>) ret.get("SubAccount");
                    userRepository.updateVoip(userid, ret);
                    ret.put("voipPwd", null);
                    ret.put("subToken", null);
                    return JSONMessage.success(null, ret);
                } else {
                    ret = ccp.querySubAccount(userid + "");
                    if ("000000".equals(ret.get("statusCode"))) {
                        ret = (Map<String, Object>) ret.get("data");
                        ret = (Map<String, Object>) ret.get("SubAccount");
                        userRepository.updateVoip(userid, ret);
                        ret.put("voipPwd", null);
                        ret.put("subToken", null);
                        return JSONMessage.success(null, ret);
                    }

                }
            } else {
                Map<String, Object> ret = new HashMap<String, Object>();
                ret.put("dateCreated", voip.getDateCreated());
                ret.put("subAccountSid", voip.getSubAccountSid());
                ret.put("voipAccount", voip.getVoipAccount());
                return JSONMessage.success(null, ret);
            }
            throw new ServiceException("voip通道建立失败");

        } else {
            throw new ServiceException("找不到用户信息");
        }
        // return JSONMessage.success(null,data);

    }

    /**
     * </p>
     * 根据voip子帐号获取用户信息
     * </p>
     *
     * @return {@link User}
     * @author limiaomiao
     * @date 2015年7月27日
     */
    @RequestMapping("/userByVoipAccount")
    public JSONMessage getUserByVoipAccount(String voipAccount) {
        User user = userRepository.getUserByVoip(voipAccount);
        if (user != null) {

            return JSONMessage.success(null, user);
        } else {
            throw new ServiceException("找不到用户信息");
        }
        // return JSONMessage.success(null,data);

    }

    /**
     * @api {[get,post]} /user/getHeaderByUserIds 根据用户id集合获取头像
     * @apiVersion 1.0.0
     * @apiName getHeaderByUserIds
     * @apiGroup 用户
     * @apiDescription 根据用户id集合获取头像
     * @apiParam {String} access_token 凭证
     * @apiParam {String[]} ids 用户id
     * @apiSuccess {int} userId 用户id
     * @apiSuccess {int} userType 用户类型
     * @apiSuccess {String} name 用户名称
     * @apiSuccess {String} headPicFileName 头像文件名称
     * @apiSuccess {String} doctorGroupName 医生集团名称
     * @apiSuccess {String} departmentsName 科室名称
     * @apiSuccess {String} title 医生职称
     * @apiAuthor 李淼淼
     * @author 李淼淼
     * @date 2015年8月25日
     */
    @RequestMapping("/getHeaderByUserIds")
    public JSONMessage getHeaderByUserIds(String[] ids) {

        if ((ids == null) || (ids.length <= 0)) {
            throw new ServiceException(20003, "parameter ids must be  array ");
        }

        List<Integer> _list = new ArrayList<Integer>();
        for (String user : ids) {
            _list.add(Integer.valueOf(user));
        }

        List<UserInfoVO> data = userManager.getHeaderByUserIds(_list);
        return JSONMessage.success(null, data);
    }

    /*
     * test sms
     *
     */
    @RequestMapping("/testsms")
    public JSONMessage getHeaderByUserIds() {
        mobSmsSdk.send("13751132072", "你好");
        return JSONMessage.success();
    }

    /**
     * @api {[get,post]} /user/registerDeviceToken 设备注册
     * @apiVersion 1.0.0
     * @apiName registerDeviceToken
     * @apiGroup 用户
     * @apiDescription 用户设备注册
     * @apiParam {String} access_token token
     * @apiParam {String} serial 客户端设备号,用于推送
     * @apiParam {String} model 客户端类型，值为ios或者android
     * @apiSuccess {Map} User 返回参数
     * @apiAuthor 李淼淼
     * @date 2015年7月30日
     */
    @RequestMapping(value = "/registerDeviceToken")
    public JSONMessage deviceToken(String serial, String model, String mode) {
        if (StringUtils.isBlank(model)) {
            model = mode;
        }
        userManager.registerDeviceToken(ReqUtil.instance.getUserId(), serial, model);
        return JSONMessage.success(null);
    }

    /**
     * @api {[get,post]} /user/getRemindVoice 获取提示音
     * @apiVersion 1.0.0
     * @apiName getRemindVoice
     * @apiGroup 用户
     * @apiDescription 获取当前登录用户提示音，如未设置，则默认取sound_bell01.mp3
     * @apiParam {String} access_token 凭证
     * @apiSuccess {String} user.userConfig.remindVoice 提示音
     * @apiAuthor 谢平
     * @date 2015年10月28日
     */
    @RequestMapping("/getRemindVoice")
    public JSONMessage getRemindVoice() {
        User user = userManager.getRemindVoice(ReqUtil.instance.getUserId());
        UserSoundVO requestMsg = new UserSoundVO();
        requestMsg.setUserId(String.valueOf(user.getUserId()));
        requestMsg.setSound(user.getUserConfig().getRemindVoice());
        // MsgHelper.getSound(requestMsg);
        return JSONMessage.success(null, user);
    }

    /**
     * @api {[get,post]} /user/setRemindVoice 设置提示音
     * @apiVersion 1.0.0
     * @apiName setRemindVoice
     * @apiGroup 用户
     * @apiDescription 设置提示音
     * @apiParam {String} access_token 凭证
     * @apiParam {String} remindVoice 提示音名称
     * @apiSuccess {Number} resultCode 返回状态码
     * @apiAuthor 谢平
     * @date 2015年10月28日
     */
    @RequestMapping("/setRemindVoice")
    public JSONMessage setRemindVoice(UserConfig param) {
        userRepository.setRemindVoice(ReqUtil.instance.getUserId(), param);
        return JSONMessage.success(null);
    }

    /**
     * @api {[get,post]} /user/setRemarks 修改备注
     * @apiVersion 1.0.0
     * @apiName setRemarks
     * @apiGroup 用户
     * @apiDescription 修改备注
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer} userId 用户Id
     * @apiParam {String} remarks 备注
     * @apiAuthor 谢平
     * @date 2015年11月24日
     */
    @RequestMapping("/setRemarks")
    public JSONMessage setRemarks(Integer userId, String remarks) {
        return JSONMessage.success(null, userManager.setRemarks(userId, remarks));
    }

    /**
     * @api {[get,post]} /user/getRemarks 获取备注
     * @apiVersion 1.0.0
     * @apiName getRemarks
     * @apiGroup 用户
     * @apiDescription 获取备注
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer} userId 用户Id
     * @apiSuccess {String} remarks 备注
     * @apiAuthor 谢平
     * @date 2015年11月24日
     */
    @RequestMapping("/getRemarks")
    public JSONMessage getRemarks(Integer userId) {
        return JSONMessage.success(null, userManager.getRemarks(userId));
    }

    /**
     * @api {[get,post]} /user/updateStatus 待审核
     * @apiVersion 1.0.0
     * @apiName updateStatus
     * @apiGroup 用户
     * @apiDescription 医生上传证书照成功后修改用户状态为待审核
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer} userId 用户Id
     * @apiAuthor 谢平
     * @date 2015年12月9日
     */
    @RequestMapping("/updateStatus")
    public JSONMessage updateStatus(Integer userId) {
        boolean updateFlag = userManager.updateStatus(userId);
        userManager.activityCommitAuthNotify(userId);
        userManager.userInfoChangeNotify(userId);
        // 发送Topic消息队列
        sendDoctorTopicService.sendCertifyTopicMes(userRepository.getUser(userId));
        return JSONMessage.success(null, updateFlag);
    }


    /**
     * @api {[get,post]} /user/updateGuideStatus 待审核
     * @apiVersion 1.0.0
     * @apiName updateGuideStatus
     * @apiGroup 用户
     * @apiDescription 医生上传证书照成功后修改用户状态为待审核
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer} userId 用户Id
     * @apiParam {String}  flag 启用（1）还是禁用（5）该账户  add by 姜宏杰
     * @apiAuthor 姜宏杰
     * @date 2016年3月2日18:46:45
     */
    @RequestMapping("/updateGuideStatus")
    public JSONMessage updateGuideStatus(Integer userId, int flag) {

        User user = userManager.getUser(userId);

        if (flag == UserStatus.forbidden.getIndex() && Objects.nonNull(user.getUserType())
                && user.getUserType().intValue() == UserType.DocGuide.getIndex()) {
            if (guideService.hasService(userId)) {
                throw new ServiceException("存在服务中的患者，无法禁用");
            }
        }

        userManager.updateGuideStatus(userId, flag);

        String status = "";
        if (flag == UserStatus.normal.getIndex()) {
            status = "启用";
        } else if (flag == UserStatus.forbidden.getIndex()) {
            status = "禁用";
        }
        operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
                OperationLogTypeDesc.DOCGUIDE, String.format("变更导医账号（ID：%1$s）状态-%2$s", userId, status));
        return JSONMessage.success(null);
    }

    /**
     * @api {[get,post]} /user/addDoctorCheckImage 新增上传医生认证图片
     * @apiVersion 1.0.0
     * @apiName addDoctorCheckImage
     * @apiGroup 用户
     * @apiDescription 新增上传医生认证图片[图片参数为空表示清空图片]
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer}   userId 用户ID 非必填（不为空，后台人员上传图片，为空 移动端 用户自己上传图片）
     * @apiParam {String...} doctorsImage 认证图片地址(为空清空用户图片)
     * @apiSuccess {String} data 图片id
     * @apiAuthor 李伟
     * @date 2015年12月9日
     */
    @RequestMapping("/addDoctorCheckImage")
    public JSONMessage addDoctorCheckImage(Integer userId, String... doctorsImage) {
        if (Objects.isNull(userId)) {
            userId = ReqUtil.instance.getUserId();
        }
        List<Integer> integers = imageDataService.addDoctorImagesCover(userId, doctorsImage);
        userManager.userInfoChangeNotify(userId);
        return JSONMessage.success(integers);
    }

    /**
     * @api {[get,post]} /user/addDoctorIdcardImage 新增上传医生身份证图片
     * @apiVersion 1.0.0
     * @apiName addDoctorIdcardImage
     * @apiGroup 用户
     * @apiDescription 新增上传医生身份证图片[图片参数为空表示清空图片]
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer}   userId 用户ID 非必填（不为空，后台人员上传图片，为空 移动端 用户自己上传图片）
     * @apiParam {String...} doctorsImage 认证图片地址(为空清空用户图片)
     * @apiSuccess {String} data 图片id
     * @apiAuthor 李伟
     * @date 2015年12月9日
     */
    @RequestMapping("/addDoctorIdcardImage")
    public JSONMessage addDoctorIdcardImage(Integer userId, String idcardImage) {
        if (Objects.isNull(userId)) {
            userId = ReqUtil.instance.getUserId();
        }

        Integer imgId = imageDataService.addDoctorIdcardImage(userId, idcardImage);

        return JSONMessage.success(imgId);
    }

    /**
     * @api {[get,post]} /user/getDoctorCheckImage 获取医生认证图片信息
     * @apiVersion 1.0.0
     * @apiName getDoctorCheckImage
     * @apiGroup 用户
     * @apiDescription 获取医生认证图片信息
     * @apiParam {String} access_token 凭证
     * @apiSuccess {String[]} data 图片集合
     * @apiSuccess {String} id 记录id
     * @apiSuccess {String} url 图片路径
     * @apiAuthor 李伟
     * @date 2015年12月9日
     */
    @RequestMapping("/getDoctorCheckImage")
    public JSONMessage getDoctorCheckImage() {
        List<Map<String, Object>> result = imageDataService
                .findDoctorImgData(ImageDataEnum.doctorCheckImage.getIndex(),
                        ReqUtil.instance.getUserId());
        User user = userManager.getUser(ReqUtil.instance.getUserId());
        if (user.getStatus() == UserEnum.UserStatus.normal.getIndex()) {
            List<Map<String, Object>> old = imageDataService
                    .getOldDoctorCertImagesList(ReqUtil.instance.getUserId());
            if ((null != old) && (old.size() > 0)) {
                result.addAll(old);
            }

        }

        return JSONMessage.success(result);
    }

    /**
     * @api {[get,post]} /user/getDoctorCheckImageByDoctorId 根据医生id获取医生认证图片信息
     * @apiVersion 1.0.0
     * @apiName getDoctorCheckImageByDoctorId
     * @apiGroup 用户
     * @apiDescription 根据医生id获取医生认证图片信息
     * @apiParam {String} access_token 凭证
     * @apiParam {String} doctorId 医生id
     * @apiSuccess {String[]} data 图片集合
     * @apiSuccess {String} id 记录id
     * @apiSuccess {String} url 图片路径
     * @apiAuthor 傅永德
     * @date 2016年6月27日
     */
    @RequestMapping("/getDoctorCheckImageByDoctorId")
    public JSONMessage getDoctorCheckImageByDoctorId(Integer doctorId) {
        List<Map<String, Object>> result = imageDataService
                .findDoctorImgData(ImageDataEnum.doctorCheckImage.getIndex(), doctorId);
        User user = userManager.getUser(ReqUtil.instance.getUserId());
        if (user.getStatus() == UserEnum.UserStatus.normal.getIndex()) {
            List<Map<String, Object>> old = imageDataService.getOldDoctorCertImagesList(doctorId);
            if ((null != old) && (old.size() > 0)) {
                result.addAll(old);
            }
        }
        return JSONMessage.success(result);
    }

    /**
     * @api {[get,post]} /user/getDoctorFile 获取医生相关 文件
     * @apiVersion 1.0.0
     * @apiName getDoctorFile
     * @apiGroup 用户
     * @apiDescription 获取医生相关 文件
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer} doctorId 医生id
     * @apiParam {Integer} type 获取图片类型（1=病例图片，2=病情图片，3=诊断记录，4=诊断录音，5=医生认证图片）
     * @apiSuccess {String[]} data 图片集合
     * @apiSuccess {String} id 记录id
     * @apiSuccess {String} url 文件路径
     * @apiAuthor 王峭
     * @date 2016年1月29日
     */
    @RequestMapping("/getDoctorFile")
    public JSONMessage getDoctorFile(Integer doctorId, Integer type) {
        //参数校验

        //读取医生相关的文件
        List<Map<String, Object>> result = imageDataService.findDoctorImgData(type, doctorId);

        return JSONMessage.success(result);
    }

    /**
     * @api {[get,post]} /user/deleteDoctorCheckImage 删除医生认证或身份证图片信息
     * @apiVersion 1.0.0
     * @apiName deleteDoctorCheckImage
     * @apiGroup 用户
     * @apiDescription 删除医生认证或身份证图片信息
     * @apiParam {String} access_token 凭证
     * @apiParam {String} imageId 图片id
     * @apiAuthor 李伟
     * @date 2015年12月9日
     */
    @RequestMapping("/deleteDoctorCheckImage")
    public JSONMessage deleteDoctorCheckImage(Integer imageId) {
        imageDataService.deleteImgDataById(imageId);
        return JSONMessage.success();
    }

    /**
     * @api {[get,post]} /user/updateDoctorCheckImage 更新医生认证图片信息
     * @apiVersion 1.0.0
     * @apiName updateDoctorCheckImage
     * @apiGroup 用户
     * @apiDescription 更新医生认证图片信息
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer}   userId 用户ID 非必填（不为空，后台人员上传图片，为空 移动端 用户自己上传图片）
     * @apiParam {String} oldImageId 老图片id
     * @apiParam {String} doctorsImage 新图片的地址
     * @apiSuccess {String} data 新图片的Id
     * @apiAuthor 李伟
     * @date 2015年12月9日
     */
    @RequestMapping("/updateDoctorCheckImage")
    public JSONMessage updateDoctorCheckImage(Integer userId, Integer oldImageId,
                                              String doctorsImage) {
        this.deleteDoctorCheckImage(oldImageId);
        return this.addDoctorCheckImage(userId, doctorsImage);
    }

    /**
     * @api {[get,post]} /user/getGuideDoctorList 得到添加的导医列表
     * @apiVersion 1.0.0
     * @apiName getGuideDoctorList
     * @apiGroup 用户
     * @apiDescription 更新医生认证图片信息
     * @apiParam {String} access_token 凭证
     * @apiParam {String} groupId 集团ID
     * @apiParam {String} userName 凭证
     * @apiParam {String} telephone 凭证
     * @apiParam {Integer} pageIndex 第几页 默认从0 页开始
     * @apiParam {Integer} pageSize 每页的大小
     * @apiSuccess {String} resultCode  返回码
     * @apiSuccess {Integer} userId 用户id
     * @apiSuccess {String} name 用户name
     * @apiSuccess {String} telephone 用户telephone
     * @apiSuccess {Long} createTime 用户createTime
     * @apiSuccess {String} status 用户status
     * @apiAuthor 姜宏杰
     * @date 2016年3月1日18:25:49
     */
    @RequestMapping("/getGuideDoctorList")
    public JSONMessage getGuideDoctorList(String groupId, String userName, String telephone,
                                          Integer pageIndex, Integer pageSize) {
        return JSONMessage.success(
                userRepository.getGuideDoctorList(groupId, userName, telephone, pageIndex, pageSize));
    }

    /**
     * @api {[get,post]} /user/oneKeyReset 一键重置密码
     * @apiVersion 1.0.0
     * @apiName oneKeyReset
     * @apiGroup 用户
     * @apiDescription 更新医生认证图片信息
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer} userId 用户id
     * @apiParam {String} telephone 电话号码
     * @apiAuthor 姜宏杰
     * @date 2016年3月1日18:25:49
     */
    @RequestMapping("/oneKeyReset")
    public JSONMessage oneKeyReset(Integer userId, String telephone) {
        String pwd = StringUtil.randomPassword();
        userManager.oneKeyReset(userId, telephone, pwd);
        return JSONMessage.success();
    }

    /**
     * @api {[get,post]} /user/updateGuidInfo 更新用户信息
     * @apiVersion 1.0.0
     * @apiName updateGuidInfo
     * @apiGroup 用户
     * @apiDescription 更新医生认证图片信息
     * @apiParam {String} access_token 凭证
     * @apiParam {String} userId 用户id
     * @apiParam {String} telephone 电话号码
     * @apiParam {String} userName 电话号码
     * @apiAuthor 姜宏杰
     * @date 2016年3月1日18:25:49
     */
    @RequestMapping("/updateGuidInfo")
    public JSONMessage updateGuidInfo(int userId, String telephone, String userName) {
        userManager.updateGuideInfo(userId, telephone, userName);
        operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
                OperationLogTypeDesc.DOCGUIDE, "变更导医信息-导医ID：" + userId);
        return JSONMessage.success();
    }

    /**
     * @api {[get,post]} /user/getUserByTypeAndNameInUserIds 根据类型在userIds范围内模糊查询
     * @apiVersion 1.0.0
     * @apiName updateGuidInfo
     * @apiGroup 用户
     * @apiDescription 根据类型在userIds范围内模糊查询
     * @apiParam {String} access_token token
     * @apiParam {String} userType 用户类型
     * @apiParam {String} userStatus 用户状态（不传为查询所有状态）
     * @apiParam {String} keyword 模糊查询的名字关键字
     * @apiParam {Intger[]} userIds 在指定的userId 范围内查询
     * @apiAuthor 罗超
     * @date 2016年6月28日16:19:14
     */
    @RequestMapping("/getUserByTypeAndNameInUserIds")
    public JSONMessage getUserByTypeAndNameInUserIds(Integer userType, Integer userStatus,
                                                     String keyword, Integer[] userIds) {
        List<Integer> userIdList = Arrays.asList(userIds);
        List<User> data = userManager
                .getUserByTypeAndNameInUserIds(userType, userStatus, keyword, userIdList);
        return JSONMessage.success(null, data);
    }

    /**
     * @api {[get,post]} /user/updateFeldsherInfo 更新医生助手信息
     * @apiVersion 1.0.0
     * @apiName updateFeldsherInfo
     * @apiGroup User
     * @apiDescription 更新医生助手信息
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer} userId 用户id
     * @apiParam {String} name 姓名
     * @apiParam {String} telephone 电话
     * @apiSuccess {String} resultCode  返回码
     * @apiAuthor 钟良
     * @date 2016年7月25日
     */
    @RequestMapping("/updateFeldsherInfo")
    public JSONMessage updateFeldsherInfo(Integer userId, String name, String telephone) {
        userManager.updateFeldsherInfo(userId, telephone, name);
        operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
                OperationLogTypeDesc.ASSISTANT, "变更医生助手信息-ID：" + userId);
        return JSONMessage.success();
    }

    /**
     * @api {[get,post]} /user/updateFeldsherStatus 医生助手启用、禁用
     * @apiVersion 1.0.0
     * @apiName updateFeldsherStatus
     * @apiGroup User
     * @apiDescription 更改医生助手状态，启用、禁用
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer} userId 用户Id
     * @apiParam {Integer} status 状态：启用：1；禁用：2
     * @apiSuccess {String} resultCode  返回码
     * @apiAuthor 钟良
     * @date 2016年7月25日
     */
    @RequestMapping("/updateFeldsherStatus")
    public JSONMessage updateFeldsherStatus(Integer userId, int status) {
        userManager.updateFeldsherStatus(userId, status);

        String statusStr = "";
        if (status == 1) {
            statusStr = "启用";
        } else if (status == 2) {
            statusStr = "禁用";
        }
        operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
                OperationLogTypeDesc.DOCGUIDE,
                String.format("变更医生助手账号（ID：%1$s）状态-%2$s", userId, statusStr));
        return JSONMessage.success();
    }

    /**
     * @api {[get,post]} /user/getFeldsherList 获取医生助手列表
     * @apiVersion 1.0.0
     * @apiName getFeldsherList
     * @apiGroup User
     * @apiDescription 获取医生助手列表，web端调用，分页查询
     * @apiParam {String} access_token 凭证
     * @apiParam {String} keywords 关键字（非必填）
     * @apiParam {Integer} pageIndex 第几页 默认从0 页开始
     * @apiParam {Integer} pageSize 每页的大小
     * @apiParam {Integer} userType 用户类型：1：患者；2：医助；3：医生；4：客服；5：集团； 6：导医；8：护士；9：店主；10：企业用户；100：游客；
     * @apiSuccess {String} resultCode  返回码
     * @apiSuccess {Integer} userId 用户id
     * @apiSuccess {String} name 姓名
     * @apiSuccess {String} telephone 电话
     * @apiSuccess {Long} createTime 创建时间
     * @apiSuccess {String} status 状态：启用：1；禁用：2
     * @apiAuthor 钟良
     * @date 2016年7月25日
     */
    @RequestMapping("/getFeldsherList")
    public JSONMessage getFeldsherList(String keywords, Integer pageIndex, Integer pageSize,
                                       Integer userType) {
        return JSONMessage
                .success(userManager.getFeldsherList(keywords, pageIndex, pageSize, userType));
    }

    /**
     * @api {[get,post]} /user/getAvailableFeldsherList 获取可用的医生助手列表
     * @apiVersion 1.0.0
     * @apiName getAvailableFeldsherList
     * @apiGroup User
     * @apiDescription 获取可用的医生助手列表，医生资格审核时需要获取可用的医生助手列表
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer} userType 用户类型：1：患者；2：医助；3：医生；4：客服；5：集团； 6：导医；8：护士；9：店主；10：企业用户；100：游客；
     * @apiSuccess {String} resultCode  返回码
     * @apiSuccess {Object[]} list
     * @apiSuccess {Integer} list.userId 用户id
     * @apiSuccess {String} list.name 姓名
     * @apiAuthor 钟良
     * @date 2016年7月25日
     */
    @RequestMapping("/getAvailableFeldsherList")
    public JSONMessage getAvailableFeldsherList(Integer userType) {
        return JSONMessage.success(userManager.getAvailableFeldsherList(userType));
    }

    /**
     * @api {[get,post]} /user/searchDoctor 搜索医生
     * @apiVersion 1.0.0
     * @apiName searchDoctor
     * @apiGroup User
     * @apiDescription 搜索医生
     * @apiParam {String} access_token 凭证
     * @apiParam {List} hospitalIdList 医院Id集团
     * @apiParam {String} keyword 搜索关键字
     * @apiParam {String} departments 科室
     * @apiSuccess {String} resultCode  返回码
     * @apiSuccess {com.dachen.health.commons.vo.User} data 用户对象
     * @apiAuthor 谢平
     * @date 2016年7月25日
     */
    @RequestMapping("/searchDoctor")
    public JSONMessage searchDoctor(String[] hospitalIdList, String keyword, String departments) {
        return JSONMessage.success(userManager.searchOkStatusDoctor(hospitalIdList, keyword, departments));
    }

    @RequestMapping(value = "addOperationRecord")
    public JSONMessage addOperationRecord(Integer creator, String objectType,String objectId,String content) {
        userOperationLogService.addOperationRecord(creator, objectType, objectId, content);
        return JSONMessage.success("success");
    }

    /**
     * @api {[get,post]} /user/getOperationRecord 获取操作记录
     * @apiVersion 1.0.0
     * @apiName getOperationRecord
     * @apiGroup User
     * @apiDescription 获取操作记录
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer} id 用户id
     * @apiParam {String} keyword 搜索关键字
     * @apiParam {Integer} pageIndex 页码
     * @apiParam {Integer} pageSize 页容量
     * @apiSuccess {String} resultCode  返回码
     * @apiSuccess {com.dachen.health.commons.vo.OperationRecordVO} data 操作记录
     * @apiAuthor 梁朝税
     * @date 2016年8月24日
     */
    @RequestMapping(value = "getOperationRecord")
    public JSONMessage getOperationRecord(String id, String keyword,
                                          @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        return JSONMessage.success(userManager.getRecord(id, keyword, pageIndex, pageSize));
    }

    /**
     * @api {[get,post]} /user/getUserIdsByName 通过name和UserType查询用户id列表
     * @apiVersion 1.0.0
     * @apiName /user/getUserIdsByName
     * @apiGroup User
     * @apiDescription 根据name和UserType查询用户id列表
     * @apiParam {String} access_token 凭证
     * @apiParam {Integer} userType 用户类型
     * @apiParam {String} name 姓名
     * @apiSuccess {String} resultCode  返回码
     * @apiSuccess {Integer[]} userIdList 用户id列表
     * @apiAuthor 钟良
     * @date 2016年10月26日
     */
    @RequestMapping(value = "getUserIdsByName")
    public JSONMessage getUserIdsByName(String name, Integer userType) {
        return JSONMessage.success(userManager.getUserIdsByName(name, userType));
    }

    /**
     * @api {[post]} /user/checkUserData 运营平台批量导入账号检查数据
     * @apiVersion 1.0.0
     * @apiName checkUserData
     * @apiGroup User
     * @apiDescription 运营平台批量导入账号
     * @apiParam {File} file 导入的文件
     * @apiSuccess {String} resultCode  返回码
     * @apiFail {String} data.resultMsg  错误原因
     * @apiAuthor 傅永德
     * @date 2016年10月28日
     */
    @RequestMapping("/checkUserData")
    public JSONMessage checkUserData(@RequestParam("file") MultipartFile file) throws HttpApiException {
        Map<String, Object> result;
        Integer adminId = ReqUtil.instance.getUserId();
        try {
            String fileName = file.getOriginalFilename();
            String type = null;
            if (fileName.endsWith("xls") || fileName.endsWith("XLS")) {
                type = "xls";
            } else if (fileName.endsWith("xlsx") || fileName.endsWith("XLSX")) {
                type = "xlsx";
            } else {
                throw new ServiceException("文件类型错误");
            }
            result = userManager
                    .checkUserData(file.getInputStream(), APP_NURSE_CLIENT_LINK(), adminId, type);
        } catch (IOException e) {
            throw new ServiceException("文件错误");
        } catch (HttpApiException e) {
            throw e;
        }
        Object errorObject = result.get("error");
        Gson gson = new Gson();
        if (errorObject != null) {
            List<UserExcelError> errors = (List<UserExcelError>) errorObject;
            return JSONMessage.failure(gson.toJson(errors));
        } else {
            return JSONMessage.success();
        }
    }

    @Autowired
    protected ShortUrlComponent shortUrlComponent;

    public String APP_NURSE_CLIENT_LINK() throws HttpApiException {
        String nurseLink = PropertiesUtil
                .getContextProperty("app.nurse.client.link");
        String shorUrl = shortUrlComponent.generateShortUrl(nurseLink);
        return shorUrl;
    }

    /**
     * @api {[post]} /user/handleFormUpload 运营平台批量导入账号
     * @apiVersion 1.0.0
     * @apiName handleFormUpload
     * @apiGroup User
     * @apiDescription 运营平台批量导入账号
     * @apiParam {File} file 导入的文件
     * @apiSuccess {String} resultCode  返回码
     * @apiSuccess {String} data.hasRegisteredCount 已经注册过的用户数
     * @apiSuccess {String} data.registerCount 本次注册的用户数
     * @apiAuthor 傅永德
     * @date 2016年10月28日
     */
    @RequestMapping("/handleFormUpload")
    public JSONMessage handleFormUpload(@RequestParam("file") MultipartFile file,
                                        HttpServletRequest request) {
        Map<String, Object> result;
        Integer adminId = ReqUtil.instance.getUserId();
        try {
            String fileName = file.getOriginalFilename();
            String type = null;
            if (fileName.endsWith("xls") || fileName.endsWith("XLS")) {
                type = "xls";
            } else if (fileName.endsWith("xlsx") || fileName.endsWith("XLSX")) {
                type = "xlsx";
            } else {
                throw new ServiceException("文件类型错误");
            }
            result = userManager.createDoctors(file.getInputStream(), APP_NURSE_CLIENT_LINK(), adminId, type);
        } catch (IOException e) {
            throw new ServiceException("文件错误");
        } catch (HttpApiException e) {
            throw new ServiceException(e.getMessage());
        }
        Object errorObject = result.get("error");
        Gson gson = new Gson();
        if (errorObject != null) {
            List<UserExcelError> errors = (List<UserExcelError>) errorObject;
            return JSONMessage.failure(gson.toJson(errors));
        } else {
            Integer registerCount = (Integer) result.get("registerCount");
            operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
                    OperationLogTypeDesc.DOCTORAUDIT, String.format("批量导入医生账号（%s位）", registerCount));
            return JSONMessage.success(result);
        }
    }

    /**
     * @api {[post]} /user/initDoctorServiceStatus 初始化用户的套餐开通状态
     * @apiVersion 1.0.0
     * @apiName initDoctorServiceStatus
     * @apiGroup User
     * @apiDescription 初始化用户的套餐开通状态
     * @apiAuthor 傅永德
     * @date 2016年10月28日
     */
    @RequestMapping("/initDoctorServiceStatus")
    public JSONMessage initDoctorServiceStatus() {
        List<Pack> packs = packMapper.getServiceOn();

        Set<Integer> userIds = Sets.newHashSet();
        if (packs != null && packs.size() > 0) {
            for (Pack pack : packs) {
                if (pack != null && pack.getPackType() != null
                        && pack.getPackType().intValue() == PackEnum.PackType.careTemplate.getIndex()) {
                    //健康关怀的要特殊处理
                    //排除价格为0的（即随访）和非启用的，与PackServiceImpl.queryPack保持一致
                    if (packService.isValidCareTemplateForPatient(pack)) {
                        if (pack.getDoctorId() != null) {
                            userIds.add(pack.getDoctorId());
                        }
                    } else {
                        continue;
                    }

                } else {
                    if (pack != null && pack.getDoctorId() != null) {
                        userIds.add(pack.getDoctorId());
                    }
                }
            }
        }

        userManager.initAllDoctorServiceStatus(userIds);
        return JSONMessage.success();
    }

    /**
     * @api {[post]} /user/patientRename 微信公众号登录注册的用户修改真实姓名
     * @apiVersion 1.0.0
     * @apiName patientRename
     * @apiGroup User
     * @apiDescription 微信公众号登录注册的用户修改真实姓名
     * @apiParam {String} name 姓名
     * @apiSuccess {String} resultCode  返回码
     * @apiAuthor liangcs
     * @date 2016年12月9日
     */
    @RequestMapping("/patientRename")
    public JSONMessage wxPatientRename(String name) {
        userManager.patientRename(name);
        return JSONMessage.success();
    }

    /**
     * @api {[post]} /user/loginBuyCareOrder 购买分享的健康关怀的登录入口
     * @apiVersion 1.0.0
     * @apiName loginBuyCareOrder
     * @apiGroup User
     * @apiDescription 购买分享的健康关怀的登录入口
     * @apiParam {String} telephone 手机号
     * @apiSuccess {String} resultCode  返回码
     * @apiAuthor liangcs
     * @date 2016年12月9日
     */
    @RequestMapping("loginBuyCareOrder")
    public JSONMessage loginBuyCareOrder(String telephone) {
        UserExample example = new UserExample();
        UserSource us = new UserSource();
        us.setSourceType(UserEnum.Source.share.getIndex());
        us.setTerminal(UserEnum.Terminal.xg.getIndex());
        example.setUserSource(us);
        example.setTelephone(telephone);
        return JSONMessage.success(userManager.loginBuyCareOrder(example));
    }

    /**
     * @api {[get,post]} /user/friends/delete 删除医生的医药代表的好友
     * @apiVersion 1.0.0
     * @apiName /user/friends/delete
     * @apiGroup 用户好友
     * @apiDescription 删除医生的医药代表的好友
     * @apiParam {String}    access_token	token
     * @apiParam {Integer}	userId			医生Id
     * @apiParam {Integer}	representUserId 医药代表Id
     * @apiSuccess {String} resultCode 返回状态码
     * @apiAuthor 钟良
     * @date 2017年6月1日
     */
    @RequestMapping("/friends/delete")
    public JSONMessage deleteFriends(@RequestParam Integer userId,
                                     @RequestParam Integer representUserId) throws HttpApiException {
        if (userId == null || userId == 0) {
            throw new ServiceException("医生Id不能为空");
        }
        if (representUserId == null || representUserId == 0) {
            throw new ServiceException("医药代表Id不能为空");
        }
        drugOrgApiClientProxy.delFriend(userId, representUserId);
        return JSONMessage.success("删除好友成功");
    }

    /**
     * @api {[get,post]} /user/validatePassword 用户密码验证
     * @apiVersion 1.0.0
     * @apiName /user/validatePassword
     * @apiGroup 用户
     * @apiDescription 用户密码验证
     * @apiParam {String}    access_token	token
     * @apiParam {String}	password			医生Id
     * @apiSuccess {String} resultCode 返回状态码
     * @apiSuccess {Integer} data   1:表示验证通过，0：验证不通过
     * @apiAuthor wangl
     * @date 2017年6月1日
     */
    @RequestMapping("/validatePassword")
    public JSONMessage validatePassword(@RequestParam(required = true) String password) {
        Integer ret = userManager.validatePassword(password);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {[get,post]} /user/disable 禁用账号
     * @apiVersion 1.0.0
     * @apiName /user/disable
     * @apiGroup 用户
     * @apiDescription 禁用账号
     * @apiParam {String}    access_token	token
     * @apiParam {String}	userId			医生Id
     * @apiSuccess {String} resultCode 返回状态码
     * @apiAuthor wangl
     * @date 2017年6月1日
     */
    @RequestMapping("/disable")
    public JSONMessage disable(@RequestParam Integer userId) {
        if (Objects.isNull(userId)) {
            throw new ServiceException("被封号医生不能为空");
        }
        userManager.disable(userId, null);
        userManager.userInfoChangeNotify(userId);//同步状态至circle
        return JSONMessage.success();
    }

    @RequestMapping(value = "/v2/disable", method = RequestMethod.POST)
    public JSONMessage disableV2(@RequestBody DisableDoctorParam doctorParam) {
        Integer userId = doctorParam.getUserId();
        if (Objects.isNull(userId)) {
            throw new ServiceException("被封号医生不能为空");
        }
        if (StringUtil.isBlank(doctorParam.getReason())) {
            throw new ServiceException("封号原因不能为空");
        }
        userManager.disable(userId, doctorParam);
        userManager.userInfoChangeNotify(userId);//同步状态至circle
        return JSONMessage.success();
    }

    /**
     * @api {[get,post]} /user/enable 解禁账号
     * @apiVersion 1.0.0
     * @apiName /user/enable
     * @apiGroup 用户
     * @apiDescription 解禁账号
     * @apiParam {String}    access_token	token
     * @apiParam {String}	userId			医生Id
     * @apiSuccess {String} resultCode 返回状态码
     * @apiAuthor wangl
     * @date 2017年6月1日
     */
    @RequestMapping("/enable")
    public JSONMessage enable(@RequestParam Integer userId) {
        userManager.enable(userId);
        userManager.userInfoChangeNotify(userId);//同步状态至circle
        return JSONMessage.success();
    }

    @RequestMapping(value = "/getDoctorInfo", method = RequestMethod.GET)
    public JSONMessage getDoctorInfo(String name) {
        List<DoctorBaseInfo> doctorBaseInfos = userManager.getDoctorInfo(name);
        for (DoctorBaseInfo info : doctorBaseInfos) {
            info.setCheckImageList(imageDataService.findImgData(ImageDataEnum.doctorCheckImage.getIndex(), info.getUserId()));
        }
        return JSONMessage.success(doctorBaseInfos);
    }

    @ApiOperation(value = "获取用户登录信息", notes = "返回PageVO")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access-token", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "pageIndex", value = "pageIndex", dataType = "int", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "pageSize", value = "pageSize", dataType = "int", paramType = "query", defaultValue = "5"),
            @ApiImplicitParam(name = "telephone", value = "telephone", dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "getUserLoginInfo", method = RequestMethod.GET)
    public JSONMessage getUserLoginInfo(@RequestParam(defaultValue = "0") Integer pageIndex,
                                        @RequestParam(defaultValue = "5") Integer pageSize,
                                        @RequestParam(required = false) String telephone) {
        return JSONMessage.success(userManager.getUserLoginInfo(pageIndex, pageSize, telephone));
    }

    @ApiOperation(value = "获取客服列表", notes = "获取客服列表", response = User.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access-token", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "pageIndex", value = "pageIndex", dataType = "int", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "pageSize", value = "pageSize", dataType = "int", paramType = "query", defaultValue = "10")
    })
    @RequestMapping(value = "customer/service", method = RequestMethod.GET)
    public JSONMessage getCustomerService(@RequestParam(defaultValue = "0") Integer pageIndex,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO pageVO = userManager.getNormalUserPaging(UserType.customerService.getIndex(), pageIndex, pageSize);
        return JSONMessage.success(pageVO);
    }

    @ApiOperation(value = "编辑用户角色", notes = "编辑用户角色", response = JSONMessage.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "param", value = "参数", required = true, dataType = "UserRoleParam")
    })
    @RequestMapping(value = "/role/{userId}", method = RequestMethod.POST)
    public JSONMessage updateUserRole(@PathVariable String userId, @RequestBody UserRoleParam param) {
        param.setUserId(userId);
        userManager.updateUserRole(param);
//        //用户权限做了修改，需要重新登录
//        userManager.logout(ReqUtil.getToken(), null);
        return JSONMessage.success();
    }

    @ApiOperation(value = "获取用户角色权限", notes = "获取用户角色权限", response = UserRoleVO.class)
    @RequestMapping(value = "role/permission", method = RequestMethod.GET)
    public JSONMessage getUserRolePermission(@RequestHeader("userID") Integer userId) {
        return JSONMessage.success(userManager.getUserRolePermission(userId));
    }

    /**
     * @api {[get,post]} /user/addDoctorCheckInfo
     * @apiVersion 1.0.0
     * @apiName /user/addDoctorCheckInfo
     * @apiGroup 用户
     * @apiDescription 增加用户认证信息，此接口兼容已上线接口
     * @apiParam {String}   access_token	token
     * @apiParam {String}	userId			医生Id
     * @apiParam {Doctor}	param			医生验证信息
     * @apiParam {String...} doctorsImage   认证图片地址(为空清空用户图片)
     * @apiSuccess {String} resultCode      返回状态码
     * @apiAuthor xuhuanjie
     * @date 2018年1月8日
     */
    @RequestMapping("/addDoctorCheckInfo")
    public JSONMessage addDoctorCheckInfo(Integer userId, Doctor param, String... doctorsImage) throws HttpApiException {
        if (Objects.isNull(userId)) {
            userId = ReqUtil.instance.getUserId();
        }
        if (StringUtil.isBlank(param.getDeptPhone())) {
            throw new ServiceException("科室电话不能为空");
        }
        userManager.updateDoctor(userId, param);
        return this.addDoctorCheckImage(userId, doctorsImage);
    }

    /**
     * @api {[get,post]} /user/updateDoctorCheckInfo
     * @apiVersion 1.0.0
     * @apiName /user/updateDoctorCheckInfo
     * @apiGroup 用户
     * @apiDescription 更新用户认证信息，此接口兼容已上线接口
     * @apiParam {String}   access_token	token
     * @apiParam {String}	userId			医生Id
     * @apiParam {Doctor}	param			医生验证信息
     * @apiParam {String}   oldImageId      老图片id
     * @apiParam {String...}doctorsImage    认证图片地址(为空清空用户图片)
     * @apiSuccess {String} resultCode      返回状态码
     * @apiAuthor xuhuanjie
     * @date 2018年1月8日
     */
    @RequestMapping("/updateDoctorCheckInfo")
    public JSONMessage updateDoctorCheckInfo(Integer userId, Doctor param, Integer oldImageId, String doctorsImage) throws HttpApiException {
        if (Objects.isNull(userId)) {
            userId = ReqUtil.instance.getUserId();
        }
        if (StringUtil.isBlank(param.getDeptPhone())) {
            throw new ServiceException("科室电话不能为空");
        }
        userManager.updateDoctor(userId, param);
        return this.updateDoctorCheckImage(userId, oldImageId, doctorsImage);
    }

    @RequestMapping(value = "/findUserById")
    public JSONMessage findUserById(@RequestParam(defaultValue = "0") int userId) {
        User user = userManager.findUserById(userId == 0 ? ReqUtil.instance.getUserId() : userId);
        return JSONMessage.success(null, user);
    }

    @ApiOperation(value = "运营后台创建运营账号", notes = "运营后台创建运营账号", response = JSONMessage.class)
    @RequestMapping(value = "admin/addUser", method = RequestMethod.POST)
    public JSONMessage addAdminUser(@RequestBody AddAdminUserParam addAdminUserParam) {
        userManager.addAdminUser(addAdminUserParam);
        return JSONMessage.success();
    }

    @ApiOperation(value = "运营后台编辑运营账号", notes = "运营后台编辑运营账号", response = JSONMessage.class)
    @RequestMapping(value = "admin/updateUser", method = RequestMethod.POST)
    public JSONMessage updateAdminUser(@RequestBody AddAdminUserParam addAdminUserParam) {
        userManager.updateAdminUser(addAdminUserParam);
        return JSONMessage.success();
    }

    @ApiOperation(value="重置密码V2", httpMethod="POST", response=JSONMessage.class, notes = "成功返回：\"resultCode：1\"")
	@ApiImplicitParams({
    	@ApiImplicitParam(name="phone", value="电话号码", required=true, dataType="String", paramType="query"),
    	@ApiImplicitParam(name="userType", value="用户类型", required=true, dataType="Integer", paramType="query"),
    	@ApiImplicitParam(name="sign", value="签名", required=true, dataType="String", paramType="query")
    })
    @RequestMapping("/nologin/preResetPasswordV2")
	public JSONMessage preResetPasswordV2(@RequestParam String phone, @RequestParam Integer userType,
			@RequestParam String sign) {
    	if (!ReqUtil.valiSMSSign(phone, sign, accessConfig)) {
			return JSONMessage.failure("签名错误");
		}
        if (userManager.isRegister(phone, userType)) {
            return sendSMSCode(phone, userType);
        }
        return JSONMessage.failure("该手机号码未注册");
    }

    @ApiOperation(value="通过语音验证重置密码V2", httpMethod="POST", response=JSONMessage.class, notes = "成功返回：\"resultCode：1\"")
	@ApiImplicitParams({
    	@ApiImplicitParam(name="phone", value="电话号码", required=true, dataType="String", paramType="query"),
    	@ApiImplicitParam(name="userType", value="用户类型", required=true, dataType="Integer", paramType="query"),
    	@ApiImplicitParam(name="sign", value="签名", required=true, dataType="String", paramType="query")
    })
    @RequestMapping("/nologin/preResetPasswordVoiceCodeV2")
	public JSONMessage preResetPasswordVoiceCodeV2(@RequestParam String phone, @RequestParam Integer userType, @RequestParam String sign) {
    	if (!ReqUtil.valiSMSSign(phone, sign, accessConfig)) {
			return JSONMessage.failure("签名错误");
		}
        if (userRepository.exsistUser(phone, userType)) {
            return sendVoiceCode(phone, userType);
        }
        return JSONMessage.failure("该手机号码未注册");
    }

    @ApiOperation(value = "修改密码发送验证码V2(APP内)", httpMethod = "POST", response = JSONMessage.class, notes = "成功返回：\"resultCode：1\"")
    @ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "String", paramType = "query")
    @RequestMapping("/updatePasswordV2/sendCode")
    public JSONMessage updatePasswordByCode(@RequestParam String sign) {
        User user = userManager.getUser(ReqUtil.instance.getUserId());
        String phone = user.getTelephone();
        Integer userType = user.getUserType();
        if (!ReqUtil.valiSMSSign(phone, sign, accessConfig)) {
            return JSONMessage.failure("签名错误");
        }
        return sendSMSCode(phone, userType);
    }

    @ApiOperation(value = "通过语音修改密码发送验证码V2(APP内)", httpMethod = "POST", response = JSONMessage.class, notes = "成功返回：\"resultCode：1\"")
    @ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "String", paramType = "query")
    @RequestMapping("/updatePasswordV2/sendVoiceCode")
    public JSONMessage updatePasswordByVoiceCode(@RequestParam String sign) {
        User user = userManager.getUser(ReqUtil.instance.getUserId());
        String phone = user.getTelephone();
        Integer userType = user.getUserType();
        if (!ReqUtil.valiSMSSign(phone, sign, accessConfig)) {
            return JSONMessage.failure("签名错误");
        }
        return sendVoiceCode(phone, userType);
    }

    @ApiOperation(value = "修改密码V2(APP内)", httpMethod = "POST", response = JSONMessage.class, notes = "成功返回：\"resultCode：1\"")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ranCode", value = "验证码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "String", paramType = "query")
    })
    @RequestMapping("/updatePasswordV2")
    public JSONMessage updateUserPasswordV2(@RequestParam String smsid, @RequestParam String ranCode, @RequestParam String password) {
        Integer userId = ReqUtil.instance.getUserId();
        User user = userManager.getUser(userId);
        boolean flag = smsRanCodeService.isCorrectCode(user.getTelephone(), smsid, ranCode);
        if (!flag) {
            throw new ServiceException("签名过期，请返回上一步");
        }
        userManager.updatePasswordV2(userId, password);
        return JSONMessage.success();
    }

    @ApiOperation(value="通过短信验证码V2", httpMethod="POST", response=JSONMessage.class, notes = "成功返回：\"resultCode：1\"")
	@ApiImplicitParams({
    	@ApiImplicitParam(name="phone", value="电话号码", required=true, dataType="String", paramType="query"),
    	@ApiImplicitParam(name="userType", value="用户类型", required=true, dataType="Integer", paramType="query"),
    	@ApiImplicitParam(name="sign", value="签名", required=true, dataType="String", paramType="query")
    })
    @RequestMapping("/nologin/sendSMSCodeV2")
    public JSONMessage sendSMSCodeV2(@RequestParam String phone, @RequestParam Integer userType,@RequestParam String sign) {
    	if (!ReqUtil.valiSMSSign(phone, sign, accessConfig)) {
			return JSONMessage.failure("签名错误");
		}
        Map<Object, Object> data = new HashMap<Object, Object>();
        String code = StringUtil.random4Code();
        SMSRanCode ranCode = new SMSRanCode(code);
        smsRanCodeService.save(phone, ranCode);
        data.put("smsid", ranCode.getId());
        boolean success = smsManager.sendRandCode(phone, code);
        if (!success) {
            return JSONMessage.failure("验证码发送失败");
        }
        return JSONMessage.success(null, data);
    }
    
    @ApiOperation(value="发送语音验证码V2", httpMethod="POST", response=JSONMessage.class, notes = "成功返回：\"resultCode：1\"")
	@ApiImplicitParams({
    	@ApiImplicitParam(name="phone", value="电话号码", required=true, dataType="String", paramType="query"),
    	@ApiImplicitParam(name="userType", value="用户类型", required=true, dataType="Integer", paramType="query"),
    	@ApiImplicitParam(name="sign", value="签名", required=true, dataType="String", paramType="query")
    })
    @RequestMapping("/nologin/sendVoiceCodeV2")
    public JSONMessage sendVoiceCodeV2(@RequestParam String phone, @RequestParam Integer userType, @RequestParam String sign) {
    	if (!ReqUtil.valiSMSSign(phone, sign, accessConfig)) {
			return JSONMessage.failure("签名错误");
		}
        logger.info("phone:" + phone + "\t userType:" + userType);
        Map<Object, Object> data = new HashMap<Object, Object>();
        String code = StringUtil.random4Code();
        SMSRanCode ranCode = new SMSRanCode(code);
        smsRanCodeService.save(phone, ranCode);
        data.put("smsid", ranCode.getId());
        boolean success = smsManager.sendVoiceCode(phone, code);
        if (!success) {
            return JSONMessage.failure("验证码发送失败");
        }
        return JSONMessage.success(null, data);
    }

    @RequestMapping(value = "/getUsersByTelList", method = {RequestMethod.POST, RequestMethod.GET})
    public JSONMessage getUsersByTelList(@RequestParam List<String> telList, @RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "15") Integer pageSize) {
        if (CollectionUtils.isEmpty(telList)) {
            return JSONMessage.success();
        }
        return JSONMessage.success(userManager.getUsersByTelList(telList, pageIndex, pageSize));
    }

    @RequestMapping(value = "/getUserSource", method = RequestMethod.GET)
    public JSONMessage getUserSource() {
        return JSONMessage.success(userManager.getUserSource());
    }

    @ApiOperation(value = "增加学习经历(APP)", notes = "增加学习经历(APP)")
    @PostMapping(value = "college/addLearningExp")
    public JSONMessage addLearningExperience(@RequestBody LearningExperienceParam param) {
        logger.info("增加学习经历入参:{}", "param = [" + JSON.toJSONString(param) + "]");
        Integer userId = ReqUtil.instance.getUserId();
        userManager.addLearningExperience(param, userId);
        return JSONMessage.success();
    }

    @ApiOperation(value = "更新学习经历(APP)", notes = "更新学习经历(APP)")
    @PostMapping(value = "college/updateLearningExp")
    public JSONMessage updateLearningExperience(@RequestBody LearningExperienceParam param) {
        logger.info("更新学习经历:{}", "param = [" + JSON.toJSONString(param) + "]");
        userManager.updateLearningExperience(param);
        return JSONMessage.success();
    }

    @ApiOperation(value = "获取学习经历(APP)", notes = "获取学习经历(APP)", response = LearningExperience.class)
    @RequestMapping(value = "college/getLearningExp", method = {RequestMethod.POST, RequestMethod.GET})
    public JSONMessage getLearningExperience() {
        Integer userId = ReqUtil.instance.getUserId();
        return JSONMessage.success(userManager.getLearningExperience(userId));
    }

    @ApiOperation(value = "删除学习经历(APP)", notes = "删除学习经历(APP);成功返回{\"data\": true/false,\"resultCode\": 1,\"resultMsg\": xx}")
    @PostMapping(value = "college/delLearningExp/{id}")
    public JSONMessage delLearningExp(@PathVariable(value = "id") String id) {
        return JSONMessage.success(userManager.delLearningExp(id));
    }

    @ApiOperation(value = "运营后台处理待完善院校信息(Web)", notes = "手动输入的院校不存在预置数据中(Web)")
    @PostMapping(value = "college/checkCustomCollege")
    public JSONMessage checkCustomCollege(@RequestBody CheckCollegeParam param) {
        logger.info("运营后台处理待完善院校信息:{}", "param = [" + JSON.toJSONString(param) + "]");
        userManager.checkCustomCollege(param.getLearningExpId(), param.getCheckCollegeName(), param.getCheckCollegeId(), param.getUserId());
        return JSONMessage.success();
    }

    @ApiOperation(value = "运营后台获取院校信息(Web)", notes = "手动输入的院校不存在预置数据中(Web)", response = CustomCollege.class)
    @PostMapping(value = "college/getCustomCollege")
    public JSONMessage getCustomCollege(@RequestBody CustomCollegeParam param) {
        logger.info("运营后台获取院校信息:{}", "param = [" + JSON.toJSONString(param) + "]");
        return JSONMessage.success(userManager.getCustomCollege(param));
    }

}
