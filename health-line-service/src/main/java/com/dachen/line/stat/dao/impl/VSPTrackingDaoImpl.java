package com.dachen.line.stat.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.line.stat.dao.IVSPTrackingDao;
import com.dachen.line.stat.entity.vo.VSPTracking;

@Repository
public class VSPTrackingDaoImpl  extends NoSqlRepository implements IVSPTrackingDao {


	@Override
	public List<VSPTracking> getVSPTrackingList(String column, Object sourceId) {
		List<VSPTracking> result = new ArrayList<VSPTracking>();
		Query<VSPTracking> uq = dsForRW.createQuery(VSPTracking.class).filter(column, sourceId);//查询搜有的数据
		result = uq.asList();
		return result;
	}



	@Override
	public void insertVSPTracking(VSPTracking trace) {
		dsForRW.insert(trace);
		
	}



	@Override
	public List<Integer> getTrackListByOrderId(String orderId) {
		List<Integer> listid = new ArrayList<Integer>();
		Query<VSPTracking> uq = dsForRW.createQuery(VSPTracking.class).filter("orderId", orderId);
		if(uq.asList().size()>0){
			for (VSPTracking vspTracking : uq) {
				listid.add(vspTracking.getCode());
			}
		}
		return listid;
	}
}
