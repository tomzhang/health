package com.dachen.health.circle.mobile.controller;

import com.dachen.commons.JSONMessage;
import com.dachen.health.circle.service.GroupTrendService;
import com.dachen.health.circle.vo.MobileGroupTrendVO;
import com.dachen.sdk.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/m/circle/group/trend")
public class MobileCircleGroupTrendController extends MobileCircleBaseController {

    @Autowired
    protected GroupTrendService groupTrendService;

    /**
     * @api {GET} /m/circle/group/trend/findPage 科室动态列表
     * @apiVersion 1.0.0
     * @apiName GroupTrend.findPage
     * @apiGroup 科室动态
     * @apiDescription 科室动态列表
     *
     * @apiParam {String} access_token token
     * @apiParam {String} groupId 科室id
     * @apiParam {Integer} pageIndex 页码
     * @apiParam {Integer} pageSize 页面大小
     *
     * @apiSuccess {Object} page page
     * @apiSuccess {Long} page.total total
     * @apiSuccess {Integer} page.pageIndex pageIndex
     * @apiSuccess {Integer} page.pageSize pageSize
     * @apiSuccess {Object[]} page.pageData pageData
     * @apiSuccess {String} page.pageData.trendId 动态id
     * @apiSuccess {String} page.pageData.groupId 科室id
     * @apiSuccess {String} page.pageData.title 标题
     * @apiSuccess {String} page.pageData.summary 摘要
     * @apiSuccess {Long} page.pageData.createTime 发布时间
     * @apiSuccess {Integer} page.pageData.doctor 医生详情
     * @apiSuccess {Object} page.pageData.ifLike 当前用户是否点赞
     *
     * @apiAuthor 肖伟
     * @date 2017年5月16日
     */
    @RequestMapping(value = "/findPage")
    public JSONMessage list(@RequestParam String groupId,
                            @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                            @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize) {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<MobileGroupTrendVO> ret = groupTrendService.findPageAndVO(currentUserId, groupId, pageIndex, pageSize);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {GET} /m/circle/group/trend/findById 获取动态详情
     * @apiVersion 1.0.0
     * @apiName GroupTrend.findById
     * @apiGroup 科室动态
     * @apiDescription 获取动态详情
     *
     * @apiParam {String} access_token token
     * @apiParam {String} trendId 动态id
     *
     * @apiSuccess {Object} groupTrend 动态
     * @apiSuccess {String} groupTrend.trendId 动态id
     * @apiSuccess {String} groupTrend.groupId 科室id
     * @apiSuccess {String} groupTrend.title 标题
     * @apiSuccess {String} groupTrend.summary 摘要
     * @apiSuccess {String} groupTrend.content 内容
     * @apiSuccess {Integer} groupTrend.status 状态, 2正常，9已删除
     * @apiSuccess {Object[]} groupTrend.videos 上传的视频列表
     * @apiSuccess {Object[]} groupTrend.annex 上传的附件列表
     * @apiSuccess {Long} groupTrend.createTime 创建时间
     * @apiSuccess {Object} groupTrend.doctor 医生详情
     * @apiSuccess {Integer} groupTrend.totalComment 总评论数
     * @apiSuccess {Integer} groupTrend.totalLike 总点赞数
     * @apiSuccess {Object[]} groupTrend.recentLikeList 最近点赞列表
     * @apiSuccess {Boolean} groupTrend.ifLike 当前用户是否点赞，true表示已点赞
     *
     * @apiAuthor 肖伟
     * @date 2017年5月16日
     */
    @RequestMapping(value = "/findById")
    public JSONMessage findById(@RequestParam("trendId") String id) {
        Integer currentUserId = this.getCurrentUserId();
        MobileGroupTrendVO ret = groupTrendService.findByIdAndVO(currentUserId, id);
        return JSONMessage.success(null, ret);
    }

}
