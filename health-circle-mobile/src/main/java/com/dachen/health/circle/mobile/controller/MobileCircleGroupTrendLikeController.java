package com.dachen.health.circle.mobile.controller;

import com.dachen.health.circle.entity.GroupTrendLike;
import com.dachen.health.circle.service.GroupTrendLikeService;
import com.dachen.health.circle.vo.MobileGroupTrendLikeVO;
import com.dachen.sdk.json.JSONMessage;
import com.dachen.sdk.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/m/circle/group/trend/like")
public class MobileCircleGroupTrendLikeController extends MobileCircleBaseController {

    @Autowired
    private GroupTrendLikeService groupTrendLikeService;

    /**
     * @api {POST} /m/circle/group/trend/like/create 创建点赞记录
     * @apiVersion 1.0.0
     * @apiName GroupTrendLike.create
     * @apiGroup 科室动态点赞
     * @apiDescription 创建点赞记录
     * @apiParam {String} access_token token
     * @apiParam {String} trendId 科室动态id
     * @apiSuccess {Boolean} success success
     * @apiAuthor 肖伟
     * @date 2017年5月25日
     */
    @RequestMapping(value = "/create")
    public JSONMessage create(@RequestParam String trendId) {
        Integer currentUserId = this.getCurrentUserId();
        GroupTrendLike ret = groupTrendLikeService.add(currentUserId, trendId);
        return JSONMessage.success(null, null != ret);
    }

    /**
     * @api {GET} /m/circle/group/trend/like/findPage 获取点赞列表
     * @apiVersion 1.0.0
     * @apiName GroupTrendLike.findPage
     * @apiGroup 科室动态点赞
     * @apiDescription 获取评论列表
     * @apiParam {String} access_token token
     * @apiParam {String} trendId 动态id
     * @apiParam {Integer} pageIndex pageIndex, 从第0页开始
     * @apiParam {Integer} pageSize pageSize
     * @apiSuccess {Object} page 一页数据
     * @apiSuccess {Object} page.total 总记录数
     * @apiSuccess {Object} page.pageData 评论列表
     * @apiSuccess {String} page.pageData.likeId 点赞记录id
     * @apiSuccess {Object} page.pageData.user 点赞人详情
     * @apiSuccess {Long} page.pageData.createTime 点赞时间
     * @apiAuthor 肖伟
     * @date 2017年5月25日
     */
    @RequestMapping(value = "/findPage")
    public JSONMessage findPage(@RequestParam(name = "trendId") String trendId,
                                       @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                                       @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize) {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupTrendLikeVO> ret = groupTrendLikeService.findPageAndVO(currentUserId, trendId, pageIndex, pageSize);
        return JSONMessage.success(null, ret);
    }

    /**
     * @apiIgnore
     * @api {POST} /m/circle/group/trend/like/remove 取消赞
     * @apiVersion 1.0.0
     * @apiName GroupTrendLike.remove
     * @apiGroup 科室动态点赞
     * @apiDescription 取消赞
     * @apiParam {String} access_token token
     * @apiParam {String} likeId 点赞记录id
     * @apiSuccess {Boolean} ret true表示成功，false表示失败
     * @apiAuthor 肖伟
     * @date 2017年5月15日
     */
    @RequestMapping(value = "/remove")
    public JSONMessage remove(@RequestParam("likeId") String id) {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupTrendLikeService.remove(currentUserId, id);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /m/circle/group/trend/like/removeByTrend 取消赞
     * @apiVersion 1.0.0
     * @apiName GroupTrendLike.removeByTrend
     * @apiGroup 科室动态点赞
     * @apiDescription 取消赞
     * @apiParam {String} access_token token
     * @apiParam {String} trendId 动态id
     * @apiSuccess {Boolean} ret true表示成功，false表示失败
     * @apiAuthor 肖伟
     * @date 2017年5月15日
     */
    @RequestMapping(value = "/removeByTrend")
    public JSONMessage removeByTrend(@RequestParam String trendId) {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupTrendLikeService.removeByTrend(currentUserId, trendId);
        return JSONMessage.success(null, ret);
    }
}
