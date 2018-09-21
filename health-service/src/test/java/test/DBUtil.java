package test;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class DBUtil {
	private static Datastore dsForHealth;
    private static Datastore dsForTigase;
	
	private static String dbName = "health";
	
	private static String ip = "192.168.3.7";
	
	private static int port = 27017;
	
	
	public static void initDb() throws Exception
	{
		MongoClient mongoClient = new MongoClient(ip,port);
		
		Morphia morphia = new Morphia();
//		morphia.mapPackage("com.dachen.health.base.entity", false);
		dsForHealth = morphia.createDatastore(mongoClient, "health");
		dsForTigase = morphia.createDatastore(mongoClient, "tigase");
	}
		

	public static void main(String... args)  throws Exception{

		initDb();
		
		DBCollection db =dsForHealth.getDB().getCollection("user");
		DBCursor dbCursor = db.find();
		while(dbCursor.hasNext())
		{
			DBObject obj =dbCursor.next(); 
//			String domain = obj.get("domain").toString();
//			String password = obj.get("password").toString();
			int id =Integer.valueOf(obj.get("_id").toString());
			
//			if(domain==null || !domain.equals("192.168.3.7"))
//			{
//				continue;
//			}
//			System.out.println(user_id);
			try
			{
				
//				int id = Integer.valueOf(user_id.substring(0, user_id.indexOf("@")));
				if(id>10000 && id<100000)
				{
					System.out.println(id);
//					db.remove(new BasicDBObject("user_id",user_id));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw e;
			}
		}
	}

}
