package com.dachen.health.circle.mobile.controller;

import com.dachen.commons.JSONMessage;
import com.dachen.health.circle.entity.GroupTrendComment;
import com.dachen.health.circle.form.TrendCommentAddForm;
import com.dachen.health.circle.service.GroupTrendCommentService;
import com.dachen.health.circle.vo.MobileGroupTrendCommentVO;
import com.dachen.sdk.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/m/circle/group/trend/comment")
public class MobileCircleGroupTrendCommentController extends MobileCircleBaseController {

    @Autowired
    private GroupTrendCommentService groupTrendCommentService;

    /**
     * @api {POST} /m/circle/group/trend/comment/create 新建评论
     * @apiVersion 1.0.0
     * @apiName GroupTrendComment.create
     * @apiGroup 科室动态评论
     * @apiDescription 新建评论或回复
     * @apiParam {String} access_token token
     * @apiParam {String} trendId 动态id
     * @apiParam {String} content 评论内容
     * @apiParam  {String[]}  imageList 图片列表
     * @apiSuccess {Object} comment 评论详情
     * @apiAuthor 肖伟
     * @date 2017年5月15日
     */
    @RequestMapping(value = "/create")
    public JSONMessage create(@Valid TrendCommentAddForm form) {
        Integer currentUserId = this.getCurrentUserId();
        GroupTrendComment ret = groupTrendCommentService.add(currentUserId, form.getTrendId(), form.getContent(),form.getImageList());
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {GET} /m/circle/group/trend/comment/findPage 获取评论列表
     * @apiVersion 1.0.0
     * @apiName GroupTrendComment.findPage
     * @apiGroup 科室动态评论
     * @apiDescription 获取评论列表
     * @apiParam {String} access_token token
     * @apiParam {String} trendId 动态id
     * @apiParam {Integer} pageIndex pageIndex, 从第0页开始
     * @apiParam {Integer} pageSize pageSize
     * @apiSuccess {Object} page 一页数据
     * @apiSuccess {Object} page.total 总记录数
     * @apiSuccess {Object} page.pageData 评论列表
     * @apiSuccess {String} page.pageData.commentId 评论id
     * @apiSuccess {String} page.pageData.content 评论内容
     * @apiSuccess {String[]} page.pageData.imageList 评论图片地址
     * @apiSuccess {Long} page.pageData.createTime 评论时间
     * @apiSuccess {Object} page.pageData.user 评论人详情
     * @apiSuccess {Object[]} page.pageData.recentReplyList 前几条回复，回复与讨论结构一致
     * @apiSuccess {Object[]} page.pageData.recentLikeList  最近点赞列表
     * @apiSuccess {Boolean} page.pageData.ifLike 当前用户是否点赞，true表示已点赞
     * @apiAuthor 肖伟
     * @date 2017年5月15日
     */
    @RequestMapping(value = "/findPage")
    public JSONMessage findPage(@RequestParam(name = "trendId") String trendId,
                                       @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                                       @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize) {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupTrendCommentVO> ret = groupTrendCommentService.findPageAndVO(currentUserId, trendId, pageIndex, pageSize);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {GET} /m/circle/group/trend/comment/findById 获取评论详情
     * @apiVersion 1.0.0
     * @apiName GroupTrendComment.findById
     * @apiGroup 科室动态评论
     * @apiDescription 获取评论详情
     * @apiParam {String} access_token token
     * @apiParam {String} trendId 动态id
     * @apiParam {String} commentId 评论id
     * @apiSuccess {Object} comment 评论详情
     * @apiSuccess {String} comment.commentId 评论id
     * @apiSuccess {String} comment.content 评论内容
     * @apiSuccess {String[]} comment.imageList 评论图片地址
     * @apiSuccess {Long} comment.createTime 评论时间
     * @apiSuccess {Object} comment.user 评论人详情
     * @apiSuccess {Integer} comment.totalReply 总回复数
     * @apiSuccess {Boolean} ifLike 当前用户是否点赞，true表示已点赞
     *
     * @apiAuthor 肖伟
     * @date 2017年5月25日
     */
    @RequestMapping(value = "/findById")
    public JSONMessage findById(@RequestParam("commentId") String id) {
        Integer currentUserId = this.getCurrentUserId();
        MobileGroupTrendCommentVO ret = groupTrendCommentService.findByIdAndVO(currentUserId, id);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /m/circle/group/trend/comment/remove 删除评论
     * @apiVersion 1.0.0
     * @apiName GroupTrendComment.remove
     * @apiGroup 科室动态评论
     * @apiDescription 删除评论
     * @apiParam {String} access_token token
     * @apiParam {String} commentId 评论的id
     * @apiSuccess {Boolean} ret true表示成功，false表示失败
     * @apiAuthor 肖伟
     * @date 2017年5月15日
     */
    @RequestMapping(value = "/remove")
    public JSONMessage remove(@RequestParam("commentId") String id) {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupTrendCommentService.remove(currentUserId, id);
        return JSONMessage.success(null, ret);
    }

    @RequestMapping(value = "/refreshRecentReplyIdList")
    public JSONMessage refreshRecentReplyIdList(@RequestParam("commentId") String id) {
        Integer currentUserId = this.getCurrentUserId();
        groupTrendCommentService.refreshRecentReplyIdListAsync();
        return JSONMessage.success();
    }
}
