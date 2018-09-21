package com.dachen.health.circle.mobile.controller;

import com.dachen.health.circle.entity.GroupTrendCommentLike;
import com.dachen.health.circle.entity.GroupTrendLike;
import com.dachen.health.circle.service.GroupTrendCommentLikeService;
import com.dachen.health.circle.service.GroupTrendLikeService;
import com.dachen.health.circle.vo.MobileGroupTrendCommentLikeVO;
import com.dachen.health.circle.vo.MobileGroupTrendLikeVO;
import com.dachen.sdk.json.JSONMessage;
import com.dachen.sdk.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 动态评论点赞
 * Created By lim
 * Date: 2017/6/2
 * Time: 2017年6月2日15:18:38
 */
@RestController
@RequestMapping("/m/circle/group/trend/comment/like")
public class MobileCircleGroupTrendCommentLikeController extends MobileCircleBaseController {

    @Autowired
    private GroupTrendCommentLikeService groupTrendCommentLikeService;

    /**
     * @api {POST} /m/circle/group/trend/comment/like/create 创建点赞记录
     * @apiVersion 1.0.0
     * @apiName GroupTrendCommentLike.create
     * @apiGroup 科室动态评论点赞
     * @apiDescription 创建点赞记录
     * @apiParam {String} access_token token
     * @apiParam {String} commentId 科室动态评论id
     * @apiSuccess {Boolean} success success
     * @apiAuthor 李敏
     * @date 2017年6月2日
     */
    @RequestMapping(value = "/create")
    public JSONMessage create(@RequestParam String commentId) {
        Integer currentUserId = this.getCurrentUserId();
        GroupTrendCommentLike ret = groupTrendCommentLikeService.add(currentUserId, commentId);
        return JSONMessage.success(null, null != ret);
    }

    /**
     * @api {GET} /m/circle/group/trend/comment/like/findPage 获取点赞列表
     * @apiVersion 1.0.0
     * @apiName GroupTrendCommentLike.findPage
     * @apiGroup 科室动态评论点赞
     * @apiDescription 获取评论点赞列表
     * @apiParam {String} access_token token
     * @apiParam {String} commentId 动态评论id
     * @apiParam {Integer} pageIndex pageIndex, 从第0页开始
     * @apiParam {Integer} pageSize pageSize
     * @apiSuccess {Object} page 一页数据
     * @apiSuccess {Object} page.total 总记录数
     * @apiSuccess {Object} page.pageData 评论点赞列表
     * @apiSuccess {String} page.pageData.likeId 点赞记录id
     * @apiSuccess {Object} page.pageData.user 点赞人详情
     * @apiSuccess {Long} page.pageData.createTime 点赞时间
     * @apiAuthor 李敏
     * @date 2017年6月2日
     */
    @RequestMapping(value = "/findPage")
    public JSONMessage findPage(@RequestParam(name = "commentId") String commentId,
                                       @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                                       @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize) {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupTrendCommentLikeVO> ret = groupTrendCommentLikeService.findPageAndVO(currentUserId, commentId, pageIndex, pageSize);
        return JSONMessage.success(null, ret);
    }

    /**
     * @apiIgnore
     * @api {POST} /m/circle/group/trend/comment/like/remove 取消赞
     * @apiVersion 1.0.0
     * @apiName GroupTrendCommentLike.remove
     * @apiGroup 科室动态评论点赞
     * @apiDescription 取消赞
     * @apiParam {String} access_token token
     * @apiParam {String} likeId 点赞记录id
     * @apiSuccess {Boolean} ret true表示成功，false表示失败
     * @apiAuthor 李敏
     * @date 2017年6月2日
     */
    @RequestMapping(value = "/remove")
    public JSONMessage remove(@RequestParam("likeId") String id) {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupTrendCommentLikeService.remove(currentUserId, id);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /m/circle/group/trend/comment/like/removeByCommentId 取消赞
     * @apiVersion 1.0.0
     * @apiName GroupTrendCommentLike.removeByCommentId
     * @apiGroup 科室动态评论点赞
     * @apiDescription 取消赞
     * @apiParam {String} access_token token
     * @apiParam {String} commentId 动态评论id
     * @apiSuccess {Boolean} ret true表示成功，false表示失败
     * @apiAuthor 李敏
     * @date 2017年6月2日
     */
    @RequestMapping(value = "/removeByCommentId")
    public JSONMessage removeByCommentId(@RequestParam String commentId) {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupTrendCommentLikeService.removeByCommentId(currentUserId, commentId);
        return JSONMessage.success(null, ret);
    }
}
