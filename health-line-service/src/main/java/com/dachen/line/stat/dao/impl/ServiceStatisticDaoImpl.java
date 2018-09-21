package com.dachen.line.stat.dao.impl;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.line.stat.dao.IServiceStatisticDao;
import com.dachen.line.stat.entity.vo.ServiceStatistic;
import com.dachen.line.stat.util.Constant;
import com.dachen.util.MongodbUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Repository
public class ServiceStatisticDaoImpl extends NoSqlRepository implements
		IServiceStatisticDao {

	/**
	 * 获取统计服务人数
	 */
	public ServiceStatistic getServiceStatisticService() {
		ServiceStatistic object = new ServiceStatistic();
		DBObject obj = dsForRW.getDB().getCollection("v_service_statistic")
				.findOne();

		if (obj != null) {
			int dbtotalReceptionNum = MongodbUtil.getInteger(obj,
					"totalReceptionNum");
			object.setTotalReceptionNum(dbtotalReceptionNum);
		}
		return object;
	}

	/**
	 * 更新接待人数字段
	 */
	public void updateTotalReceptionNum() {
		int dbtotalReceptionNum = 0;
		DBObject query = new BasicDBObject();
		DBObject obj = dsForRW.getDB().getCollection("v_service_statistic")
				.findOne();
		String id =null;
		if (obj != null) {
			dbtotalReceptionNum = MongodbUtil.getInteger(obj,
					"totalReceptionNum");
			 id = MongodbUtil.getString(obj,"_id");
			query.put("_id",  new ObjectId(id));
		}
		else
		{	
//			dsForRW.getDB().getCollection("v_service_statistic").
		}
		BasicDBObject update = new BasicDBObject();
		int  temp = dbtotalReceptionNum+Integer.parseInt(Constant.totalReceptionNum());
		System.out.println("需要更新到数据库中的数据="+temp);
		//update.put("_id",  id);
		update.put("totalReceptionNum", temp);
		if (!update.isEmpty()) {
			WriteResult result=	dsForRW.getDB().getCollection("v_service_statistic")
					.update(query, new BasicDBObject("$set", update));
			System.out.println(result.getUpsertedId());
		}
	}
}
