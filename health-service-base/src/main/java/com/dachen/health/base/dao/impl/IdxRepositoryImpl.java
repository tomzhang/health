package com.dachen.health.base.dao.impl;

import org.springframework.stereotype.Repository;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.dao.IdxRepository;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Repository
public class IdxRepositoryImpl extends NoSqlRepository implements IdxRepository{

    /**
     * </p>获取自增id</p>
     * @author fanp
     * @date 2015年8月7日
     */
    @Override
    public Integer nextPayNoIdx(int idxType) {
        DBObject obj = getIdx(idxType);
        if (obj == null)
        	return 0;
        return MongodbUtil.getInteger(obj, "num").intValue();
    }
    
    @Override
    public Integer nextIdx(int idxType) {
    	if (IdxRepository.idxType.userId == idxType) {
    		throw new ServiceException("请从auth服务获取用户ID");
    	}
    	
    	//该if语句执行一段时间后可以删除
		if (IdxRepository.idxType.userId == idxType && jedisTemplate.get(IdxRepository.JedisKey.keyUserId) != null) {
			int maxId = jedisTemplate.incr(IdxRepository.JedisKey.keyUserId).intValue();
			updateNum(idxType, maxId);
			jedisTemplate.del(IdxRepository.JedisKey.keyUserId);
		}
		DBObject obj = getIdx(idxType);
        if (obj == null)
        	return 0;
		return MongodbUtil.getInteger(obj, "num").intValue();
    }

    /**
     * </p>获取医生号</p>
     * @param idxType
     * @return
     * @author fanp
     * @date 2015年8月11日
     */
    public String nextDoctorNum(int idxType){
    	//该if语句执行一段时间后可以删除
    	if (IdxRepository.idxType.doctorNum == idxType && jedisTemplate.get(IdxRepository.JedisKey.keyDoctorNum) != null) {
    		int maxId = jedisTemplate.incr(IdxRepository.JedisKey.keyDoctorNum).intValue();
			int maxNum = maxId % 10000;
			if (maxNum > 999) {
				resetNum(idxType, maxNum);
			} else {
				updateNum(idxType, maxNum);
			}
			jedisTemplate.del(IdxRepository.JedisKey.keyDoctorNum);
    	}
    	return getDoctorNum(idxType);
    }
    
    private void updateNum(int idxType, int num) {
    	DBObject query = new BasicDBObject();
		query.put("_id", idxType);

		DBObject update = new BasicDBObject();
		update.put("$set", new BasicDBObject("num", num));

		dsForRW.getDB().getCollection("sys_idx").update(query, update);
    }

	private String getDoctorNum(int idxType) {

		DBObject obj = getIdx(idxType);

		Integer num = MongodbUtil.getInteger(obj, "num");
		Integer prefix = MongodbUtil.getInteger(obj, "prefix");
		num = num == null ? 0 : num;
		prefix = prefix == null ? 0 : prefix;

		num = resetNum(idxType, num);

		StringBuffer sb = new StringBuffer();
		sb.append("9");
		sb.append(StringUtil.format(prefix, 3));
		sb.append(StringUtil.format(num, 4));
		return sb.toString();
	}

	public String nextOrderNoIdx(int idxType) {
        DBObject obj = getIdx(idxType);
        
        Integer num = MongodbUtil.getInteger(obj, "num");
        Integer prefix = MongodbUtil.getInteger(obj, "prefix");
        num = num == null ? 0 : num;
        prefix = prefix == null ? 0 : prefix;
        
        num = resetNum(idxType, num);
        
        StringBuffer sb=new StringBuffer();
        sb.append("8");
        sb.append(StringUtil.format(prefix,3));
        sb.append(StringUtil.format(num,4));
        
        return sb.toString();
	}
	
	public String nextMsgTemplateNum(int idxType) {
        DBObject obj = getIdx(idxType);
        
        Integer num = MongodbUtil.getInteger(obj, "num");
        Integer prefix = MongodbUtil.getInteger(obj, "prefix");
        num = num == null ? 0 : num;
        prefix = prefix == null ? 0 : prefix;
        
        num = resetNum(idxType, num);
        
        StringBuffer sb=new StringBuffer();
        sb.append(StringUtil.format(num,4));
        
        return sb.toString();
	}
	
	private DBObject getIdx(int idxType) {
		return dsForRW.getDB().getCollection("sys_idx").findAndModify(
				new BasicDBObject("_id",idxType),
				null, 
				null, 
				false, 
				new BasicDBObject("$inc",new BasicDBObject("num",1)), 
				true, 
				true);
	}
	
	private Integer resetNum(int idxType, Integer num) {
		if (num > 999) {
			num = 0;

			DBObject query = new BasicDBObject();
			query.put("_id", idxType);

			DBObject update = new BasicDBObject();
			update.put("$inc", new BasicDBObject("prefix", 1));
			update.put("$set", new BasicDBObject("num", num));

			dsForRW.getDB().getCollection("sys_idx").update(query, update);
		}
		return num;
	}
}
