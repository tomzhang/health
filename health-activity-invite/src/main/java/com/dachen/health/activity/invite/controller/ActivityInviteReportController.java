package com.dachen.health.activity.invite.controller;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.activity.invite.form.InvitationReportForm;
import com.dachen.health.activity.invite.form.RegistrationReportForm;
import com.dachen.health.activity.invite.service.CircleInviteReportService;
import com.dachen.health.activity.invite.service.UserRegisterService;
import com.dachen.health.activity.invite.util.MapUtil;
import com.dachen.health.activity.invite.vo.InvitationReportVO;
import com.dachen.health.activity.invite.vo.RegistrationReportVO;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.pack.income.util.ExcelUtil;
import com.dachen.sdk.page.Pagination;
import com.dachen.util.DateUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 钟良
 * @desc
 * @date:2017/6/5 21:07 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@RestController
@RequestMapping("/activity/invite/report")
public class ActivityInviteReportController extends ActivityInviteBaseController {

    @Autowired
    private CircleInviteReportService circleInviteReportService;
    @Autowired
    private UserRegisterService userRegisterService;

    /**
     * @api {get} /activity/invite/report/invitation 邀请数据
     * @apiVersion 1.0.0
     * @apiName /activity/invite/report/invitation
     * @apiGroup 活动管理
     * @apiDescription 邀请数据
     *
     * @apiParam {String}      access_token             token
     * @apiParam {String}      userName                 邀请人姓名
     * @apiParam {String}      activityId               活动Id
     * @apiParam {Integer}     subsystem                来源子系统（医生圈-17、药企圈-16）
     *
     * @apiSuccess {String}         resultCode          返回状态码
     * @apiSuccess {String}     	data  				data
     * @apiSuccess {String}     	data.pageCount  	总页数
     * @apiSuccess {String}     	data.pageIndex  	页码
     * @apiSuccess {String}     	data.pageSize  		每页数据大小
     * @apiSuccess {String}     	data.total  		总记录数
     * @apiSuccess {String}     	data.pageData  		分页对象
     * @apiSuccess {String}     	data.pageData.userName  		    邀请人姓名
     * @apiSuccess {String}     	data.pageData.activityName  		活动名称
     * @apiSuccess {String}     	data.pageData.subsystem  		    来源子系统（医生圈-17、药企圈-16）
     * @apiSuccess {String}     	data.pageData.hospital  		    医院
     * @apiSuccess {String}     	data.pageData.title  		        职称
     * @apiSuccess {String}     	data.pageData.wechatCount  		    微信邀请数
     * @apiSuccess {String}     	data.pageData.smsCount  		    短信邀请数
     * @apiSuccess {String}     	data.pageData.qrcodeCount  		    二维码邀请数
     * @apiSuccess {String}     	data.pageData.registeredCount  		已注册用户数
     * @apiSuccess {String}     	data.pageData.autherizedCount  		已认证用户数
     *
     * @apiSuccessExample {json} Success-Response:
     *      HTTP/1.1 200 OK
     *       {
     *          "data": {
     *              "pageCount": 1,
     *              "pageData": [
     *                  {
     *                      "activityName": "拉新活动",
     *                      "autherizedCount": 0,
     *                      "hospital": "清苑县妇幼保健院",
     *                      "qrcodeCount": 2,
     *                      "registeredCount": 0,
     *                      "smsCount": 1,
     *                      "subsystem": 17,
     *                      "title": "副主任医师",
     *                      "userName": "屈军利",
     *                      "wechatCount": 1
     *                  }
     *              ],
     *              "pageIndex": 0,
     *              "pageSize": 15,
     *              "start": 0,
     *              "total": 3
     *              },
     *          "resultCode": 1
     *       }
     * @apiAuthor 钟良
     * @date 2017年6月5日
     */
    @RequestMapping(value = "/invitation", method = RequestMethod.GET)
    public JSONMessage invitation(InvitationReportForm form,
        @RequestParam(defaultValue = "0") Integer pageIndex,
        @RequestParam(defaultValue = "15") Integer pageSize) {
        return JSONMessage.success(circleInviteReportService.listInvitation(form, pageIndex, pageSize));
    }

