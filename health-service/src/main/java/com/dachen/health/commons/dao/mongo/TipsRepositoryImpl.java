package com.dachen.health.commons.dao.mongo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.dao.TipsRepository;
import com.dachen.health.commons.vo.Tips;
import com.dachen.util.MongodbUtil;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class TipsRepositoryImpl extends NoSqlRepository implements TipsRepository{

	public List<Tips> findAll(){
	    List<Tips> list = new ArrayList<Tips>();
	    
	    DBCursor cursor = dsForRW.getDB().getCollection("sys_tips").find();
	    while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            
            Tips tips = new Tips();
            tips.setTipsKey(MongodbUtil.getInteger(obj, "tips_key"));
            tips.setTipsValue(MongodbUtil.getString(obj, "tips_value"));
            
            list.add(tips);
        }
	    
	    return list;
	}

}
