package com.dachen.health.circle.mobile.controller;

import com.dachen.commons.JSONMessage;
import com.dachen.health.circle.entity.GroupTrendComment;
import com.dachen.health.circle.entity.GroupTrendCommentReply;
import com.dachen.health.circle.form.TrendCommentAddForm;
import com.dachen.health.circle.form.TrendCommentReplyAddForm;
import com.dachen.health.circle.service.GroupTrendCommentReplyService;
import com.dachen.health.circle.service.GroupTrendCommentService;
import com.dachen.health.circle.vo.MobileGroupTrendCommentReplyVO;
import com.dachen.health.circle.vo.MobileGroupTrendCommentVO;
import com.dachen.sdk.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/m/circle/group/trend/comment/reply")
public class MobileCircleGroupTrendCommentReplyController extends MobileCircleBaseController {

    @Autowired
    private GroupTrendCommentReplyService groupTrendCommentReplyService;

    /**
     * @api {POST} /m/circle/group/trend/comment/reply/addByComment 回复评论
     * @apiVersion 1.0.0
     * @apiName GroupTrendCommentReply.addByComment
     * @apiGroup 科室动态评论回复
     * @apiDescription 回复评论
     * @apiParam {String} access_token token
     * @apiParam {String} commentId 评论id
     * @apiParam {String} content 评论内容
     * @apiSuccess {Object} reply reply
     * @apiAuthor 肖伟
     * @date 2017年5月31日
     */
    @RequestMapping(value = "/addByComment")
    public JSONMessage addByComment(@RequestParam String commentId, @RequestParam String content) {
        Integer currentUserId = this.getCurrentUserId();
        GroupTrendCommentReply ret = groupTrendCommentReplyService.addByComment(currentUserId, commentId, content);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /m/circle/group/trend/comment/reply/addByReply 回复回复
     * @apiVersion 1.0.0
     * @apiName GroupTrendCommentReply.addByReply
     * @apiGroup 科室动态评论回复
     * @apiDescription 回复回复
     * @apiParam {String} access_token token
     * @apiParam {String} replyToId 回复的id
     * @apiParam {String} content 评论内容
     * @apiSuccess {Object} reply reply
     * @apiAuthor 肖伟
     * @date 2017年5月31日
     */
    @RequestMapping(value = "/addByReply")
    public JSONMessage addByReply(@RequestParam String replyToId, @RequestParam String content) {
        Integer currentUserId = this.getCurrentUserId();
        GroupTrendCommentReply ret = groupTrendCommentReplyService.addByReply(currentUserId, replyToId, content);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {GET} /m/circle/group/trend/comment/reply/findPage 获取回复列表
     * @apiVersion 1.0.0
     * @apiName GroupTrendCommentReply.findCommentPage
     * @apiGroup 科室动态评论回复
     * @apiDescription 获取回复列表
     * @apiParam {String} access_token token
     * @apiParam {String} commentId 评论id
     * @apiParam {Integer} pageIndex pageIndex, 从第0页开始
     * @apiParam {Integer} pageSize pageSize
     * @apiSuccess {Object} page 一页数据
     * @apiSuccess {Object} page.total 总记录数
     * @apiSuccess {Object} page.pageData 回复列表
     * @apiSuccess {String} page.pageData.replyId 回复id
     * @apiSuccess {String} page.pageData.commentId 回复的评论（冗余）
     * @apiSuccess {String} page.pageData.content 回复内容
     * @apiSuccess {Long} page.pageData.createTime 回复时间
     * @apiSuccess {Object} page.pageData.user 回复人详情
     * @apiSuccess {String} page.pageData.replyToId 被回复的id
     * @apiSuccess {Object} page.pageData.replyToUser 被回复人详情
     * @apiAuthor 肖伟
     * @date 2017年5月31日
     */
    @RequestMapping(value = "/findPage")
    public JSONMessage findPage(@RequestParam String commentId,
                                       @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                                       @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize) {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupTrendCommentReplyVO> ret = groupTrendCommentReplyService.findPageAndVO(currentUserId, commentId, pageIndex, pageSize);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {POST} /m/circle/group/trend/comment/reply/remove 删除回复
     * @apiVersion 1.0.0
     * @apiName GroupTrendCommentReply.remove
     * @apiGroup 科室动态评论回复
     * @apiDescription 删除回复
     * @apiParam {String} access_token token
     * @apiParam {String} replyId 回复的id
     * @apiSuccess {Boolean} ret true表示成功，false表示失败
     * @apiAuthor 肖伟
     * @date 2017年5月31日
     */
    @RequestMapping(value = "/remove")
    public JSONMessage remove(@RequestParam("replyId") String id) {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = groupTrendCommentReplyService.remove(currentUserId, id);
        return JSONMessage.success(null, ret);
    }
}
