package com.dachen.health.group.common.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.group.common.dao.ILabelDao;
import com.dachen.health.group.common.entity.param.LabelParam;
import com.dachen.health.group.common.entity.po.Label;
import com.dachen.health.group.common.entity.vo.LabelVO;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**|
 * 
 * @author pijingwei
 * @date 2015/8/13
 */
@Repository
public class LabelDaoImpl extends NoSqlRepository implements ILabelDao {

	@Override
	public void save(Label label) {
		dsForRW.save(label);
	}

	@Override
	public void update(Label label) {
		DBObject query = new BasicDBObject();
		DBObject update = new BasicDBObject();
		
		if(!StringUtil.isEmpty(label.getName())) {
			update.put("name", label.getName());
		}
		
		if(!StringUtil.isEmpty(label.getDescription())) {
			update.put("description", label.getDescription());
		}
		
		query.put("_id", new ObjectId(label.getId()));
		dsForRW.getDB().getCollection("c_label").update(query, new BasicDBObject("$set",update));
	}

	@Override
	public void delete(String... ids) {
		BasicDBList values = new BasicDBList();
		BasicDBObject in = new BasicDBObject();
		for (String id : ids) {
			values.add(new ObjectId(id));
		}
		in.put("$in", values);
		
		dsForRW.getDB().getCollection("c_label").remove(new BasicDBObject("_id", in));
	}

	@Override
	public PageVO search(LabelParam param) {
		DBObject query = new BasicDBObject();
		
		if(!StringUtil.isEmpty(param.getName())) {
			query.put("name", param.getName());
		}
		
		if(!StringUtil.isEmpty(param.getDescription())) {
			query.put("description", param.getDescription());
		}
		
		if(!StringUtil.isEmpty(param.getRelationId())) {
			query.put("relationId", param.getRelationId());
		}
		
		if (param.getStartTime() != null || param.getEndTime() != null) {
			DBObject timeQuery = new BasicDBObject();
	        if (param.getStartTime() != null) {
	            timeQuery.put("$gte", param.getStartTime());
	        }
	        if (param.getEndTime() != null) {
	            timeQuery.put("$lt", param.getEndTime());
	        }
	        query.put("creatorDate", timeQuery);
        }
		
		DBCursor cursor = dsForRW.getDB().getCollection("c_label").find(query);
		List<LabelVO> labelList = new ArrayList<LabelVO>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			LabelVO label = new LabelVO();
			
			label.setId(obj.get("_id").toString());
			label.setName(obj.get("name").toString());
			label.setDescription(obj.get("description") == null ? "" : obj.get("description").toString());
			label.setCreator(obj.get("creator").toString());
			label.setCreatorDate(Long.valueOf(obj.get("creatorDate").toString()));
			
			labelList.add(label);
		}
		
		PageVO page = new PageVO();
        page.setPageData(labelList);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(dsForRW.getDB().getCollection("c_label").count(query));
        
        return page;
	}

	@Override
	public Label findLabelByName(Label label) {
		return dsForRW.createQuery(Label.class).field("name").equal(label.getName()).field("relationId").equal(label.getRelationId()).get();
	}

	
	
}
