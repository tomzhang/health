package com.dachen.health.circle.mobile.controller;

import com.dachen.health.circle.entity.GroupUnionApply;
import com.dachen.health.circle.service.Group2Service;
import com.dachen.health.circle.service.GroupUnionApplyService;
import com.dachen.health.circle.vo.MobileGroupUnionApplyVO;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.json.JSONMessage;
import com.dachen.sdk.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/m/circle/group/union/apply")
public class MobileCircleGroupUnionApplyController extends MobileCircleBaseController {

    @Autowired
    protected GroupUnionApplyService groupUnionApplyService;

    @Autowired
    protected Group2Service group2Service;

    /**
     * @api {GET} /m/circle/group/union/apply/findPageByGroup 获取科室待申请加入的医联体列表
     * @apiVersion 1.0.0
     * @apiName GroupUnionApply.findApplyPageByGroup
     * @apiGroup 科室申请加入医联体
     * @apiDescription 获取科室待申请的医联体列表
     * @apiParam {String} access_token token
     * @apiParam {String} groupId groupId
     * @apiParam {String} kw 关键字
     * @apiParam {Integer} pageIndex pageIndex
     * @apiParam {Integer} pageSize pageSize
     * @apiSuccess {Object} Page 一页申请记录
     * @apiSuccess {Object[]} page.list GroupUnionApply详情列表
     * @apiSuccess {String} page.list.id 申请记录id，如果为空，说明没有申请过
     * @apiSuccess {String} page.list.union 医联体详情
     * @apiSuccess {Integer} page.list.status 申请记录id不为空时，将有申请记录的状态，1表示申请处理中
     *
     * @apiAuthor 肖伟
     * @date 2017年05月22日
     */
    @RequestMapping("/findPageByGroup")
    public JSONMessage findPageByGroup(@RequestParam String groupId, String kw,
                                           @RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "20") Integer pageSize) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupUnionApplyVO> ret = groupUnionApplyService.findPageByGroupAndVO(currentUserId, groupId, kw, pageIndex, pageSize);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {GET} /m/circle/group/union/apply/findDetailById 获取申请详情
     * @apiVersion 1.0.0
     * @apiName GroupUnionApply.findDetailById
     * @apiGroup 科室申请加入医联体
     * @apiDescription 获取申请详情
     * @apiParam {String} access_token token
     * @apiParam {String} applyId applyId
     * @apiSuccess {Object} apply apply GroupUnionApply详情
     * @apiSuccess {String} apply.applyId 申请id
     * @apiSuccess {Integer} apply.statusId 申请状态。1处理中，2已同意，3已拒绝，8已关闭，9已删除
     * @apiSuccess {Object} apply.groupUnion 医联体信息
     * @apiSuccess {Object} apply.group     科室信息
     * @apiSuccess {Object} apply.user      申请人的信息
     * @apiSuccess {String} apply.msg      通知文本
     *
     * @apiAuthor 肖伟
     * @date 2017年05月22日
     */
    @RequestMapping("/findDetailById")
    public JSONMessage findDetailById(@RequestParam("applyId") String id) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        MobileGroupUnionApplyVO ret = groupUnionApplyService.findDetailByIdAndVO(currentUserId, id);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /m/circle/group/union/apply/create 科室申请加入医联体
     * @apiVersion 1.0.0
     * @apiName GroupUnionApply.create
     * @apiGroup 科室申请加入医联体
     * @apiDescription 科室申请加入医联体
     * @apiParam {String} access_token token
     * @apiParam {String} groupId 当前的科室id
     * @apiParam {String} unionId 被申请的医联体id
     * @apiParam {String} msg 申请信息
     * @apiSuccess {Boolean} boolean true表示成功，false表示失败
     * @apiAuthor 肖伟
     * @date 2017年05月22日
     */
    @RequestMapping("/create")
    public JSONMessage create(@RequestParam String groupId, @RequestParam String unionId, @RequestParam String msg) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        GroupUnionApply ret = groupUnionApplyService.create(currentUserId, groupId, unionId, msg);
        return JSONMessage.success(null, null != ret);
    }

    /**
     * @api {POST} /m/circle/group/union/apply/confirm 医联体管理员确认申请
     * @apiVersion 1.0.0
     * @apiName GroupUnionApply.confirm
     * @apiGroup 科室申请加入医联体
     * @apiDescription 医联体成员确认邀请
     * @apiParam {String} access_token token
     * @apiParam {String} applyId applyId
     * @apiParam {Integer} agree 同意为1，拒绝为0
     * @apiSuccess {Boolean} boolean true表示成功，false表示失败
     * @apiAuthor 肖伟
     * @date 2017年05月22日
     */
    @RequestMapping("/confirm")
    public JSONMessage confirm(@RequestParam(name = "applyId") String id, @RequestParam Integer agree) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = false;
        switch (agree) {
            case 1:
                ret = groupUnionApplyService.accept(currentUserId, id);
                break;
            case 0:
                ret = groupUnionApplyService.refuse(currentUserId, id);
                break;
        }
        return JSONMessage.success(null, ret);
    }
}
