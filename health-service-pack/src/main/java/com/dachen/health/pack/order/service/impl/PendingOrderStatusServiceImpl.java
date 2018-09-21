package com.dachen.health.pack.order.service.impl;

import com.dachen.health.pack.order.dao.IPendingOrderStatusDao;
import com.dachen.health.pack.order.entity.po.PendingOrderStatus;
import com.dachen.health.pack.order.service.IPendingOrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by qinyuan.chen
 * Date:2016/12/29
 * Time:18:52
 */
@Service
public class PendingOrderStatusServiceImpl implements IPendingOrderStatusService{

    @Autowired
    private IPendingOrderStatusDao pendingOrderStatusDao;

    @Override
    public PendingOrderStatus add(PendingOrderStatus pos) {
        return pendingOrderStatusDao.add(pos);
    }

    @Override
    public void updatePendingOrderStatus(PendingOrderStatus pos) {
        pendingOrderStatusDao.updateById(pos);
    }

    @Override
    public void deleteByOrderId(Integer orderId) {
        pendingOrderStatusDao.deleteByOrderId(orderId);
    }

    @Override
    public PendingOrderStatus queryByOrderId(Integer orderId) {
        return pendingOrderStatusDao.queryByOrderId(orderId);
    }
}
