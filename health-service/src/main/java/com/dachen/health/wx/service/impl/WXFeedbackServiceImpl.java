package com.dachen.health.wx.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.wx.dao.WXFeedbackDao;
import com.dachen.health.wx.po.WXFeedback;
import com.dachen.health.wx.service.WXFeedbackService;
import com.dachen.util.CheckUtils;

/**
 * Created by fuyongde on 2017/2/14.
 */
@Service
public class WXFeedbackServiceImpl implements WXFeedbackService {

    @Resource
    private WXFeedbackDao wxFeedbackDao;

    @Override
    public void saveWXFeedback(String phone, String content, String code) {
        WXFeedback wxFeedback = new WXFeedback();

        if (StringUtils.isBlank(phone) || !CheckUtils.checkMobile(phone)) {
            throw new ServiceException("请输入正确的手机号码");
        }

        if (StringUtils.isBlank(content)) {
            throw new ServiceException("反馈内容为空，请重新输入");
        }

        wxFeedback.setPhone(phone);
        wxFeedback.setContent(content);
        wxFeedback.setCode(code);

        wxFeedbackDao.saveWXFeedback(wxFeedback);
    }

    @Override
    public PageVO findWXFeedback(Integer pageIndex, Integer pageSize) {
        return wxFeedbackDao.findWXFeedback(pageIndex, pageSize);
    }
}
