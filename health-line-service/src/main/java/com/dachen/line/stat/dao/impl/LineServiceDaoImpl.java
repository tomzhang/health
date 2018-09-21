package com.dachen.line.stat.dao.impl;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.entity.po.CheckSuggest;
import com.dachen.line.stat.dao.ILineServiceDao;
import com.dachen.line.stat.entity.vo.LineService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Repository
public class LineServiceDaoImpl extends NoSqlRepository implements
		ILineServiceDao {

	@Override
	public LineService getLineServiceById(String id) {
		Query<LineService> query = dsForRW.createQuery(LineService.class)
				.field("_id").equal(new ObjectId(id));
		return query.get();
	}

	@Override
	public void deleteUserLineService(String LineServiceId) {
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(LineServiceId));
		dsForRW.getDB().getCollection("v_line_service").remove(query);

	}

	@Override
	public Object insertUserLineService(LineService LineService) {
		Object messagid = dsForRW.insert(LineService).getId();

		return messagid;
	}

	@Override
	public boolean checkLineServiceByTitle(String title) {
		DBObject query = new BasicDBObject();
		query.put("title", title);

		long ss = dsForRW.createQuery(LineService.class).filter("title", title)
				.countAll();
		if (ss > 0) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 如果存在就不插入，不存在就插入
	 * @return
	 */
	@Override
	public Object insertUserLineServiceAndCheck(LineService lineService) {
		Object insertFalg = null;
		String title = null;
		if (null != lineService) {
			title = lineService.getTitle();
			if (!checkLineServiceByTitle(title)) {
				insertFalg = insertUserLineService(lineService);
			}
		}
		return insertFalg;
	}

	@Override
	public CheckSuggest getCheckSuggestById(String id) {
		Query<CheckSuggest> query = dsForRW.createQuery(CheckSuggest.class)
				.field("_id").equal(id);
		return query.get();
	}

}
