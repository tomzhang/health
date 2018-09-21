package com.dachen.health.circle.mobile.controller;

import com.dachen.health.circle.entity.GroupFollow;
import com.dachen.health.circle.service.GroupFollowService;
import com.dachen.health.circle.vo.MobileGroupFollowVO;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.json.JSONMessage;
import com.dachen.sdk.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/m/circle/group/follow")
public class MobileCircleGroupFollowController extends MobileCircleBaseController {

    @Autowired
    protected GroupFollowService groupFollowService;

    /**
     * @api {POST} /m/circle/group/follow/add 关注科室
     * @apiVersion 1.0.0
     * @apiName GroupFollow.add
     * @apiGroup 科室关注
     * @apiDescription 关注科室
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupId 科室id
     *
     * @apiSuccess {Boolean} success true表示成功，false表示失败
     *
     * @apiAuthor 肖伟
     * @date 2017年05月09日
     */
    @RequestMapping("/add")
    public JSONMessage add(String groupId) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        GroupFollow groupFollow = groupFollowService.add(currentUserId, groupId);
        return JSONMessage.success(null, null == groupFollow?false:true);
    }

    /**
     * @api {POST} /m/circle/group/follow/removeByGroup 取关科室
     * @apiVersion 1.0.0
     * @apiName GroupFollow.removeByGroup
     * @apiGroup 科室关注
     * @apiDescription 取关科室
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupId 科室id
     *
     * @apiSuccess {Boolean} success true表示成功，false表示失败
     *
     * @apiAuthor 肖伟
     * @date 2017年05月09日
     */
    @RequestMapping("/removeByGroup")
    public JSONMessage removeByGroup(String groupId) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupFollowService.removeByGroup(currentUserId, groupId);
        return JSONMessage.success(null, ret);
    }

    /**
     * @apiIgnore
     * @api {POST} /m/circle/group/follow/remove 取关科室
     * @apiVersion 1.0.0
     * @apiName GroupFollow.remove
     * @apiGroup 科室关注
     * @apiDescription 取关科室
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupFollowId 关注id
     *
     * @apiSuccess {Boolean} success true表示成功，false表示失败
     *
     * @apiAuthor 肖伟
     * @date 2017年05月09日
     */
    @RequestMapping("/remove")
    public JSONMessage remove(@RequestParam("groupFollowId") String id) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupFollowService.remove(currentUserId, id);
        return JSONMessage.success(null, ret);
    }

    /**
     * @apiIgnore
     * @api {GET} /m/circle/group/follow/findMyList 我关注的科室
     * @apiVersion 1.0.0
     * @apiName GroupFollow.findMyList
     * @apiGroup 科室关注
     * @apiDescription 我关注的科室
     *
     * @apiParam {String} access_token token
     *
     * @apiSuccess {Object[]} GroupFollowList
     * @apiSuccess {String} GroupFollowList.followId
     * @apiSuccess {String} GroupFollowList.groupId
     * @apiSuccess {String} GroupFollowList.groupName
     * @apiSuccess {String} GroupFollowList.groupType
     * @apiSuccess {Integer} GroupFollowList.userId
     *
     *
     * @apiAuthor 肖伟
     * @date 2017年05月09日
     */
    @RequestMapping("/findMyList")
    public JSONMessage findMyList() throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        List<MobileGroupFollowVO> list = groupFollowService.findListAndVO(currentUserId);
        return JSONMessage.success(null, list);
    }

    /**
     * @api {GET} /m/circle/group/follow/more 关注更多
     * @apiVersion 1.0.0
     * @apiName GroupFollow.more
     * @apiGroup 科室关注
     * @apiDescription 关注更多
     *
     * @apiParam {String} access_token token
     * @apiParam {String} kw kw
     * @apiParam {Integer} pageIndex pageIndex
     * @apiParam {Integer} pageSize pageSize
     *
     * @apiSuccess {Object[]} GroupFollowList   关注列表
     * @apiSuccess {String} GroupFollowList.followId  关注id
     * @apiSuccess {Integer} GroupFollowList.userId
     * @apiSuccess {String} GroupFollowList.groupId
     * @apiSuccess {String} GroupFollowList.group 科室详情
     *
     * @apiAuthor 肖伟
     * @date 2017年05月23日
     */
    @RequestMapping("/more")
    public JSONMessage more(String kw, @RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "20") Integer pageSize) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupFollowVO> list = groupFollowService.findMoreAndVO(currentUserId, kw, pageIndex, pageSize);
        return JSONMessage.success(null, list);
    }

}
