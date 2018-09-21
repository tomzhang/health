package com.dachen.health.controller.wx;

import com.dachen.commons.JSONMessage;
import com.dachen.health.wx.service.WXFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by fuyongde on 2017/2/14.
 */
@RestController
@RequestMapping("/wx-feedback")
public class WXFeedbackController {

    @Autowired
    private WXFeedbackService wxFeedbackService;

    /**
     * @api {get/post} /wx-feedback/saveFeedback	保存微信反馈
     * @apiVersion 1.0.0
     * @apiName saveFeedback
     * @apiGroup 微信反馈
     * @apiDescription 保存微信反馈
     *
     * @apiParam {String} phone 电话
     * @apiParam {String} content  反馈内容
     * @apiParam {String} code  微信返回的code
     * @apiSuccess {Integer} 	resultCode      1 成功
     *
     * @apiAuthor 傅永德
     * @date 2016年2月14日
     */
    @RequestMapping(value = "/saveFeedback")
    public JSONMessage feedback(
            @RequestParam(name = "phone", required = true) String phone,
            @RequestParam(name = "content", required = true) String content,
            @RequestParam(name = "code", required = true) String code
    ) {
        wxFeedbackService.saveWXFeedback(phone, content, code);
        return JSONMessage.success();
    }

    /**
     * @api {get/post} /wx-feedback/getFeedbackList	获取反馈列表
     * @apiVersion 1.0.0
     * @apiName getFeedbackList
     * @apiGroup 微信反馈
     * @apiDescription 获取微信反馈列表
     *
     * @apiParam {String}  access_token token
     * @apiParam {Integer} pageIndex 页码（必填）
     * @apiParam {Integer} pageSize  页面大小（必填）
     * @apiSuccess {Integer} 	resultCode      1 成功
     * @apiSuccess {Object[]} 	data.pageData      对象数据
     * @apiSuccess {String} 	data.pageData.content      内容
     * @apiSuccess {Long} 	data.pageData.createTime      反馈时间
     * @apiSuccess {String} 	data.pageData.phone      电话
     * @apiSuccess {Integer} 	data.total      总数
     *
     * @apiAuthor 傅永德
     * @date 2016年2月14日
     */
    @RequestMapping(value = "getFeedbackList")
    public JSONMessage getFeedbackList(
            @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        return JSONMessage.success(wxFeedbackService.findWXFeedback(pageIndex, pageSize));
    }
}
