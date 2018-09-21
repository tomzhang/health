package com.dachen.health.pack.order.dao;

import com.dachen.health.auto.dao.BaseDao;
import com.dachen.health.pack.order.entity.po.PendingOrderStatus;

import java.util.List;

/**
 * Created by qinyuan.chen
 * Date:2016/12/28
 *
 */
public interface IPendingOrderStatusDao extends BaseDao<PendingOrderStatus>{

    //添加待处理订单状态
    public PendingOrderStatus add(PendingOrderStatus pendingOrderStatus);

    //通过ID更新待处理订单状态
    public void updateById(PendingOrderStatus pendingOrderStatus);

    //通过订单ID更新待处理订单状态
    public void updateByOrderId(PendingOrderStatus pendingOrderStatus);

    //删除待处理订单状态
    public void deleteByOrderId(Integer orderId);

    //根据订单ID查询订单待处理状态
    public PendingOrderStatus queryByOrderId(Integer orderId);

    //查询所有待处理的订单ID
    public List<Integer> queryAllOrderIds();

}
