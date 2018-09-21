package com.dachen.health.checkbill.dao;

import com.dachen.health.checkbill.entity.po.XGCheckItemReq;

import java.util.List;

/**
 * Created by fuyongde on 2017/1/12.
 */
public interface XGCheckItemReqDao {

    void save(XGCheckItemReq xgCheckItemReq);

    XGCheckItemReq findByCheckItemId(String checkItemId);

    XGCheckItemReq findById(String id);

    void update(XGCheckItemReq xgCheckItemReq);

    /**
     * 查询没有成功的请求
     * @return
     */
    List<XGCheckItemReq> findNoSuccess();
}
