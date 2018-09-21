package com.dachen.health.controller.user;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.JSONMessage;
import com.dachen.health.task.AbnormalLogin;
import com.dachen.health.user.entity.param.AbnormalSendMsgParam;
import com.dachen.health.user.entity.param.LoginDevicesParam;
import com.dachen.health.user.entity.po.AbnormalLoginDevice;
import com.dachen.health.user.entity.vo.AbnormalLoginDevicesExVO;
import com.dachen.health.user.service.IAbnormalDevicesService;
import com.dachen.util.ExportHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Author: xuhuanjie
 * Date: 2018-09-04
 * Time: 18:27
 * Description:
 */
@Api(value = "/异常登录设备", description = "异常登录设备")
@RestController
@RequestMapping("/abnormal")
public class AbnormalLoginController {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbnormalLoginController.class);

    @Autowired
    private AbnormalLogin abnormalLogin;

    @Autowired
    private IAbnormalDevicesService abnormalDevicesService;

    @ApiOperation(value = "手动触发统计异常设备", httpMethod = "POST", notes = "成功返回：\"success\"")
    @PostMapping("/statsLogin")
    public JSONMessage statsLogin(@RequestParam Long startTime, @RequestParam Long endTime) {
        LOGGER.info("统计异常设备", " " + "startTime = [" + startTime + "], endTime = [" + endTime + "]");
        abnormalLogin.statsLoginDevices(startTime, endTime);
        return JSONMessage.success();
    }

    @ApiOperation(value = "运营后台获取异常设备列表", response = AbnormalLoginDevice.class, httpMethod = "POST", notes = "成功返回：\"success\"")
    @PostMapping(value = "/getLoginDevices")
    public JSONMessage getAbnormalLoginDevices(@RequestBody LoginDevicesParam param) {
        LOGGER.info("运营后台获取异常设备列表param:{}", "param = [" + JSON.toJSONString(param) + "]");
        return JSONMessage.success(abnormalDevicesService.getAbnormalLoginDevices(param));
    }

    @ApiOperation(value = "处理,改变状态", httpMethod = "POST", notes = "成功返回：\"success\"")
    @PostMapping(value = "/updateStatus/{deviceId}")
    public JSONMessage updateStatus(@PathVariable(value = "deviceId") String deviceId) {
        LOGGER.info("处理,改变状态,设备ID:{}", "deviceId = [" + deviceId + "]");
        abnormalDevicesService.updateStatus(deviceId);
        return JSONMessage.success();
    }

    @ApiOperation(value = "取消重点", httpMethod = "POST", notes = "成功返回：\"success\"")
    @PostMapping(value = "/cancelImportant/{deviceId}")
    public JSONMessage cancelImportant(@PathVariable(value = "deviceId") String deviceId) {
        LOGGER.info("取消重点,设备ID:{}", "deviceId = [" + deviceId + "]");
        abnormalDevicesService.cancelImportant(deviceId);
        return JSONMessage.success();
    }

    @ApiOperation(value = "发送短信", httpMethod = "POST", notes = "成功返回：\"success\"")
    @PostMapping(value = "/sendMsg")
    public JSONMessage sendMsg(@RequestBody AbnormalSendMsgParam param) {
        LOGGER.info("发送短信", "param = [" + param + "]");
        abnormalDevicesService.setImportant(param.getDeviceId());
        abnormalDevicesService.sendMsg(param.getPhone(), param.getContent());
        return JSONMessage.success();
    }

    @ApiOperation(value = "导出异常设备", httpMethod = "GET", notes = "成功返回：\"success\"")
    @RequestMapping(value = "/exportAbnormalDevices", method = RequestMethod.GET)
    public JSONMessage exportAbnormalDevices(HttpServletResponse response, LoginDevicesParam param) {
        LOGGER.info("导出异常设备:{}", "param = [" + JSON.toJSONString(param) + "]");
        long begin = System.currentTimeMillis();
        try {
            List<AbnormalLoginDevicesExVO> devicesExVOs = abnormalDevicesService.exportAbnormalDevices(param);
            LOGGER.info("导出异常设备查询耗时:{}" + (System.currentTimeMillis() - begin) + "ms");
            if (CollectionUtils.isEmpty(devicesExVOs)) {
                return JSONMessage.success();
            }
            List<Map<String, Object>> mapList = abnormalDevicesService.listToMap(devicesExVOs);
            String fileName = "异常设备数据";
            String[] columnNames = {"设备Id", "异常次数", "用户名", "手机号", "医院名称", "科室名称", "职称", "邀请人Id", "邀请人姓名", "注册来源", "登陆时间", "处理状态", "处理人Id", "处理人姓名", "处理时间"};
            String[] keys = {"deviceId", "abnormalCount", "userName", "phone", "hospital", "dept", "title", "inviteId", "inviteName", "source", "loginTime", "status", "handlerId", "handlerName", "handleTime"};
            ExportHelper.writeData(response, fileName, columnNames, keys, mapList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return JSONMessage.error(e);
        }
        LOGGER.info("导出异常设备流导出耗时:{}" + (System.currentTimeMillis() - begin) + "ms");
        return JSONMessage.success();
    }

}
