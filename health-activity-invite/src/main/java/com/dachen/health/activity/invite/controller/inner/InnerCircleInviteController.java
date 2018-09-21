package com.dachen.health.activity.invite.controller.inner;

import com.dachen.commons.JSONMessage;
import com.dachen.health.activity.invite.form.CircleInviteForm;
import com.dachen.health.activity.invite.service.CircleInviteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 钟良
 * @desc
 * @date:2017/5/22 15:42 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@RestController
@RequestMapping("/inner_api/circle/invite")
public class InnerCircleInviteController {
    private Logger logger = LoggerFactory.getLogger(InnerCircleInviteController.class);

    @Autowired
    private CircleInviteService circleInviteService;

    /**
     * @api {post} /inner_api/circle/invite/circleInvite 新增活动邀请记录
     * @apiVersion 1.0.0
     * @apiName /inner_api/circle/invite/circleInvite
     * @apiGroup 活动邀请注册（内部API）
     * @apiDescription 新增活动邀请记录
     *
     * @apiParam {Integer}       userId             邀请人Id（医生或者医药代表）
     * @apiParam {String}        inviteActivityId   邀请活动Id
     * @apiParam {Integer}       subsystem          来源子系统（医生圈-17、药企圈-16）
     * @apiParam {String}        way                邀请方式（短信-sms，微信-wechat，二维码-qrcode）
     *
     * @apiSuccess {String}     resultCode  	返回状态码
     *
     * @apiAuthor 钟良
     * @date 2017年5月22日
     */
    @RequestMapping(value = "/circleInvite", method = RequestMethod.POST)
    public JSONMessage createCircleInvite(CircleInviteForm form) {
        String tag = "create";
        logger.info("{}. form={}", tag, form);

        circleInviteService.save(form);
        return JSONMessage.success();
    }
}
