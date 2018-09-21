package com.dachen.line.stat.dao.impl;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.line.stat.dao.IMonitorLogDao;
import com.dachen.line.stat.entity.vo.MonitorLog;

@Repository
public class MonitorLogDaoImpl  extends NoSqlRepository implements IMonitorLogDao {

	@Override
	public MonitorLog getMonitorLogById(String id) {
		Query<MonitorLog> query = dsForRW.createQuery(MonitorLog.class).field("_id")
				.equal(new ObjectId(id));
		return query.get();
	}
	@Override
	public Object insertUserMonitorLog(MonitorLog MonitorLog) {
		Object messagid = dsForRW.insert(MonitorLog).getId();
		
		return messagid;
	}

	@Override
	public MonitorLog getMonitorLogList(String serviceId, int code,int type) {
		Query<MonitorLog> uq = dsForRW.createQuery(MonitorLog.class).filter("serviceId",serviceId).filter("serviceCode",code).filter("type", type);// 查询下单时间大于当前时间2小时的订单
		return uq.get();
	}

}
