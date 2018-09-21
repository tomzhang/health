package com.dachen.health.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.UserLoginInfo;
import com.dachen.health.user.dao.IAbnormalDevicesDAO;
import com.dachen.health.user.entity.param.LoginDevicesParam;
import com.dachen.health.user.entity.po.AbnormalLoginDevice;
import com.dachen.health.user.entity.po.LoginUser;
import com.dachen.health.user.entity.vo.AbnormalLoginDevicesExVO;
import com.dachen.health.user.entity.vo.AbnormalLoginVO;
import com.dachen.health.user.service.IAbnormalDevicesService;
import com.dachen.manager.RemoteSysManagerUtil;
import com.dachen.util.BeanUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mobsms.sdk.SmsEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: xuhuanjie
 * Date: 2018-09-05
 * Time: 15:41
 * Description:
 */
@Service
public class AbnormalDevicesServiceImpl implements IAbnormalDevicesService {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbnormalDevicesServiceImpl.class);

    @Autowired
    private IAbnormalDevicesDAO abnormalDevicesDAO;

    @Autowired
    private UserManager userManager;

    @Autowired
    private RemoteSysManagerUtil remoteSysManagerUtil;


    @Override
    public List<AbnormalLoginVO> statsLoginDevices(Long startTime, Long endTime, Long limit) {
        Iterator<AbnormalLoginVO> detailIterator = abnormalDevicesDAO.statsLoginDevices(startTime, endTime, limit);
        return IteratorUtils.toList(detailIterator);
    }

    @Override
    public void updateAbnormalLoginDevice(AbnormalLoginVO abnormalLoginDevice) {
        // 和数据库的异常用户比对
        List<LoginUser> loginUsers = this.updateLoginUsersByDBLoginUser(abnormalLoginDevice);
        abnormalLoginDevice.setLoginUser(loginUsers);
        abnormalDevicesDAO.updateLoginDevice(abnormalLoginDevice);
        // 若该设备ID存在已处理的异常设备  则异常次数同步加一 (批量更新)
        abnormalDevicesDAO.updateHandleDeviceCount(abnormalLoginDevice.getSerial());
    }

    private List<LoginUser> updateLoginUsersByDBLoginUser(AbnormalLoginVO abnormalLoginDevice) {
        // init
        List<LoginUser> newLoginUsers = Lists.newArrayListWithCapacity(64);
        // 当日新统计的异常用户集合
        List<LoginUser> loginUsersParam = abnormalLoginDevice.getLoginUser();
        // 待处理状态
        Integer unCheckStatus = 0;
        // 数据库的异常用户集合
        List<LoginUser> loginUsersDB = this.getAbnormalLoginDeviceByDeviceId(abnormalLoginDevice.getSerial(), unCheckStatus).getLoginUsers();
        // add
        newLoginUsers.addAll(loginUsersParam);
        /* 当日新统计的异常用户集合没有的 则加入 */
        Map<Integer, LoginUser> usersParamMap = Maps.uniqueIndex(loginUsersParam, LoginUser::getUserId);
        for (LoginUser loginUser : loginUsersDB) {
            Integer userId = loginUser.getUserId();
            if (!usersParamMap.containsKey(userId)) {
                newLoginUsers.add(loginUser);
            }
        }
        return newLoginUsers;
    }

    @Override
    public List<UserLoginInfo> getUserLoginInfoByDeviceIds(Long startTime, Long endTime, List<String> deviceIds) {
        return IteratorUtils.toList(abnormalDevicesDAO.getUserLoginInfoByDeviceIds(startTime, endTime, deviceIds));
    }

    @Override
    public AbnormalLoginDevice getAbnormalLoginDeviceByDeviceId(String deviceId, Integer status) {
        return abnormalDevicesDAO.getAbnormalLoginDeviceByDeviceId(deviceId, status);
    }

    @Override
    public Boolean existsAbnormalLoginDevice(String deviceId) {
        return abnormalDevicesDAO.existsAbnormalLogin(deviceId);
    }

    @Override
    public void insertAbnormalLoginDevice(AbnormalLoginVO abnormalLoginDevice) {
        /* 查询已处理的异常设备异常次数 若该设备首次异常 则次数为1 反之加一*/
        // 已处理状态
        Integer checkStatus = 1;
        Integer abnormalCount = 1;
        Boolean important = Boolean.FALSE;
        String deviceId = abnormalLoginDevice.getSerial();
        AbnormalLoginDevice abnormalLoginDeviceDB = this.getAbnormalLoginDeviceByDeviceId(deviceId, checkStatus);
        if (Objects.nonNull(abnormalLoginDeviceDB)) {
            abnormalCount = abnormalLoginDeviceDB.getAbnormalCount() + 1;
            important = abnormalLoginDeviceDB.getImportant();
            // 若该设备ID存在已处理的异常设备  则异常次数同步加一 (批量更新)
            abnormalDevicesDAO.updateHandleDeviceCount(deviceId);
        }
        abnormalLoginDevice.setCount(abnormalCount);
        abnormalLoginDevice.setImportant(important);
        abnormalDevicesDAO.insertAbnormalLoginDevice(abnormalLoginDevice);
    }

    @Override
    public PageVO getAbnormalLoginDevices(LoginDevicesParam param) {
        if (Objects.isNull(param.getStatus())) {
            throw new ServiceException("状态不能为空");
        }
        PageVO abnormalLoginDevices = abnormalDevicesDAO.getAbnormalLoginDevices(param);
        List<AbnormalLoginDevice> abnormalLoginDevicesPageData = (List<AbnormalLoginDevice>) abnormalLoginDevices.getPageData();
        /* 取学币  学币是否被禁用... */
        if (!CollectionUtils.isEmpty(abnormalLoginDevicesPageData)) {
            ArrayList<LoginUser> userList = Lists.newArrayList();
            abnormalLoginDevicesPageData.stream().forEach(x -> userList.addAll(x.getLoginUsers()));
            Set<String> userIdSet = userList.stream().map(LoginUser::getUserId).map(x -> String.valueOf(x)).collect(Collectors.toSet());
            List<String> userIdList = Lists.newArrayList(userIdSet);
            Map<String, String> params = new HashMap();
            String creditURL = "http://CREDIT/inner_api/user/findByUserIds";
            String isInBlacklistURL = "http://CREDIT/inner/blacklist/isInBlacklist?userIds={userIds}";
            params.put("userIds", String.join(",", userIdList));
            // 请求学币余额
            String creditResponse = remoteSysManagerUtil.post(creditURL, params);
            // 请求学币是否被禁用
            String blacklistResponse = remoteSysManagerUtil.get(isInBlacklistURL, params);
            // 禁用学币的userId集合
            List<String> blackUserIdList = JSON.parseObject(blacklistResponse, List.class);
            Map<String, Map<String, Object>> userIdKeyMap = JSON.parseObject(creditResponse, Map.class);
            if (Objects.nonNull(userIdKeyMap) && userIdKeyMap.size() > 0) {
                for (AbnormalLoginDevice abnormalLoginDevice : abnormalLoginDevicesPageData) {
                    abnormalLoginDevice.getLoginUsers().stream().forEach(x -> {
                        Map<String, Object> xCredit = userIdKeyMap.get(Integer.toString(x.getUserId()));
                        if (Objects.nonNull(xCredit) && Objects.nonNull(xCredit.get("balance"))) {
                            x.setCredit(Long.parseLong(xCredit.get("balance").toString()));
                            if (!CollectionUtils.isEmpty(blackUserIdList) && blackUserIdList.contains(Integer.toString(x.getUserId()))) {
                                x.setIsBlack(Boolean.TRUE);
                            }
                        }
                    });
                }
            }
        }
        return abnormalLoginDevices;
    }

    @Override
    public void updateStatus(String deviceId) {
        if (StringUtil.isBlank(deviceId)) {
            throw new ServiceException("设备ID不能为空");
        }
        Integer handlerId = ReqUtil.instance.getUserId();
        String handlerName = userManager.getUser(handlerId).getName();
        abnormalDevicesDAO.updateStatus(deviceId, handlerId, handlerName);
    }

    @Override
    public void cancelImportant(String deviceId) {
        if (StringUtil.isBlank(deviceId)) {
            throw new ServiceException("设备ID不能为空");
        }
        abnormalDevicesDAO.updateImportant(deviceId, Boolean.FALSE);
    }

    @Override
    public void sendMsg(String phone, String content) {
        Map<String, String> params = Maps.newHashMap();
        params.put("tel", phone);
        params.put("content", content);
        /**
         * @see SmsEnum
         */
        params.put("ext", "7");
        Object response = remoteSysManagerUtil.post(UserEnum.SEND_SMS_URL, params);
        LOGGER.info(response.toString());
    }

    @Override
    public void setImportant(String deviceId) {
        if (StringUtil.isBlank(deviceId)) {
            throw new ServiceException("设备ID不能为空");
        }
        // 批量更新
        abnormalDevicesDAO.updateImportant(deviceId, Boolean.TRUE);
    }

    @Override
    public List<AbnormalLoginDevicesExVO> exportAbnormalDevices(LoginDevicesParam param) {
        if (Objects.isNull(param.getStatus())) {
            throw new ServiceException("状态不能为空");
        }
        List<AbnormalLoginDevice> abnormalLoginDevices = abnormalDevicesDAO.listAllQuery(param);
        if (CollectionUtils.isEmpty(abnormalLoginDevices)) {
            return null;
        }
        List<AbnormalLoginDevicesExVO> abnormalLoginDevicesExVOs = Lists.newArrayList();
        for (AbnormalLoginDevice loginDevice : abnormalLoginDevices) {
            for (LoginUser loginUser : loginDevice.getLoginUsers()) {
                AbnormalLoginDevicesExVO copy = BeanUtil.copy(loginUser, AbnormalLoginDevicesExVO.class);
                copy.setAbnormalCount(loginDevice.getAbnormalCount());
                copy.setDeviceId(loginDevice.getDeviceId());
                copy.setStatus(loginDevice.getStatus());
                copy.setHandlerId(loginDevice.getHandlerId());
                copy.setHandlerName(loginDevice.getHandlerName());
                copy.setHandleTime(loginDevice.getHandleTime());
                abnormalLoginDevicesExVOs.add(copy);
            }
        }
        return abnormalLoginDevicesExVOs;
    }

    @Override
    public List<Map<String, Object>> listToMap(List<AbnormalLoginDevicesExVO> devices) {
        DateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<Map<String, Object>> result = Lists.newArrayList();
        Map<String, Object> sheet = new HashMap();
        sheet.put("sheetName", "sheet1");
        result.add(sheet);
        for (AbnormalLoginDevicesExVO device : devices) {
            Map<String, Object> map = Maps.newHashMap();
            if (StringUtil.isNoneBlank(device.getDeviceId())) {
                map.put("deviceId", device.getDeviceId());
            }
            if (Objects.nonNull(device.getAbnormalCount())) {
                map.put("abnormalCount", device.getAbnormalCount());
            }
            if (StringUtil.isNoneBlank(device.getUserName())) {
                map.put("userName", device.getUserName());
            }
            if (StringUtil.isNoneBlank(device.getPhone())) {
                map.put("phone", device.getPhone());
            }
            if (StringUtil.isNoneBlank(device.getHospital())) {
                map.put("hospital", device.getHospital());
            }
            if (StringUtil.isNoneBlank(device.getDept())) {
                map.put("dept", device.getDept());
            }
            if (StringUtil.isNoneBlank(device.getTitle())) {
                map.put("title", device.getTitle());
            }
            if (Objects.nonNull(device.getInviteId())) {
                map.put("inviteId", device.getInviteId());
            }
            if (StringUtil.isNoneBlank(device.getInviteName())) {
                map.put("inviteName", device.getInviteName());
            }
            if (StringUtil.isNoneBlank(device.getSource())) {
                map.put("source", device.getSource());
            }
            if (Objects.nonNull(device.getLoginTime())) {
                map.put("loginTime", sdfTime.format(device.getLoginTime()));
            }
            if (Objects.nonNull(device.getStatus())) {
                map.put("status", Objects.equals(device.getStatus(), 0) ? "待处理" : "已处理");
            }
            if (Objects.equals(device.getStatus(), 1)) {
                if (Objects.nonNull(device.getHandlerId())) {
                    map.put("handlerId", device.getHandlerId());
                }
                if (StringUtil.isNoneBlank(device.getHandlerName())) {
                    map.put("handlerName", device.getHandlerName());
                }
                if (Objects.nonNull(device.getHandleTime())) {
                    map.put("handleTime", sdfTime.format(device.getHandleTime()));
                }
            }
            result.add(map);
        }
        return result;
    }

}
