package com.dachen.line.stat.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.line.stat.dao.IServiceImageDao;
import com.dachen.line.stat.entity.vo.ServiceImage;
import com.dachen.line.stat.util.ConfigUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Repository
public class ServiceImageDaoImpl  extends NoSqlRepository implements IServiceImageDao {

	@Override
	public List<ServiceImage> getServiceImageList(String column,
			Object sourceId) {
		List<ServiceImage> result = new ArrayList<ServiceImage>();
		Query<ServiceImage> uq = dsForRW.createQuery(ServiceImage.class).filter(column, sourceId);//查询搜有的数据
		result = uq.asList();
		return result;
	}

	@Override
	public List<String> getServiceImageStringList(String column,
			Object sourceId) {
		List<String> imageStrs = new ArrayList<String>();
		List<ServiceImage> images =  getServiceImageList( column,sourceId);
		if(ConfigUtil.checkCollectionIsEmpty(images))
		{	
			for(ServiceImage image :images)
			{	
				imageStrs.add(image.getImageId());
			}
		}
		return imageStrs;
	}



	@Override
	public void updateUserServiceImage(String id, String content) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteUserServiceImage(String ServiceImageId) {
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(ServiceImageId));
		dsForRW.getDB().getCollection("v_service_image").remove(query);
	}

	@Override
	public Object insertUserServiceImage(ServiceImage ServiceImage) {
		dsForRW.insert(ServiceImage);
		return null;
	}
	
	
	

}
