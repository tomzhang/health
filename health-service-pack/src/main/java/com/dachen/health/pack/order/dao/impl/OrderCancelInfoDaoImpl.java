package com.dachen.health.pack.order.dao.impl;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.pack.order.dao.OrderCancelInfoDao;
import com.dachen.health.pack.order.entity.po.OrderCancelInfo;
import org.springframework.stereotype.Repository;

/**
 * Created by fuyongde on 2017/2/15.
 */
@Repository
public class OrderCancelInfoDaoImpl extends NoSqlRepository implements OrderCancelInfoDao {
    @Override
    public void save(OrderCancelInfo orderCancelInfo) {
        Long now = System.currentTimeMillis();
        orderCancelInfo.setCancelTime(now);
        orderCancelInfo.setCreateTime(now);
        dsForRW.insert(orderCancelInfo);
    }
}
