package com.dachen.health.activity.invite.controller;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.activity.invite.enums.InviteEnum;
import com.dachen.health.activity.invite.form.CircleInviteForm;
import com.dachen.health.activity.invite.form.ShortUrlForm;
import com.dachen.health.activity.invite.service.CircleInviteService;
import com.dachen.health.activity.invite.vo.MobileInviteVO;
import com.dachen.health.commons.constants.UserEnum.Source;
import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activity/invite/circle")
public class ActivityInviteCircleController extends ActivityInviteBaseController {

    @Autowired
    protected CircleInviteService circleInviteService;

    /**
     * @api {post} /activity/invite/circle/shortUrl 生成医生圈邀请短链
     * @apiVersion 1.0.0
     * @apiName /activity/invite/circle/shortUrl
     * @apiGroup 活动邀请注册
     * @apiDescription 生成医生圈邀请短链
     *
     * @apiParam {String}      access_token             token
     * @apiParam {String}      type                     类型：dept表示科室，doctor表示医生
     * @apiParam {String}      typeId                   类型id：dept时为科室id，doctor时为医生Id
     * @apiParam {String}      inviteActivityId         邀请活动Id
     * @apiParam {String}      registerActivityId       注册活动Id
     * @apiParam {String}      way                      邀请方式（短信-sms，微信-wechat）
     *
     * @apiSuccess {String}         resultCode          返回状态码
     * @apiSuccess {String}     	note  				短信发送内容
     * @apiSuccess {String}     	shortUrl  			邀请短链
     *
     * @apiAuthor 钟良
     * @date 2017年5月22日
     */
    @RequestMapping(value = "/shortUrl", method = RequestMethod.POST)
    public JSONMessage circleInviteShortUrl(ShortUrlForm form) throws HttpApiException {
        //请求参数校验
        form.check();
        Integer doctorId = this.getCurrentUserId();
        form.setDoctorId(doctorId);

        MobileInviteVO mobileInviteVO = getShortUrl(form);

        return JSONMessage.success(null, mobileInviteVO);
    }

    private MobileInviteVO getShortUrl(ShortUrlForm form) throws HttpApiException {
        //类型，根据不同类型生成对应类型的短链
        InviteEnum.QrCodeTypeEnum qrCodeTypeEnum = InviteEnum.QrCodeTypeEnum.eval(form.getType());
        MobileInviteVO inviteVO = null;
        switch (qrCodeTypeEnum) {
            case Dept:
                inviteVO = getDeptShortUrl(form);
                break;
            case Doctor:
                inviteVO = getDoctorShortUrl(form);
                break;
            default:
                throw new ServiceException("forbidden");
        }
        return inviteVO;
    }

    private MobileInviteVO getDeptShortUrl(ShortUrlForm form) throws HttpApiException {
        return circleInviteService.createInviteShortUrl(form.getDoctorId(), form.getTypeId(), form.getWay(), Source.doctorCircle.getIndex());
    }

    private MobileInviteVO getDoctorShortUrl(ShortUrlForm form) throws HttpApiException {
        //因为生成的是医生圈邀请短链，所以子系统为医生圈
        MobileInviteVO mobileInviteVO = circleInviteService.createInviteShortUrl(form.getDoctorId(), form.getInviteActivityId(),
            form.getRegisterActivityId(), form.getWay(), Source.doctorCircle.getIndex());

        //生成了邀请短链就表示邀请了，新增活动邀请记录
        createInviteRecord(form.getInviteActivityId(), form.getWay(), form.getDoctorId());
        return mobileInviteVO;
    }

    private void createInviteRecord(String inviteActivityId, String way, Integer doctorId) {
        circleInviteService.save(doctorId, inviteActivityId, way, Source.doctorCircle.getIndex());
    }
}
