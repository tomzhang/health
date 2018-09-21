package com.dachen.health.circle.mobile.controller;

import com.dachen.commons.JSONMessage;
import com.dachen.health.circle.entity.GroupTrendCommentCredit;
import com.dachen.health.circle.entity.GroupTrendCredit;
import com.dachen.health.circle.form.TrendCommentCreditAddForm;
import com.dachen.health.circle.form.TrendCreditAddForm;
import com.dachen.health.circle.service.GroupTrendCommentCreditService;
import com.dachen.health.circle.service.GroupTrendCreditService;
import com.dachen.health.circle.vo.MobileGroupTrendCommentCreditVO;
import com.dachen.health.circle.vo.MobileGroupTrendCreditVO;
import com.dachen.sdk.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 科室动态打赏学分
 * Created By lim
 * Date: 2017/6/7
 * Time: 16:48
 */
@RestController
@RequestMapping("/m/circle/group/trend/credit")
public class MobileCircleGroupTrendCreditController extends MobileCircleBaseController {
    @Autowired
    private GroupTrendCreditService groupTrendCreditService;

    /**
     * @api {POST} /m/circle/group/trend/credit/create 创建动态打赏
     * @apiVersion 1.0.0
     * @apiName GroupTrendCredit.create
     * @apiGroup 科室动态打赏
     * @apiDescription 创建点赞记录
     * @apiParam {String} access_token token
     * @apiParam {String} trendId 科室动态id
     * @apiParam {Intger} credit 打赏学分数值
     * @apiSuccess {Boolean} success success
     * @apiAuthor 李敏
     * @date 2017年6月7日16:56:25
     */
    @RequestMapping(value = "/create")
    public JSONMessage create(@Valid TrendCreditAddForm form) {
        Integer currentUserId = this.getCurrentUserId();
        GroupTrendCredit ret = groupTrendCreditService.add(currentUserId, form.getTrendId(), form.getCredit());
        return JSONMessage.success(null, null != ret);
    }


    /**
     * @api {POST} /m/circle/group/trend/credit/findPage 动态打赏列表
     * @apiVersion 1.0.0
     * @apiName GroupTrendCredit.findPage
     * @apiGroup 科室动态打赏
     * @apiDescription 动态打赏记录列表
     * @apiParam {String} access_token token
     * @apiParam {String} trendId 动态id
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
    public JSONMessage create(@RequestParam(name = "trendId") String trendId,
                              @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                              @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize) {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupTrendCreditVO> ret = groupTrendCreditService.findPageAndVO(currentUserId, trendId, pageIndex, pageSize);
        return JSONMessage.success(null, ret);
    }

}
