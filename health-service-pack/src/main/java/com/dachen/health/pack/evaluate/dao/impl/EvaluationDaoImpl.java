package com.dachen.health.pack.evaluate.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.pack.evaluate.dao.IEvaluationDao;
import com.dachen.health.pack.evaluate.entity.Evaluation;
import com.dachen.health.pack.evaluate.entity.vo.EvaluationStatVO;
import com.dachen.util.MongodbUtil;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Repository
public class EvaluationDaoImpl extends NoSqlRepository implements IEvaluationDao {
	
	@Resource
	private IBaseDataDao baseDataDao;
	
	public Evaluation add(Evaluation eva) {
		String id = dsForRW.insert(eva).getId().toString();
		return dsForRW.createQuery(Evaluation.class).field("id").equal(new ObjectId(id)).get();
	}
	
	public Evaluation getByOrderId(Integer orderId) {
		return dsForRW.createQuery(Evaluation.class).field("orderId").equal(orderId).get();
	}
	
	public List<EvaluationStatVO> getEvaluationStatVO(Integer doctorId) {
		DBCollection collection = dsForRW.getDB().getCollection("t_evaluation");
        // 匹配条件
        DBObject matchFields = new BasicDBObject();
        matchFields.put("doctorId", doctorId);
        DBObject match = new BasicDBObject("$match", matchFields);

        // 查询字段
        BasicDBObject fields = new BasicDBObject();
        fields.put("_id", 0);
        fields.put("itemIds", 1);
        DBObject project = new BasicDBObject("$project", fields);

        // 切割字段，为数组
        DBObject unwind = new BasicDBObject("$unwind", "$itemIds");

        // 分组条件
        DBObject groupFields = new BasicDBObject("_id", "$itemIds");
        groupFields.put("count", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        
        // 排序条件
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("count",-1));

        List<DBObject> pipeline = new ArrayList<DBObject>();
        pipeline.add(match);
        pipeline.add(project);
        pipeline.add(unwind);
        pipeline.add(group);
        pipeline.add(sort);

        AggregationOutput output = collection.aggregate(pipeline);

        Iterator<DBObject> it = output.results().iterator();
        
        List<EvaluationStatVO> list = new ArrayList<EvaluationStatVO>();
        while (it.hasNext()) {
            DBObject obj = it.next();

            EvaluationStatVO vo = new EvaluationStatVO();
            vo.setId(MongodbUtil.getString(obj, "_id"));
            vo.setName(baseDataDao.getEvaluationItem(vo.getId()).getName());
            vo.setCount((Integer) obj.get("count"));
            list.add(vo);
        }
        
        return list;
	}
	
	public List<Evaluation> getEvaluations(Integer doctorId) {
		return dsForRW.createQuery(Evaluation.class).filter("doctorId", doctorId).order("-createTime").asList();
	}
}
