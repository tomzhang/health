package com.dachen.health.pack.order.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.PackEnum;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.pack.order.dao.IOrderSessionContainerDao;
import com.dachen.health.pack.order.entity.po.OrderSessionContainer;

@Repository
public class OrderSessionContainerDao extends NoSqlRepository  implements IOrderSessionContainerDao {


	@Override
	public List<OrderSessionContainer> findByUser(Integer doctorId, Integer userId, Integer patientId) {
		List<OrderSessionContainer> list = dsForRW.createQuery(OrderSessionContainer.class)
				.field("doctorId").equal(doctorId)
				.field("userId").equal(userId)
				.field("patientId").equal(patientId)
				.field("packType").notEqual(PackEnum.PackType.checkin.getIndex())
				.asList();
		return list;
	}

	@Override
	public void updateStatus(String id, Integer orderStatus) {
        UpdateOperations<OrderSessionContainer> ops = dsForRW.createUpdateOperations(OrderSessionContainer.class);
        ops.set("status",orderStatus);
        Query<OrderSessionContainer> q = dsForRW.createQuery(OrderSessionContainer.class).field("_id").equal(new ObjectId(id));
        dsForRW.updateFirst(q, ops);
	}

    @Override
    public OrderSessionContainer findTextPhone(Integer doctorId, Integer userId, Integer patientId,Integer careOrderId, Integer sessionCategory) {
    	Query<OrderSessionContainer> q = dsForRW.createQuery(OrderSessionContainer.class)
							                .field("doctorId").equal(doctorId)
							                .field("userId").equal(userId)
							                .field("patientId").equal(patientId)
							                .field("sessionType").equal(sessionCategory);
    	if(careOrderId != null)
    		q.field("careOrderId").equal(careOrderId);
    	return q.get();
    }

	@Override
	public void insert(OrderSessionContainer po) {
		dsForRW.insert(po);
	}

	@Override
	public void updateById(String id, Integer orderId, Integer packType, Integer orderSessionId,
			Integer totalReplyCount, Integer replyCount , Integer status) {
		 UpdateOperations<OrderSessionContainer> ops = dsForRW.createUpdateOperations(OrderSessionContainer.class);
		 if(orderId != null)
			 ops.set("orderId",orderId);
		 if(packType != null)
			 ops.set("packType", packType);
		 if(orderSessionId != null)
			 ops.set("orderSessionId", orderSessionId);
		 if(totalReplyCount != null)
			 ops.set("totalReplyCount", totalReplyCount);
		 if(replyCount != null)
			 ops.set("replidCount", replyCount);
		 if(status != null)
			 ops.set("status", status);
		 Query<OrderSessionContainer> q = dsForRW.createQuery(OrderSessionContainer.class).field("_id").equal(new ObjectId(id));
	     dsForRW.updateFirst(q, ops);
	}

	@Override
	public List<OrderSessionContainer> findByMsgGroupId(String messageGroupId) {
		List<OrderSessionContainer> list = dsForRW.createQuery(OrderSessionContainer.class)
			   .field("msgGroupId").equal(messageGroupId)
			   .asList();
		List<OrderSessionContainer> oscList = null;
		if(list != null && list.size() > 2){
			oscList = new ArrayList<>();
			Iterator<OrderSessionContainer> ite = list.iterator();
			OrderSessionContainer item = null;
			while (ite.hasNext()){
				OrderSessionContainer osc = ite.next();
				if(osc.getSessionType() == OrderEnum.OrderSessionCategory.care_in_text_tel_integral.getIndex()){
					if(osc.getStatus() == OrderEnum.OrderStatus.已完成.getIndex())
						item = osc;
					else
						oscList.add(osc);
				}else if(osc.getSessionType() == OrderEnum.OrderSessionCategory.care.getIndex())
					oscList.add(osc);
			}
			if(oscList.size() < 2)
				oscList.add(item);
		}
		return oscList == null ? list : oscList;
	}

	@Override
	public OrderSessionContainer findByMsgGroupIdAndType(String messageGroupId, Integer sessionCategory) {
		return dsForRW.createQuery(OrderSessionContainer.class)
				   .field("msgGroupId").equal(messageGroupId)
				   .field("sessionType").equal(sessionCategory)
				   .get();
	}

	@Override
	public OrderSessionContainer findByOrderId(Integer orderId) {
		return dsForRW.createQuery(OrderSessionContainer.class)
				.field("orderId").equal(orderId)
				.get();
	}

	@Override
	public OrderSessionContainer findById(String id) {
		return dsForRW.createQuery(OrderSessionContainer.class)
				.field("_id").equal(new ObjectId(id))
				.get();
	}

	@Override
	public void deleteById(String id) {
		dsForRW.delete(dsForRW.createQuery(OrderSessionContainer.class).field("_id").equal(new ObjectId(id)));
	}
}
