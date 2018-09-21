package com.dachen.line.stat.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.line.stat.dao.ICheckResultsDao;
import com.dachen.line.stat.dao.IServiceImageDao;
import com.dachen.line.stat.entity.vo.CheckResults;
import com.dachen.line.stat.util.ConfigUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Repository
public class CheckResultsDaoImpl  extends NoSqlRepository implements ICheckResultsDao {

	@Autowired
   private IServiceImageDao serviceDao;
	

	@Override
	public void updateCheckResults(String id, String content) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteCheckResults(String CheckResultsId) {
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(CheckResultsId));
		dsForRW.getDB().getCollection("v_check_results").remove(query);
	}

	@Override
	public Object insertCheckResults(CheckResults checkResults) {
		dsForRW.insert(checkResults);
		return null;
	}

	@Override
	public List<String> getCheckResultsStringList(String column, Object sourceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CheckResults> getCheckResultsList(String column, Object sourceId) {
		List<CheckResults> results = new ArrayList<CheckResults>();
		Query<CheckResults> uq = dsForRW.createQuery(CheckResults.class).filter(column, sourceId);//查询搜有的数据
		results = uq.asList();
		return results;
	}


	@Override
	public void insertBatchCheckResults(List<CheckResults> CheckResults) {
		if(ConfigUtil.checkCollectionIsEmpty(CheckResults))
		{	
			for(CheckResults result:CheckResults)
			{	
				insertCheckResults(result);
			}
		}
	}

	
	
}
