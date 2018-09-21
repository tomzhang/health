package com.dachen.health.wx.dao;

import com.dachen.commons.page.PageVO;
import com.dachen.health.wx.po.WXFeedback;

/**
 * Created by fuyongde on 2017/2/14.
 */
public interface WXFeedbackDao {

    /**
     * 新增一条反馈
     *
     * @param wxFeedback
     */
    void saveWXFeedback(WXFeedback wxFeedback);

    /**
     * 反馈列表
     *
     * @param pageIndex 页码
     * @param pageSize  页面大小
     * @return
     */
    PageVO findWXFeedback(Integer pageIndex, Integer pageSize);
}
