package com.dachen.health.circle.mobile.controller;

import com.dachen.health.circle.entity.GroupDoctorInvite;
import com.dachen.health.circle.service.GroupDoctor2Service;
import com.dachen.health.circle.service.GroupDoctorInviteService;
import com.dachen.health.circle.vo.MobileGroupDoctorVO;
import com.dachen.health.group.group.service.impl.GroupDoctorServiceImpl;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.exception.ServiceException;
import com.dachen.sdk.json.JSONMessage;
import com.dachen.sdk.page.Pagination;
import com.dachen.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/m/circle/group/doctor/invite")
public class MobileCircleGroupDoctorInviteController extends MobileCircleBaseController {

    @Autowired
    protected GroupDoctor2Service groupDoctor2Service;

    @Autowired
    protected GroupDoctorInviteService groupDoctorInviteService;

    /**
     * @api {POST} /m/circle/group/doctor/invite/create 科室管理员邀请医生
     * @apiVersion 1.0.0
     * @apiName GroupDoctor.invite.create
     * @apiGroup 科室管理员邀请医生
     * @apiDescription 邀请医生
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupId 当前科室id
     * @apiParam {String} doctorId 被邀请的医生id
     *
     * @apiSuccess {Boolean} success true表示成功，false表示失败
     *
     * @apiAuthor 肖伟
     * @date 2017年06月14日
     *
     * @see GroupDoctorServiceImpl#saveGroupDoctor
     */

    @RequestMapping("/create")
    public JSONMessage create(String groupId, Integer doctorId) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        GroupDoctorInvite ret = groupDoctorInviteService.create(currentUserId, groupId, doctorId);
        return JSONMessage.success(null, null != ret);
    }

    /**
     * @api {POST} /m/circle/group/doctor/invite/confirm 医生确定邀请
     * @apiVersion 1.0.0
     * @apiName GroupDoctor.inviteConfirm
     * @apiGroup 科室管理员邀请医生
     * @apiDescription 医生确定邀请
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupDoctorId groupDoctorId
     * @apiParam {Integer} agree 1表示同意，0表示拒绝
     *
     * @apiSuccess {Boolean} success true表示成功，false表示失败
     *
     * @apiAuthor 肖伟
     * @date 2017年06月14日
     *
     * @see com.dachen.health.controller.group.group.GroupUserController#confirmByJoin
     */
    @RequestMapping("/confirm")
    public JSONMessage confirm(@RequestParam("groupDoctorId") String id, @RequestParam Integer agree) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = false;
        switch (agree) {
            case 1:
                ret = groupDoctorInviteService.accept(currentUserId, id);
                break;
            case 0:
                ret = groupDoctorInviteService.refuse(currentUserId, id);
                break;
            default:
                throw new ServiceException("agree is wrong.");
        }
        return JSONMessage.success(null, ret);
    }


    /**
     * @api {GET} /m/circle/group/doctor/invite/findDetailById 获取邀请详情
     * @apiVersion 1.0.0
     * @apiName GroupDoctorInvite.findDetailById
     * @apiGroup 科室管理员邀请医生
     * @apiDescription 获取邀请详情
     * @apiParam {String} access_token token
     * @apiParam {String} groupDoctorId groupDoctorId
     * @apiSuccess {Object} apply apply 邀请详情
     * @apiSuccess {String} apply.groupDoctorId groupDoctorId
     * @apiSuccess {String} apply.status 邀请状态。C正在使用，I邀请待确认，N邀请拒绝
     * @apiSuccess {Object} apply.group     科室信息
     * @apiSuccess {Object} apply.doctor     医生信息
     * @apiSuccess {String} apply.msg      通知文本
     *
     * @apiAuthor 肖伟
     * @date 2017年06月14日
     */
    @RequestMapping("/findDetailById")
    public JSONMessage findDetailById(@RequestParam("groupDoctorId") String id) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        MobileGroupDoctorVO ret = groupDoctorInviteService.findDetailByIdAndGroupDoctorVO(currentUserId, id);
        return JSONMessage.success(null, ret);
    }

}
