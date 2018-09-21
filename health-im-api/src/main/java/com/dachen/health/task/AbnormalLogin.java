package com.dachen.health.task;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.UserLoginInfo;
import com.dachen.health.commons.vo.UserSource;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.health.user.entity.po.LoginUser;
import com.dachen.health.user.entity.vo.AbnormalLoginVO;
import com.dachen.health.user.service.IAbnormalDevicesService;
import com.dachen.redis.RedisLock;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author: xuhuanjie
 * Date: 2018-09-03
 * Time: 15:31
 * Description:
 */
@Component
public class AbnormalLogin {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbnormalLogin.class);


    private static final String LIMIT = "abnormal.login.limit";

    @Autowired
    private UserManager userManager;

    @Autowired
    private IAbnormalDevicesService abnormalDevicesService;

    /**
     * 每天晚上2点30开始统计
     */
    @Scheduled(cron = "0 30 2 * * ?")
    @RedisLock(key = "abnormal-login-lock")
    public void statsLoginDevices() {
        Long limit = Long.valueOf(PropertiesUtil.getContextProperty(LIMIT));
        Long startTime = getStartTime();
        Long endTime = getEndTime();
        // 定时统计出来的异常设备记录
        List<AbnormalLoginVO> abnormalLoginDevices = abnormalDevicesService.statsLoginDevices(startTime, endTime, limit);
        statsAbnormalLoginDevices(abnormalLoginDevices, startTime, endTime);
    }

    @RedisLock(key = "abnormal-login-lock-trigger")
    public void statsLoginDevices(Long startTime, Long endTime) {
        Long limit = Long.valueOf(PropertiesUtil.getContextProperty(LIMIT));
        if (Objects.isNull(startTime) || Objects.isNull(endTime) || startTime >= endTime || startTime <= 0 || endTime <= 0) {
            throw new ServiceException("时间入参不合法");
        }
        // 定时统计出来的异常设备记录
        List<AbnormalLoginVO> abnormalLoginDevices = abnormalDevicesService.statsLoginDevices(startTime, endTime, limit);
        statsAbnormalLoginDevices(abnormalLoginDevices, startTime, endTime);
    }

    private void statsAbnormalLoginDevices(List<AbnormalLoginVO> abnormalLoginDevices, Long startTime, Long endTime) {
        if (!CollectionUtils.isEmpty(abnormalLoginDevices)) {
            // 异常设备ID集合
            List<String> deviceIds = abnormalLoginDevices.stream().map(AbnormalLoginVO::getSerial).collect(Collectors.toList());
            LOGGER.info("异常设备ID集合:{},-----Size:{}", JSON.toJSON(deviceIds), deviceIds.size());
            // 异常登录记录集合
            List<UserLoginInfo> userLoginInfo = abnormalDevicesService.getUserLoginInfoByDeviceIds(startTime, endTime, deviceIds);
            /* user集合 */
            Set<String> telephoneSet = userLoginInfo.stream().map(UserLoginInfo::getTelephone).collect(Collectors.toSet());
            List<User> users = userManager.getUsersByTelSet(telephoneSet);
            // 开发环境会有脏数据,如果有重复的key,则保留key1,舍弃key2
            Map<String, User> telUserMap = users.stream().collect(Collectors.toMap(User::getTelephone, a -> a, (k1, k2) -> k1));
            // 异常登录记录 通过设备Id分组
            Map<String, List<UserLoginInfo>> userLoginInfoMap = userLoginInfo.stream().collect(Collectors.groupingBy(UserLoginInfo::getSerial));
            /* 构造LoginUser(该设备登陆的用户列表) */
            for (AbnormalLoginVO abnormalLoginDevice : abnormalLoginDevices) {
                String serial = abnormalLoginDevice.getSerial();
                /* 设备号为空字符串 不统计 */
                if (StringUtil.isBlank(serial)) {
                    continue;
                }
                // LoginUser List
                List<LoginUser> loginUsers = Lists.newArrayList();
                List<UserLoginInfo> userLoginInfoList = userLoginInfoMap.get(serial);
                LOGGER.info("设备Id为:{}的user集合Size:{}", serial, userLoginInfoList.size());
                for (UserLoginInfo loginInfo : userLoginInfoList) {
                    LoginUser loginUser = new LoginUser();
                    User user = telUserMap.get(loginInfo.getTelephone());
                    if (Objects.isNull(user)) {
                        LOGGER.error("找不到用户信息,手机号:{}", loginInfo.getTelephone());
                        continue;
                    }
                    loginUser.setUserId(user.getUserId());
                    loginUser.setPhone(user.getTelephone());
                    loginUser.setUserName(user.getName());
                    loginUser.setLoginTime(loginInfo.getLoginTime());
                    Doctor doctor = user.getDoctor();
                    if (Objects.nonNull(doctor)) {
                        loginUser.setDept(doctor.getDepartments());
                        loginUser.setHospital(doctor.getHospital());
                        loginUser.setTitle(doctor.getTitle());
                    }
                    UserSource source = user.getSource();
                    if (Objects.nonNull(source)) {
                        Integer sourceType = source.getSourceType();
                        if (Objects.nonNull(sourceType)) {
                            UserEnum.Source sourceEnum = UserEnum.Source.getEnum(sourceType);
                            loginUser.setSource(sourceEnum == null ? "" : sourceEnum.getSource());
                        }
                        Integer inviteId = source.getInviterId();
                        if (Objects.nonNull(inviteId)) {
                            loginUser.setInviteId(inviteId);
                            User inviteUser = userManager.getUserInfoById(inviteId);
                            loginUser.setInviteName(inviteUser == null ? "" : inviteUser.getName());
                        }
                    }
                    loginUsers.add(loginUser);
                }
                abnormalLoginDevice.setLoginUser(loginUsers);
                // update or insert
                if (Objects.equals(abnormalDevicesService.existsAbnormalLoginDevice(serial), Boolean.TRUE)) {
                    abnormalDevicesService.updateAbnormalLoginDevice(abnormalLoginDevice);
                } else {
                    abnormalDevicesService.insertAbnormalLoginDevice(abnormalLoginDevice);
                }
            }
        }
    }

    /**
     * 获取开始时间
     *
     * @return
     */
    private Long getStartTime() {
        // 获取0点
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Long endTime = todayStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long startTime = endTime - 86400000L;
        return startTime;
    }

    /**
     * 获取结束时间
     *
     * @return
     */
    private Long getEndTime() {
        // 获取0点
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Long endTime = todayStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return endTime;
    }

}
