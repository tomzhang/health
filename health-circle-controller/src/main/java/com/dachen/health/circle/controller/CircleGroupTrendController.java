package com.dachen.health.circle.controller;

import com.dachen.commons.JSONMessage;
import com.dachen.health.circle.entity.GroupTrend;
import com.dachen.health.circle.form.GroupTrendAddForm;
import com.dachen.health.circle.form.GroupTrendUpdateForm;
import com.dachen.health.circle.service.GroupTrendService;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/circle/group/trend")
public class CircleGroupTrendController extends CircleBaseController {

    @Autowired
    protected GroupTrendService groupTrendService;

    /**
     * @api {POST} /circle/group/trend/create 发布动态
     * @apiVersion 1.0.0
     * @apiName GroupTrend.create
     * @apiGroup 科室动态
     * @apiDescription 发布动态
     * @apiParam {String} access_token token
     * @apiParam {String} groupId 科室id
     * @apiParam {String} title 标题
     * @apiParam {String} picUrl 题图地址
     * @apiParam {String} summary 简介（从正文中提取前200个字）
     * @apiParam {String} content 正文
     * @apiParam {String}  videosJson     资料视频说明(json字符串 [{"play_url":"视频播放地址",
     * "play_first":"第一帧图片地址", "play_time":"视频播放时长", "size":"视频大小", "suffix":"文件后缀名" }] )
     * @apiParam {String}  attachmentsJson     资料附件(json字符串： [{"type":"类别0-文件 1-超链接", "explain":"说明",
     * "link":"链接地址", "name":"名称" "file":{"file_id":"id", "file_url":"文件地址", "size":"文件大小",
     * "sizeStr":"文件大小显示值", "file_name":"附件名称", "type":"附件类型", "suffix":"后缀名" }] )
     * @apiSuccess {Object} groupTrend groupTrend
     * @apiAuthor 肖伟
     * @date 2017年5月16日
     */
    @RequestMapping("/create")
    public JSONMessage create(@Valid GroupTrendAddForm form) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        GroupTrend groupTrend = groupTrendService.add(currentUserId, form);
        return JSONMessage.success(null, groupTrend);
    }

    /**
     * @api {POST} /circle/group/trend/update 更新动态
     * @apiVersion 1.0.0
     * @apiName GroupTrend.update
     * @apiGroup 科室动态
     * @apiDescription 更新动态
     * @apiParam {String} access_token token
     * @apiParam {String} groupId 科室id
     * @apiParam {String} title 标题
     * @apiParam {String} picUrl 题图地址
     * @apiParam {String} summary 简介（从正文中提取前200个字）
     * @apiParam {String} content 正文
     * @apiParam {String}  videosJson     资料视频json
     * @apiParam {String}  attachmentsJson     资料附件json
     * @apiSuccess {Object} groupTrend groupTrend
     * @apiAuthor 肖伟
     * @date 2017年5月25日
     */
    @RequestMapping("/update")
    public JSONMessage update(@RequestParam String id, @Valid GroupTrendUpdateForm form) {
        Integer currentUserId = this.getCurrentUserId();
        GroupTrend groupTrend = groupTrendService.update(currentUserId, id, form);
        return JSONMessage.success(null, groupTrend);
    }

    /**
     * @api {GET} /circle/group/trend/findPage 动态列表
     * @apiVersion 1.0.0
     * @apiName GroupTrend.findPage
     * @apiGroup 科室动态
     * @apiDescription 动态列表
     * @apiParam {String} access_token token
     * @apiParam {String} groupId 科室id
     * @apiParam {Integer} pageIndex 页码
     * @apiParam {Integer} pageSize 页面大小
     * @apiSuccess {Object} page page
     * @apiAuthor 肖伟
     * @date 2017年5月16日
     */
    @RequestMapping(value = "/findPage")
    public JSONMessage findPage(@RequestParam(name = "groupId") String groupId,
                                @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                                @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        Integer currentUserId = this.getCurrentUserId();
        Pagination<GroupTrend> ret = groupTrendService.findPage(currentUserId, groupId, pageIndex, pageSize);
        return JSONMessage.success(null, ret);
    }

    /**
     * @api {GET} /circle/group/trend/findById 获取动态详情
     * @apiVersion 1.0.0
     * @apiName GroupTrend.findById
     * @apiGroup 科室动态
     * @apiDescription 获取动态详情
     * @apiParam {String} access_token token
     * @apiParam {String} id 动态id
     * @apiSuccess {String} groupTrend groupTrend
     * @apiSuccess {String} groupTrend.id 动态id
     * @apiSuccess {String} groupTrend.groupId 科室id
     * @apiSuccess {String} groupTrend.summary 摘要
     * @apiSuccess {String} groupTrend.content 内容
     * @apiSuccess {String} groupTrend.status 状态（2正常，9删除）
     * @apiSuccess {String} groupTrend.videos.play_url 视频地址
     * @apiSuccess {String} groupTrend.videos.play_first 第一帧图片地址
     * @apiSuccess {String} groupTrend.videos.play_time 视频播放时长
     * @apiSuccess {String} groupTrend.videos.size 视频大小
     * @apiSuccess {String} groupTrend.videos.suffix 文件后缀名
     * @apiSuccess {String} groupTrend.attachments.type 附件类型
     * @apiSuccess {String} groupTrend.attachments.explain 说明
     * @apiSuccess {String} groupTrend.attachments.link 连接地址
     * @apiSuccess {String} groupTrend.attachments.file.file_id 附件id
     * @apiSuccess {String} groupTrend.attachments.file.file_url 附件地址
     * @apiSuccess {String} groupTrend.attachments.file.size 附件大小
     * @apiSuccess {String} groupTrend.attachments.file.sizeStr 附件大小显示值
     * @apiSuccess {String} groupTrend.attachments.file.file_name 附件名称
     * @apiSuccess {String} groupTrend.attachments.file.type 附件类型
     * @apiSuccess {String} groupTrend.attachments.file.suffix 文件后缀名
     * @apiSuccess {String} groupTrend.attachments.name 文件名称
     * @apiAuthor 肖伟
     * @date 2017年5月16日
     */
    @RequestMapping(value = "/findById")
    public JSONMessage getById(@RequestParam String id) {
        Integer currentUserId = this.getCurrentUserId();
        GroupTrend groupTrend = groupTrendService.findFullById(currentUserId,id);
        return JSONMessage.success(null, groupTrend);
    }

    /**
     * @api {POST} /circle/group/trend/remove 删除动态
     * @apiVersion 1.0.0
     * @apiName GroupTrend.remove
     * @apiGroup 科室动态
     * @apiDescription 删除动态
     * @apiParam {String} access_token token
     * @apiParam {String} id 动态id
     * @apiSuccess {Boolean} success 成功true，失败false
     * @apiAuthor 肖伟
     * @date 2017年5月16日
     */
    @RequestMapping(value = "/remove")
    public JSONMessage remove(String id) {
        Integer currentUserId = this.getCurrentUserId();
        boolean ret = this.groupTrendService.remove(currentUserId, id);
        return JSONMessage.success(null, ret);
    }

}
