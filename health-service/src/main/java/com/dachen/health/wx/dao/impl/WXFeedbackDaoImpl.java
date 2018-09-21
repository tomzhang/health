package com.dachen.health.wx.dao.impl;

import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.wx.dao.WXFeedbackDao;
import com.dachen.health.wx.po.WXFeedback;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * Created by fuyongde on 2017/2/14.
 */
@Repository
public class WXFeedbackDaoImpl extends NoSqlRepository implements WXFeedbackDao {
    @Override
    public void saveWXFeedback(WXFeedback wxFeedback) {
        Long now = System.currentTimeMillis();
        wxFeedback.setCreateTime(now);
        dsForRW.insert(wxFeedback);
    }

    @Override
    public PageVO findWXFeedback(Integer pageIndex, Integer pageSize) {
        if (Objects.isNull(pageIndex)) {
            pageIndex = 0;
        }
        if (Objects.isNull(Objects.isNull(pageSize))) {
            pageSize = 10;
        }
        int skip = pageIndex * pageSize;
        skip = skip < 0 ? 0 : skip;
        List<WXFeedback> wxFeedbacks = dsForRW.createQuery(WXFeedback.class).order("-createTime").offset(skip).limit(pageSize).asList();
        long count = dsForRW.createQuery(WXFeedback.class).countAll();
        PageVO pageVO = new PageVO();
        pageVO.setPageData(wxFeedbacks);
        pageVO.setPageIndex(pageIndex);
        pageVO.setTotal(count);
        pageVO.setPageSize(pageSize);
        return pageVO;
    }

}
