package com.dachen.health.circle.mobile.controller;

import com.dachen.health.circle.entity.GroupUnionInvite;
import com.dachen.health.circle.service.Group2Service;
import com.dachen.health.circle.service.GroupUnionInviteService;
import com.dachen.health.circle.vo.MobileGroupUnionInviteVO;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.json.JSONMessage;
import com.dachen.sdk.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/m/circle/group/union/invite")
public class MobileCircleGroupUnionInviteController extends MobileCircleBaseController {

    @Autowired
    protected GroupUnionInviteService groupUnionInviteService;

    @Autowired
    protected Group2Service group2Service;

    /**
     * @api {GET} /m/circle/group/union/invite/findPageByUnion 获取医联体待邀请加入的科室或者圈子列表
     * @apiVersion 1.0.0
     * @apiName GroupUnionInvite.findInvitePageByUnion
     * @apiGroup 医联体邀请科室
     * @apiDescription 获取医联体待邀请加入的科室或者圈子列表
     * @apiParam {String} access_token token
     * @apiParam {String} unionId unionId
     * @apiParam {String} kw 关键字
     * @apiParam {Integer} pageIndex pageIndex
     * @apiParam {Integer} pageSize pageSize
     * @apiSuccess {Object} page 一页邀请数据
     * @apiSuccess {Object[]} page.groupUnionInviteList 邀请详情列表
     * @apiSuccess {String} page.groupUnionInviteList.id 邀请记录id，如果为空，说明没有邀请过
     * @apiSuccess {String} page.groupUnionInviteList.group 科室详情
     * @apiSuccess {Integer} page.groupUnionInviteList.statusId 邀请记录的状态，1表示邀请处理中
     *
     * @apiAuthor 肖伟
     * @date 2017年05月22日
     */
    @RequestMapping("/findPageByUnion")
    public JSONMessage findInvitePageByUnion(@RequestParam String unionId, String kw,
                                           @RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "20") Integer pageSize) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupUnionInviteVO> ret = groupUnionInviteService.findPageByUnionAndVO(currentUserId, unionId, kw, pageIndex, pageSize);
        return JSONMessage.success(null, ret);
    }


    /**
     * @api {GET} /m/circle/group/union/invite/findDetailById 获取邀请详情
     * @apiVersion 1.0.0
     * @apiName GroupUnionInvite.findDetailById
     * @apiGroup 医联体邀请科室
     * @apiDescription 获取邀请详情
     * @apiParam {String} access_token token
     * @apiParam {String} inviteId inviteId
     * @apiSuccess {Object} invite 邀请详情
     * @apiSuccess {String} invite.inviteId 邀请id
     * @apiSuccess {Integer} invite.statusId 邀请状态。1处理中，2已同意，3已拒绝，8已关闭，9已删除
     * @apiSuccess {Object} invite.groupUnion 医联体信息
     * @apiSuccess {Object} invite.group     科室信息
     * @apiSuccess {Object} invite.user      邀请人的信息
     * @apiSuccess {String} invite.msg      通知文本
     * @apiAuthor 肖伟
     * @date 2017年05月22日
     */
    @RequestMapping("/findDetailById")
    public JSONMessage findDetailById(@RequestParam("inviteId") String id) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        MobileGroupUnionInviteVO ret = groupUnionInviteService.findDetailByIdAndVO(currentUserId, id);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /m/circle/group/union/invite/create 医联体邀请科室或者圈子成员
     * @apiVersion 1.0.0
     * @apiName GroupUnionInvite.create
     * @apiGroup 医联体邀请科室
     * @apiDescription 医联体邀请科室或者圈子成员
     * @apiParam {String} access_token token
     * @apiParam {String} unionId 当前的医联体id
     * @apiParam {String} groupId 被邀请的科室id
     * @apiSuccess {Boolean} boolean true表示成功，false表示失败
     * @apiAuthor 肖伟
     * @date 2017年05月22日
     */
    @RequestMapping("/create")
    public JSONMessage create(@RequestParam String unionId, @RequestParam String groupId) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        GroupUnionInvite ret = groupUnionInviteService.create(currentUserId, unionId, groupId);
        return JSONMessage.success(null, null != ret);
    }

    /**
     * @api {POST} /m/circle/group/union/invite/confirm 科室管理员确认邀请
     * @apiVersion 1.0.0
     * @apiName GroupUnionInvite.confirm
     * @apiGroup 医联体邀请科室
     * @apiDescription 科室管理员确认邀请
     * @apiParam {String} access_token token
     * @apiParam {String} inviteId inviteId
     * @apiParam {Integer} agree 同意为1，拒绝为0
     * @apiSuccess {Boolean} boolean true表示成功，false表示失败
     * @apiAuthor 肖伟
     * @date 2017年05月22日
     */
    @RequestMapping("/confirm")
    public JSONMessage confirm(@RequestParam(name = "inviteId") String id, @RequestParam Integer agree) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = false;
        switch (agree) {
            case 1:
                ret = groupUnionInviteService.accept(currentUserId, id);
                break;
            case 0:
                ret = groupUnionInviteService.refuse(currentUserId, id);
                break;
        }
        return JSONMessage.success(null, ret);
    }
}
