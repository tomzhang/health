package com.dachen.health.openApi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dachen.common.auth.Auth2Helper;
import com.dachen.common.auth.data.AccessToken;
import com.dachen.commons.constants.Constants.ResultCode;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.micro.Result;
import com.dachen.commons.micro.comsume.RibbonManager;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.Source;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.example.UserExample;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.UserConstant;
import com.dachen.health.commons.vo.UserSource;
import com.dachen.health.openApi.dao.IThirdAppDAO;
import com.dachen.health.openApi.entity.CircleInfo;
import com.dachen.health.openApi.entity.OpenUserVO;
import com.dachen.health.openApi.entity.SimpleOpenUserVO;
import com.dachen.health.openApi.entity.ThirdApp;
import com.dachen.health.openApi.service.IOpenUserService;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.health.user.entity.po.Doctor.Check;
import com.dachen.manager.RemoteServiceResult;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liangcs
 * @desc
 * @date:2017/5/2 18:09
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Service
public class OpenUserServiceImpl implements IOpenUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenUserServiceImpl.class);

    private static final Integer YES = 1;

    private static final Integer CODE_LENGTH = 32;

    //超时时间
    private static final Integer ACTIVE_TIME = 30 * 60;

    @Autowired
    private Auth2Helper auth2Helper;

    @Autowired
    private IThirdAppDAO thirdAppDAO;

    @Autowired
    private JedisTemplate redis;

    @Autowired
    private NotifyRetryHandle notifyRetryHandle;

    @Autowired
    private UserManager userManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RibbonManager ribbonManager;

    @Override
    public Map<String, String> login(String appId, String telephone, String password,
        Integer userType) {

        if (StringUtils.isAnyEmpty(appId, telephone, password) || Objects.isNull(userType)) {
            throw new ServiceException("参数缺失");
        }

        //从缓存里取第三方应用信息
        ThirdApp thirdApp = getAppInfo(appId);

        User user = userManager.getUser(telephone, userType);
        if (Objects.isNull(user)) {
            throw new ServiceException("该手机号未注册");
        }

        // 先判断用户是否需要重置密码、若用户需要重置密码
        if (Objects.nonNull(user.getNeedReSetPassword()) && user.getNeedReSetPassword() && !(
            Objects.nonNull(user.getFarm()) && user.getFarm().getNeedResetPhoneAndPass())) {
            throw new ServiceException("请重置密码");
        }

        // 增加未激活状态判断
        if (UserConstant.SuspendStatus.tempForbid.getIndex() == user.getSuspend()
            || UserEnum.UserStatus.forbidden.getIndex() == user.getStatus()
            || UserEnum.UserStatus.offLine.getIndex() == user.getStatus()
            || UserEnum.UserStatus.logOut.getIndex() == user.getStatus()) {
            throw new ServiceException("账号不正常，暂时无法登陆");
        }

        Integer val = auth2Helper.validatePassword(telephone, password, userType);
        if (!Objects.equals(YES, val)) {
            throw new ServiceException("账号密码不匹配");
        }

        String code = StringUtil.randomString(CODE_LENGTH);

        //格式化存储
        redis.set(formatCode(code), formatUserId(user.getUserId(), appId));
        redis.expire(code, ACTIVE_TIME);

        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("callbackUrl", thirdApp.getCallbackUrl());

        return map;

    }

    @Override
    public OpenUserVO getUser(String appId, String code, String sign) {

        //判断code
        String value = redis.get(formatCode(code));
        if (StringUtil.isEmpty(value)) {
            throw new ServiceException("code无效");
        }

        //验证签名
        ThirdApp thirdApp = getAppInfo(appId);
        if (!verifySign(appId, thirdApp.getKey(), code, sign)) {
            throw new ServiceException("非法操作");
        }

        //验证appId
        String[] values = value.split(":");
        if (!Objects.equals(values[1], appId)) {
            throw new ServiceException("操作越权");
        }

        Integer userId = Integer.valueOf(values[0]);
        User user = userManager.getUser(userId);

        return covertUser(user);
    }

    @Override
    public OpenUserVO getUserByOpenId(String openId) {
        Integer userId = auth2Helper.getUserIdByOpenId(openId);
        if(Objects.isNull(userId)){
            throw new ServiceException("openId不合法");
        }
        User user = userManager.getUser(userId);
        return covertUser(user);
    }

    @Override
    public List<OpenUserVO> getUserArea(String timestamp, String appId, String sign, List<Integer> userIds) {

        String timeOut = String.format("app_access_frequency_%s", appId);

        String result = redis.get(timeOut);
        if (StringUtil.isEmpty(result)) {
            redis.set(timeOut, appId);
            redis.expire(timeOut, 5);
        } else {
            throw new ServiceException("访问频繁");
        }


        //验证签名
        ThirdApp thirdApp = getAppInfo(appId);
        if (!verifySignWithTS(timestamp, appId, thirdApp.getKey(), sign)) {
            throw new ServiceException("非法操作");
        }

        List<User> users = userRepository.findUsersWithOutStatusByIds(userIds);

        List<OpenUserVO> vos = new ArrayList<>();
        if (CollectionUtils.isEmpty(users)) {
            return vos;
        }
        List<AccessToken> accessTokens = auth2Helper.getOpenIdList(userIds);
        Map<Integer,String> userIdToOpenId = accessTokens.stream()
                .collect(Collectors.toMap(AccessToken::getUserId,AccessToken::getOpenId,(oldValue,newValue)->oldValue));
        for (User user : users) {
            String openId = userIdToOpenId.get(user.getUserId());
            OpenUserVO openUserVO = covertUser(user);
            openUserVO.setOpenId(openId);
            vos.add(openUserVO);
        }
        return vos;
    }



    @Override
    public Integer getUserId(String appId, String code) {

        //判断code
        String value = redis.get(formatCode(code));
        if (StringUtil.isEmpty(value)) {
            throw new ServiceException("code无效");
        }

        //验证appId
        String[] values = value.split(":");
        if (!Objects.equals(values[1], appId)) {
            throw new ServiceException("操作越权");
        }

        return Integer.valueOf(values[0]);

    }

    @Override
    @Async
    public void sendNotify(String appId, Integer userId, Integer status, String remark) {

        ThirdApp thirdApp = getAppInfo(appId);
        String notifyUrl = thirdApp.getNotifyUrl();

        Map<String, String> params = new HashMap<>();
        params.put("status", status + "");
        params.put("userId", userId + "");
        params.put("remark", remark);

        try {
            notifyRetryHandle.execute(notifyUrl, params);
        } catch (Exception e) {
        }

    }

    @Override
    public ThirdApp init(String name, String callbackUrl, String notifyUrl) {
        ThirdApp thirdApp = new ThirdApp();
        thirdApp.setAppId(StringUtil.randomString(CODE_LENGTH));
        thirdApp.setName(name);
        thirdApp.setKey(StringUtil.randomString(CODE_LENGTH * 2));
        thirdApp.setCallbackUrl(callbackUrl);
        thirdApp.setNotifyUrl(notifyUrl);
        return thirdAppDAO.save(thirdApp);
    }

    @Override
    public User update(String appId, String code, String headPicFileName, String hospital,
        String hospitalId, String dept, String deptId, String title) {

        UserExample userExample = new UserExample();
        Doctor doctor = new Doctor();
        doctor.setTitle(title);
        doctor.setDeptId(deptId);
        doctor.setDepartments(dept);
        doctor.setHospital(hospital);
        doctor.setHospitalId(hospitalId);
        userExample.setDoctor(doctor);
        userExample.setHeadPicFileName(headPicFileName);

        Integer userId = getUserId(appId, code);

        User user = userManager.getUser(userId);
        if (Objects.equals(user.getStatus(), UserStatus.normal.getIndex())) {
            throw new ServiceException("该用户已审核通过");
        }

        //更新信息
        try {
            user = userManager.updateUser(userId, userExample);

        }  catch (HttpApiException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public SimpleOpenUserVO getSimpleUserByOpenId(String openId) {
        Integer userId = auth2Helper.getUserIdByOpenId(openId);
        if(Objects.isNull(userId)){
            throw new ServiceException("openId不合法");
        }
        User user = userManager.getUser(userId);
        SimpleOpenUserVO simpleOpenUserVO = covertSimpleUser(user);
        simpleOpenUserVO.setOpenId(openId);
        return simpleOpenUserVO;
    }

    @Override
    public Map<String, String> loginByCode(String appId, String telephone, Integer userType) {
        if (StringUtils.isAnyEmpty(appId, telephone) || Objects.isNull(userType)) {
            throw new ServiceException("参数缺失");
        }
        //从缓存里取第三方应用信息
        ThirdApp thirdApp = getAppInfo(appId);
        User user = userManager.getUser(telephone, userType);
        if (Objects.isNull(user)) {
            throw new ServiceException("该手机号未注册");
        }
        // 先判断用户是否需要重置密码、若用户需要重置密码
        if (Objects.nonNull(user.getNeedReSetPassword()) && user.getNeedReSetPassword() && !(
                Objects.nonNull(user.getFarm()) && user.getFarm().getNeedResetPhoneAndPass())) {
            throw new ServiceException("请重置密码");
        }
        // 增加未激活状态判断
        if (UserConstant.SuspendStatus.tempForbid.getIndex() == user.getSuspend()
                || UserEnum.UserStatus.forbidden.getIndex() == user.getStatus()
                || UserEnum.UserStatus.offLine.getIndex() == user.getStatus()
                || UserEnum.UserStatus.logOut.getIndex() == user.getStatus()) {
            throw new ServiceException("账号不正常，暂时无法登陆");
        }
        AccessToken tokenVo = auth2Helper.loginByTel(telephone, userType, false, null, null, ReqUtil.instance.getHeaderInfo().getDeviceType(), ReqUtil.instance.getHeaderInfo().getAppName());
        if (Objects.isNull(tokenVo)) {
            throw new ServiceException("登录失败");
        }
        String code = StringUtil.randomString(CODE_LENGTH);
        //格式化存储
        redis.set(formatCode(code), formatUserId(user.getUserId(), appId));
        redis.expire(code, ACTIVE_TIME);
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("callbackUrl", thirdApp.getCallbackUrl());
        return map;
    }

    private SimpleOpenUserVO covertSimpleUser(User user) {
        SimpleOpenUserVO vo = new SimpleOpenUserVO();
        vo.setName(user.getName());
        vo.setTelephone(user.getTelephone());
        vo.setStatus(user.getStatus());
        vo.setUserId(user.getUserId());
        vo.setHeadPicFileName(user.getHeadPicFileName());
        vo.setSuspend(user.getSuspend());

        Doctor doctor = user.getDoctor();
        if (Objects.nonNull(doctor)) {
            Check check = doctor.getCheck();
            if (Objects.nonNull(check)) {
                vo.setHospital(check.getHospital());
                vo.setHospitalId(check.getHospitalId());
                vo.setDepartment(check.getDepartments());
                vo.setDeptId(check.getDeptId());
                vo.setProvinceId(doctor.getProvinceId());
                vo.setProvince(doctor.getProvince());
                vo.setCityId(doctor.getCityId());
                vo.setCity(doctor.getCity());
                vo.setCountryId(doctor.getCountryId());
                vo.setCountry(doctor.getCountry());
            } else {
                vo.setHospital(doctor.getHospital());
                vo.setHospitalId(doctor.getHospitalId());
                vo.setDepartment(doctor.getDepartments());
                vo.setDeptId(doctor.getDeptId());
            }
        }
        /* remote get circleId */
        List<CircleInfo> circleList = Lists.newArrayList();
        try {
            Result result = ribbonManager.get("http://CIRCLE/inner/base/findLoginCircleByUserId?userId={userId}", Result.class, user.getUserId());
            if (!Objects.equals(result.getResultCode(), ResultCode.Success)) {
                throw new ServiceException(result.getResultMsg());
            }
            circleList = JSONArray.parseArray(JSON.toJSONString(result.getData()), CircleInfo.class);
        } catch (Exception ex) {
            LOGGER.error("getUserCircles, 获取用户加入的圈子和科室, exception:{}", ex);
        }
        if (!CollectionUtils.isEmpty(circleList)) {
            vo.setCircleIdList(circleList.stream().map(o -> String.valueOf(o.getId())).collect(Collectors.toList()));
        }
        return vo;
    }

    @Override
    public Boolean checkAppId(String appId) {

        ThirdApp app = getAppInfo(appId);

        return Objects.nonNull(app);
    }

    private String formatCode(String code) {
        return MessageFormat.format("openCode:{0}", code);
    }

    private String formatUserId(Integer userId, String appId) {
        return MessageFormat.format("{0}:{1}", String.valueOf(userId), appId);
    }

    private String formatAppId(String appId) {
        return MessageFormat.format("appId:{0}", appId);
    }

    private ThirdApp getAppInfo(String appId) {

        ThirdApp thirdApp;
        String app = redis.get(formatAppId(appId));
        if (StringUtil.isEmpty(app)) {
            thirdApp = thirdAppDAO.findByAppId(appId);
            if (Objects.isNull(thirdApp)) {
                throw new ServiceException("appId非法");
            }
            redis.set(formatAppId(appId), JSON.toJSONString(thirdApp));
            redis.expire(formatAppId(appId), 60);
        } else {
            thirdApp = JSON.parseObject(app, ThirdApp.class);
        }

        return thirdApp;
    }

    private Boolean verifySign(String appId, String key, String code, String sign) {

        StringBuilder sb = new StringBuilder();
        sb.append("appId=").append(appId).append("&");
        sb.append("code=").append(code).append("&");
        sb.append("key=").append(key);

        String s = DigestUtils.md5Hex(sb.toString());

        return Objects.equals(s, sign);

    }

    private Boolean verifySignWithTS(String timestamp, String appId, String key, String sign) {
        StringBuilder sb = new StringBuilder();
        sb.append("appId=").append(appId).append("&");
        sb.append("key=").append(key);

        String s = DigestUtils.md5Hex(sb.toString());

        return Objects.equals(s, sign);
    }

    private OpenUserVO covertUser(User user) {
        OpenUserVO vo = new OpenUserVO();

        vo.setHeadPicFileName(user.getHeadPicFileName());
        vo.setName(user.getName());
        vo.setArea(user.getArea());
        vo.setTelephone(user.getTelephone());
        vo.setRegisterTime(user.getCreateTime());
        vo.setStatus(user.getStatus());
        vo.setUserId(user.getUserId());
        vo.setSuspend(user.getSuspend());
        List<AccessToken> openIdList = auth2Helper.getOpenIdList(Arrays.asList(user.getUserId()));
        if (!CollectionUtils.isEmpty(openIdList)) {
            vo.setOpenId(openIdList.get(0).getOpenId());
        }
        Doctor doctor = user.getDoctor();
        if (Objects.nonNull(doctor)) {

            Check check = doctor.getCheck();
            if (Objects.nonNull(check)) {
                vo.setAuthenticateTime(check.getCheckTime());
                vo.setHospital(check.getHospital());
                vo.setDepartment(check.getDepartments());
                vo.setTitle(check.getTitle());
                vo.setProvinceId(doctor.getProvinceId());
                vo.setProvince(doctor.getProvince());
                vo.setCityId(doctor.getCityId());
                vo.setCity(doctor.getCity());
                vo.setCountryId(doctor.getCountryId());
                vo.setCountry(doctor.getCountry());
            } else {
                vo.setHospital(doctor.getHospital());
                vo.setDepartment(doctor.getDepartments());
                vo.setTitle(doctor.getTitle());
            }
        }

        UserSource userSource = user.getSource();
        if (Objects.nonNull(userSource)) {
            //邀请人
            if (Objects.nonNull(userSource.getInviterId())) {
                    vo.setInviterName(getUserNameById(userSource.getInviterId()));
            }

            //用户来源
            if (Objects.nonNull(userSource.getSourceType())) {
                Source source = Source.getEnum(userSource.getSourceType());
                vo.setSource(source.getSource());
            }

            if (StringUtil.isNotEmpty(userSource.getAppId())) {
                ThirdApp thirdApp = getAppInfo(userSource.getAppId());
                vo.setSource(thirdApp.getName());
            }
        }


        return vo;
    }

    private String getUserNameById(Integer id) {

        Map<String, List<Integer>> idMap = Maps.newHashMap();
        List<Integer> ids = Lists.newArrayList();
        ids.add(id);
        idMap.put("userId", ids);

        String response = ribbonManager.post("http://AUTH2/v2/user/getSimpleUser", idMap);

        RemoteServiceResult result = JSON.parseObject(response, RemoteServiceResult.class);
        if (!Objects.equals(result.getResultCode(), ResultCode.Success)) {
            throw new ServiceException(result.getDetailMsg());
        }

        List<AuthSimpleUser> users = JSONArray.parseArray(JSON.toJSONString(result.getData()), AuthSimpleUser.class);

        if (!CollectionUtils.isEmpty(users)) {
            return users.get(0).getName();
        }
        return null;
    }

}

class AuthSimpleUser {
    private Integer id;
    private String name;
    private String headPic;
    private Integer userType;
    private String telephone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
