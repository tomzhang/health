package com.dachen.health.user.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.vo.UserLoginInfo;
import com.dachen.health.user.entity.param.LoginDevicesParam;
import com.dachen.health.user.entity.po.AbnormalLoginDevice;
import com.dachen.health.user.entity.vo.AbnormalLoginDevicesExVO;
import com.dachen.health.user.entity.vo.AbnormalLoginVO;

import java.util.List;
import java.util.Map;

/**
 * Author: xuhuanjie
 * Date: 2018-09-05
 * Time: 15:38
 * Description:
 */
public interface IAbnormalDevicesService {

    /**
     * 定时任务统计异常登录设备
     *
     * @param startTime
     * @param endTime
     * @param limit
     * @return
     */
    List<AbnormalLoginVO> statsLoginDevices(Long startTime, Long endTime, Long limit);

    /**
     * 根据deviceId更新异常登录设备记录
     *
     * @param abnormalLoginDevice
     */
    void updateAbnormalLoginDevice(AbnormalLoginVO abnormalLoginDevice);

    /**
     * 根据deviceId集合取登录信息
     *
     * @param startTime
     * @param endTime
     * @param deviceIds
     * @return
     */
    List<UserLoginInfo> getUserLoginInfoByDeviceIds(Long startTime, Long endTime, List<String> deviceIds);

    /**
     * 根据设备Id和状态取异常设备
     *
     * @param deviceId
     * @param status
     * @return
     */
    AbnormalLoginDevice getAbnormalLoginDeviceByDeviceId(String deviceId, Integer status);

    /**
     * 该设备Id是否存在异常登录记录
     *
     * @param deviceId
     * @return
     */
    Boolean existsAbnormalLoginDevice(String deviceId);

    /**
     * 插入异常登录设备记录
     *
     * @param abnormalLoginDevice
     */
    void insertAbnormalLoginDevice(AbnormalLoginVO abnormalLoginDevice);

    /**
     * 分页获取异常登录设备记录列表
     *
     * @param param
     * @return
     */
    PageVO getAbnormalLoginDevices(LoginDevicesParam param);

    /**
     * 运营后台处理 更新记录
     *
     * @param deviceId
     */
    void updateStatus(String deviceId);

    /**
     * 取消重点关注设备
     *
     * @param deviceId
     */
    void cancelImportant(String deviceId);

    /**
     * 发短信
     *
     * @param phone
     * @param content
     */
    void sendMsg(String phone, String content);

    /**
     * 重点关注设备
     *
     * @param deviceId
     */
    void setImportant(String deviceId);

    /**
     * 导出符条件的异常设备数据
     *
     * @param param
     * @return
     */
    List<AbnormalLoginDevicesExVO> exportAbnormalDevices(LoginDevicesParam param);

    /**
     * @param devices
     * @return
     */
    List<Map<String, Object>> listToMap(List<AbnormalLoginDevicesExVO> devices);

}
