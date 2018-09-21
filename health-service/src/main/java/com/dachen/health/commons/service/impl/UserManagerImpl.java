package com.dachen.health.commons.service.impl;

import com.alibaba.fastjson.JSON;
import com.dachen.common.auth.Auth2Helper;
import com.dachen.common.auth.data.AccessToken;
import com.dachen.common.exception.CommonErrorCode;
import com.dachen.common.exception.CommonException;
import com.dachen.commons.KeyBuilder;
import com.dachen.commons.constants.Constants;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.commons.user.UserSessionService;
import com.dachen.drugorg.api.client.DrugOrgApiClientProxy;
import com.dachen.drugorg.api.entity.CSimpleUser;
import com.dachen.health.base.constant.UserChangeTypeEnum;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.param.OpenDoctorParam;
import com.dachen.health.base.entity.po.Area;
import com.dachen.health.base.entity.po.HospitalPO;
import com.dachen.health.base.entity.vo.AreaVO;
import com.dachen.health.base.entity.vo.DepartmentVO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.base.entity.vo.TitleVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.base.utils.SortByChina;
import com.dachen.health.common.helper.UserHelper;
import com.dachen.health.commons.constants.DoctorTopicEnum;
import com.dachen.health.commons.constants.HospitalLevelEnum;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.Source;
import com.dachen.health.commons.constants.UserEnum.UserLevel;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.constants.UserLogEnum;
import com.dachen.health.commons.dao.UserLogRespository;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.example.UserExample;
import com.dachen.health.commons.example.UserQueryExample;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.*;
import com.dachen.health.commons.vo.User.UserSettings;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.group.doctor.service.ICommonGroupDoctorService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.operationLog.constant.OperationLogTypeDesc;
import com.dachen.health.operationLog.mq.OperationLogMqProducer;
import com.dachen.health.permission.entity.param.UserRoleParam;
import com.dachen.health.permission.entity.po.Permission;
import com.dachen.health.permission.entity.po.Role;
import com.dachen.health.permission.entity.vo.UserRoleVO;
import com.dachen.health.permission.service.IRoleService;
import com.dachen.health.system.entity.param.DoctorCheckParam;
import com.dachen.health.system.service.IDoctorCheckService;
import com.dachen.health.user.UserInfoNotify;
import com.dachen.health.user.entity.param.*;
import com.dachen.health.user.entity.po.*;
import com.dachen.health.user.entity.vo.IsNewAccountVO;
import com.dachen.health.user.entity.vo.OperationRecordVO;
import com.dachen.health.user.entity.vo.SimUserVO;
import com.dachen.health.user.entity.vo.UserInfoVO;
import com.dachen.health.wx.model.WXUserInfo;
import com.dachen.health.wx.remote.WXRemoteManager;
import com.dachen.im.server.data.response.Result;
import com.dachen.lbs.service.NearbyManager;
import com.dachen.lbs.vo.NearbyUser;
import com.dachen.manager.RemoteSysManagerUtil;
import com.dachen.mq.producer.BasicProducer;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mobsms.sdk.MobSmsSdk;
import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import static com.dachen.util.StringUtils.randomCode;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service(UserManagerImpl.BEAN_ID)  @Lazy(true)
public class UserManagerImpl extends NoSqlRepository implements UserManager {
    public static final String BEAN_ID = "UserManagerImpl";
    Logger logger = LoggerFactory.getLogger(UserManagerImpl.class);

    @Autowired
    private Auth2Helper auth2Helper;

    @Autowired
    protected IBusinessServiceMsg businessMsgService;

    @Autowired
    protected NearbyManager nearbyManager;

    @Autowired
    protected IMsgService msgService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ICommonGroupDoctorService commongdService;

    @Resource
    protected MobSmsSdk sdk;

    @Autowired
    protected IBaseDataDao baseDataDao;

    @Resource
    protected WXRemoteManager wxManager;

    @Autowired
    protected IBaseDataService baseDataService;

    @Autowired
    protected MobSmsSdk mobSmsSdk;

    @Autowired
    protected UserLogRespository userLogRespository;

    @Autowired
    protected IDoctorCheckService doctorCheckService;

    @Autowired
    protected DiseaseTypeRepository diseaseTypeRepository;

    @Autowired
    protected UserSessionService userSessionService;

    @Autowired
    private RemoteSysManagerUtil remoteSysManagerUtil;

    @Autowired
    private DrugOrgApiClientProxy drugOrgApiClientProxy;

    @Autowired
    private OperationLogMqProducer operationLogMqProducer;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private  FaceRecognitionService faceRecognitionService;

    @Autowired
    private CircleService circleService;

    @Autowired
    private SendDoctorTopicService sendDoctorTopicService;

    @Autowired
    private UserOperationLogService userOperationLogService;

    @Override
    public User createInactiveUser(String telephone, UserSource userSource) {
        return createInactiveUserWithName(null, telephone, userSource);
    }

    private User createInactiveUserWithName(String name, String telephone, UserSource userSource) {
        User existuser = userRepository.getUserByTelAndType(telephone, UserType.patient.getIndex());
        if (existuser != null) {
            return existuser;
        }
        if (StringUtil.isEmpty(name)) {
            name = telephone;
        }
        User user = new User();

        Integer userId = auth2Helper.addAccount(telephone, "", UserType.patient.getIndex(), null, false);

        user.setUserId(userId);
        user.setName(name);
        user.setTelephone(telephone);
        user.setUserType(UserType.patient.getIndex());
        user.setStatus(UserStatus.inactive.getIndex());
        user.setCreateTime(System.currentTimeMillis());
        user.setModifyTime(System.currentTimeMillis());
        if (userSource != null) {
            user.setSource(userSource);
        }
        userRepository.addUser(user);
        return user;
    }

    @Override
    public User.UserSettings getSettings(int userId) {
        return userRepository.getSettings(userId);
    }

    @Override
    public User getUser(int userId) {
        User user = userRepository.getUser(userId);
        if (null == user)
            throw new ServiceException("用户不存在:" + userId);

        return user;
    }

    @Override
    public User getUserInfoById(int userId) {
        return userRepository.getUser(userId);
    }

    @Override
    public WeChatUserInfo getWeChatUserInfoById(Integer userId) {
        if (userId == null || userId == 0) {
            throw new ServiceException("用户Id不能为空");
        }
        User user = userRepository.getUser(userId);
        if (null == user) {
            throw new ServiceException("用户不存在:" + userId);
        }

        return user.getWeInfo();
    }

    @Override
    public User getUserNoException(int userId) {
        User user = userRepository.getUser(userId);
        return user;
    }

    @Override
    public Integer validatePassword(String password) {
        if (StringUtil.isNotBlank(password)) {
            UserSession us = ReqUtil.instance.getUser();
            if (us != null) {
                return auth2Helper.validatePassword(us.getTelephone(), password, us.getUserType());
            }
        }
        return 0;
    }