    /**
     * @api {get} /activity/invite/report/invitation/export 邀请数据导出
     * @apiVersion 1.0.0
     * @apiName /activity/invite/report/invitation/export
     * @apiGroup 活动管理
     * @apiDescription 邀请数据导出
     *
     * @apiParam {String}      access_token             token
     * @apiParam {String}      userName                 邀请人姓名
     * @apiParam {String}      activityId               活动Id
     * @apiParam {Integer}     subsystem                来源子系统（医生圈-17、药企圈-16）
     *
     * @apiSuccess {String}         resultCode          返回状态码
     * @apiSuccess {String}     	data  				data
     * @apiSuccess {String}     	data.pageCount  	总页数
     * @apiSuccess {String}     	data.pageIndex  	页码
     * @apiSuccess {String}     	data.pageSize  		每页数据大小
     * @apiSuccess {String}     	data.total  		总记录数
     * @apiSuccess {String}     	data.pageData  		分页对象
     * @apiSuccess {String}     	data.pageData.userName  		    邀请人姓名
     * @apiSuccess {String}     	data.pageData.activityName  		活动名称
     * @apiSuccess {String}     	data.pageData.subsystem  		    来源子系统（医生圈-17、药企圈-16）
     * @apiSuccess {String}     	data.pageData.hospital  		    医院
     * @apiSuccess {String}     	data.pageData.title  		        职称
     * @apiSuccess {String}     	data.pageData.wechatCount  		    微信邀请数
     * @apiSuccess {String}     	data.pageData.smsCount  		    短信邀请数
     * @apiSuccess {String}     	data.pageData.qrcodeCount  		    二维码邀请数
     * @apiSuccess {String}     	data.pageData.registeredCount  		已注册用户数
     * @apiSuccess {String}     	data.pageData.autherizedCount  		已认证用户数
     *
     * @apiAuthor 钟良
     * @date 2017年6月5日
     */
    @RequestMapping(value = "/invitation/export", method = RequestMethod.GET)
    public JSONMessage invitation_export(InvitationReportForm form, HttpServletResponse response) {
        String fileName = "活动管理-邀请数据";

        String[] columnNames = new String[]{"邀请人", "邀请人Id", "邀请人openId", "医院", "职称", "活动", "微信邀请数", "短信邀请数", "二维码邀请数", "短信邀请数", "已注册用户数", "已认证用户数"};//列名
        String[] keys = new String[]{"userName", "userId", "openId", "hospital", "title", "activityName", "wechatCount", "smsCount", "qrcodeCount", "smsCount", "registeredCount", "autherizedCount"};

        Integer pageIndex = 0;
        long pageSize = circleInviteReportService.listInvitationCount(form);
        if (pageSize == 0L){
            throw new ServiceException("没有查询到数据");
        }
        Pagination<InvitationReportVO> pagination = circleInviteReportService.listInvitation(form, pageIndex, Long.valueOf(pageSize).intValue());

        List<Map<String, Object>> list;
        try {
            list = createExcelRecord(pagination.getPageData());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("数据导出失败");
        }

        try {
            writeData(response, fileName, columnNames, keys, list);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return JSONMessage.error(ex);
        }
        return JSONMessage.success();
    }

