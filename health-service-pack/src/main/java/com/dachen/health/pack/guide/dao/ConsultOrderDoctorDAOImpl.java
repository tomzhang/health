package com.dachen.health.pack.guide.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.pack.guide.entity.po.ConsultOrderDoctorPO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Repository
public class ConsultOrderDoctorDAOImpl extends NoSqlRepository implements IConsultOrderDoctorDAO{

	@Override
	public void addConsultOrder(ConsultOrderDoctorPO po) {
		dsForRW.save(po);
	}
	
	@Override
	public ConsultOrderDoctorPO getConsultOrderDoctor(String id) {
		return dsForRW.createQuery(ConsultOrderDoctorPO.class).filter("_id", new ObjectId(id)).get();
	}

	@Override
	public List<ConsultOrderDoctorPO> getOrderByUser(int userId,String id) {
		Query<ConsultOrderDoctorPO> uq=dsForRW.createQuery(ConsultOrderDoctorPO.class)
				.filter("comsultOrderId", id).order("status,-createTime");
		return uq.asList();
	}

	@Override
	public boolean updateOrderState(String ids, String id) {
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(ids));
		query.put("comsultOrderId", id);
		DBObject update = new BasicDBObject();
		update.put("status", OrderEnum.OrderStatus.待支付.getIndex());
		WriteResult wr = dsForRW.getDB().getCollection("t_consult_order_doctor").update(query,
				new BasicDBObject("$set", update));
		if (wr != null && wr.isUpdateOfExisting()) {
			return true;
		}
		return false;
	}

	@Override
	public ConsultOrderDoctorPO getOrderByUserById(int doctorId, String id) {
		Query<ConsultOrderDoctorPO> uq=dsForRW.createQuery(ConsultOrderDoctorPO.class)
				.filter("doctorId", String.valueOf(doctorId)).filter("comsultOrderId", id);
		return uq.get();
	}

	@Override
	public void colseOrderState(String ids, String id) {
		DBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(ids));
        query.put("comsultOrderId",id);
        DBObject update = new BasicDBObject();
        update.put("status",OrderEnum.OrderStatus.已完成.getIndex());
        dsForRW.getDB().getCollection("t_consult_order_doctor").update(query, new BasicDBObject("$set", update));
	}
}
