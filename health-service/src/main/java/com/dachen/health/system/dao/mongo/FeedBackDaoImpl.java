package com.dachen.health.system.dao.mongo;

import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.system.dao.IFeedBackDao;
import com.dachen.health.system.entity.param.FeedBackQuery;
import com.dachen.health.system.entity.po.FeedBack;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FeedBackDaoImpl extends NoSqlRepository implements IFeedBackDao{

	@Autowired
	private UserRepository userRepository;

	@Override
	public void save(FeedBack feedBack) {
	 dsForRW.save(feedBack);
	}

	@Override
	public PageVO queryFeedBack(FeedBackQuery feedBackQuery) {
				DBCollection dbCollection = dsForRW.getDB().getCollection(
						"s_feed_back");
				BasicDBObject q = new BasicDBObject();
				if(feedBackQuery.getUserId()!=null)
				{
				q.put("userId", feedBackQuery.getUserId());
				}
				if (0 != feedBackQuery.getStartTime())
				{
					q.put("createTime", new BasicDBObject("$gte", feedBackQuery.getStartTime()));
				}
				if (0 != feedBackQuery.getEndTime())
				{
					q.put("createTime", new BasicDBObject("$lte", feedBackQuery.getEndTime()));
				}
				long total = dbCollection.count(q);
				java.util.List<DBObject> pageData = Lists.newArrayList();
				DBCursor cursor;
				if(feedBackQuery.getPageIndex()!=null)
				{
				 cursor = dbCollection.find(q).sort(new BasicDBObject("createTime", -1)).skip(feedBackQuery.getPageIndex() * feedBackQuery.getPageSize())
						.limit(feedBackQuery.getPageSize());
				}
				else
				{
					cursor = dbCollection.find(q).sort(new BasicDBObject("createTime", -1));
					feedBackQuery.setPageIndex(0);
					feedBackQuery.setPageSize(feedBackQuery.getPageSize());
				}
				while (cursor.hasNext()) {
					BasicDBObject dbObj = (BasicDBObject) cursor.next();
					if(dbObj.containsField("userId")&&userRepository.getUser(dbObj.getInt("userId"))!=null)
					{
					dbObj.put("userName",userRepository.getUser(dbObj.getInt("userId")).getName());
					dbObj.put("userTypeTiltle",UserType.getEnum(userRepository.getUser(dbObj.getInt("userId")).getUserType()).getTitle());
					}
				
					pageData.add(dbObj);
				}
				
				 
				
				return new PageVO(pageData, total, feedBackQuery.getPageIndex(),feedBackQuery.getPageSize());
	}

	@Override
	public FeedBack getFeedBackById(String id) {
		Query<FeedBack> query = dsForRW.createQuery(FeedBack.class).field("_id").equal(new ObjectId(id));
		FeedBack feedBack= query.get();
		if(feedBack.getUserId()!=null&&userRepository.getUser(feedBack.getUserId())!=null)
		{
		feedBack.setUserName(userRepository.getUser(feedBack.getUserId()).getName());
		feedBack.setUserTypeTiltle(UserType.getEnum(userRepository.getUser(feedBack.getUserId()).getUserType()).getTitle());
		}
		return feedBack;
	}
	
}
