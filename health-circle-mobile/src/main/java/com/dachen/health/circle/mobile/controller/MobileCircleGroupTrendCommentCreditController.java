package com.dachen.health.circle.mobile.controller;

import com.dachen.commons.JSONMessage;
import com.dachen.health.circle.entity.GroupTrendCommentCredit;
import com.dachen.health.circle.form.TrendCommentCreditAddForm;
import com.dachen.health.circle.service.GroupTrendCommentCreditService;
import com.dachen.health.circle.vo.MobileGroupTrendCommentCreditVO;
import com.dachen.sdk.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 评论打赏学分
 * Created By lim
 * Date: 2017/6/7
 * Time: 16:48
 */
@RestController
@RequestMapping("/m/circle/group/trend/comment/credit")
public class MobileCircleGroupTrendCommentCreditController extends MobileCircleBaseController {
    @Autowired
    private GroupTrendCommentCreditService groupTrendCommentCreditService;

    /**
     * @apiIgnore deprecated
     * @api {POST} /m/circle/group/trend/comment/credit/create 创建评论打赏
     * @apiVersion 1.0.0
     * @apiName GroupTrendCommentCredit.create
     * @apiGroup 科室动态评论打赏
     * @apiDescription 创建点赞记录
     * @apiParam {String} access_token token
     * @apiParam {String} commentId 科室动态评论id
     * @apiParam {Intger} credit 打赏学分数值
     * @apiSuccess {Boolean} success success
     * @apiAuthor 李敏
     * @date 2017年6月7日16:56:25
     */
    @RequestMapping(value = "/create")
    public JSONMessage create(@Valid TrendCommentCreditAddForm form) {
        Integer currentUserId = this.getCurrentUserId();
        GroupTrendCommentCredit ret = groupTrendCommentCreditService.add(currentUserId, form.getCommentId(), form.getCredit());
        return JSONMessage.success(null, null != ret);
    }


    /**
     * @apiIgnore deprecated
     * @api {POST} /m/circle/group/trend/comment/credit/findPage 评论打赏列表
     * @apiVersion 1.0.0
     * @apiName GroupTrendCommentCredit.findPage
     * @apiGroup 科室动态评论打赏
     * @apiDescription 评论打赏记录列表
     * @apiParam {String} access_token token
     * @apiParam {String} commentId 动态评论id
     * @apiParam {Integer} pageIndex pageIndex, 从第0页开始
     * @apiParam {Integer} pageSize pageSize
     * @apiSuccess {Object} page 一页数据
     * @apiSuccess {Object} page.total 总记录数
     * @apiSuccess {Object} page.pageData 评论打赏列表
     * @apiSuccess {String} page.pageData.credit 打赏学分
     * @apiSuccess {Object} page.pageData.user 打赏人详情
     * @apiSuccess {Long} page.pageData.createTime 打赏时间
     * @apiSuccessExample {json} success-Response:
     * {
     * "data": {
     *  "pageCount": 1,
     *  "pageData": [
     *       {
     *        "createTime": 1496830600397,
     *        "credit": 50,
     *        "user": {
     *                  "headPicUrl": "http://avatar.test.file.dachentech.com.cn/2b0185f13834420fa752411995a79141",
     *                  "intro": "wadefefwaawfaaaaaaaaaaaa觉得咖啡看看犯困就就犯困犯困开发奶粉可可粉麻烦吗你发那么繁忙更多怀念当年的你能否见到你就大哭方面看反馈绝对绝对经典家肯德基反馈反馈激发肌肤健康的开发开放看看反馈反馈那份激动咖啡看看反馈反馈的确是不",
     *                  "name": "黄登品",
     *                  "skill": "aaa",
     *                  "title": "主任医师",
     *                  "userId": 100196
     *                }
     *       }
     *    ],
     *  "pageIndex": 0,
     *  "pageSize": 15,
     *  "start": 0,
     *  "total": 1
     * },
     *  "resultCode": 1
     * }
     * @apiAuthor 李敏
     * @date 2017年6月7日17:23:00
     */
    @RequestMapping(value = "/findPage")
    public JSONMessage create(@RequestParam(name = "commentId") String commentId,
                              @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                              @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize) {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupTrendCommentCreditVO> ret = groupTrendCommentCreditService.findPageAndVO(currentUserId, commentId, pageIndex, pageSize);
        return JSONMessage.success(null, ret);
    }

}