    @Override
    public User getUser(int userId, int toUserId) {
        User user = getUser(toUserId);

        // 获取医生对患者的分组标签
        DoctorPatient docPat = dsForRW.createQuery(DoctorPatient.class).field("userId").equal(userId).field("toUserId")
                .equal(toUserId).get();
        if (docPat != null && docPat.getTags() != null) {
            List<String> tags = new ArrayList<String>();
            for (String tag : docPat.getTags()) {
                // 不返回系统标签
                if (!TagUtil.SYS_TAG.contains(tag)) {
                    tags.add(tag);
                }
            }
            user.setTags(tags.toArray(new String[0]));
        }

        Map<String, Object> map = commongdService.getContactBySameGroup(userId, toUserId);
        user.setGroupContact((String) map.get("groupContact"));
        user.setGroupRemark((String) map.get("groupRemark"));
        user.setGroupSame((Integer) map.get("groupSame"));
        try {
            /**
             * 登录成功添加集团相关信息返回 只限定于userType=3的集团账户
             */
            if (UserEnum.UserType.doctor.getIndex() == user.getUserType()) {
                user = commongdService.getGroupListByUserId(user);
                // 判断医院是否三甲
                if (user.getDoctor() != null && StringUtil.isNotBlank(user.getDoctor().getHospitalId())) {
                    HospitalVO hospital = baseDataDao.getHospital(user.getDoctor().getHospitalId());
                    if (hospital != null && hospital.getLevel() != null
                            && hospital.getLevel().equals(HospitalLevelEnum.Three3.getAlias())) {
                        user.setIs3A("1");
                    } else {
                        user.setIs3A("0");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public User getUser(String telephone) {
        return userRepository.getUser(telephone);
    }

    @Override
    public User getUser(String telephone, Integer userType) {
        return userRepository.getUser(telephone, userType);
    }

    @Override
    public List<User> getUserByNameAndTelephoneAndType(User user) {
        return userRepository.getUserByNameAndTelephoneAndType(user.getName(), user.getTelephone(), user.getUserType());
    }

    @Override
    public int getUserId(String accessToken) {
        return 0;
    }

    @Override
    public boolean isRegister(String telephone, int userType) {
        User user = getUser(telephone, userType);
        return isRegister(user);
    }

    public boolean isRegister(User user) {
        if (user != null) {
            if (user.getUserType() == UserType.patient.getIndex()) {
                return user.getStatus() != UserStatus.inactive.getIndex();
            }
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> login(UserExample example) {
        if (example == null) {
            throw new ServiceException("请求参数为空");
        }
        if (StringUtil.isEmpty(example.getTelephone())) {
            throw new ServiceException("帐号不能为空");
        }

        if (example.getUserType() == null) {
            throw new ServiceException("用户类型不能为空");
        }

        // 如果userType为医生，位数不是11位，则去数据库中按照医生号查询
        if (example.getUserType().intValue() == UserEnum.UserType.doctor.getIndex() && example.getTelephone().length() != 11) {
            //根据医生号去查询
            User tempUser = userRepository.findByDoctorNum(example.getTelephone());
            if (tempUser == null) {
                throw new ServiceException(1040101, "帐号不存在");
            } else {
                example.setTelephone(tempUser.getTelephone());
            }
        }

        User user = userRepository.getUser(example.getTelephone(), example.getUserType());

        if (!isRegister(user)) {
            throw new ServiceException(1040101, "帐号不存在");
        }

        // 先判断用户是否需要重置密码、若用户需要重置密码，则返回，否则判断密码是否为空，执行之前的逻辑（2016-05-17傅永德）
        if (user.getNeedReSetPassword() != null && user.getNeedReSetPassword() && !(user.getFarm() != null && user.getFarm().getNeedResetPhoneAndPass())) {
            throw new ServiceException(1040103, "请重置密码");
        } else {

            if (StringUtil.isEmpty(example.getPassword())) {
                throw new ServiceException("密码不能为空");
            }

            if(UserConstant.SuspendStatus.tempForbid.getIndex() == user.getSuspend()){
                throw new ServiceException("您的账号已被管理员封号");
            }

            // 增加未激活状态判断
            if (UserEnum.UserStatus.forbidden.getIndex() == user.getStatus()
                    || UserEnum.UserStatus.offLine.getIndex() == user.getStatus()
                    || UserEnum.UserStatus.logOut.getIndex() == user.getStatus()
                    || UserEnum.UserStatus.inactive.getIndex() == user.getStatus()) {
                throw new ServiceException(1040101, "账号不正常，暂时无法登陆");
            }

            //医生助手，被禁用之后不能登录医生助手app
            if (user.getUserType() == UserType.assistant.getIndex()) {
                if (user.getStatus() == 2) {//status 状态：启用：1；禁用：2
                    throw new ServiceException(1040101, "账号被禁用，暂时无法登录");
                }
            }

            // 影响了医生圈工作台登录（BUG：YSQ-9144）
//            if (user.getFarm() != null && user.getFarm().getNeedResetPhoneAndPass()) {
//                throw new ServiceException(1040104, "请升级您的APP版本");
//            }

            AccessToken tokenVo = auth2Helper.loginByTelAndPwd(example.getTelephone(), example.getPassword(), example.getUserType(), example.getSerial(), ReqUtil.instance.getHeaderInfo().getDeviceType(), ReqUtil.instance.getHeaderInfo().getAppName());

            if (tokenVo == null)
                throw new ServiceException(1040102, "用户名或密码无效");

            setDefaultVoice(user);
            //更新用户最后登录的时间
            userRepository.updateLastLoginTime(user.getUserId());
            return getLoginUserInfo(example, user, tokenVo.getToken());

        }

    }

    /**
     * 验证码登录，未注册不能登录
     */
    public Map<String, Object> loginByCode(UserExample example) {
        User user = userRepository.getUserByTelAndType(example.getTelephone(), example.getUserType());
        if (!isRegister(user)) {
            throw new ServiceException(1040101, "帐号不存在");
        }
        //登录时记录最后登录的时间
        userRepository.updateLastLoginTime(user.getUserId());
        AccessToken tokenVo = auth2Helper.loginByTel(example.getTelephone(), example.getUserType(), false, null, example.getSerial(), ReqUtil.instance.getHeaderInfo().getDeviceType(), ReqUtil.instance.getHeaderInfo().getAppName());
        if (tokenVo == null)
            throw new ServiceException("登录失败");
        return getLoginUserInfo(example, user, tokenVo.getToken());
    }

    /**
     * 验证码登录，未注册用户自动注册登录。区别于loginByCode
     */
    public Map<String, Object> loginByCaptcha(UserExample example) {
        User user = userRepository.getUserByTelAndType(example.getTelephone(), example.getUserType());

        if (!isRegister(user)) {
            example.setName(example.getTelephone());
            return registerIMUser(example);
        }
        //登录时记录最后登录的时间
        userRepository.updateLastLoginTime(user.getUserId());
        AccessToken tokenVo = auth2Helper.loginByTel(example.getTelephone(), example.getUserType(), false, null, example.getSerial(), ReqUtil.instance.getHeaderInfo().getDeviceType(), ReqUtil.instance.getHeaderInfo().getAppName());
        if (tokenVo == null)
            throw new ServiceException("登录失败");
        return getLoginUserInfo(example, user, tokenVo.getToken());
//		return getUserInfo(example, user);
    }

    /**
     * 通过开放平台获取微信用户信息
     */
    public Map<String, Object> getWeChatStatus4Open(String code, Integer userType) {
        WXUserInfo wxinfo = wxManager.getUserInfoByOpen(code);
        return getWeChatStatus(wxinfo, userType);
    }

    /**
     * 通过关注公众号获取微信用户信息
     */
    public Map<String, Object> getWeChatStatus4MP(String code, Integer userType) {
        WXUserInfo wxinfo = wxManager.getUserInfoByPub(code);
        return getWeChatStatus(wxinfo, userType);
    }

    /**
     * 把私有（private）改成公共（public），仅仅为了做自动化测试
     *
     * @param wxinfo
     * @param userType
     * @return
     */
    public Map<String, Object> getWeChatStatus(WXUserInfo wxinfo, Integer userType) {
        Map<String, Object> map = Maps.newHashMap();
        AccessToken tokenVo = auth2Helper.getWechatToken(wxinfo.getUnionid(), userType, null, null, null);
        if (tokenVo == null) {
            map.put("wechatStatus", "1");//未绑定
        } else {
            //已绑定，直接登录，返回登录信息
            map.put("wechatStatus", "2");//已绑定
            //更新历史数据openid、mpOpenid
            userRepository.updateWeChatInfo(tokenVo.getUserId(), BeanUtil.copy(wxinfo, WeChatUserInfo.class));
            User user = getUser(tokenVo.getUserId());
            UserExample example = BeanUtil.copy(user, UserExample.class);
            map.putAll(getLoginUserInfo(example, user, tokenVo.getToken()));
        }
        return map;
    }

    /**
     * 微信登录（开放平台）
     */
    public Map<String, Object> loginByWeChat4Open(UserExample example, String code) throws HttpApiException {
        WXUserInfo wxinfo = wxManager.getUserInfoByOpen(code);
        return loginByWeChat(example, wxinfo);
    }

    /**
     * 微信登录（公众号）
     */
    public Map<String, Object> loginByWeChat4MP(UserExample example, String code) throws HttpApiException {
        WXUserInfo wxinfo = wxManager.getUserInfoByPub(code);
        return loginByWeChat(example, wxinfo);
    }

    /**
     * 把私有（private）改成公共（public），仅仅为了做自动化测试
     *
     * @param example
     * @param wxinfo
     * @return
     */
    public Map<String, Object> loginByWeChat(UserExample example, WXUserInfo wxinfo) throws HttpApiException {
        if (example.getUserType() == null) {
            example.setUserType(UserType.patient.getIndex());
        }
        AccessToken tokenVo = auth2Helper.getWechatToken(wxinfo.getUnionid(), example.getUserType(), example.getSerial(), ReqUtil.instance.getHeaderInfo().getDeviceType(), ReqUtil.instance.getHeaderInfo().getAppName());
//		AuthVO auth = authManager.getAuth(AccountTypeEnum.wechat.name(), wxinfo.getUnionid(), example.getUserType());
        if (tokenVo != null) {//已绑定
            //更新历史数据openid、mpOpenid
            userRepository.updateWeChatInfo(tokenVo.getUserId(), BeanUtil.copy(wxinfo, WeChatUserInfo.class));
            User user = getUser(tokenVo.getUserId());
            example = BeanUtil.copy(user, UserExample.class);
            return getLoginUserInfo(example, user, tokenVo.getToken());
        }
        User user = userRepository.getUserByTelAndType(example.getTelephone(), example.getUserType());
        if (!isRegister(user)) {//未注册

            //过滤特殊字符, 过滤完之后的字符数少于2则用用户的手机号码作为玄关用户昵称
            String nickName = filterNickName(wxinfo.getNickname());
            if (nickName.length() < 2) {
                example.setName(example.getTelephone());
            } else {
                example.setName(nickName);
            }

            example.setSex(Integer.valueOf(wxinfo.getSex()));
            example.setWeUserInfo(BeanUtil.copy(wxinfo, WeChatUserInfo.class));
            String headimgurl = uploadHeadPic(wxinfo.getHeadimgurl());
            example.setHeadPicFileName(headimgurl);
            //手机号注册
            Map<String, Object> data = registerIMUser(example);
            if (data == null || data.get("user") == null) {
                return data;
            }
            user = (User) data.get("user");

            //微信公众号登录注册的用户可修改名字
            User rename = new User();
            rename.setUserId(user.getUserId());
            rename.setRename(1);
            user = userRepository.updateUser(rename);

            data.put("user", user);

            //登录时记录最后登录的时间
            userRepository.updateLastLoginTime(user.getUserId());
            auth2Helper.bindWeChat(user.getUserId(), wxinfo.getUnionid(), user.getUserType(), example.getSerial(), ReqUtil.instance.getHeaderInfo().getDeviceType(), ReqUtil.instance.getHeaderInfo().getAppName());
            if (StringUtil.isNotBlank(headimgurl)) {
                userRepository.headPicModifyNotify(user);
            }
            data.put("hasPassword", hasPwd(user));
            // 返回用户是否通过人身检测
            data.put("faceRec", faceRecognitionService.passFaceRec(user.getUserId()));
            // 异步发送Topic消息队列
            sendDoctorTopicService.sendLoginTopicMes(user, ReqUtil.getVersion(), DoctorTopicEnum.DoctorLoginTypeEnum.LoginByPassWord.getIndex());
            try {
                // 返回灰度方案
                data.put("grayVisibility", remoteSysManagerUtil.getNotJson(UserEnum.SEND_GRAY_VISIBILITY_URL, ReqUtil.instance.getHeaderInfo().getDeviceType(), ReqUtil.getVersion(), user.getTelephone()));
            } catch (Exception e) {
                logger.error("ex:{},userId:{}" + e.getMessage(), user.getUserId());
            }
            return data;
        } else {//已注册
            //未绑定，绑定微信
//			authManager.bindWeChat(user.getUserId(), user.getUserType(), wxinfo.getUnionid());
            tokenVo = auth2Helper.bindWeChat(user.getUserId(), wxinfo.getUnionid(), user.getUserType(), example.getSerial(), ReqUtil.instance.getHeaderInfo().getDeviceType(), ReqUtil.instance.getHeaderInfo().getAppName());
            //更新微信用户信息
            userRepository.updateWeChatInfo(user.getUserId(), BeanUtil.copy(wxinfo, WeChatUserInfo.class));
            //博德嘉联不使用了，取消登录时的用户信息更新
            //更新用户的端信息
//			updateTerminal(user);

            //登录时记录最后登录的时间
            userRepository.updateLastLoginTime(user.getUserId());
            return getLoginUserInfo(example, user, tokenVo.getToken());
//			return getUserInfo(example, user);
        }
    }

    /**
     * 是否设置了密码
     *
     * @param user
     * @return
     */
    private boolean hasPwd(User user) {
        Integer bindFlag = auth2Helper.isSetupPwd(user.getUserId());
        if (bindFlag != null && bindFlag.intValue() == 1)
            return true;
        return false;
    }

    public boolean isBindWechat(String telephone, Integer userType) {
        User user = userRepository.getUserByTelAndType(telephone, userType);
        if (user == null) {
            return false;
        }
        Integer bindFlag = auth2Helper.isBindWechat(telephone, userType);
        if (bindFlag != null && bindFlag.intValue() == 1)
            return true;
        return false;
    }

    private String uploadHeadPic(String headimgurl) {
        try {
            byte[] bytes = FileUtil.getImageFromURL(headimgurl);
            String key = QiniuUtil.upload(bytes, StringUtil.randomUUID(), "avatar");
            return MessageFormat.format(QiniuUtil.QINIU_URL(), "avatar", key);
        } catch (Exception e) {
            logger.error("微信登录注册账号时头像上传七牛失败！", e);
        }
        return null;
    }

    private Map<String, Object> getLoginUserInfo(UserExample example, User user, String accessToken) {
        // 获取用户加入的圈子信息
        Future<String> future = circleService.findCircleByUserId(user.getUserId());
        // 获取上次登录日志
        User.LoginLog login = userRepository.getLogin(user.getUserId());
        // 保存登录日志
        userRepository.updateLogin(user.getUserId(), example);
        Map<String, Object> data = new HashMap<>();
        data.put("access_token", accessToken);
        data.put("expires_in", Constants.Expire.DAY7);

        data.put("userId", user.getUserId());

        data.put("hasPassword", hasPwd(user));

        if (Objects.isNull(login)) {
            login = new User.LoginLog();
        }
        login.setLoginTime(System.currentTimeMillis());

        data.put("login", login);
        data.put("user", user);
        // 返回用户是否通过人身检测
        data.put("faceRec", faceRecognitionService.passFaceRec(user.getUserId()));
        data.put("_tk", UserHelper.createTk("" + user.getUserId()));
        // 异步发送Topic消息队列
        sendDoctorTopicService.sendLoginTopicMes(user, ReqUtil.getVersion(), DoctorTopicEnum.DoctorLoginTypeEnum.LoginByPassWord.getIndex());

        try {
            //设置导医关联的医生集团，只限定userType=6的集团导医账户
            if (user.getDoctorGuider() != null && StringUtils.isNotBlank(user.getDoctorGuider().getGroupId())) {
                List<Map<String, Object>> groupList = commongdService.getGroupListForDocGuide(user);
                user.setGroupList(groupList);
            }
            if (UserType.doctor.getIndex() == user.getUserType()) {
                user.setLoginGroupId(example.getLoginGroupId());
                user = commongdService.getGroupListByUserId(user);
                String circleJson = future.get(3, TimeUnit.SECONDS);
                if (circleJson != null) {
                    List<CircleVO> circles = JSONUtil.parseList(CircleVO.class, circleJson);
                    // 返回加入圈子信息
                    data.put("circle", circles);
                    // 同步AccessToken中的圈子ID
                    circleService.syncCircleToTokenInfoWhenLogin(circles, user.getUserId());
                }
                /* 返回灰度方案 web 版本号写死*/
                String deviceType = ReqUtil.instance.getHeaderInfo().getDeviceType();
                String version = ReqUtil.getVersion();
                if ("web".equalsIgnoreCase(deviceType)) {
                    version = "1.0.0";
                }
                data.put("grayVisibility", remoteSysManagerUtil.getNotJson(UserEnum.SEND_GRAY_VISIBILITY_URL, deviceType, version, user.getTelephone()));
            }

        } catch (TimeoutException e) {
            logger.error("+++++++++超时！" + e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        // 保存登录日志
        try {
			// 登录成功后注册该设备信息到im中 add by chengwei
            boolean invalid = false;
            if (user != null && user.getSettings() != null) {
                int ispushflag = user.getSettings().getIspushflag();
                if (ispushflag == 2) {
                    // 不接收消息提醒
                    invalid = true;
                }
            }
            String deviceToken = example.getSerial();
            String client = example.getModel();
            msgService.registerDeviceToken(deviceToken, client, user.getUserId(), invalid);
        } catch (Exception e) {
        }
        return data;
    }

    @Override
    public Map<String, Object> loginAuto(String access_token, int userId, String serial) {
        try {
            boolean tokenExists = false;
            //自动登录从auth中获取token，不从缓存中获取（ReqUtil.instance.getUserIdFromCache）
            AccessToken tokenVo = auth2Helper.validateToken(access_token);
            if (tokenVo != null)
                tokenExists = true;
            Map<String, Object> result = Maps.newHashMap();
            if (tokenExists) {
                // 异步获取医生的圈子信息
                Future<String> future = circleService.findCircleByUserId(userId);

                User user = this.getUser(userId);
                User.LoginLog loginLog = user.getLoginLog();
                int serialStatus = null == loginLog ? 1 : (serial.equals(loginLog.getSerial()) ? 2 : 3);
                loginLog.setLoginTime(System.currentTimeMillis());

                setDefaultVoice(user);
                result.put("access_token", tokenVo.getToken());
                result.put("serialStatus", serialStatus);
                result.put("userId", userId);
                result.put("name", user.getName());
                result.put("login", loginLog);
                result.put("user", user);
                result.put("_tk", UserHelper.createTk("" + user.getUserId()));
                result.put("telephone",user.getTelephone());
                // 返回用户是否通过人身检测
                result.put("faceRec", faceRecognitionService.passFaceRec(user.getUserId()));
                result.put("hasPassword", hasPwd(user));
                if (UserType.doctor.getIndex() == user.getUserType()) {
                    //设置开通赠送服务情况
                    if (user.getDoctor() != null && user.getDoctor().getCheckInGive() == null) {
                        user.getDoctor().setCheckInGive(1);
                    }
                    try {
                        user = commongdService.getGroupListByUserId(user);
                        String circleJson = future.get(3, TimeUnit.SECONDS);
                        if (circleJson != null) {
                            List<CircleVO> circles = JSONUtil.parseList(CircleVO.class, circleJson);
                            // 返回加入圈子信息
                            result.put("circle", circles);
                            // 同步AccessToken中的圈子ID
                            circleService.syncCircleToTokenInfoWhenLogin(circles, userId);
                        }
                        // 返回灰度方案
                        result.put("grayVisibility", remoteSysManagerUtil.getNotJson(UserEnum.SEND_GRAY_VISIBILITY_URL, ReqUtil.instance.getHeaderInfo().getDeviceType(), ReqUtil.getVersion(), user.getTelephone()));
                    } catch (TimeoutException e) {
                        logger.error("+++++++++超时！" + e.getMessage(), e);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }

                try {
                    msgService.updateDeviceToken(serial);
                    //自动登录也更新用户最后的登录时间
                    userRepository.updateLastLoginTime(userId);
                    // 异步发送Topic消息队列
                    sendDoctorTopicService.sendLoginTopicMes(user, ReqUtil.getVersion(), DoctorTopicEnum.DoctorLoginTypeEnum.LoginAuto.getIndex());
                } catch (Exception e) {
                }
            }
            // 1=令牌存在、0=令牌不存在
            result.put("tokenExists", (tokenExists ? 1 : 0));

            return result;
        } catch (NullPointerException e) {
            throw new ServiceException("帐号不存在");
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void logout(String accessToken, String serial) {
        try {
            auth2Helper.logout(accessToken);
            msgService.removeDeviceToken(serial);
            User user = userRepository.getUser(ReqUtil.instance.getUserId());
            // 异步发送Topic消息队列
            sendDoctorTopicService.sendLoginTopicMes(user, ReqUtil.getVersion(), DoctorTopicEnum.DoctorLoginTypeEnum.LoginOut.getIndex());
        } catch (Exception e) {
        }
    }

    @Override
    public List<DBObject> query(UserQueryExample param) {
        return userRepository.queryUser(param);
    }

    @Override
    public Map<String, Object> register(UserExample example) {
        // 缓存用户，判断是否为激活用户
        User inactionUser = getUser(example.getTelephone(), example.getUserType());
        if (isRegister(inactionUser)) {
            throw new ServiceException("手机号已被注册");
        }
        // 生成userId
        Integer userId = auth2Helper.addAccount(example.getTelephone(), example.getPassword(), example.getUserType(), null, false);
        // 新增用户
        Map<String, Object> data = userRepository.addUser(userId, example);

        if (null != data) {

            try {
                NearbyUser userPoi = new NearbyUser();
                userPoi.setBirthday(example.getBirthday());
                userPoi.setDescription(example.getDescription());
                userPoi.setLatitude(example.getLatitude());
                userPoi.setLongitude(example.getLongitude());
                userPoi.setNickname(example.getNickname());
                userPoi.setSex(example.getSex());
                userPoi.setUserId(userId);
                nearbyManager.saveUser(userPoi);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("nearbyManager#saveUser fail...", e);
            }

            return data;
        }
        throw new ServiceException("用户注册失败");
    }

    @Override
    public Map<String, Object> registerIMUser(UserExample example) {
        if (StringUtils.isBlank(example.getTelephone()) || !CheckUtils.checkMobile(example.getTelephone())) {
            throw new ServiceException("请输入正确的手机号码");
        }
        // 缓存用户，判断是否为激活用户
        User inactionUser = userRepository.getUserByTelAndType(example.getTelephone(), example.getUserType());
        if (isRegister(inactionUser)) {
            throw new ServiceException("该手机号码已注册");
        }

        String pwd = "";
        //导医和医生助手
        if (example.getUserType() == UserEnum.UserType.DocGuide.getIndex()
                || example.getUserType() == UserEnum.UserType.assistant.getIndex()) {
            pwd = StringUtil.randomPassword();
            example.setPassword(pwd);
        }

        AccessToken tokenVo = null;
        if (inactionUser == null) {
            example.setAddUser(true);
            // 生成userId
            tokenVo = auth2Helper.registerAndLogin(example.getTelephone(), example.getPassword(), example.getUserType(), null, example.getSerial(), ReqUtil.instance.getHeaderInfo().getDeviceType(), ReqUtil.instance.getHeaderInfo().getAppName());
        } else {
            example.setAddUser(false);
            tokenVo = auth2Helper.resetPassword(inactionUser.getUserId(), example.getPassword(), example.getSerial(), ReqUtil.instance.getHeaderInfo().getDeviceType(), ReqUtil.instance.getHeaderInfo().getAppName());
        }

        //若inactionUser用户不为空，则将example中的UserSource置为空（2016-6-8傅永德）
        if (null != inactionUser && inactionUser.getUserType() == UserEnum.UserType.patient.getIndex()) {
            example.setUserSource(null);
        }

        // 添加用户
        Map<String, Object> data = userRepository.addUser(tokenVo.getUserId(), example);
        
        data.put("access_token", tokenVo.getToken());
        data.put("expires_in", Constants.Expire.DAY7);
        if (null != data) {
            User user = (User) data.get("user");
            if (user != null && user.getUserType() == UserType.patient.getIndex()) {
                // 1、发送欢迎卡片；
                UserInfoNotify.registerSuccessNotify(user.getUserId());
                // 2、创建本人患者信息
                if (user.getUserType() == UserType.patient.getIndex()) {
                    UserInfoNotify.patientInfoModifyNotify(user.getUserId());
                }

                if (inactionUser != null && inactionUser.getStatus() == UserStatus.inactive.getIndex()) {
                    // 1、通知医生其邀请的用户已注册；2、更新用户名下的订单为已激活
                    UserInfoNotify.userActivateNotify(user.getUserId());
                }
            }
            // 发送短信
            if (user != null && user.getUserType() == UserEnum.UserType.DocGuide.getIndex()) {
                String content = "您的导医账户已开通，初始密码为" + pwd + "。您可以通过当前手机号登录账户，请尽快修改密码。";
                sdk.send(example.getTelephone(), content);
            }
            // 新增医生助手，发送短信
            if (user != null && user.getUserType() == UserEnum.UserType.assistant.getIndex()) {
                String content = "您的医生助手账户已开通，初始密码为" + pwd + "。您可以通过当前手机号登录账户，请尽快修改密码。";
                sdk.send(example.getTelephone(), content);
                UserInfoNotify.registerSuccessNotify(user.getUserId());
            }
            //通知药店圈，用户已经注册
            if (user.getUserType() == UserEnum.UserType.patient.getIndex()) {
                if (user.getSource() != null) {
                    if (user.getSource().getSourceType().intValue() == UserEnum.Source.drugStore.getIndex()) {
                        UserInfoNotify.notifyPatientMessageUpdate(user.getUserId());
                    }
                }
            }
            /* doctor register */
            if (Objects.nonNull(user) && Objects.equals(UserType.doctor.getIndex(), user.getUserType())) {
                /* 用户信息注册通知 */
                user.setOpenId(tokenVo.getOpenId());
                UserInfoNotify.notifyUserRegister(user);
                // 异步发送Topic消息队列
                sendDoctorTopicService.sendRegisterTopicMes(user);
                // 返回用户是否通过人身检测
                data.put("faceRec", faceRecognitionService.passFaceRec(user.getUserId()));
            }
            data.put("hasPassword", hasPwd(user));
            return data;
        }
        throw new ServiceException("用户注册失败");
    }

    @Override
    public Map<String, Object> registerByWechat(UserExample example, String code) {
        WXUserInfo wxinfo = wxManager.getUserInfoByPub(code);
        example.setWeUserInfo(BeanUtil.copy(wxinfo, WeChatUserInfo.class));
        return registerIMUser(example);
    }

    public Integer registerGroup(UserExample example,
                                 String groupId, Integer inviteId) {
        // 验证参数
        if (StringUtil.isBlank(example.getTelephone())) {
            throw new ServiceException("手机号为空");
        }
        if (StringUtil.isBlank(example.getPassword())) {
            throw new ServiceException("密码为空");
        }
        if (StringUtil.isBlank(example.getName())) {
            throw new ServiceException("姓名为空");
        }
        if (StringUtil.isBlank(groupId) || "null".equals(groupId)) {
            throw new ServiceException("集团为空");
        }
        if (inviteId == null || inviteId == 0) {
            logger.error("groupId:'" + groupId + "',inviteId:'" + inviteId + "',inviteId is null");
            // throw new ServiceException("邀请人为空");
        }
        // 判断集团和邀请人是否存在
        DBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(groupId));
        } catch (Exception e) {
            throw new ServiceException("集团id有误");
        }

        DBObject groupObj = dsForRW.getDB().getCollection("c_group").findOne(query);
        if (groupObj == null) {
            throw new ServiceException("集团不存在");
        }
        if (inviteId != null && !"null".equals(inviteId)) {
            query.put("_id", Integer.valueOf(inviteId));
            DBObject userObj = dsForRW.getDB().getCollection("user").findOne(query);
            if (userObj == null) {
                logger.error("groupId:'" + groupId + "'邀请人不存在");
            }
        }
        // 注册用户
        example.setUserType(UserEnum.UserType.doctor.getIndex());
        Map<String, Object> data = registerIMUser(example);
        // 医生id
        Integer userId = (Integer) data.get("userId");
        return userId;
    }

    public Integer registerByAdmin(UserExample example, String groupId) {
        // 验证参数
        if (StringUtil.isBlank(example.getTelephone())) {
            throw new ServiceException("手机号为空");
        }

        // 校验手机号码格式
        boolean b = CheckUtils.checkMobile(example.getTelephone());
        if (b == false) {
            throw new ServiceException("请输入正确的手机号码");
        }

        if (StringUtil.isBlank(example.getName())) {
            throw new ServiceException("姓名为空");
        }
        if (StringUtil.isBlank(groupId) || "null".equals(groupId)) {
            throw new ServiceException("集团为空");
        }
        // 判断集团和邀请人是否存在
        DBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(groupId));
        } catch (Exception e) {
            throw new ServiceException("集团id有误");
        }

        DBObject groupObj = dsForRW.getDB().getCollection("c_group").findOne(query);
        if (groupObj == null) {
            throw new ServiceException("集团不存在");
        }

        // 注册用户
        example.setUserType(UserEnum.UserType.doctor.getIndex());
        Map<String, Object> data = registerIMUser(example);

        // 医生id
        Integer userId = (Integer) data.get("userId");
        return userId;

    }

    @Override
    public UpdateResults updateName(int userId, String newName) {
        Query<User> query = dsForRW.find(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("name", newName);
        ops.set("modifyTime", new Date().getTime());
        UpdateResults ret = dsForRW.update(query, ops);
        // 用户信息修改通知
        UserInfoNotify.notifyUserUpdate(userId);
        User user = new User();
        user.setUserId(userId);
        user.setName(newName);
        //userRepository.updateUserRedisCache(user);
        userRepository.removeUserRedisCache(user.getUserId());
        return ret;
    }

    @Override
    public void updatePassword(int userId, String oldPassword, String newPassword) {
        Result result = auth2Helper.updatePassword(ReqUtil.instance.getToken(), userId, newPassword, oldPassword, true);
        if(!result.sucess())
            throw new ServiceException(result.getResultMsg());
    }

    public boolean existsUser(String telephone, Integer userType) {
        if (userRepository.exsistUser(telephone, userType)) {
            return true;
        }
        return false;
    }

    @Override
    public void updateTel(int userId, String telephone) {
        User user = userRepository.getUser(userId);

        User check = userRepository.getUser(telephone, user.getUserType());
        if (check != null) {
            throw new ServiceException("该手机号已被注册。");
        }
        //修改业务数据 手机号
        userRepository.updateTel(userId, telephone);

        String msg = null;
        if (user.getUserType() == UserType.doctor.getIndex()) {
            msg = baseDataService.toContent("1045", "医生圈");
        }
        if (user.getUserType() == UserType.patient.getIndex()) {
            msg = baseDataService.toContent("1045", "玄关健康");
        }

        mobSmsSdk.send(user.getTelephone(), msg, BaseConstants.XG_YSQ_APP);
        mobSmsSdk.send(telephone, msg, BaseConstants.XG_YSQ_APP);

        Integer id = ReqUtil.instance.getUserId();
        OperationRecord operationRecord = new OperationRecord();
        if (null == id || id.intValue() == 0) {
            //若获取不到当前登录用户的id，则以传来的userId来进行存储
            operationRecord.setCreator(userId);
        } else {
            operationRecord.setCreator(id);
        }
        operationRecord.setCreateTime(System.currentTimeMillis());
        operationRecord.setObjectId(userId + "");
        operationRecord.setObjectType(UserLogEnum.OperateType.update.getOperate());
        operationRecord.setChange(new Change(UserLogEnum.infoType.telephone.getType(), "手机号码", user.getTelephone().toString(), telephone.toString()));
        userLogRespository.addOperationRecord(operationRecord);

        logger.info("用户#{}修改手机号为{}", userId, telephone);
        //修改账号数据 手机号
        auth2Helper.updateAccount(null, userId, telephone, true);
        if (user.getUserType() == UserType.patient.getIndex()) {
            UserInfoNotify.patientInfoModifyNotify(user.getUserId());
        }
    }

    @Override
    public boolean updateSettings(int userId, UserSettings userSettings) {
        DBObject id = new BasicDBObject("_id", userId);
        DBObject values = new BasicDBObject();
        if (userSettings != null) {
            userSettings.verifys();

        } else {
            throw new ServiceException("userSetting is null");
        }

        if (userSettings.getAllowAtt() != null) {
            values.put("settings.allowAtt", userSettings.getAllowAtt());
        }
        if (userSettings.getAllowGreet() != null) {
            values.put("settings.allowGreet", userSettings.getAllowGreet());
        }
        if (userSettings.getFriendsVerify() != null) {
            values.put("settings.friendsVerify", userSettings.getFriendsVerify());
        }
        if (userSettings.getIspushflag() != null) {
            values.put("settings.ispushflag", userSettings.getIspushflag());
        }
        if (userSettings.getNeedAssistant() != null) {
            values.put("settings.needAssistant", userSettings.getNeedAssistant());
        }
        if (userSettings.getPatientVerify() != null) {
            values.put("settings.patientVerify", userSettings.getPatientVerify());
        }
        if (userSettings.getDoctorVerify() != null) {
            values.put("settings.doctorVerify", userSettings.getDoctorVerify());
        }
        if (userSettings.getDispMsgDetail() != null) {
            values.put("settings.dispMsgDetail", userSettings.getDispMsgDetail());
        }
        DBObject o = new BasicDBObject("$set", values);
        dsForRW.getCollection(User.class).update(id, o);
        return true;
    }

    @Override
    public User updateUser(int userId, UserExample param) throws HttpApiException {
        User updateUser = userRepository.updateUser(userId, param);
        businessMsgService.userChangeNotify(UserChangeTypeEnum.PROFILE_CHANGE, userId, null);
        return updateUser;
    }

    @Override
    public User updateDoctor(int userId, Doctor param) throws HttpApiException {
        User updateDoctor = userRepository.updateDoctor(userId, param);
        businessMsgService.userChangeNotify(UserChangeTypeEnum.PROFILE_CHANGE, userId, null);
        return updateDoctor;
    }

    @Override
    public User updateAssistant(int userId, Assistant param) throws HttpApiException {
        User updateAssistant = userRepository.updateAssistant(userId, param);
        businessMsgService.userChangeNotify(UserChangeTypeEnum.PROFILE_CHANGE, userId, null);
        return updateAssistant;
    }

    @Override
    public UpdateResults updatePassword(String phone, Integer userType, String password) {
        Query<User> query = dsForRW.find(User.class).field("telephone").equal(phone).field("userType").equal(userType);
        User dbUser = query.get();
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);

        // 重置密码时，将需要重置密码的值变为false（2015-05-17傅永德）
        ops.set("needResetPassword", new Boolean(false));
        if (dbUser != null && dbUser.getFarm() != null) {
            Map<String, Object> farm = Maps.newHashMap();
            farm.put("needResetPhoneAndPass", new Boolean(false));
            ops.set("farm", farm);
            /**清理当前用户登录的token**/
//			authComponent.clearToken(dbUser.getUserId());
        }
        UpdateResults ret = dsForRW.update(query, ops);
        auth2Helper.resetPassword(dbUser.getUserId(), password, null, null, null);
        return ret;

    }

    public boolean setRemarks(Integer userId, String remarks) {
        return userRepository.setRemarks(userId, remarks);
    }

    public String getRemarks(Integer userId) {
        return getUser(userId).getRemarks();
    }

    @Override
    public User getRemindVoice(Integer userId) {
        User user = getUser(userId);
        setDefaultVoice(user);
        return user;
    }

    /**
     * 设置默认铃声
     *
     * @param user
     */
    private void setDefaultVoice(User user) {
        if (user == null)
            return;
        if (user.getUserConfig() == null) {
            UserConfig userConfig = new UserConfig();
            userConfig.setRemindVoice("sound_bell01.mp3");// 默认铃声
            user.setUserConfig(userConfig);
        } else if (user.getUserConfig().getRemindVoice() == null) {
            user.getUserConfig().setRemindVoice("sound_bell01.mp3");// 默认铃声
        }
    }

    @Override
    public UserSession getUserById(Integer id) {
        UserSession user = ReqUtil.instance.getUser(id);

        if (Objects.isNull(user)) {
            User tempUser = userRepository.getUser(id);

            if (Objects.nonNull(tempUser)) {
                user = new UserSession();
                user.setUserId(id);
                user.setName(tempUser.getName());
                user.setUserType(tempUser.getUserType());
                user.setHeadPicFileName(tempUser.getHeadPicFileName());
                user.setBirthday(tempUser.getBirthday());
                user.setTelephone(tempUser.getTelephone());
                user.setSex(tempUser.getSex());
                user.setStatus(tempUser.getStatus());
                user.setCreateTime(tempUser.getCreateTime());
            }
            if (Objects.nonNull(user)) {
                try {
                    userSessionService.storeUserSessionInCache(id);
                } finally {
                    // jedisPool.returnResource(resource);
                }
            }
        }

        return user;
    }

    @Override
    public List<User> getHeaderPicName(List<Integer> userIdList) {
        return userRepository.getHeaderPicName(userIdList);
    }

    @Override
    public List<UserInfoVO> getHeaderByUserIds(List<Integer> userIds) {

        // 批量查询用户name headPicFileName 用户类型，科室、医生集团
        // 首先要从id筛选出是患者还是医生。

		/*
		 * 医生 查询 == 用户name headPicFileName 用户类型，科室、医生集团 非医生 == 用户name
		 * headPicFileName 用户类型
		 */

        // VOs
        List<UserInfoVO> userInfoVOs = new ArrayList<UserInfoVO>();

        // 查询出医生的资料
        if (userIds != null && userIds.isEmpty() == false) {
            List<User> users = userRepository.findUsers(userIds);
            for (User _user : users) {
                UserInfoVO userInfoVO = new UserInfoVO();
                userInfoVO.userId = _user.getUserId();
                userInfoVO.userType = _user.getUserType();
                userInfoVO.name = _user.getName();
                userInfoVO.headPicFileName = _user.getHeadPicFileName();

                if (_user.getUserType() == 3) {
                    // 是医生
                    if (_user.getDoctor() != null) {
                        userInfoVO.departmentsName = _user.getDoctor().getDepartments();
                        userInfoVO.title = _user.getDoctor().getTitle();
                    }
                    // 查询GroupId
                    String groupId = commongdService.getGroupIdByUser(String.valueOf(_user.getUserId()));
                    // 通过GroupId得到doctorGroupName
                    userInfoVO.doctorGroupName = commongdService.getGroupNameByGroupId(groupId);
                }
                // ADD VO
                userInfoVOs.add(userInfoVO);
            }
        }

        return userInfoVOs;
    }

    /**
     * ispushflag是否接收通知：1正常接收，2不接收
     */
    @Override
    public boolean updateSettings(int userId, String serial, Integer ispushflag) {
        DBObject id = new BasicDBObject("_id", userId);
        DBObject values = new BasicDBObject();
        values.put("settings.ispushflag", ispushflag);
        DBObject o = new BasicDBObject("$set", values);
        dsForRW.getCollection(User.class).update(id, o);
        // TODO
        msgService.updatePushStatus(userId, serial, ispushflag == 1 ? false : true);
        return true;
    }

    @Override
    public void registerDeviceToken(int userId, String deviceToken, String model) {
        boolean invalid = false;
        User user = userRepository.getUser(userId);
        if (user != null && user.getSettings() != null) {
            int ispushflag = user.getSettings().getIspushflag();
            if (ispushflag == 2) {
                // 不接收消息提醒
                invalid = true;
            }
        }
        msgService.registerDeviceToken(deviceToken, model, userId, invalid);
    }

    @Override
    public boolean updateStatus(Integer userId) {
        User user = userRepository.getUser(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        if (user.getUserType() != UserType.doctor.getIndex()) {
            throw new ServiceException("用户类型错误");
        }
        userRepository.updateSubmitTime(userId, System.currentTimeMillis());
        return userRepository.updateStatus(userId, UserStatus.uncheck.getIndex());
    }

    @Override
    public boolean updateInvitationInfo(Integer userId, Integer inviterId, Integer subsystem, String way, Boolean deptInvitation) {
        User user = userRepository.getUser(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        if (user.getUserType() != UserType.doctor.getIndex()) {
            throw new ServiceException("用户类型错误");
        }
        return userRepository.updateInvitationInfo(userId, inviterId, subsystem, way, deptInvitation);
    }

    @Override
    public User getDoctorByTelOrNum(String number) {
        return userRepository.getDoctorByTelOrNum(number);
    }

    @Override
    public User getDoctorByTelOrNumNoStatus(String number) {
        return userRepository.getDoctorByTelOrNumNoStatus(number);
    }

    @Override
    public PageVO searchConsultationDoctors(Set<Integer> doctorIds, List<String> hospitalIds, String name,
                                            String deptId, Integer pageIndex, Integer pageSize) {
        PageVO pageVo = new PageVO();
        long count = userRepository.searchConsultationDoctorsCount(doctorIds, hospitalIds, name, deptId);
        pageIndex = pageIndex == null ? pageVo.getPageIndex() : pageIndex;
        pageSize = pageSize == null ? pageVo.getPageSize() : pageSize;
        List<User> list = userRepository.getConsultationDoctors(doctorIds, hospitalIds, name, deptId, pageIndex,
                pageSize);
        pageVo.setTotal(count);
        pageVo.setPageData(list);
        return pageVo;
    }

    @Override
    public List<User> getDoctorsByIds(List<Integer> doctorIds) {
        return userRepository.getDoctorsByIds(doctorIds);
    }

    @Override
    public PageVO fingDoctors(int flag, List<Integer> doctorList, Integer cityId, String hospitalId, String title,
                              String deptId, Integer pageIndex, Integer pageSize) {
        PageVO pageVo = new PageVO();
        if (null != pageIndex && pageIndex > 0) {
            pageVo.setPageIndex(pageIndex);
        }
        if (null != pageSize && pageSize > 0) {
            pageVo.setPageSize(pageSize);
        }
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        long count = 0;
        if (null != doctorList && doctorList.size() > 0) {
            count = userRepository.fingTotalDoctorCount(flag, doctorList, cityId, hospitalId, title, deptId, pageIndex,
                    pageSize);
            list = userRepository.fingDoctorList(flag, doctorList, cityId, hospitalId, title, deptId, pageIndex,
                    pageSize);
        }

        pageVo.setTotal(count);
        pageVo.setPageData(list);
        return pageVo;
    }

    @Override
    public PageVO searchConsultationDoctorsByKeyword(Set<Integer> beSearchIds, String keyword, Integer pageIndex,
                                                     Integer pageSize) {
        PageVO pageVo = new PageVO();
        long count = userRepository.searchConsultationDoctorsByKeywordCount(beSearchIds, keyword);
        pageIndex = pageIndex == null ? pageVo.getPageIndex() : pageIndex;
        pageSize = pageSize == null ? pageVo.getPageSize() : pageIndex;
        List<User> list = userRepository.searchConsultationDoctorsByKeyword(beSearchIds, keyword, pageIndex, pageSize);
        pageVo.setTotal(count);
        pageVo.setPageData(list);
        return pageVo;
    }

    @Override
    public PageVO fingDoctorByKeyWord(String keyWord, Integer pageIndex, Integer pageSize, List<Integer> doctorList) {
        PageVO pageVo = new PageVO();
        if (null != pageIndex && pageIndex > 0) {
            pageVo.setPageIndex(pageIndex);
        }
        if (null != pageSize && pageSize > 0) {
            pageVo.setPageSize(pageSize);
        }
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        long count = 0;
        count = userRepository.fingDoctorByKeyWord(keyWord, pageIndex, pageSize, doctorList);
        list = userRepository.fingDoctorByKeyWordList(keyWord, pageIndex, pageSize, doctorList);
        pageVo.setTotal(count);
        pageVo.setPageData(list);
        return pageVo;
    }


    @Override
    public PageVO searchAppointmentDoctor(String keyWord, Integer pageIndex, Integer pageSize, List<Integer> doctorList, List<Integer> keywordUserIds) {
        PageVO pageVo = new PageVO();
        if (null != pageIndex && pageIndex > 0) {
            pageVo.setPageIndex(pageIndex);
        }
        if (null != pageSize && pageSize > 0) {
            pageVo.setPageSize(pageSize);
        }
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        long count = 0;
        count = userRepository.searchAppointmentDoctorCount(keyWord, pageIndex, pageSize, doctorList, keywordUserIds);
        list = userRepository.searchAppointmentDoctor(keyWord, pageIndex, pageSize, doctorList, keywordUserIds);
        pageVo.setTotal(count);
        pageVo.setPageData(list);
        return pageVo;
    }

    public List<User> searchDoctor(String[] hospitalIdList, String keyword, String departments) {
        return userRepository.searchDoctor(hospitalIdList, keyword, departments);
    }

    public List<User> searchOkStatusDoctor(String[] hospitalIdList, String keyword, String departments) {
        return userRepository.searchOkStatusDoctor(hospitalIdList, keyword, departments);
    }

    @Override
    public List<User> searchDoctorByKeyword(String keyword, String departments) {
        return userRepository.searchOkStatusDoctorByKeyword(keyword, departments);
    }

    @Override
    public List<Integer> getDoctorsByNameOrHospitalNameOrTelephone(String doctorName, String hospitalName, String telephone) {
        return userRepository.getDoctorsByNameOrHospitalNameOrTelephone(doctorName, hospitalName, telephone);
    }

    @Override
    public User getUser(int userId, String name) {

        return userRepository.getUser(userId, name);
    }

    @Override
    public List<Integer> getUserIdList(String name) {
        return userRepository.getUserIdList(name);
    }

    @Override
    public void oneKeyReset(int userId, String telephone, String pwd) {
        User user = userRepository.getUser(userId);
        if (user != null) {
            auth2Helper.resetPassword(user.getUserId(), pwd, null, null, null);
            String content = "您的导医账户密码已重置成功，新的密码为:" + pwd + "。您可以通过当前手机号登录账户。";
            sdk.send(telephone, content);
        } else {
            throw new ServiceException("找不到对应用户 : " + userId);
        }
    }

    @Override
    public void updateGuideInfo(int userId, String telephone, String username) {
        // 更新成功之后要是电话号码发生了变化就要去短信通知用户
        User us = this.getUser(userId);
        if (!telephone.equals(us.getTelephone())) {
            if (isRegister(telephone, UserEnum.UserType.DocGuide.getIndex()))
                throw new ServiceException("手机号已被注册");
            String content = "您的导医账户手机号已更改，您可以通过当前手机号登录账户。";
            sdk.send(telephone, content);
            // CMSClient.send(telephone,content);//发送短消息
        }
        User user = new User();
        user.setUserId(userId);
        user.setName(username);
        user.setTelephone(telephone);
        user.setUserType(UserEnum.UserType.DocGuide.getIndex());
        userRepository.updateUser(user);
    }

    @Override
    public List<User> findDoctorsInIds(List<Integer> packFriendIds, Integer pageIndex, Integer pageSize) {
        return userRepository.findDoctorsInIds(packFriendIds, pageIndex, pageSize);
    }

    @Override
    public long findDoctorsInIdsCount(List<Integer> packFriendIds) {
        if (packFriendIds != null && packFriendIds.size() > 0) {
            return userRepository.findDoctorsInIdsCount(packFriendIds);
        }
        return 0l;
    }

    public void updateGuideStatus(Integer userId, int state) {
        User user = userRepository.getUser(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        userRepository.updateStatus(userId, state);
    }

    @Override
    public String getGroupNameById(String id) {
        String groupName = "";
        DBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(id));
        } catch (Exception e) {
            throw new ServiceException("集团id有误");
        }
        DBObject groupObj = dsForRW.getDB().getCollection("c_group").findOne(query);
        if (groupObj == null) {
            throw new ServiceException("集团不存在");
        }
        groupName = MongodbUtil.getString(groupObj, "name");
        return groupName;
    }

    @Override
    public PageVO findDoctorsByCondition(DoctorParam param) {
        return userRepository.findDoctorsByCondition(param);
    }

    @Override
    public PageVO findDoctorsByOrgId(String appKey, String orgId, String hospitalId, String deptId,
        String name,
        Integer userId, Integer status,
        Long ts,
        Integer pageIndex, Integer pageSize) {
        //调用药企圈接口判断企业Id与appKey是否是绑定过的
        try {
            Boolean result = drugOrgApiClientProxy.checkAppKey(appKey, orgId);
            if (Objects.equals(result, Boolean.FALSE)) {
                throw new ServiceException("企业Id与appKey未绑定");
            }
        } catch (HttpApiException e) {
            logger.error(e.getMessage(), e);
        }

        String kangzheOrgId = PropertiesUtil.getContextProperty("kangzhe.org.id");
        if (StringUtil.equals(kangzheOrgId, orgId)) {
            //如果企业Id是康哲的企业Id，则返回所有医生信息
            return userRepository.findDoctorsByOrgId(orgId, hospitalId, deptId, name,userId, status, ts, pageIndex, pageSize);
        }
        return new PageVO(new ArrayList<>(), 0L, 0, 10);
    }

    public PageVO findDoctorInfoByModifyTime(OpenDoctorParam param) {
        PageVO pageVO = userRepository.findDoctorInfoByModifyTime(param);
        List<User> users = (List<User>) pageVO.getPageData();


        List<Integer> userIdList = Lists.transform(users, x -> x.getUserId());

        Map<Integer, String> openIdMap = new HashMap<>();
        try {
            List<AccessToken> accessTokenList = auth2Helper.getOpenIdList(userIdList);
            if (!CollectionUtils.isEmpty(accessTokenList)) {
                for (AccessToken at : accessTokenList) {
                    if (Objects.nonNull(at.getUserId())) {
                        openIdMap.put(at.getUserId(), at.getOpenId());
                    }
                }
            }
        } catch (Exception ex) {
            logger.info(ex.getMessage(), ex);
        }


        List<OpenDoctorExtVO> doctors = new ArrayList<>();
        for (User user : users) {
            if (user == null || user.getDoctor() == null) {
                continue;
            }
            OpenDoctorExtVO vo = BeanUtil.copy(user, OpenDoctorExtVO.class);
            Doctor doctor = user.getDoctor();
            vo.setDoctorNum(doctor.getDoctorNum());
            vo.setDeptId(doctor.getDeptId());
            vo.setDepartments(doctor.getDepartments());
            vo.setDeptPhone(doctor.getDeptPhone());
            vo.setHospitalId(doctor.getHospitalId());
            vo.setHospital(doctor.getHospital());
            vo.setTitleRank(doctor.getTitleRank());
            vo.setTitle(doctor.getTitle());
            vo.setOpenId(openIdMap.get(user.getUserId()));
            doctors.add(vo);
        }
        return new PageVO(doctors, pageVO.getTotal(), param.getPageIndex(), param.getPageSize());
    }

    @Override
    public PageVO findDoctorsByName(String keyword, Integer pageIndex, Integer pageSize) {

        return userRepository.findDoctorsByName(keyword, pageIndex, pageSize);
    }

    @Override
    public PageVO findHospitalByCondition(DoctorParam param) {
        return userRepository.findHospitalByCondition(param);
    }

    @Override
    public List<Map<String, Object>> findHospitalByConditionNoPaging(String keywords) {
        return userRepository.findHospitalByConditionNoPaging(keywords);
    }

    @Override
    public PageVO getPlatformSelectedDoctors(List<Integer> notInDoctorIds, String keyWord, Integer pageIndex,
                                             Integer pageSize) {
        return userRepository.getPlatformSelectedDoctors(notInDoctorIds, keyWord, pageIndex, pageSize);
    }

    @Override
    public List<User> getUserByTypeAndFuzzyName(Integer userType, String name) {

        return null;
    }

    @Override
    public List<User> getNormalUser(int type) {
        return userRepository.getNormalUser(type);
    }

    @Override
    public PageVO getNormalUserPaging(Integer type, Integer pageIndex, Integer pageSize) {
        PageVO pageVO = userRepository.getNormalUserPaging(type, pageIndex, pageSize);
        List<User> userList = (List<User>) pageVO.getPageData();
        if (!CollectionUtils.isEmpty(userList)) {
            for (User user : userList) {
                if (!CollectionUtils.isEmpty(user.getRoleIds())) {
                    List<Role> roleList = roleService.getByIds(user.getRoleIds());
                    user.setRoleList(roleList);
                }
            }
        }

        return pageVO;
    }

    @Override
    public List<User> getUserByTypeAndNameInUserIds(Integer userType, Integer userStatus, String name,
                                                    List<Integer> userIds) {
        if (UserType.getEnum(userType) == null)
            throw new ServiceException("枚举类型不正确");
        return userRepository.getUserByTypeAndNameInUserIds(userType, userStatus, name, userIds);
    }

    @Override
    public void updateTerminal(User user) {
        if (null == user) {
            throw new ServiceException("用户为空");
        }

        UserExample example = new UserExample();
        UserSource userSource = user.getSource();
        if (null == userSource) {
            userSource = new UserSource();
        }
        if (ReqUtil.instance.isBDJL()) {
            userSource.setTerminal(UserEnum.Terminal.bdjl.getIndex());
        } else {
            userSource.setTerminal(UserEnum.Terminal.xg.getIndex());
        }
        example.setUserSource(userSource);
        user = userRepository.updateUserNotChangeStatus(user.getUserId(), example);
        userRepository.terminalModifyNotify(user);
    }

    /**
     * 更新医生助手信息
     */
    @Override
    public void updateFeldsherInfo(Integer userId, String telephone, String name) {
        // 更新成功之后要是电话号码发生了变化就要去短信通知用户
        User us = this.getUser(userId);
        if (!telephone.equals(us.getTelephone())) {
            if (isRegister(telephone, UserEnum.UserType.assistant.getIndex()))
                throw new ServiceException("手机号已被注册");
            String content = "您的导医账户手机号已更改，您可以通过当前手机号登录账户。";
            sdk.send(telephone, content);
        }
        User user = new User();
        user.setUserId(userId);
        user.setName(name);
        user.setTelephone(telephone);
        user.setUserType(UserEnum.UserType.assistant.getIndex());
        userRepository.updateUser(user);
        //修改账号
//		authManager.updateTel(userId, telephone);
        auth2Helper.updateAccount(null, userId, telephone, true);
    }

    @Override
    public void updateFeldsherStatus(Integer userId, int status) {
        User user = userRepository.getUser(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        if (status != 1 && status != 2) {
            throw new ServiceException("参数status错误");
        }
        //绑定有医生的助手不可以禁用
        if (status == 2) {
            List<User> doctorList = queryDoctorsByAssistantId(null, userId);
            if (doctorList != null && doctorList.size() > 0) {
                throw new ServiceException("绑定有医生的助手不可以禁用");
            }
        }
        userRepository.updateFeldsherStatus(userId, status);
    }

    @Override
    public PageVO getFeldsherList(String keywords, Integer pageIndex, Integer pageSize, Integer userType) {
        //参数校验
        if (userType == null || userType == 0) {
            throw new ServiceException("参数userType不能为空");
        }
        return userRepository.getFeldsherList(keywords, pageIndex, pageSize, userType);
    }

    @Override
    public List<Map<String, String>> getAvailableFeldsherList(Integer userType) {
        //参数校验
        if (userType == null || userType == 0) {
            throw new ServiceException("参数userType不能为空");
        }
        List<User> userList = userRepository.getAvailableFeldsherList(userType);
        List<Map<String, String>> feldsherList = new ArrayList<>();
        if (userList != null && userList.size() > 0) {
            for (User user : userList) {
                Map<String, String> feldsherMap = new HashMap<>();
                feldsherMap.put("userId", user.getUserId() + "");
                feldsherMap.put("name", user.getName());

                feldsherList.add(feldsherMap);
            }
        }
        return feldsherList;
    }

    @Override
    public User getFeldsherByDoctor(Integer doctorId) {
        //参数校验
        if (doctorId == null || doctorId == 0) {
            throw new ServiceException("参数doctorId不能为空");
        }
        User user = userRepository.getUser(doctorId);
        //每个医生都必须有唯一一个医生助手
        if (user.getDoctor() == null || user.getDoctor().getAssistantId() == null || user.getDoctor().getAssistantId() == 0) {
            throw new ServiceException("参数doctorId不合法");
        }
        return userRepository.getUser(user.getDoctor().getAssistantId());
    }

    @Override
    public List<User> queryDoctorsByAssistantId(String keywords, Integer assistantId) {
        if (assistantId == null || assistantId == 0) {
            throw new ServiceException("用户不存在");
        }
        List<User> list = userRepository.queryDoctorsByAssistantId(keywords, assistantId);
        setExpertise(list);
        //按姓名排序
        Collections.sort(list, new SortByChina<User>("name"));
        return list;
    }

    private void setExpertise(List<User> list) {
        for (User u : list) {
            Doctor doc = u.getDoctor();
            String diseaseStr = "";
            if (doc != null) {
                List<String> diseaseTypeIds = doc.getExpertise();
                if (diseaseTypeIds != null && !diseaseTypeIds.isEmpty()) {
                    List<DiseaseType> diseaseTypes = diseaseTypeRepository.findByIds(diseaseTypeIds);
                    for (DiseaseType d : diseaseTypes) {
                        diseaseStr += d.getName() + ",";
                    }
                    if (diseaseStr.endsWith(",")) {
                        diseaseStr = diseaseStr.substring(0, diseaseStr.length() - 1);
                    }
                }
            }
            if (StringUtil.isEmpty(diseaseStr) && StringUtil.isEmpty(doc.getSkill())) {
                doc.setSkill("暂无");
            } else {
                String skill = doc.getSkill() == null ? "" : doc.getSkill();
                doc.setSkill(diseaseStr + "\n" + skill);
            }

        }
    }

    @Override
    public List<User> findUserByIds(List<Integer> ids) {
        return userRepository.findUserList(ids);
    }

    @Override
    public List<User> getUsers(List<Integer> userIdList) {
        return userRepository.getUsers(userIdList);
    }

    @Override
    public PageVO getAllUserIdByUserType(Integer userType, Integer pageIndex, Integer pageSize) {
        return userRepository.getAllUserIdByUserType(userType, pageIndex, pageSize);
    }

    @Override
    public PageVO getRecord(String id, String keyword, Integer pageIndex, Integer pageSize) {
        if (id == null) {
            throw new ServiceException("用户ID不能为空");
        }

        List<OperationRecordVO> result = Lists.newArrayList();

        DBObject recordQuery = new BasicDBObject();
        if (StringUtil.isNotEmpty(keyword)) {
            BasicDBList key = new BasicDBList();
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            key.add(new BasicDBObject("content", pattern));
            recordQuery.put(QueryOperators.OR, key);
        }

        recordQuery.put("objectId", id);

        DBCursor rCursor = dsForRW.getDB().getCollection("t_user_record").find(recordQuery).sort(new BasicDBObject("createTime", -1)).skip(pageIndex * pageSize).limit(pageSize);
        long count = dsForRW.getDB().getCollection("t_user_record").find(recordQuery).count();
        while (rCursor.hasNext()) {
            DBObject obj = rCursor.next();
            OperationRecordVO vo = new OperationRecordVO();
            vo.setCreateTime(MongodbUtil.getLong(obj, "createTime"));
            vo.setContent(MongodbUtil.getString(obj, "content"));
            Integer creator = MongodbUtil.getInteger(obj, "creator");

            User user = userRepository.getUser(creator);
            if (user != null) {
                vo.setCreatorName(user.getName());
            }
            result.add(vo);
        }
        return new PageVO(result, count, pageIndex, pageSize);
    }

    @Override
    public List<Integer> getDoctorIds(String name) {
        return userRepository.getDoctorIds(name, UserType.doctor.getIndex());
    }

    @Override
    public List<Integer> getUserIdsByName(String name, Integer userType) {
        if (StringUtil.isEmpty(name)) {
            throw new ServiceException("参数name不能为空");
        }
        if (userType == null || userType == 0) {
            throw new ServiceException("参数userType不能为空");
        }

        return userRepository.getUserIdsByName(name, userType);
    }

    @Override
    public Map<String, Object> createDoctors(InputStream inputStream, String nurseShortLinkUrl, Integer adminId, String type) throws HttpApiException {
        Map<String, Object> userData = this.checkUserData(inputStream, nurseShortLinkUrl, adminId, type);

        List<UserExcelError> error = (List<UserExcelError>) userData.get("error");
        List<UserExcelData> data = (List<UserExcelData>) userData.get("data");

        Map<String, Object> result = Maps.newHashMap();

        if (error != null && error.size() > 0) {
            result.put("error", error);
            return result;
        } else {
            //处理新增用户的逻辑

            if (data == null) {
                throw new ServiceException("数据错误");
            }

            if (data != null && data.size() > 500) {
                throw new ServiceException("导入用户数量超出限制");
            }

            UserSource userSource = new UserSource();

            int hasRegisteredCount = 0;
            int registerCount = 0;

            // new userId List
            List<Integer> userIds = Lists.newArrayList();

            for (UserExcelData user : data) {
                //先判断用户是否存在
                User inactionUser = userRepository.getUserByTelAndType(user.getPhone(), UserEnum.UserType.doctor.getIndex());
                if (isRegister(inactionUser)) {
                    hasRegisteredCount++;
                    continue;
                }
                //新增用户
                UserExample userExample = new UserExample();
                userExample.setName(user.getName());
                userExample.setTelephone(user.getPhone());
                userExample.setUserType(UserEnum.UserType.doctor.getIndex());

                if (user.getDoctorSource() != null && user.getDoctorSource().intValue() == 1) {
                    //农牧项目
                    userSource.setSourceType(UserEnum.Source.farmAdminLot.getIndex());
                    Farm farm = new Farm();
                    farm.setNeedResetPhoneAndPass(new Boolean(true));
                    farm.setIsFarm(new Boolean(true));
                    userExample.setFarm(farm);
                    userExample.setNeedResetPassword(new Boolean(false));
                } else {
                    userSource.setSourceType(UserEnum.Source.checkAdminLot.getIndex());
                    userExample.setNeedResetPassword(new Boolean(true));
                }
                userSource.setTerminal(UserEnum.Terminal.xg.getIndex());
                userExample.setUserSource(userSource);
                userExample.setAddUser(true);
                userExample.setPassword("654321");
                userExample.setDoctorNum(user.getDoctorNum());

                // 生成userId
//				Integer userId = getKey(inactionUser, userExample);
                Integer userId = auth2Helper.addAccount(userExample.getTelephone(), userExample.getPassword(), userExample.getUserType(), null, false);
                userIds.add(userId);

                //再更新用户信息
                userRepository.addUser(userId, userExample);
                
                OperationRecord operationRecord = new OperationRecord();
                operationRecord.setCreateTime(System.currentTimeMillis());
                operationRecord.setCreator(adminId);
                operationRecord.setObjectId(userId + "");

                if (user.getDoctorSource() != null && user.getDoctorSource().intValue() == 1) {
                    operationRecord.setObjectType(UserLogEnum.OperateType.farm.getOperate());
                    operationRecord.setContent("农牧项目批量导入。");
                    operationRecord.setChange(new Change(UserLogEnum.OperateType.farm.getOperate(), "农牧项目批量导入。", null, "农牧项目批量导入。"));
                } else {
                    operationRecord.setObjectType(UserLogEnum.OperateType.inport.getOperate());
                    operationRecord.setContent("运营批量导入。");
                    operationRecord.setChange(new Change(UserLogEnum.OperateType.inport.getOperate(), "运营批量导入。", null, "运营批量导入。"));
                }

                userLogRespository.addOperationRecord(operationRecord);

                //再调用更新接口
                Doctor doctor = new Doctor();
                doctor.setDepartments(user.getDepartment());
                doctor.setHospital(user.getHospital());
                doctor.setHospitalId(user.getHospitalId());
                doctor.setDeptId(user.getDepartmentId());
                doctor.setTitle(user.getTitle());
                doctor.setCity(user.getCityName());
                doctor.setCityId(user.getCity());
                doctor.setProvince(user.getProvinceName());
                doctor.setProvinceId(user.getProvince());
                doctor.setCountry(user.getCountryName());
                doctor.setCountryId(user.getCountry());
                doctor.setAssistantId(user.getAssistantId());
                doctor.setSkill(user.getSkill());
                doctor.setIntroduction(user.getIntroduction());
                userExample.setSex(user.getSex());
                userExample.setDoctor(doctor);
                updateUser(userId, userExample);

                //再调用审核接口
                DoctorCheckParam doctorCheckParam = new DoctorCheckParam();
                User admin = getUser(adminId);
                doctorCheckParam.setChecker(admin.getName());
                doctorCheckParam.setCheckerId(admin.getUserId());
                doctorCheckParam.setNurseShortLinkUrl(nurseShortLinkUrl);
                doctorCheckParam.setAssistantId(user.getAssistantId());
                doctorCheckParam.setUserId(userId);
                doctorCheckParam.setTitle(user.getTitle());
                doctorCheckParam.setName(user.getName());
                doctorCheckParam.setHospitalId(user.getHospitalId());
                doctorCheckParam.setHospital(user.getHospital());
                doctorCheckParam.setDepartments(user.getDepartment());
                doctorCheckParam.setDeptId(user.getDepartmentId());
                doctorCheckParam.setRole(1);
                doctorCheckParam.setAssistantId(user.getAssistantId());
                doctorCheckParam.setSendSMS(false);
                doctorCheckParam.setSkill(doctor.getSkill());
                doctorCheckParam.setIntroduction(doctor.getIntroduction());
                doctorCheckParam.setSex(user.getSex());
                doctorCheckParam.setCheckTime(System.currentTimeMillis());
                doctorCheckParam.setRemark("运营后台批量导入医生");
                doctorCheckService.checked(doctorCheckParam);
                registerCount++;
            }
            // 批量 get set openId
            List<AccessToken> accessTokenList = auth2Helper.getOpenIdList(userIds);
            if (!CollectionUtils.isEmpty(accessTokenList)) {
                Map<Integer, AccessToken> accessTokenMap = Maps.uniqueIndex(accessTokenList, AccessToken::getUserId);
                if (!CollectionUtils.isEmpty(accessTokenMap)) {
                    for (Integer userId : userIds) {
                        /* 用户信息注册通知 */
                        User userDB = getUser(userId);
                        AccessToken accessToken = accessTokenMap.get(userId);
                        if (Objects.nonNull(accessToken) && Objects.nonNull(userDB)) {
                            userDB.setOpenId(accessToken.getOpenId());
                            UserInfoNotify.notifyUserRegister(userDB);
                        }
                    }
                }
            }
            result.put("hasRegisteredCount", hasRegisteredCount);
            result.put("registerCount", registerCount);
            return result;
        }

    }

    public static int getWordCount(String s) {
        if (StringUtils.isEmpty(s)) {
            return 0;
        }
        s = s.replaceAll("[^\\x00-\\xff]", "**");
        int length = s.length();
        return length;
    }

    public static String getLastEigth(String str) {
        return str.substring(str.length() - 8, str.length());
    }

    @Override
    public Map<String, Object> checkUserData(InputStream inputStream, String nurseShortLinkUrl, Integer adminId, String fileType) {
        Workbook workbook = null;

        try {
            if (StringUtils.equals("xls", fileType)) {
                workbook = new HSSFWorkbook(inputStream);
            } else if (StringUtils.equals("xlsx", fileType)) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                throw new ServiceException("文件类型错误");
            }
        } catch (IOException e) {
            throw new ServiceException("文件类型错误");
        }

        Sheet sheet = workbook.getSheetAt(0);
        int rowStart = sheet.getFirstRowNum();
        int rowEnd = sheet.getLastRowNum();

        List<UserExcelData> data = Lists.newArrayList();
        List<UserExcelError> error = Lists.newArrayList();

        //查询出全部的医院
        List<HospitalVO> hospitalVOs = baseDataDao.getAllHospitals();
        Map<String, HospitalVO> hospitalMap = Maps.newHashMap();
        hospitalVOs.forEach((hospital) -> {
            hospitalMap.put(hospital.getName(), hospital);
        });
        //询出全部的科室
        List<DepartmentVO> departmentVOs = baseDataDao.getAllDepartments();
        Map<String, DepartmentVO> departmentMap = Maps.newHashMap();
        departmentVOs.forEach((department) -> {
            departmentMap.put(department.getName(), department);
        });
        //查询出全部的职称
        List<TitleVO> titleVOs = baseDataDao.getAllTitles();
        Map<String, TitleVO> titleMap = Maps.newHashMap();
        titleVOs.forEach((tempTitle) -> {
            titleMap.put(tempTitle.getName(), tempTitle);
        });
        //查出全部的地区
        List<AreaVO> areas = baseDataDao.getAllAreas();
        Map<Integer, String> areaMap = Maps.newHashMap();
        areas.forEach((area) -> {
            areaMap.put(area.getCode(), area.getName());
        });
        //查出全部的医生助手
        List<User> assistantList = userRepository.getAvailableFeldsherList(UserEnum.UserType.assistant.getIndex());
        Set<String> assistantNameSet = Sets.newHashSet();
        Map<String, Integer> assistantMap = Maps.newHashMap();
        assistantList.forEach((assistant) -> {
            assistantNameSet.add(assistant.getName());
        });
        assistantNameSet.forEach((name) -> {
            assistantList.forEach((assistant) -> {
                if (StringUtils.equals(name, assistant.getName())) {
                    assistantMap.put(name, assistant.getUserId());
                }
            });
        });
        //查出以前农牧项目的医生的医生号
        Set<String> farmDoctorNumSet = userRepository.getFarmUserDoctorNums();
        // 标题行
        List<String> title = Lists.newArrayList();

        //标题行
        Row titleRow = sheet.getRow(rowStart);
        int titleCellStart = titleRow.getFirstCellNum();
        int titleCellEnd = titleRow.getLastCellNum();

        for (int j = titleCellStart; j <= titleCellEnd; j++) {
            Cell cell = titleRow.getCell(j);
            if (null == cell) {
                continue;
            }
            String value = null;
            if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                value = cell.getStringCellValue();
                title.add(value);
            } else {
                throw new ServiceException("导入文件格式错误！");
            }
        }

        Set<String> titleSet = Sets.newConcurrentHashSet();
        titleSet.add("姓名*");
        titleSet.add("手机号码*");
        titleSet.add("医疗机构名称*");
        titleSet.add("科室*");
        titleSet.add("职称*");
        titleSet.add("性别");
        titleSet.add("个人简介");
        titleSet.add("擅长");
        titleSet.add("医生助手*");
        titleSet.add("医生来源*");

        int nameColumn = 0;
        int phoneColumn = 1;
        int hospitalColumn = 2;
        int departmentColumn = 3;
        int titleColumn = 4;
        int sexColumn = 5;
        int introductionColumn = 6;
        int skillColumn = 7;
        int assistantColumn = 8;
        int doctorSourceColumn = 9;

        StringBuffer titleError = new StringBuffer();

        for (String titleTemp : titleSet) {
            if (!title.contains(titleTemp)) {
                if (titleTemp.contains("*")) {
                    titleTemp = titleTemp.replace("*", "");
                }
                titleError.append(titleTemp).append("、");
            }
        }
        if (titleError != null && titleError.length() > 0) {
            titleError.setLength(titleError.length() - 1);
            throw new ServiceException("导入文件格式错误，" + titleError.toString() + "字段缺失！");
        }

        for (int i = 0; i < title.size(); i++) {
            String tempTitle = title.get(i);
            if (StringUtils.equals("姓名*", tempTitle)) {
                nameColumn = i;
            } else if (StringUtils.equals("手机号码*", tempTitle)) {
                phoneColumn = i;
            } else if (StringUtils.equals("医疗机构名称*", tempTitle)) {
                hospitalColumn = i;
            } else if (StringUtils.equals("科室*", tempTitle)) {
                departmentColumn = i;
            } else if (StringUtils.equals("职称*", tempTitle)) {
                titleColumn = i;
            } else if (StringUtils.equals("性别", tempTitle)) {
                sexColumn = i;
            } else if (StringUtils.equals("个人简介", tempTitle)) {
                introductionColumn = i;
            } else if (StringUtils.equals("擅长", tempTitle)) {
                skillColumn = i;
            } else if (StringUtils.equals("医生助手*", tempTitle)) {
                assistantColumn = i;
            } else if (StringUtils.equals("医生来源*", tempTitle)) {
                doctorSourceColumn = i;
            }
        }

        Integer columns[] = {nameColumn, phoneColumn, hospitalColumn, departmentColumn, titleColumn, sexColumn, introductionColumn, skillColumn, assistantColumn, doctorSourceColumn};
        Integer columnMax = Collections.max(Arrays.asList(columns));
        Integer columnMin = Collections.min(Arrays.asList(columns));

        Set<String> thisTimeDoctorNum = Sets.newHashSet();

        // 由于第一行是标题，固不参与
        for (int i = (rowStart + 1); i <= rowEnd; i++) {
            Row row = sheet.getRow(i);
            if (null == row) {
                continue;
            }
            //获取行的第一列时，若第一列为空，则会出问题，固直接从标题行的最小值开始,到标题行的最大值结束。 row.getFirstCellNum(); row.getLastCellNum();
            int cellStart = columnMin.intValue();
            int cellEnd = columnMax.intValue();

            UserExcelData userExcelData = new UserExcelData();
            UserExcelError userExcelError = new UserExcelError();
            userExcelError.setRow(i + 1);
            List<String> errorMessage = Lists.newArrayList();

            for (int k = cellStart; k <= cellEnd; k++) {
                Cell cell = row.getCell(k);
                if (cell != null) {
                    String value = null;
                    if (k == nameColumn) {
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            value = cell.getStringCellValue();
                            // 校验医生姓名长度
                            if (StringUtils.isNotEmpty(value)) {
                                if (getWordCount(value) > UserExcelData.NAME_LENGTH) {
                                    errorMessage.add("姓名长度最多10个汉字");
                                } else {
                                    userExcelData.setName(value);
                                }
                            } else {
                                errorMessage.add("姓名为空");
                            }
                        } else {
                            errorMessage.add("姓名字段格式不符合要求");
                        }
                    } else if (k == phoneColumn) {
                        //校验手机号码
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                            DecimalFormat df = new DecimalFormat("0");
                            value = df.format(cell.getNumericCellValue());
                            if (CheckUtils.checkMobile(value)) {
                                String doctorNum = getLastEigth(value);
                                if (!farmDoctorNumSet.contains(doctorNum) && !thisTimeDoctorNum.contains(doctorNum) && !doctorNum.startsWith("9")) {
                                    userExcelData.setPhone(value);
                                    userExcelData.setDoctorNum(doctorNum);
                                    thisTimeDoctorNum.add(doctorNum);
                                } else {
                                    errorMessage.add("医生号码重复");
                                }
                            } else {
                                errorMessage.add("手机号码格式错误");
                            }
                        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            value = cell.getStringCellValue();
                            if (CheckUtils.checkMobile(value)) {
                                String doctorNum = getLastEigth(value);
                                if (!farmDoctorNumSet.contains(doctorNum) && !thisTimeDoctorNum.contains(doctorNum) && !doctorNum.startsWith("9")) {
                                    userExcelData.setPhone(value);
                                    userExcelData.setDoctorNum(doctorNum);
                                    thisTimeDoctorNum.add(doctorNum);
                                } else {
                                    errorMessage.add("医生号码重复");
                                }
                            } else {
                                errorMessage.add("手机号码格式错误");
                            }
                        } else {
                            errorMessage.add("手机号码字段格式不符合要求");
                        }
                    } else if (k == hospitalColumn) {
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            value = cell.getStringCellValue();
                            if (StringUtils.isNotEmpty(value) && hospitalMap.containsKey(value)) {
                                userExcelData.setHospital(value);
                                HospitalVO hospitalVO = hospitalMap.get(value);
                                userExcelData.setHospitalId(hospitalVO.getId());
                                userExcelData.setProvince(hospitalVO.getProvince());
                                userExcelData.setProvinceName(areaMap.get(hospitalVO.getProvince()));
                                userExcelData.setCity(hospitalVO.getCity());
                                userExcelData.setCityName(areaMap.get(hospitalVO.getCity()));
                                userExcelData.setCountry(hospitalVO.getCountry());
                                userExcelData.setCountryName(areaMap.get(hospitalVO.getCountry()));
                            } else {
                                errorMessage.add("医疗机构不存在");
                            }
                        } else {
                            errorMessage.add("医疗机构字段格式不符合要求");
                        }
                    } else if (k == departmentColumn) {
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            value = cell.getStringCellValue();
                            if (StringUtils.isNotEmpty(value) && departmentMap.containsKey(value)) {
                                userExcelData.setDepartment(value);
                                DepartmentVO departmentVO = departmentMap.get(value);
                                userExcelData.setDepartmentId(departmentVO.getId());
                            } else {
                                errorMessage.add("科室不存在");
                            }
                        } else {
                            errorMessage.add("科室字段格式不符合要求");
                        }
                    } else if (k == titleColumn) {
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            value = cell.getStringCellValue();
                            if (StringUtils.isNotEmpty(value) && titleMap.containsKey(value)) {
                                userExcelData.setTitle(value);
                            } else {
                                errorMessage.add("职称不存在");
                            }
                        } else {
                            errorMessage.add("职称字段格式不符合要求");
                        }
                    } else if (k == sexColumn) {
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            value = cell.getStringCellValue();
                            if (StringUtils.equals("男", value)) {
                                userExcelData.setSex(1);
                            } else if (StringUtils.equals("女", value)) {
                                userExcelData.setSex(2);
                            } else {
                                errorMessage.add("性别格式错误");
                            }
                        } else {
                            userExcelData.setSex(null);
                        }
                    } else if (k == introductionColumn) {
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            value = cell.getStringCellValue();
                            if (getWordCount(value) > UserExcelData.INTRODUCTION_LENGTH) {
                                errorMessage.add("个人简介长度最多4000个汉字");
                            } else {
                                userExcelData.setIntroduction(value);
                            }
                        } else {
                            userExcelData.setIntroduction(null);
                        }
                    } else if (k == skillColumn) {
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            value = cell.getStringCellValue();
                            if (getWordCount(value) > UserExcelData.INTRODUCTION_LENGTH) {
                                errorMessage.add("擅长长度最多4000个汉字");
                            } else {
                                userExcelData.setSkill(value);
                            }
                        } else {
                            userExcelData.setSkill(null);
                        }
                    } else if (k == assistantColumn) {
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            value = cell.getStringCellValue();
                            if (StringUtils.isNotEmpty(value) && assistantMap.containsKey(value)) {
                                userExcelData.setAssistantId(assistantMap.get(value));
                            } else {
                                errorMessage.add("医生助手信息错误");
                            }
                        } else {
                            errorMessage.add("医生助手字段格式不符合要求");
                        }
                    } else if (k == doctorSourceColumn) {
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                            Double source = cell.getNumericCellValue();
                            Integer doctorSource = source.intValue();
                            userExcelData.setDoctorSource(doctorSource);
                        } else {
                            errorMessage.add("医生来源字段格式不符合要求");
                        }
                    }
                } else {
                    if (k == nameColumn) {
                        errorMessage.add("姓名为空");
                    } else if (k == phoneColumn) {
                        errorMessage.add("手机号码格式错误");
                    } else if (k == hospitalColumn) {
                        errorMessage.add("医疗机构不存在");
                    } else if (k == departmentColumn) {
                        errorMessage.add("科室不存在");
                    } else if (k == titleColumn) {
                        errorMessage.add("职称不存在");
                    } else if (k == assistantColumn) {
                        errorMessage.add("医生助手不存在");
                    } else if (k == doctorSourceColumn) {
                        errorMessage.add("医生来源不存在");
                    }
                    continue;
                }

            }
            if (errorMessage != null && errorMessage.size() > 0) {
                userExcelError.setMsg(errorMessage);
                error.add(userExcelError);
            }
            if (userExcelData.getPhone() != null) {
                data.add(userExcelData);
            }
        }

        Map<String, Object> result = Maps.newHashMap();
        if (data != null && data.size() > 500) {
            throw new ServiceException("导入用户数量超出限制");
        }
        if (error != null && error.size() > 0) {
            result.put("error", error);
            return result;
        } else {
            result.put("data", data);
            return result;
        }
    }

    @Override
    public boolean existUser(String doctorNum) {
        if (StringUtils.isEmpty(doctorNum)) {
            throw new ServiceException("医生号为空");
        }

        User user = null;

        if (doctorNum.length() != 11) {
            user = userRepository.findByDoctorNum(doctorNum);
        } else {
            user = userRepository.getUser(doctorNum, UserEnum.UserType.doctor.getIndex());
        }
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Map<String, Object> resetPhoneAndPassword(String doctorNum, String newPhone, String newPassword) {
        if (StringUtils.isEmpty(doctorNum)) {
            throw new ServiceException("医生号为空");
        }

        User user = null;

        if (doctorNum.length() != 11) {
            user = userRepository.findByDoctorNum(doctorNum);
        } else {
            user = userRepository.getUser(doctorNum, UserEnum.UserType.doctor.getIndex());
        }

        if (user == null) {
            throw new ServiceException("账号不存在");
        }

        //先更新手机号
        updateTel(user.getUserId(), newPhone);
        //再更新密码
        updatePassword(newPhone, UserType.doctor.getIndex(), newPassword);
        Map<String, Object> result = Maps.newHashMap();
        result.put("user", user);
        return result;
    }

    @Override
    public void updateDoctorServiceStatus(Integer userId, Integer serviceStatus) {
        userRepository.updateDoctorServiceStatus(userId, serviceStatus);
    }

    @Override
    public void updateDoctorCheckInGiveStatus(Integer userId, Integer checkInGive) {
        userRepository.updateDoctorCheckInGiveStatus(userId, checkInGive);
    }

    @Override
    public void initAllDoctorServiceStatus(Set<Integer> userIds) {
        if (userIds != null && userIds.size() > 0) {
            for (Integer userId : userIds) {
                updateDoctorServiceStatus(userId, UserEnum.ServiceStatus.open.getIndex());
            }
        }
    }

    private String filterNickName(String name) {
        if (StringUtil.isEmpty(name)) {
            return "";
        }

        name = name.replaceAll("[^(a-zA-Z0-9\\u4e00-\\u9fa5-_.)]", "");

        if (name.length() > 20) {
            name = name.substring(0, 20);
        }
        return name;
    }

    @Override
    public boolean logoutByWeChat4MP(String telephone) {
        if (StringUtil.isEmpty(telephone)) {
            throw new ServiceException("参数telephone不能为空");
        }
        //目前只有患者可用绑定微信
        User user = userRepository.getUserByTelAndType(telephone, UserEnum.UserType.patient.getIndex());
        if (user != null) {
            auth2Helper.unbindWechat(user.getUserId());
            // 异步发送Topic消息队列
            sendDoctorTopicService.sendLoginTopicMes(user, ReqUtil.getVersion(), DoctorTopicEnum.DoctorLoginTypeEnum.LoginOut.getIndex());
//			return authManager.unBindWeChat(user.getUserId());
            return true;
        }

        return false;
    }

    @Override
    public void patientRename(String name) {

        if (StringUtil.isEmpty(name)) {
            throw new ServiceException("姓名不能为空");
        }

        Integer userId = ReqUtil.instance.getUserId();
        User user = getUser(userId);
        if (null == user || UserEnum.UserType.patient.getIndex() != user.getUserType()) {
            throw new ServiceException("用户非法");
        }

        user.setName(name);
        user.setRename(0);
        userRepository.updateUser(user);

        UserInfoNotify.patientInfoModifyNotify(user.getUserId());

    }

    @Override
    public Map<String, Object> loginBuyCareOrder(UserExample example) {

        logger.info("Telephone: " + example.getTelephone() + " register for bug careOrder.");
        if (example.getUserType() == null) {
            example.setUserType(UserType.patient.getIndex());
        }
        //String accountNum, Integer userType, Boolean addUser, String orgId, String deviceId, String deviceType, String appName

//		AuthVO auth = authManager.getAuth(AccountTypeEnum.tel.name(), example.getTelephone(), example.getUserType());
        try {
            AccessToken tokenVo = auth2Helper.loginByTel(example.getTelephone(), example.getUserType(), false, null, example.getSerial(), ReqUtil.instance.getHeaderInfo().getDeviceType(), ReqUtil.instance.getHeaderInfo().getAppName());
            //账号存在就登录返回
            if (tokenVo != null) {
                logger.info("Telephone: " + example.getTelephone() + " is authed.");
                User user = getUser(tokenVo.getUserId());
                example = BeanUtil.copy(user, UserExample.class);
                return getLoginUserInfo(example, user, tokenVo.getToken());
//          return getUserInfo(example, user);
            }
        } catch (CommonException ce) {
            if (ce.getResultCode() != null && ce.getResultCode().intValue() != CommonErrorCode.ACCOUNT_PWD_WRONG.getCode())
                throw new RuntimeException(ce);
        }

        User user = userRepository.getUserByTelAndType(example.getTelephone(), example.getUserType());
        example.setName(example.getTelephone());
        Map<String, Object> data = registerIMUser(example);
        if (data == null || data.get("user") == null) {
            return data;
        }
        user = (User) data.get("user");

        //微信公众号登录注册的用户可修改名字
        User rename = new User();
        rename.setUserId(user.getUserId());
        rename.setRename(1);
        rename.setNeedReSetPassword(true);
        user = userRepository.updateUser(rename);

        data.put("user", user);

        //登录时记录最后登录的时间
        userRepository.updateLastLoginTime(user.getUserId());
        data.put("hasPassword", hasPwd(user));
        // 返回用户是否通过人身检测
        data.put("faceRec", faceRecognitionService.passFaceRec(user.getUserId()));
        // 异步发送Topic消息队列
        sendDoctorTopicService.sendLoginTopicMes(user, ReqUtil.getVersion(), DoctorTopicEnum.DoctorLoginTypeEnum.LoginByPassWord.getIndex());
        try {
            // 返回灰度方案
            data.put("grayVisibility", remoteSysManagerUtil.getNotJson(UserEnum.SEND_GRAY_VISIBILITY_URL, ReqUtil.instance.getHeaderInfo().getDeviceType(), ReqUtil.getVersion(), user.getTelephone()));
        } catch (Exception e) {
            logger.error("ex:{},userId:{}" + e.getMessage(), user.getUserId());
        }
        return data;

    }

    @Override
    public boolean checkDoctor(Integer doctorId) {
        User user = userRepository.getUser(doctorId);
        if (user == null) {
            return false;
        } else if (user.getUserType() == 3 && user.getStatus() == UserEnum.UserStatus.normal.getIndex()) {
            return true;
        }

        return false;
    }

    @Autowired
    protected ShortUrlComponent shortUrlComponent;

    @Override
    public User invitePatientFromDrugStore(String drugStoreId, String storeName, String userName, String telephone) throws HttpApiException {
        if (StringUtil.isEmpty(storeName) || StringUtil.isEmpty(userName) || StringUtil.isEmpty(telephone)) {
            throw new ServiceException("参数不全");
        }
        if (!CheckUtils.checkMobile(telephone)) {
            throw new ServiceException("手机号码格式不正确");
        }
        User patUser = userRepository.getUser(telephone, UserType.patient.getIndex());
        if (patUser != null) {
            return patUser;
        }
        UserSource userSource = new UserSource();
        userSource.setSourceType(UserEnum.Source.drugStore.getIndex());
        userSource.setGroupId(drugStoreId);
        patUser = createInactiveUserWithName(userName, telephone, userSource);
        String content = "【" + storeName + "】邀请你加入玄关健康，随时与我沟通、地址：" + shortUrlComponent.generateShortUrl(BaseConstants.XG_DOWN_PAT());
        mobSmsSdk.send(telephone, content);
        return patUser;
    }

    @Override
    public PageVO getByKeyword(String keyword, Integer pageIndex, Integer pageSize) {

        return userRepository.getByKeyword(keyword, pageIndex, pageSize);
    }

    @Override
    public void uncheck(User doctor, Integer checkerId, String name) {
        userRepository.uncheck(doctor, checkerId, name);
    }

    @Override
    public void updateUserSessionCache(User user) {
        userRepository.updateUserSessionCache(user);
    }

    @Override
    public Map<String,Object> getDoctorStatusByTelephone(String telephone) {
        Map<String,Object> map=new HashMap<>();
        User user = userRepository.getUser(telephone, UserType.doctor.getIndex());
        if (user == null) {
            map.put("status",3);
            return map;
        } else if (user.getStatus().equals( UserStatus.normal.getIndex())) {
            map.put("status",1);
            map.put("user",user);
            return map;
        } else {
            map.put("status",2);
            map.put("user",user);
            return map;
        }

    }

    @Override
    public void disable(Integer userId, DisableDoctorParam doctorParam) {
        User user = userRepository.getUser(userId);
        if (user == null)
            throw new ServiceException("当前用户不存在");
        User dbParam = new User();
        dbParam.setSuspend(UserConstant.SuspendStatus.tempForbid.getIndex());
        dbParam.setUserId(userId);
        if (Objects.nonNull(doctorParam)) {
            doctorParam.setAdminId(ReqUtil.instance.getUserId());
            SuspendInfo suspendInfo = new SuspendInfo(doctorParam.getReason(), System.currentTimeMillis());
            dbParam.setSuspendInfo(suspendInfo);
            /* 操作记录 */
            userOperationLogService.logDisableRecord(user, doctorParam);
        }
        //但求跨机事务别挂
        userRepository.updateUser(dbParam);
        auth2Helper.deleteAccountOrRecover(user.getTelephone(), user.getUserType(), true, "您的账号已被禁用,如有需要请联系管理员");
    }

    @Override
    public void enable(Integer userId) {
        User user = userRepository.getUser(userId);
        if(user == null)
            throw new ServiceException("当前用户不存在");
        User dbparam = new User();
        dbparam.setSuspend(UserConstant.SuspendStatus.normal.getIndex());
        dbparam.setUserId(userId);
        //但求跨机事务别挂
        userRepository.updateUser(dbparam);
        auth2Helper.deleteAccountOrRecover(user.getTelephone(), user.getUserType(),false, null);
    }

    public void updateGiveCoin(Integer userId, Integer giveCoin) {
        userRepository.updateGiveCoin(userId,giveCoin);
    }


    private static Long scanTime = 0L;
	@Override
	public void handLimitPeriodUser() {
		List<User> peroidList = userRepository.getLimitPeriodUser(scanTime);
		//设置扫描时间
		scanTime = System.currentTimeMillis();
    	List<Integer> userIds = new ArrayList<>();
    	if(!CollectionUtils.isEmpty(peroidList)){
    		for(User user : peroidList ){
    			businessMsgService.sendEventForDoctor(user.getUserId(), user.getUserId(),user.getStatus(), user.getName(), UserLevel.Expire.getIndex(),user.getLimitedPeriodTime());
    			userIds.add(user.getUserId());
    		}
//    		userRepository.batchUserToPeriod(userIds, UserLevel.Expire.getIndex());
    	}
	}

	@Override
	public boolean upgradeUserLevel(Integer userId,String reason) {
		User user = userRepository.getUser(userId);
		Map<String,String> opeartorLog = new HashMap<>();
        opeartorLog.put("operationType",OperationLogTypeDesc.DOCOTORLEVELCHANGE);
		if (Objects.nonNull(user)
				&& (Objects.equals(UserLevel.Tourist.getIndex(), user.getBaseUserLevel())
						|| Objects.equals(UserLevel.Expire.getIndex(), user.getBaseUserLevel()))
				&& Objects.equals(user.getUserType(), UserType.doctor.getIndex())) {
//			userRepository.updateUserLevel(user.getUserId(), UserLevel.TemporaryUser.getIndex(), (Objects.nonNull(user.getCreateTime())?user.getCreateTime():System.currentTimeMillis())+UserEnum.GUEST_LIMITED_PERIOD+UserEnum.ADD_LIMITED_PERIOD);
//			//发送审核指令
//			businessMsgService.sendEventForDoctor(user.getUserId(), user.getUserId(),user.getStatus(), user.getName(),UserEnum.UserLevel.TemporaryUser.getIndex(),(Objects.nonNull(user.getCreateTime())?user.getCreateTime():System.currentTimeMillis())+UserEnum.GUEST_LIMITED_PERIOD+UserEnum.ADD_LIMITED_PERIOD);
			//临时会员的到期时间改为永久 那就不需判断当前游客有效期是否到期了 by xuhuanjie
			userRepository.updateUserLevel(user.getUserId(), UserLevel.TemporaryUser.getIndex(),
					UserEnum.FOREVER_LIMITED_PERIOD);
			// 发送审核指令
			businessMsgService.sendEventForDoctor(user.getUserId(), user.getUserId(), user.getStatus(), user.getName(),
					UserEnum.UserLevel.TemporaryUser.getIndex(), UserEnum.FOREVER_LIMITED_PERIOD);
			opeartorLog.put("content",
					String.format("医生(%1$s)%2$s升级为临时用户,有效期由(%3$s)变为(%4$s)", user.getTelephone(), reason,
							DateUtil.formatDate2Str(user.getLimitedPeriodTime(), null),
							DateUtil.formatDate2Str(UserEnum.FOREVER_LIMITED_PERIOD)));
		}
		if (!CollectionUtils.isEmpty(opeartorLog) && Objects.equals(opeartorLog.size(), 2)) {
        	/*operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),opeartorLog.get("operationType"),opeartorLog.get("content"));*/
        	userInfoChangeNotify(userId);
        }
		return true;
	}


	@Override
    public UpdateResults updateUserLimitPeriod(Integer userId, Long time) {
		if(Objects.isNull(userId)){
			throw new ServiceException("用户id为null");
		}
		if(Objects.isNull(time)){
			throw new ServiceException("修改用户时间为空");
		}
		User user = userRepository.getUser(userId);
        if (Objects.nonNull(user) && UserType.doctor.getIndex() == user.getUserType()) {
        	Query<User> query = dsForRW.find(User.class).field("_id").equal(userId);
            UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
            ops.set("limitedPeriodTime", time);
            ops.set("modifyTime", new Date().getTime());
            UpdateResults ret = dsForRW.update(query, ops);
            userRepository.removeUserRedisCache(userId);
            //发送审核指令给前端同步到期时间
            businessMsgService.sendEventForDoctor(user.getUserId(), user.getUserId(),user.getStatus(), user.getName(),user.getUserLevel(),time);
        	operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),
                OperationLogTypeDesc.DOCOTORINFO, String.format("修改%1$s（%2$s）有效期为（%3$s）", user.getName(), user.getTelephone(), DateUtil.formatDate2Str(time, null)));
        	return ret;
        }else{
        	throw new ServiceException("获取医生用户信息异常");
        }
    }

	@Override
	public void sendSmsToDownload(Integer userId,String content,Integer type){
		try {
			User user = userRepository.getUser(userId);
	    	String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.arouseDoctor.MedicalCircle"));
	        //远程调用参数
	        Map<String, String> params = new HashMap<String, String>();
	        params.put("tel", user.getTelephone());

	        params.put("content", String.format(UserEnum.CIRCLE_SMS_CONTENT_TEMPLATE,content,generateUrl));

	        // 拓展 0=玄关健康 1=康哲，2=IBO，3=博德嘉联 6医生圈
	        params.put("ext", "6");
	        Object response = remoteSysManagerUtil.post(UserEnum.SEND_SMS_URL, params);
	        logger.info(response.toString());
		} catch (HttpApiException e) {
			logger.error("圈子发送短信唤起app异常.userId{}.content{}.type{}",userId,content,type);
		}

	}

	public void userInfoChangeNotify(Integer userId) {
        logger.info("用户信息更新，发送MQ消息。userId={}", userId);
        BasicProducer.fanoutMessage("drugorgUserInfoEvent", String.valueOf(userId));// 兼容历史版本
        User user = getUser(userId);
        if (user.getUserType() == UserType.doctor.getIndex()) {
            List<AccessToken> openIdList = auth2Helper.getOpenIdList(Arrays.asList(userId));
            if (!CollectionUtils.isEmpty(openIdList)) {
                user.setOpenId(openIdList.get(0).getOpenId());
            }
            BasicProducer.fanoutMessage("doctorInfoChange", JSON.toJSONString(user));
        }
        // 异步发送Topic消息队列
        sendDoctorTopicService.sendChangeInfoTopicMes(user);
    }

    @Override
    public List<HospitalVO> findHospitalByName(String name) {
        DBObject query = new BasicDBObject();
        if(StringUtil.isEmpty(name)){
            return new ArrayList<>();
        }
        Pattern p1 = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
        query.put("name", p1);

        DBCursor cursor = dsForRW.getDB().getCollection("b_hospital").find(query);
        List<HospitalVO> data = new ArrayList<HospitalVO>();
        Set<Integer> areaSet = new HashSet<Integer>();
        HospitalVO p = null;
        DBObject dbObj = null;
        HospitalPO.Loc loc = null;
        while(cursor.hasNext()){
            dbObj = cursor.next();
            p = new HospitalVO();
            if(!Objects.isNull(dbObj.get("_id"))){
                p.setId(dbObj.get("_id").toString());
            }
            if(!Objects.isNull(dbObj.get("name"))){
                p.setName(dbObj.get("name").toString());
            }
            if(!Objects.isNull(dbObj.get("level"))){
                p.setLevel(dbObj.get("level").toString());
            }
            if(!Objects.isNull(dbObj.get("province"))){
                p.setProvince(Integer.valueOf(dbObj.get("province").toString()));
                areaSet.add(p.getProvince());
            }
            if(!Objects.isNull(dbObj.get("city"))){
                p.setCity(Integer.valueOf(dbObj.get("city").toString()));
                areaSet.add(p.getCity());
            }
            if(!Objects.isNull(dbObj.get("country"))){
                p.setCountry(Integer.valueOf(dbObj.get("country").toString()));
                areaSet.add(p.getCountry());
            }
            if(!Objects.isNull(dbObj.get("status"))){
                p.setStatus(Integer.valueOf(dbObj.get("status").toString()));
            }
            if(!Objects.isNull(dbObj.get("address"))){
                p.setAddress(dbObj.get("address").toString());
            }
            if(!Objects.isNull(dbObj.get("lastUpdatorTime"))){
                p.setLastUpdatorTime(Long.valueOf(dbObj.get("lastUpdatorTime").toString()));
            }
            loc = new HospitalPO.Loc();
            if(!Objects.isNull(dbObj.get("lat"))){
                loc.setLat(Double.valueOf(dbObj.get("lat").toString()));
                p.setLat(dbObj.get("lat").toString());
            }
            if(!Objects.isNull(dbObj.get("lng"))){
                loc.setLng(Double.valueOf(dbObj.get("lng").toString()));
                p.setLng(dbObj.get("lng").toString());
            }
            p.setLoc(loc);
            data.add(p);
        }

        List<Area> areaList = null;
        if(!org.apache.commons.collections.CollectionUtils.isEmpty(areaSet)){
            areaList = findArea(areaSet);
            for(HospitalVO v : data){
                for(Area a : areaList){
                    if(!Objects.isNull(v.getProvince())
                            && a.getCode().equals(v.getProvince().toString())){
                        v.setProvinceName(a.getName());
                    }
                    if(!Objects.isNull(v.getCity())
                            && a.getCode().equals(v.getCity().toString())){
                        v.setCityName(a.getName());
                    }
                    if(!Objects.isNull(v.getCountry())
                            && a.getCode().equals(v.getCountry().toString())){
                        v.setCountryName(a.getName());
                    }
                }
            }
        }

        return data;
    }
    public List<Area> findArea(Collection<Integer> coll) {
        Query<Area> query = dsForRW.createQuery(Area.class);
        if(coll != null){
            query.field("code").in(coll);
        }
        query.order("_id");
        return query.asList();
    }


    @Override
	public void sendSmsToDownloadByTels(String tels,String content,String smsType){
		try {
	    	String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.arouseDoctor.MedicalCircle"));
	        //远程调用参数
	        Map<String, String> params = new HashMap<String, String>();
	        params.put("tel", tels);

	        params.put("content", String.format(UserEnum.CIRCLE_SMS_CONTENT_TEMPLATE,content,generateUrl));

	        // 拓展 0=玄关健康 1=康哲，2=IBO，3=博德嘉联 6医生圈
	        params.put("ext", smsType);
	        Object response = remoteSysManagerUtil.post(UserEnum.SEND_SMS_INNER_URL, params);
	        logger.info(response.toString());
		} catch (HttpApiException e) {
			logger.error("圈子发送短信唤起app异常.tels{}.content{}.smsType{}",tels,content,smsType);
		}

    }

    @Override
    public void activityDoctorLoginNotify(Integer userId) {
    	try {
    		logger.info("activityDoctorLoginNotify双旦运营活动通知，发送MQ消息。userId={}", userId);
    		User user = getUser(userId);
    		if (user.getUserType() == UserType.doctor.getIndex()) {
    			BasicProducer.fanoutMessage("operationDoctorLogin", JSON.toJSONString(BeanUtil.copy(user, SimpleUserVO.class)));
    		}
    	} catch (Exception e) {
    		logger.error("activityDoctorLoginNotify双旦运营活动通知，发送MQ消息失败！！", e);
    	}
    }

	@Override
    public void activityCommitAuthNotify(Integer userId) {
        logger.info("activityCommitAuthNotify双旦运营活动通知，发送MQ消息。userId={}", userId);
        User user = getUser(userId);
        if (user.getUserType() == UserType.doctor.getIndex()) {
            BasicProducer.fanoutMessage("operationCommitAuth", JSON.toJSONString(BeanUtil.copy(user, SimpleUserVO.class)));
        }
    }

    /**
     * 异步保存用户登录信息
     * @param telephone
     * @param userAgent
     */
    @Override
    public void saveUserLoginInfo(String serial, String telephone, String userAgent) {
        //判断移动端机型是iPhone还是android
        String userAgentString = null;
        if(Objects.isNull(userAgent)){
            logger.error("获取User-Agent失败");
        }else {
            if(userAgent.contains("android")){
                //注意android手机User-Agent最后有一个/mobile
                int indexOfBackslash = userAgent.lastIndexOf("/");
                if(indexOfBackslash > 0){
                    userAgentString = userAgent.substring(0,userAgent.lastIndexOf("/"))
                            .substring(userAgent.indexOf("android") + "android".length() + 1);
                }else{
                    logger.error("User-Agent不符合约定");
                }
            }else if(userAgent.contains("ios")){
                userAgentString = userAgent.substring(userAgent.indexOf("ios") + "ios".length() + 1);
            }else{
                logger.error("移动端传递的User-Agent中，未包含关键字 android 或 ios");
            }
            if(Objects.nonNull(userAgentString)){
                int indexOfBackslash = userAgentString.indexOf("/");
                if(indexOfBackslash > 0){
                    String phoneModel = userAgentString.substring(0,userAgentString.indexOf("/"));
                    String romVersion = userAgentString.substring(indexOfBackslash + 1);
                    userRepository.saveUserLoginInfo(serial, ReqUtil.getVersion(), telephone, phoneModel, romVersion);
                }
            }else {
                logger.error("User-Agent不符合约定");
            }
        }
    }

    @Override
    public PageVO getUserLoginInfo(int pageIndex, int pageSize, String telephone) {
        Query<UserLoginInfo> query = dsForRW.createQuery(UserLoginInfo.class);
        if(!StringUtil.isNullOrEmpty(telephone)){
            query.field("telephone").contains(telephone);
        }
        int offset = pageIndex * pageSize;
        query.offset(offset).limit(pageSize);
        query.order("-loginTime");

        PageVO page = new PageVO();
        page.setPageData(query.asList());
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        page.setTotal(query.countAll());

        return page;
    }

    /**
     * 注册用户 time为空查平台所有注册用户数 time不为空查time以后注册的用户数
     * @param time
     * @return
     */
    @Override
    public Integer getRegistryUserCount(Long time) {

        Query<User> query =  dsForRW.createQuery(User.class);
        if(time != null){
            query.field("createTime").greaterThanOrEq( time);
        }
        query.field("userType").equal(UserType.doctor.getIndex());
        int count = Math.toIntExact(query.countAll());


        return count;
    }

    /**
     * 认证通过用户 time为空查平台所有认证用户数 time不为空查time以后认证的用户数
     * @param time
     * @return
     */
    @Override
    public Integer getCertifyUserCount(Long time) {

        Query<User> query =  dsForRW.createQuery(User.class);
        if(time != null){
            query.field("doctor.check.checkTime").greaterThanOrEq( time);
        }
        query.field("userType").equal(UserType.doctor.getIndex());
        query.field("status").equal(UserStatus.normal.getIndex());
        int count = Math.toIntExact(query.countAll());

        return count;
    }


    /**
     * 活跃用户
     * @param today
     * @return
     */
    @Override
    public Integer getActivityUserCount(Long today) {
        Query<User> query =  dsForRW.createQuery(User.class);
        query.field("lastLoginTime").greaterThanOrEq(today);
        query.field("userType").equal(UserType.doctor.getIndex());
        int count = Math.toIntExact(query.countAll());

        return count;
    }

    /**
     * 查询一级城市的个数
     * @param city
     * @return
     */
    @Override
    public Integer getUserPositionCount(String city) {
        Query<User> query =  dsForRW.createQuery(User.class);
        query.field("doctor.province").equal(city);
        query.field("userType").equal(UserType.doctor.getIndex());
        int count = Math.toIntExact(query.countAll());

        return count;
    }

    /**
     * 提交认证用户数
     * @param time
     * @return
     */
    @Override
    public Integer getSubmitUserCount(Long time) {
        Query<User> query =  dsForRW.createQuery(User.class);
        if(time!=null) {
            query.field("submitTime").greaterThanOrEq(time);
        }else {
            query.field("status").equal(UserStatus.uncheck.getIndex());
        }
        query.field("userType").equal(UserType.doctor.getIndex());
        int count = Math.toIntExact(query.countAll());

        return count;
    }

    public List<DoctorBaseInfo> getDoctorInfo(String name) {
        List<DoctorBaseInfo> doctorInfos = new ArrayList<DoctorBaseInfo>();
        List<User> users = userRepository.findByName(name);
        for (User user : users) {
            DoctorBaseInfo info = new DoctorBaseInfo();
            info.setUserId(user.getUserId());
            info.setName(user.getName());
            info.setTelephone(user.getTelephone());
            info.setStatus(user.getStatus());
            if (user.getDoctor() != null) {
                info.setHospital(user.getDoctor().getHospital());
                info.setDepartments(user.getDoctor().getDepartments());
            }

            if (user.getSource() != null) {
                if (user.getSource().getInviterId() != null) {
                    User inviter = userRepository.getUser(user.getSource().getInviterId());
                    info.setInviterName(inviter != null ? inviter.getName() : null);
                    //来源id
                    info.setInviterId(user.getSource().getInviterId());
                }
                if (user.getSource().getSourceType() != null) {
                    UserEnum.Source source = UserEnum.Source.getEnum(user.getSource().getSourceType());
                    info.setRegisterSource(source != null ? source.getSource() : null);
                }
            }
            doctorInfos.add(info);
        }
        //来源是药企代表要需要重药企代表哪里得到
        if(!CollectionUtils.isEmpty(doctorInfos)) {
        	List<Integer> drugorgUserIds = new ArrayList<Integer>();
            for (DoctorBaseInfo vo : doctorInfos) {
                if(Objects.equals(Source.drugOrg.getSource(), vo.getRegisterSource())){
                	if(Objects.nonNull(vo.getInviterId())){
                		drugorgUserIds.add(vo.getInviterId());
                	}
                }
            }
            List<CSimpleUser> simpleUsers = null;
            try {
                simpleUsers = drugOrgApiClientProxy.getSimpleUser(drugorgUserIds);
            } catch (HttpApiException e) {
                logger.error(e.getMessage(), e);
            }
            Map<Integer, CSimpleUser> userMap =  new HashMap<>();
            if(!CollectionUtils.isEmpty(simpleUsers)) {
                userMap = Maps.uniqueIndex(simpleUsers, (x) -> x.getId());
            }
            for (DoctorBaseInfo info : doctorInfos) {
            	if(Objects.equals(Source.drugOrg.getSource(), info.getRegisterSource())){
                    CSimpleUser inviter = userMap.get(info.getInviterId());
                    if (inviter != null) {
                    	info.setInviterName(inviter.getName());
                    }else{
                    	info.setInviterName("");
                    }
                }
            }
        }
        return doctorInfos;
    }

    @Override
    public void updateUserRole(UserRoleParam param) {
        userRepository.updateUserRole(Integer.parseInt(param.getUserId()), param.getRoleIds());
    }

    @Override
    public UserRoleVO getUserRolePermission(Integer userId) {
        UserRoleVO vo = new UserRoleVO();
        User user = userRepository.getUser(userId);
        if (Objects.nonNull(user) && !CollectionUtils.isEmpty(user.getRoleIds())) {
            ArrayList<Role> roleList = Lists.newArrayList();
            for (String roleId : user.getRoleIds()) {
                Role role = roleService.getById(roleId);
                roleList.add(role);
            }
            /*
               Bug mongo In查询多层对象有误 改为单个id查询
               List<Role> roleList = roleService.getByIds(user.getRoleIds());
            */
            List<String> permissionIds = getPermissionIds(roleList);
            vo.setUserId(userId+"");
            vo.setRoleIds(user.getRoleIds());
            vo.setPermissionIds(permissionIds);
        }
        return vo;
    }

    private List<String> getPermissionIds(List<Role> roleList) {
        List<String> permissionIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roleList)) {
            for (Role role : roleList) {
                List<Permission> permissionList = role.getPermissionList();
                if (!CollectionUtils.isEmpty(permissionList)) {
                    for (Permission permission : permissionList) {
                        permissionIds.add(permission.getId());
                        List<Permission> children = permission.getChildren();
                        if (!CollectionUtils.isEmpty(children)) {
                            for (Permission child : children) {
                                permissionIds.add(child.getId());
                            }
                        }
                    }
                }
            }
        }
        return permissionIds;
    }

    @Override
	public List<Map<String, Object>> departmentTopn(Integer topn) {
		Long startTime = System.currentTimeMillis();
    	List<Map<String,Object>> listMap = new ArrayList<>();
		List<Map.Entry<String, Integer>> topDempatemts= userRepository.getDepartmentsTopnByUser(topn,UserType.doctor.getIndex(),UserStatus.normal.getIndex());
		for(int i=0;i<topDempatemts.size();i++){
			Entry<String,Integer> ent=topDempatemts.get(i);
			Map<String,Object> map = new HashMap<>();
			map.put("department", ent.getKey());
			map.put("certifyDoctors", ent.getValue());
			map.put("regDoctors",userRepository.getDoctorNumByDepartments(UserType.doctor.getIndex(),ent.getKey()));
			listMap.add(map);
		}
		logger.info("departmentTopn.doTime-{}",System.currentTimeMillis()-startTime);
        return listMap;
	}


    @Override
	public List<Map<String, Object>> departmentTopnNew(Integer topn) {
		Long startTime = System.currentTimeMillis();
		List<Map<String, Object>>  topDempatemts= userRepository.getDepartmentsTopnByUserNew(topn,UserType.doctor.getIndex());
		logger.info("departmentTopnNew.doTime-{}",System.currentTimeMillis()-startTime);
        return topDempatemts;
	}

    @Override
    public User findUserById(int userId) {
        User user =  getUser(userId );
        Doctor doc = user.getDoctor();
        String diseaseStr = "";
        if (doc != null) {
            List<String> diseaseTypeIds = doc.getExpertise();
            if (diseaseTypeIds != null && !diseaseTypeIds.isEmpty()) {
                List<DiseaseType> diseaseTypes = diseaseTypeRepository.findByIds(diseaseTypeIds);
                for (DiseaseType d : diseaseTypes) {
                    diseaseStr += d.getName() + ",";
                }
                if (diseaseStr.endsWith(",")) {
                    diseaseStr = diseaseStr.substring(0, diseaseStr.length() - 1);
                }
            }
        }
        if (StringUtil.isNotEmpty(diseaseStr)
                || StringUtil.isNotEmpty(doc.getSkill())) {
            String skill = doc.getSkill() == null ? "" : doc.getSkill();
            doc.setSkill(diseaseStr + "。" + skill);
        }
        return user;
    }

    @Override
    public void addAdminUser(AddAdminUserParam addAdminUserParam) {
        /* check */
        if (StringUtil.isEmpty(addAdminUserParam.getTelephone())) {
            throw new ServiceException("手机号不能为空");
        }
        if (!CheckUtils.checkMobile(addAdminUserParam.getTelephone())) {
            throw new ServiceException("手机号码格式不正确");
        }
        if (!Objects.equals(addAdminUserParam.getUserType(), UserType.customerService.getIndex())) {
            throw new ServiceException("用户类型不正确");
        }
        if (StringUtil.isEmpty(addAdminUserParam.getName())) {
            throw new ServiceException("名字不能为空");
        }
        if (StringUtil.isEmpty(addAdminUserParam.getPassword())) {
            throw new ServiceException("密码不能为空");
        }
        if (StringUtil.isEmpty(addAdminUserParam.getRepeatPassword())) {
            throw new ServiceException("重复密码不能为空");
        }
        if (CollectionUtils.isEmpty(addAdminUserParam.getRoleIds())) {
            throw new ServiceException("用户角色不能为空");
        }
        if (!Objects.equals(addAdminUserParam.getPassword(), addAdminUserParam.getRepeatPassword())) {
            throw new ServiceException("密码不一致");
        }
        /* user exists or not*/
        User user = getUser(addAdminUserParam.getTelephone(), addAdminUserParam.getUserType());
        if (Objects.nonNull(user)) {
            throw new ServiceException("手机号已被注册");
        }
        /* get userId */
        Integer userId = auth2Helper.addAccount(addAdminUserParam.getTelephone(), addAdminUserParam.getPassword(), addAdminUserParam.getUserType(), null, false);
        UserExample userExampleCopy = BeanUtil.copy(addAdminUserParam, UserExample.class);
        userExampleCopy.setAddUser(true);
        /* set user source */
        UserSource userSource = new UserSource();
        userSource.setSourceType(Source.checkAdmin.getIndex());
        userSource.setTerminal(UserEnum.Terminal.xg.getIndex());
        userExampleCopy.setUserSource(userSource);
        /* add user */
        Map<String, Object> data = userRepository.addUser(userId, userExampleCopy);
        if (Objects.isNull(data.get("user"))) {
            throw new ServiceException("用户注册失败");
        }
        userRepository.updateUserRoleIds(userId, addAdminUserParam.getRoleIds());
    }

    @Override
    public void updateAdminUser(AddAdminUserParam addAdminUserParam) {
        /* check */
        Integer userId = addAdminUserParam.getUserId();
        String name = addAdminUserParam.getName();
        String telephone = addAdminUserParam.getTelephone();
        String password = addAdminUserParam.getPassword();
        String repeatPassword = addAdminUserParam.getRepeatPassword();
        List<String> roleIds = addAdminUserParam.getRoleIds();
        if (Objects.isNull(userId)) {
            throw new ServiceException("用户Id不能为空");
        }
        if (StringUtil.isEmpty(telephone)) {
            throw new ServiceException("手机号不能为空");
        }
        if (!CheckUtils.checkMobile(telephone)) {
            throw new ServiceException("手机号码格式不正确");
        }
        if (!Objects.equals(addAdminUserParam.getUserType(), UserType.customerService.getIndex())) {
            throw new ServiceException("用户类型不正确");
        }
        if (StringUtil.isEmpty(name)) {
            throw new ServiceException("名字不能为空");
        }
        if (CollectionUtils.isEmpty(roleIds)) {
            throw new ServiceException("用户角色不能为空");
        }
        /* user exists or not*/
        User user = this.getUser(userId);
        /* update password */
        if (StringUtil.isNoneBlank(password) && StringUtil.isNoneBlank(repeatPassword) && Objects.equals(password, repeatPassword)) {
            auth2Helper.resetPassword(user.getUserId(), password, ReqUtil.instance.getHeaderInfo().getDeviceId(), ReqUtil.instance.getHeaderInfo().getDeviceType(), ReqUtil.instance.getHeaderInfo().getAppName());
        }
        /* if not equal,update telephone */
        if (!Objects.equals(user.getTelephone(), telephone)) {
            /* telephone has been used or not */
            User getUserByTelAndType = this.getUser(telephone, addAdminUserParam.getUserType());
            if (Objects.nonNull(getUserByTelAndType)) {
                throw new ServiceException("手机号已被使用");
            }
            Integer id = ReqUtil.instance.getUserId();
            OperationRecord operationRecord = new OperationRecord();
            if (Objects.isNull(id) || Objects.equals(id.intValue(), 0)) {
                /* 若获取不到当前登录用户的id，则以传来的userId来进行存储 */
                operationRecord.setCreator(userId);
            } else {
                operationRecord.setCreator(id);
            }
            operationRecord.setCreateTime(System.currentTimeMillis());
            operationRecord.setObjectId(String.valueOf(userId));
            operationRecord.setObjectType(UserLogEnum.OperateType.update.getOperate());
            operationRecord.setChange(new Change(UserLogEnum.infoType.telephone.getType(), "手机号码", user.getTelephone().toString(), telephone.toString()));
            userLogRespository.addOperationRecord(operationRecord);
            logger.info("运营后台管理员#{}修改手机号为{}", userId, telephone);
            /* 修改账号数据 手机号 */
            auth2Helper.updateAccount(null, userId, telephone, true);
        }
        userRepository.updateAdminUser(user, addAdminUserParam);
    }

    @Override
    public IsNewAccountVO isNewAccount(String telephone, Integer type) {
        if (isBlank(telephone)) {
            throw new ServiceException("手机号不能为空");
        }
        IsNewAccountVO isNewAccountVO = new IsNewAccountVO();
        User user = userRepository.getUser(telephone, UserType.doctor.getIndex());
        /* 新账号 */
        if (Objects.isNull(user)) {
            isNewAccountVO.setIsNewAccount(Boolean.TRUE);
            String token = randomCode();
            saveToken(type, telephone, token);
            isNewAccountVO.setToken(token);
        } else {
            isNewAccountVO.setIsNewAccount(Boolean.FALSE);
            isNewAccountVO.setUserStatus(user.getStatus());
            Doctor doctor = user.getDoctor();
            if (Objects.nonNull(doctor)) {
                isNewAccountVO.setHospitalId(doctor.getHospitalId());
                isNewAccountVO.setHospital(doctor.getHospital());
                isNewAccountVO.setDeptId(doctor.getDeptId());
                isNewAccountVO.setDepartments(doctor.getDepartments());
                isNewAccountVO.setTitle(doctor.getTitle());
            }
        }
        return isNewAccountVO;
    }

    @Override
    public PageVO findDoctorsByParamCondition(DoctorParam param, List<String> phones) {
        return userRepository.findDoctorsByParamCondition(param,phones);
    }

    @Override
    public PageVO getUsersByTelList(List<String> telList, Integer pageIndex, Integer pageSize) {
        PageVO usersByTelList = userRepository.getUsersByTelList(telList, pageIndex, pageSize);
        List<User> pageData = (List<User>) usersByTelList.getPageData();
        List<SimUserVO> simpleUserVOS = new ArrayList<>();
        for (User user : pageData) {
            SimUserVO simpleUserVO = new SimUserVO();
            simpleUserVO.setUserId(user.getUserId());
            simpleUserVO.setName(user.getName());
            simpleUserVO.setTelephone(user.getTelephone());
            simpleUserVO.setUserType(user.getUserType());
            simpleUserVO.setHeadPicFileName(user.getHeadPicFileName());
            if (Objects.nonNull(user.getDoctor())) {
                Doctor doctor = user.getDoctor();
                simpleUserVO.setHospitalId(doctor.getHospitalId());
                simpleUserVO.setHospital(doctor.getHospital());
                simpleUserVO.setDeptId(doctor.getDeptId());
                simpleUserVO.setDepartments(doctor.getDepartments());
                simpleUserVO.setTitle(doctor.getTitle());
            }
            simpleUserVOS.add(simpleUserVO);
        }
        usersByTelList.setPageData(simpleUserVOS);
        return usersByTelList;
    }

    @Override
    public Map<String,Map<String,String>> getUserSource() {
        UserEnum.Source[] values = UserEnum.Source.values();
        LinkedHashMap<String, String> sourceMap = new LinkedHashMap<>(values.length);
        for (UserEnum.Source source : values) {
            sourceMap.put(String.valueOf(source.getIndex()), source.getSource());
        }
        Map<String,Map<String,String>> map = new HashMap<>();
        map.put("sourceMap",sourceMap);
        return map;
    }

    @Override
    public void updatePasswordV2(Integer userId, String password) {
        auth2Helper.resetPassword(userId, password, null, null, null);
    }

    @Override
    public void addLearningExperience(LearningExperienceParam param, Integer userId) {
        if (StringUtil.isBlank(param.getCollegeName())) {
            throw new ServiceException("毕业院校不能为空");
        }
        if (StringUtil.isBlank(param.getDepartments())) {
            throw new ServiceException("院系不能为空");
        }
        if (StringUtil.isBlank(param.getQualifications())) {
            throw new ServiceException("学历不能为空");
        }
        if (Objects.isNull(param.getStartTime())) {
            throw new ServiceException("入学时间不能为空");
        }
        // 根据userId获取学习经历数量
        Integer learningExpCount = userRepository.getLearningExpCount(userId);
        if (Objects.nonNull(learningExpCount) && learningExpCount >= 10) {
            throw new ServiceException("不能超过10个学习经历");
        }
        String learningExperienceId = userRepository.addLearningExperience(param, userId);
        /* 与前端约定如果ID为空 则该院校名字是用户手动输入的 */
        if (StringUtil.isBlank(param.getCollegeId()) && StringUtil.isNoneBlank(learningExperienceId)) {
            this.addCustomCollege(param.getCollegeName(), learningExperienceId, userId);
        }
    }

    @Override
    public void updateLearningExperience(LearningExperienceParam param) {
        if (StringUtil.isBlank(param.getId())) {
            throw new ServiceException("id不能为空");
        }
        if (StringUtil.isBlank(param.getCollegeName())) {
            throw new ServiceException("毕业院校不能为空");
        }
        if (StringUtil.isBlank(param.getDepartments())) {
            throw new ServiceException("院系不能为空");
        }
        if (StringUtil.isBlank(param.getQualifications())) {
            throw new ServiceException("学历不能为空");
        }
        if (Objects.isNull(param.getStartTime())) {
            throw new ServiceException("入学时间不能为空");
        }
        userRepository.updateLearningExperience(param);
        /* 与前端约定如果ID为空 则该院校名字是用户手动输入的 */
        if (StringUtil.isBlank(param.getCollegeId())) {
            this.addCustomCollege(param.getCollegeName(), param.getId(), ReqUtil.instance.getUserId());
        }
    }

    @Override
    public List<LearningExperience> getLearningExperience(Integer userId) {
        return userRepository.getLearningExperience(userId);
    }

    @Override
    public Boolean delLearningExp(String id) {
        if (StringUtil.isBlank(id)) {
            throw new ServiceException("id不能为空");
        }
        return userRepository.delLearningExperience(id);
    }

    @Override
    public void updateLearningExperienceMul(LearningExperienceParam param) {
        if (StringUtil.isBlank(param.getCollegeName())) {
            throw new ServiceException("毕业院校不能为空");
        }
        if (StringUtil.isBlank(param.getCollegeId())) {
            throw new ServiceException("院系ID不能为空");
        }
        userRepository.updateLearningExperienceMul(param);
    }

    private void addCustomCollege(String collegeName, String learningExperienceId, Integer userId) {
        CustomCollegeParam customCollegeParam = new CustomCollegeParam();
        customCollegeParam.setCustomCollegeName(collegeName);
        customCollegeParam.setLearningExpId(learningExperienceId);
        customCollegeParam.setUserId(userId);
        User user = userRepository.getUser(userId);
        if (Objects.nonNull(user)) {
            customCollegeParam.setName(user.getName());
            customCollegeParam.setTelephone(user.getTelephone());
        }
        if (userRepository.existCustomCollege(customCollegeParam.getLearningExpId())) {
            userRepository.updateCustomCollege(customCollegeParam);
        } else {
            userRepository.addCustomCollege(customCollegeParam);
        }
    }

    @Override
    public void checkCustomCollege(String learningExpId, String checkCollegeName, String checkCollegeId, Integer userId) {
        if (StringUtil.isBlank(learningExpId)) {
            throw new ServiceException("学习经历ID不能为空");
        }
        if (StringUtil.isBlank(checkCollegeId)) {
            throw new ServiceException("修改院校ID不能为空");
        }
        if (StringUtil.isBlank(checkCollegeName)) {
            throw new ServiceException("修改院校名字不能为空");
        }
        // 更新处理信息
        userRepository.checkCustomCollege(learningExpId, checkCollegeName, checkCollegeId);
        /* 审核后 更新学习经历 */
        LearningExperienceParam learningExperienceParam = new LearningExperienceParam();
        learningExperienceParam.setId(learningExpId);
        learningExperienceParam.setCollegeId(checkCollegeId);
        learningExperienceParam.setCollegeName(checkCollegeName);
        userRepository.updateLearningExperience(learningExperienceParam);
        /* 管理员处理之后强制退出 */
        auth2Helper.invalidToken(userId);
    }

    @Override
    public PageVO getCustomCollege(CustomCollegeParam param) {
        if (Objects.isNull(param.getStatus())) {
            throw new ServiceException("状态不能为空");
        }
        return userRepository.getCustomCollege(param);
    }

    @Override
    public List<User> getUsersByTelSet(Set<String> telSet) {
        if (CollectionUtils.isEmpty(telSet)) {
            return null;
        }
        return userRepository.getUsersByTelSet(telSet);
    }

    private static String getTokenKey(String telephone, Integer type) {
        String key = null;
        if (Objects.nonNull(type)) {
            key = telephone + "_" + type;
        }
        return KeyBuilder.getIsNewAccountTokenKey(key);
    }

    private void saveToken(Integer type, String telephone, String token) {
        jedisTemplate.set(getTokenKey(telephone, type), token);
        jedisTemplate.expire(getTokenKey(telephone, type), KeyBuilder.isNewAccountTokenActiveTime);
    }

}
