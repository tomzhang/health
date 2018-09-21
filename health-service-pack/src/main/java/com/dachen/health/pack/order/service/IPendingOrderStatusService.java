package com.dachen.health.pack.order.service;

import com.dachen.health.pack.order.entity.po.PendingOrderStatus;

/**
 * Created by qinyuan.chen
 * Date:2016/12/29
 * Time:18:51
 */
public interface IPendingOrderStatusService {


    public PendingOrderStatus add(PendingOrderStatus pos);

    public void updatePendingOrderStatus(PendingOrderStatus pos);

    //删除待处理订单状态
    public void deleteByOrderId(Integer orderId);

    //根据订单ID查询订单待处理状态
    public PendingOrderStatus queryByOrderId(Integer orderId);
}
