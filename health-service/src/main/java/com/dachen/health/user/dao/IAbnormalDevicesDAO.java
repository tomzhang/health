package com.dachen.health.user.dao;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.vo.UserLoginInfo;
import com.dachen.health.user.entity.param.LoginDevicesParam;
import com.dachen.health.user.entity.po.AbnormalLoginDevice;
import com.dachen.health.user.entity.vo.AbnormalLoginVO;

import java.util.Iterator;
import java.util.List;

/**
 * Author: xuhuanjie
 * Date: 2018-09-05
 * Time: 15:43
 * Description:
 */
public interface IAbnormalDevicesDAO {

    Iterator<AbnormalLoginVO> statsLoginDevices(Long startTime, Long endTime, Long limit);

    void updateLoginDevice(AbnormalLoginVO abnormalLoginDevice);

    AbnormalLoginDevice getAbnormalLoginDeviceByDeviceId(String deviceId, Integer status);

    Iterator<UserLoginInfo> getUserLoginInfoByDeviceIds(Long startTime, Long endTime, List<String> deviceIds);

    Boolean existsAbnormalLogin(String deviceId);

    void insertAbnormalLoginDevice(AbnormalLoginVO abnormalLoginDevice);

    PageVO getAbnormalLoginDevices(LoginDevicesParam param);

    void updateStatus(String id, Integer handlerId, String handlerName);

    void updateImportant(String deviceId, Boolean status);

    List<AbnormalLoginDevice> listAllQuery(LoginDevicesParam param);

    void updateHandleDeviceCount(String deviceId);
}
