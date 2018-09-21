package com.dachen.health.circle.mobile.controller;

import com.dachen.health.circle.service.GroupUnionMemberService;
import com.dachen.health.circle.vo.MobileGroupUnionMemberVO;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.json.JSONMessage;
import com.dachen.sdk.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/m/circle/group/union/member")
public class MobileCircleGroupUnionMemberController extends MobileCircleBaseController {

    @Autowired
    protected GroupUnionMemberService groupUnionMemberService;

    /**
     * @api {GET} /m/circle/group/union/member/findPage 获取成员列表
     * @apiVersion 1.0.0
     * @apiName GroupUnionMember.findPage
     * @apiGroup 医联体成员
     * @apiDescription 获取成员列表
     *
     * @apiParam {String} access_token token
     * @apiParam {String} unionId unionId
     * @apiParam {Integer} pageIndex pageIndex
     * @apiParam {Integer} pageSize pageSize
     *
     * @apiSuccess {Object} page    一页成员数据
     * @apiSuccess {Object[]} page.pageData  成员数据列表
     * @apiSuccess {String} page.pageData.memberId  成员id
     * @apiSuccess {String} page.pageData.unionId  成员unionId
     * @apiSuccess {String} page.pageData.groupId  成员groupId
     * @apiSuccess {Integer} page.pageData.ifMain  是否主体
     * @apiSuccess {Long} page.pageData.createTime  成员加入时间
     * @apiSuccess {Boolean} page.pageData.ifCanRemove 当前用户是否可移除此成员
     * @apiSuccess {Object} page.pageData.group  成员详情
     *
     * @apiAuthor 肖伟
     * @date 2017年05月09日
     */
    @RequestMapping("/findPage")
    public JSONMessage findPage(@RequestParam String unionId, @RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "20") Integer pageSize) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupUnionMemberVO> page = groupUnionMemberService.findPageAndVO(currentUserId, unionId, pageIndex, pageSize);
        return JSONMessage.success(null, page);
    }

    /**
     * @api {GET} /m/circle/group/union/member/findPageByGroup 获取科室的医联体列表
     * @apiVersion 1.0.0
     * @apiName GroupUnionMember.findUnionPageByGroup
     * @apiGroup 医联体成员
     * @apiDescription 获取科室的医联体列表
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupId groupId
     * @apiParam {Integer} pageIndex pageIndex
     * @apiParam {Integer} pageSize pageSize
     *
     * @apiSuccess {Object} page    一页数据
     * @apiSuccess {Object[]} page.pageData  数据列表
     * @apiSuccess {String} page.pageData.memberId  成员id
     * @apiSuccess {String} page.pageData.unionId  成员unionId
     * @apiSuccess {String} page.pageData.groupId  成员groupId
     * @apiSuccess {Integer} page.pageData.ifMain  是否主体
     * @apiSuccess {Long} page.pageData.createTime  成员加入时间
     * @apiSuccess {Object} page.pageData.union  医联体详情
     *
     * @apiAuthor 肖伟
     * @date 2017年05月23日
     */
    @RequestMapping("/findPageByGroup")
    public JSONMessage findUnionPageByGroup(@RequestParam String groupId, @RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "20") Integer pageSize) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupUnionMemberVO> page = groupUnionMemberService.findPageByGroup(currentUserId, groupId, pageIndex, pageSize);
        return JSONMessage.success(null, page);
    }

    /**
     * @api {POST} /m/circle/group/union/member/quit 退出
     * @apiVersion 1.0.0
     * @apiName GroupUnionMember.quit
     * @apiGroup 医联体
     * @apiDescription 退出
     * @apiParam {String} access_token token
     * @apiParam {String} unionId unionId
     * @apiParam {String} memberId  成员id
     * @apiSuccess {Boolean} success success
     *
     * @apiAuthor 肖伟
     * @date 2017年05月19日
     */
    @RequestMapping("/quit")
    public JSONMessage quit(@RequestParam("memberId") String id, String unionId) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupUnionMemberService.quit(currentUserId, unionId,id);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /m/circle/group/union/member/remove 移除成员
     * @apiVersion 1.0.0
     * @apiName GroupUnionMember.remove
     * @apiGroup 医联体成员
     * @apiDescription 移除成员
     *
     * @apiParam {String} access_token token
     * @apiParam {String} memberId memberId
     *
     * @apiSuccess {Boolean} boolean true表示成功，false表示失败
     *
     * @apiAuthor 肖伟
     * @date 2017年05月19日
     */
    @RequestMapping("/remove")
    public JSONMessage remove(@RequestParam(name="memberId") String id) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupUnionMemberService.remove(currentUserId, id);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {GET} /m/circle/group/union/member/findQuitList 获取退出医联体的成员列表
     * @apiVersion 1.0.0
     * @apiName GroupUnionMember.findQuitList
     * @apiGroup 医联体成员
     * @apiDescription 获取退出医联体的成员列表
     *
     * @apiParam {String} access_token token
     * @apiParam {String} unionId unionId
     *
     * @apiSuccess {Object} page    一页成员数据
     * @apiSuccess {Object[]} page.pageData  成员数据列表
     * @apiSuccess {String} page.pageData.memberId  成员id
     * @apiSuccess {String} page.pageData.unionId  成员unionId
     * @apiSuccess {String} page.pageData.groupId  成员groupId
     * @apiSuccess {Long} page.pageData.createTime  成员加入时间
     * @apiSuccess {Object} page.pageData.group  成员详情
     *
     * @apiAuthor 肖伟
     * @date 2017年05月09日
     */
    @RequestMapping("/findQuitList")
    public JSONMessage findQuitList(@RequestParam String unionId){
        Integer currentUserId = this.getCurrentUserId();
        List<MobileGroupUnionMemberVO> page = groupUnionMemberService.findQuitList(currentUserId, unionId);
        return JSONMessage.success(null, page);
    }
}