    private void writeData(HttpServletResponse response, String fileName, String[] columnNames,
        String[] keys, List<Map<String, Object>> list) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ExcelUtil.createWorkBook(list, keys, columnNames).write(os);

        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);

        // 设置response参数，可以打开下载页面
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition",
            "attachment;filename=" + new String((fileName + ".xls").getBytes(), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                }catch (IOException e){
                    logger.error(e.getMessage(), e);
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                }catch (IOException e){
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private List<Map<String, Object>> createExcelRecord(List<?> list) throws Exception {
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sheetName", "sheet1");
        listmap.add(map);
        for (Object obj : list) {
            listmap.add(MapUtil.beanToMap(obj));
        }
        return listmap;
    }

    /**
     * @api {get} /activity/invite/report/registration 注册数据
     * @apiVersion 1.0.0
     * @apiName /activity/invite/report/registration
     * @apiGroup 活动管理
     * @apiDescription 注册数据
     *
     * @apiParam {String}      access_token             token
     * @apiParam {String}      userName                 邀请人姓名
     * @apiParam {String}      activityId               活动Id
     * @apiParam {Integer}     subsystem                来源子系统（医生圈-17、药企圈-16）
     * @apiParam {Long}        startTime                开始时间
     * @apiParam {Long}        endTime                  结束时间
     * @apiParam {String}      way                      邀请方式（短信-sms，微信-wechat，二维码-qrcode）
     *
     * @apiSuccess {String}         resultCode          返回状态码
     * @apiSuccess {String}     	data  				data
     * @apiSuccess {String}     	data.pageCount  	总页数
     * @apiSuccess {String}     	data.pageIndex  	页码
     * @apiSuccess {String}     	data.pageSize  		每页数据大小
     * @apiSuccess {String}     	data.total  		总记录数
     * @apiSuccess {String}     	data.pageData  		分页对象
     * @apiSuccess {String}     	data.pageData.userName  		    医生姓名
     * @apiSuccess {String}     	data.pageData.activityName  		活动名称
     * @apiSuccess {String}     	data.pageData.subsystem  		    来源子系统（医生圈-17、药企圈-16）
     * @apiSuccess {String}     	data.pageData.hospital  		    医院
     * @apiSuccess {String}     	data.pageData.title  		        职称
     * @apiSuccess {String}     	data.pageData.dept  		        科室
     * @apiSuccess {String}     	data.pageData.status  		        1-已认证；2-待审核；7-已注册；3-审核未通过
     * @apiSuccess {String}     	data.pageData.inviter  		        邀请人
     * @apiSuccess {String}     	data.pageData.way  		            邀请方式：短信-sms、微信-wechat、二维码-qrcode
     *
     * @apiSuccessExample {json} Success-Response:
     *      HTTP/1.1 200 OK
     *      {
                "data": {
                    "pageCount": 1,
                    "pageData": [
                        {
                            "activityName": "注册活动",
                            "dept": "呼吸内科",
                            "hospital": "北京中医药大学东直门医院",
                            "inviter": "gccc",
                            "registrationTime": 1496716957649,
                            "status": 7,
                            "subsystem": 16,
                            "title": "主任医师",
                            "userName": "66",
                            "way": "wechat"
                        }
                    ],
                    "pageIndex": 0,
                    "pageSize": 15,
                    "start": 0,
                    "total": 3
                    },
                "resultCode": 1
            }
     *
     * @apiAuthor 钟良
     * @date 2017年6月5日
     */
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public JSONMessage registration(RegistrationReportForm form,
        @RequestParam(defaultValue = "0") Integer pageIndex,
        @RequestParam(defaultValue = "15") Integer pageSize) {
        return JSONMessage.success(userRegisterService.userRegistration(form, pageIndex, pageSize));
    }

    /**
     * @api {get} /activity/invite/report/registration/export 注册数据导出
     * @apiVersion 1.0.0
     * @apiName /activity/invite/report/registration/export
     * @apiGroup 活动管理
     * @apiDescription 注册数据导出
     *
     * @apiParam {String}      access_token             token
     * @apiParam {String}      userName                 邀请人姓名
     * @apiParam {String}      activityId               活动Id
     * @apiParam {Integer}     subsystem                来源子系统（医生圈-17、药企圈-16）
     * @apiParam {Long}        startTime                开始时间
     * @apiParam {Long}        endTime                  结束时间
     * @apiParam {String}      way                      邀请方式（短信-sms，微信-wechat，二维码-qrcode）
     *
     * @apiSuccess {String}         resultCode          返回状态码
     * @apiSuccess {String}     	data  				data
     * @apiSuccess {String}     	data.pageCount  	总页数
     * @apiSuccess {String}     	data.pageIndex  	页码
     * @apiSuccess {String}     	data.pageSize  		每页数据大小
     * @apiSuccess {String}     	data.total  		总记录数
     * @apiSuccess {String}     	data.pageData  		分页对象
     * @apiSuccess {String}     	data.pageData.userName  		    医生姓名
     * @apiSuccess {String}     	data.pageData.activityName  		活动名称
     * @apiSuccess {String}     	data.pageData.subsystem  		    来源子系统（医生圈-17、药企圈-16）
     * @apiSuccess {String}     	data.pageData.hospital  		    医院
     * @apiSuccess {String}     	data.pageData.title  		        职称
     * @apiSuccess {String}     	data.pageData.dept  		        科室
     * @apiSuccess {String}     	data.pageData.status  		        1-已认证；2-待审核；7-已注册
     * @apiSuccess {String}     	data.pageData.inviter  		        邀请人
     * @apiSuccess {String}     	data.pageData.way  		            邀请方式：短信-sms、微信-wechat、二维码-qrcode
     *
     * @apiAuthor 钟良
     * @date 2017年6月6日
     */
    @RequestMapping(value = "/registration/export", method = RequestMethod.GET)
    public JSONMessage registration_export(RegistrationReportForm form, HttpServletResponse response) {
        String fileName = "活动管理-注册数据";

        String[] columnNames = new String[]{"注册时间", "医生姓名", "医院", "科室", "职称", "活动", "邀请来源", "注册方式", "认证状态", "邀请人", "邀请人Id", "邀请人openId"};//列名
        String[] keys = new String[]{"registrationTime", "userName", "hospital", "dept", "title", "activityName", "subsystem", "way", "status", "inviter", "inviterId", "inviterOpenId"};

        Integer pageIndex = 0;
        long pageSize = userRegisterService.userRegistrationCount(form);
        if (pageSize == 0L){
            throw new ServiceException("没有查询到数据");
        }
        Pagination<RegistrationReportVO> pagination = userRegisterService.userRegistration(form, pageIndex, Long.valueOf(pageSize).intValue());

        List<Map<String, Object>> list;
        try {
            list = createExcelRecord(pagination.getPageData());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("数据导出失败");
        }

        for (int i = 1; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            Long registrationTime = (Long) map.get("registrationTime");
            map.put("registrationTime", DateUtil.formatDate2Str(registrationTime));

            String way = (String) map.get("way");
            switch (way) {
                case "sms":
                    way = "短信注册";
                    break;
                case "wechat":
                    way = "微信注册";
                    break;
                case "qrcode":
                    way = "二维码注册";
                    break;
                default:
                    throw new ServiceException("forbidden");
            }
            map.put("way", way);

            Integer status = (Integer) map.get("status");
            UserStatus userStatus = UserStatus.getEnum(status);
            String statusStr;
            switch (userStatus) {
                case normal:
                    statusStr = "已认证";
                    break;
                case uncheck:
                    statusStr = "待审核";
                    break;
                case Unautherized:
                    statusStr = "已注册";
                    break;
                case fail:
                    statusStr = "未通过";
                    break;
                default:
                    statusStr = userStatus.getTitle();
            }
            map.put("status", statusStr);
        }

        try {
            writeData(response, fileName, columnNames, keys, list);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return JSONMessage.error(ex);
        }
        return JSONMessage.success();
    }
}
