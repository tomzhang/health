package com.dachen.health.pack.order.dao;

import com.dachen.health.pack.order.entity.po.OrderSessionContainer;

import java.util.List;

public interface IOrderSessionContainerDao {

	List<OrderSessionContainer> findByUser(Integer doctorId, Integer userId, Integer patientId);

	void updateStatus(String id, Integer orderStatus);

	OrderSessionContainer findTextPhone(Integer doctorId, Integer userId, Integer patientId, Integer careOrderId, Integer sessionCategory);

	void insert(OrderSessionContainer po);

	void updateById(String id, Integer orderId, Integer packType, Integer orderSessionId, Integer totalReplyCount, Integer replyCount, Integer status);

	List<OrderSessionContainer> findByMsgGroupId(String messageGroupId);

	OrderSessionContainer findByMsgGroupIdAndType(String messageGroupId, Integer sessionCategory);

	OrderSessionContainer findByOrderId(Integer orderId);

	OrderSessionContainer findById(String id);

    void deleteById(String id);
}
