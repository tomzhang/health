package com.dachen.health.activity.invite.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.activity.invite.enums.InviteEnum;
import com.dachen.health.activity.invite.enums.InviteEnum.QrCodeTypeEnum;
import com.dachen.health.activity.invite.form.QrCodeForm;
import com.dachen.health.activity.invite.service.CircleInviteService;
import com.dachen.health.activity.invite.service.QrCode2Service;
import com.dachen.health.activity.invite.vo.MobileInviteVO;
import com.dachen.health.base.entity.po.QrScanParamPo;
import com.dachen.health.commons.constants.GroupEnum.GroupType;
import com.dachen.health.commons.constants.UserEnum.InviteWayEnum;
import com.dachen.health.commons.constants.UserEnum.Source;
import com.dachen.health.commons.constants.UserEnum.UserLevel;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.DESUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Maps;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activity/invite/qrcode")
public class ActivityInviteQrCodeController extends ActivityInviteBaseController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected QrCode2Service qrCodeService;

    @Autowired
    protected CircleInviteService circleInviteService;

    @Autowired
    private IGroupDoctorService groupDoctor;

    @Autowired
    private UserManager userManager;


    /**
     * @api {POST} /activity/invite/qrcode/qrcode 生成二维码
     * @apiVersion 1.0.0
     * @apiName /activity/invite/qrcode/qrcode
     * @apiGroup 二维码
     * @apiDescription 生成二维码
     *
     * @apiParam {String} access_token token
     * @apiParam {String} type      类型, dept表示科室，doctor表示医生
     * @apiParam {String} typeId    类型id，dept时为科室id，doctor时为医生Id
     * @apiParam {String} inviteActivityId    邀请活动Id
     * @apiParam {String} registerActivityId    注册活动Id
     *
     * @apiSuccess {String} resultCode 返回状态码：0-失败；1-成功；
     * @apiSuccess {String} data.qrCodeImageUrl 二维码url
     *
     * @apiAuthor 钟良
     * @date 2017年5月25日
     */
    @RequestMapping(value = "/qrcode", method = RequestMethod.POST)
    public JSONMessage generate(QrCodeForm form) {
        //请求参数校验
        form.check();
        Integer doctorId = this.getCurrentUserId();
        form.setDoctorId(doctorId);
        //生成二维码
        String qrCodeImageUrl = getQrCodeImageUrl(form);
        Map<String, Object> map = new HashMap<>(1);
        map.put("qrCodeImageUrl", qrCodeImageUrl);
        return JSONMessage.success(null, map);
    }

    private String getQrCodeImageUrl(QrCodeForm form) {
        //二维码类型，根据不同类型生成对应类型的二维码
        InviteEnum.QrCodeTypeEnum qrCodeTypeEnum = InviteEnum.QrCodeTypeEnum.eval(form.getType());
        String qrCodeImageUrl = "";
        switch (qrCodeTypeEnum) {
            case Dept:
                qrCodeImageUrl = qrCodeService.generateDeptQrCode(form);
                break;
            case Doctor:
                qrCodeImageUrl = getDoctorQrCodeImageUrl(form);
                break;
            case ActivityDoctor:
                qrCodeImageUrl = getActivityDoctorQrCodeImageUrl(form);
                break;
            default:
                throw new ServiceException("forbidden");
        }
        return qrCodeImageUrl;
    }

    private String getDoctorQrCodeImageUrl(QrCodeForm form) {
        String qrCodeImageUrl = qrCodeService.generateDoctorQrCode(form);

        //生成了邀请短链就表示邀请了，新增活动邀请记录
        createInviteRecord(form.getInviteActivityId(), InviteWayEnum.qrcode.toString(), form.getDoctorId());
        return qrCodeImageUrl;
    }

    private String getActivityDoctorQrCodeImageUrl(QrCodeForm form) {
        String qrCodeImageUrl = qrCodeService.generateActivityDoctorQrCode(form);

        //生成了邀请短链就表示邀请了，新增活动邀请记录
        createInviteRecord(form.getInviteActivityId(), InviteWayEnum.qrcode.toString(), form.getDoctorId());
        return qrCodeImageUrl;
    }
    private void createInviteRecord(String inviteActivityId, String way, Integer doctorId) {
        circleInviteService.save(doctorId, inviteActivityId, way, Source.doctorCircle.getIndex());
    }

    /**
     * @api {GET} /activity/invite/qrcode/scan 扫描二维码
     * @apiVersion 1.0.0
     * @apiName /activity/invite/qrcode/scan
     * @apiGroup 二维码
     * @apiDescription 扫描二维码
     *
     * @apiParam {String} access_token token医生圈扫描需要传这个参数，第三方扫描不需要传
     * @apiParam {String} tk tk
     *
     * @apiSuccess String data.url 用户对象实体
     *
     * @apiAuthor 钟良
     * @date 2017年5月25日
     */
    @RequestMapping(value = "/scan", method = RequestMethod.GET)
    public JSONMessage scan(@RequestParam String tk, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String src;
        try{
            src = DESUtil.decrypt(tk);
        }catch(Exception ex){
            src = DESUtil.decrypt(java.net.URLDecoder.decode(tk, "UTF-8"));
        }
        if (StringUtils.isBlank(src)) {
            return JSONMessage.failure("数据解密出错:tk=" + tk);
        }
        String[] splits = src.split("&");
        if (splits.length < 2) {
            return JSONMessage.failure("数据解密出错:src=" + src);
        }

        //类型, dept表示科室，doctor表示医生
        String type = splits[0];
        //类型id，dept时为科室id，doctor时为医生Id
        String typeId = splits[1];

        //医生圈扫描会带上access_token。第三方（微信，支付宝等等）扫描不会带上access_token
        String access_token = request.getParameter("access_token");
        if (!StringUtil.isBlank(access_token)) {
            /*int fromUserId = ReqUtil.instance.getUserId(access_token);
            // 请求令牌是否有效
            if (fromUserId == 0) {
                return JSONMessage.failure("请求令牌无效!");
            }*/

            User user = userManager.getUser(ReqUtil.instance.getUserIdFromAuth(access_token));
            if (Objects.nonNull(user) && Objects.equals(user.getBaseUserLevel(), UserLevel.Tourist.getIndex())) {
                return JSONMessage.failure("您还未通过资格审核，暂时无法浏览");
            }
            Map<String, String> data = getData(typeId, type);
            return JSONMessage.success(null, data);
        }

        InviteEnum.QrCodeTypeEnum qrCodeTypeEnum = InviteEnum.QrCodeTypeEnum.eval(type);
        String url = "";
        switch (qrCodeTypeEnum) {
            case Dept:
                url = createDeptShortUrl(splits, typeId);
                response.sendRedirect(url);
                break;
            case Doctor:
                url = createDoctorShortUrl(splits, typeId);
                response.sendRedirect(url);
                break;
            case ActivityDoctor:
                url = createActivityDoctorShortUrl(splits, typeId);
                response.sendRedirect(url);
                break;
            default:
                throw new ServiceException("ForBidden");
        }
        return null;
    }

    private Map<String, String> getData(String id, String type) {
        Map<String, String> data = Maps.newHashMap();
        data.put("typeId", id);
        data.put("type", type);
        if (QrCodeTypeEnum.Doctor.getId().equals(type)){
//            List<String> groupDoctorVo = groupDoctor.getGroup(Integer.parseInt(id), GroupType.dept.getIndex());
//            if (groupDoctorVo == null || groupDoctorVo.size() != 1) {
//                throw new ServiceException("该医生主集团设置异常：未设置或者设置的主集团超过1个");
//            }
//            String groupId = groupDoctorVo.get(0);//集团Id

            data.put("doctorId", id);
//            data.put("groupId", groupId);
        }
        return data;
    }

    private String createDoctorShortUrl(String[] splits, String typeId) throws HttpApiException {
        Integer doctorId = Integer.parseInt(typeId);
        String inviteActivityId = splits[2];//邀请活动Id
        String registerActivityId = splits[3];//注册活动Id
        Integer subsystem = Integer.parseInt(splits[4]);//来源子系统
        String way = splits[5];//邀请方式
        MobileInviteVO mobileInviteVO = circleInviteService.createInviteShortUrl(doctorId, inviteActivityId,
            registerActivityId, way, subsystem);
        return mobileInviteVO.getShortUrl();
    }

    private String createDeptShortUrl(String[] splits, String typeId) throws HttpApiException {
        String doctorId = splits[2];//邀请医生Id
        Integer subsystem = Integer.parseInt(splits[3]);//来源子系统
        String way = splits[4];//邀请方式
        MobileInviteVO mobileInviteVO = circleInviteService.createInviteShortUrl(Integer.parseInt(doctorId), typeId, way, subsystem);
        return mobileInviteVO.getShortUrl();
    }

    private String createActivityDoctorShortUrl(String[] splits, String typeId) throws HttpApiException {
        Integer doctorId = Integer.parseInt(typeId); //活动医生id
        String inviteActivityId = splits[2];//邀请活动Id
        String registerActivityId = splits[3];//注册活动Id
        Integer subsystem = Integer.parseInt(splits[4]);//来源子系统
        String way = splits[5];//邀请方式
        MobileInviteVO mobileInviteVO = circleInviteService.createActivityDoctorShortUrl(doctorId, inviteActivityId,
                registerActivityId, way, subsystem);
        return mobileInviteVO.getShortUrl();
    }

}
