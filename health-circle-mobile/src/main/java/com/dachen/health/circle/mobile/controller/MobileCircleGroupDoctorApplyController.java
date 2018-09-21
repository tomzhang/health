package com.dachen.health.circle.mobile.controller;

import com.dachen.health.circle.entity.GroupDoctorApply;
import com.dachen.health.circle.service.GroupDoctor2Service;
import com.dachen.health.circle.service.GroupDoctorApplyService;
import com.dachen.health.circle.vo.MobileGroupDoctorVO;
import com.dachen.health.circle.vo.MobileGroupUnionApplyVO;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.exception.ServiceException;
import com.dachen.sdk.json.JSONMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/m/circle/group/doctor/apply")
public class MobileCircleGroupDoctorApplyController extends MobileCircleBaseController {

    @Autowired
    protected GroupDoctor2Service groupDoctor2Service;

    @Autowired
    protected GroupDoctorApplyService groupDoctorApplyService;

    /**
     * @api {POST} /m/circle/group/doctor/apply/create 当前医生申请加入科室
     * @apiVersion 1.0.0
     * @apiName GroupDoctor.applyCreate
     * @apiGroup 医生申请加入科室
     * @apiDescription 当前医生申请加入科室
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupId 被申请的科室id
     * @apiParam {String} msg msg
     *
     * @apiSuccess {Boolean} success true表示成功，false表示失败
     *
     * @apiAuthor 肖伟
     * @date 2017年06月14日
     *
     */

    @RequestMapping("/create")
    public JSONMessage applyCreate(@RequestParam String groupId, @RequestParam String msg) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        GroupDoctorApply apply= groupDoctorApplyService.create(currentUserId, groupId, msg);
        return JSONMessage.success(null, null != apply);
    }



    /**
     * @api {POST} /m/circle/group/doctor/apply/confirm 科室管理员确定邀请
     * @apiVersion 1.0.0
     * @apiName GroupDoctor.applyConfirm
     * @apiGroup 医生申请加入科室
     * @apiDescription 科室管理员确定邀请
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
                ret = groupDoctorApplyService.accept(currentUserId, id);
                break;
            case 0:
                ret = groupDoctorApplyService.refuse(currentUserId, id);
                break;
            default:
                throw new ServiceException("agree is wrong.");
        }
        return JSONMessage.success(null, ret);
    }


    /**
     * @api {GET} /m/circle/group/doctor/apply/findDetailById 获取申请详情
     * @apiVersion 1.0.0
     * @apiName GroupDoctorApply.findDetailById
     * @apiGroup 医生申请加入科室
     * @apiDescription 获取申请详情
     * @apiParam {String} access_token token
     * @apiParam {String} groupDoctorId groupDoctorId
     * @apiSuccess {Object} apply apply 申请详情
     * @apiSuccess {String} apply.groupDoctorId groupDoctorId
     * @apiSuccess {String} apply.status 申请状态。C正在使用，J申请待确认，M申请拒绝
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
        MobileGroupDoctorVO ret = groupDoctorApplyService.findDetailByIdAndGroupDoctorVO(currentUserId, id);
        return JSONMessage.success(null, ret);
    }
}
