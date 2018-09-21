package com.dachen.health.wx.service;

import com.dachen.commons.page.PageVO;

/**
 * Created by fuyongde on 2017/2/14.
 */
public interface WXFeedbackService {

    /**
     * 新增一条反馈
     *
     * @param phone   电话
     * @param content 反馈内容
     * @param code    微信返回的code
     */
    void saveWXFeedback(String phone, String content, String code);

    /**
     * 反馈列表
     *
     * @param pageIndex 页码
     * @param pageSize  页面大小
     * @return
     */
    PageVO findWXFeedback(Integer pageIndex, Integer pageSize);
}
