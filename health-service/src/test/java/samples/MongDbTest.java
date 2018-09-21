package samples;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.dachen.health.group.entity.po.Member;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongDbTest {
	private static Datastore dsForTigase;
	
	public static void main(String[] args) throws Exception {
		init();
		
		ObjectId roomId = new ObjectId("557ea5912b810e86f423a1f3");
		int userId= 100114;
		
		
		if(isCreator(roomId,100114))
		{
			return;
		}
//		Member member = (Member) getMember(roomId,Integer.valueOf(userId));
//		member.setRole(2);
//		dsForTigase.save(member);
		
		DBCollection dbCollection = dsForTigase.getCollection(Member.class);
		DBObject condition = new BasicDBObject("roomId", roomId).append("userId", userId);
		
//		DBCursor cursor =dbCollection.find(condition);
//		while (cursor.hasNext()) 
//		{
//			System.out.println(cursor.next());
//		}
		
		DBObject updateObj =new BasicDBObject("$set",new BasicDBObject("nickname", "123456789"));
		dbCollection.update(condition, updateObj); 
	}
	private static boolean isCreator(ObjectId roomId, int userId)
	{
		DBObject q = new BasicDBObject().append("roomId", roomId).append("role", 1);
		
		DBCursor cursor = dsForTigase.getCollection(Member.class).find(q);
		while (cursor.hasNext()) {
			return true;
		}
		
		return false;
	}
	
	
	
	private static void init() throws Exception
	{
		MongoClient mongoClient = new MongoClient("localhost",27017);
		
		dsForTigase = getMorphia().createDatastore(mongoClient, "tigase");
	}
	
	private static Morphia getMorphia()
	{
		Morphia morphia = new Morphia();
		// 手动加载
		if (0 == morphia.getMapper().getMappedClasses().size()) {

			morphia.map(com.dachen.health.group.entity.po.Room.class);
//			morphia.map(com.dachen.health.group.entity.po.Room.Member.class);
//			morphia.map(com.dachen.health.group.entity.po.Room.Notice.class);
			morphia.map(com.dachen.health.commons.vo.User.class);
		}
		return morphia;
	}
}
