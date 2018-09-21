package test;

import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;

import com.dachen.health.commons.vo.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceOutput;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class TestSync {
	private static Datastore ds;

	public static Datastore ds2;

	static {
		try {
			Mapper mapper = new Mapper();
			mapper.addMappedClass(User.class);

			// MongoClient mongoClient = new MongoClient(Arrays.asList(new
			// ServerAddress("192.168.1.240", 28017)));
			MongoClient mongoClient = new MongoClient(Arrays.asList(new ServerAddress("192.168.1.240", 28017)));

			String dbName = "health";

			ds = new Morphia(mapper).createDatastore(mongoClient, dbName);

			MongoClient mongoClient2 = new MongoClient(Arrays.asList(new ServerAddress("121.37.30.15", 28017)));

			String dbName2 = "health";

			ds2 = new Morphia(mapper).createDatastore(mongoClient2, dbName2);
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) throws Exception {
		// DBCursor cursor = ds.getDB().getCollection("u_login").find(new
		// BasicDBObject("_id", 1));
		// while (cursor.hasNext())
		// System.out.println(cursor.next());
		// LoginLog t=new LoginLog();
		// long count =
		// ds.createQuery(LoginLog.class).field("_id").equal(1).countAll();
		// if(0==count){
		// ds.save(t);
		// }else{
		// ds.up
		// }
		// System.out.println(count);
		// tokenStatus
		// serialStatus
		// DBCursor cursor = ds.getDB().getCollection("user").find();
		// while (cursor.hasNext()) {
		// DBObject obj = cursor.next();
		// BasicDBObject o = new BasicDBObject("level", 1);
		// o.put("vip", 0);
		//
		// ds.getDB().getCollection("user").update(obj, new
		// BasicDBObject("$set", o));
		// }
		// saveConfig();
		// ds.save(TestResume.getResume());
		// ResumeVO resume = ds.find(ResumeVO.class).get();
		// System.out.println(resume);
		// gb();
		// gb2();
		// Collect collect = new Collect();
		// collect.setMsgId("dfadfasdf");
		// Key<Collect> a = ds.save(collect);
		// System.out.println(JSON.toJSON(a.getId().toString()));

		// Jedis jedis = new Jedis("121.37.30.15", 6380);
		//
		// Query<User> query =
		// ds.createQuery(User.class).field(MongoOperator.ID).equal(100729);
		// User user = query.get();
		//
		// jedis.set("user_" + user.getUserId(), JSON.toJSONString(user));
		//
		// Query<Fans> q = ds.createQuery(Fans.class);
		// q.field("userId").equal(100729);
		//
		// jedis.set("fans_" + user.getUserId(), JSON.toJSONString(q.asList()));
		//
		// Query<Friends> q2 = ds.createQuery(Friends.class);
		// q2.field("userId").equal(100729);
		// q2.or(q.criteria("status").equal(Friends.Status.Attention),
		// q2.criteria("status").equal(Friends.Status.Friends));
		//
		// jedis.set("atts_" + user.getUserId(),
		// JSON.toJSONString(q2.asList()));

		// jedis.close();

	}

	public static void gb2() {
		StringBuffer sb = new StringBuffer();
		sb.append(" function() { ");
		sb.append(" 	emit({ ");
		sb.append(" 		id : this.id ");
		sb.append(" 	}, { ");
		sb.append(" 		count : this.count ");
		sb.append(" 	}); ");
		sb.append(" } ");
		String map = sb.toString();
		sb = new StringBuffer();
		sb.append(" function (key, values) { ");
		sb.append(" 	var total = 0; ");
		sb.append(" 	for (var i = 0; i < values.length; i++) { ");
		sb.append(" 		total += values[i].count; ");
		sb.append(" 	} ");
		sb.append(" 	return total; ");
		sb.append(" } ");
		String reduce = sb.toString();
		DBCollection inputCollection = ds.getDB().getCollection("gift");

		DBObject query = new BasicDBObject("messageId", new ObjectId("53d544a2c443f0470ea4d0cc"));

		MapReduceOutput mapReduceOutput = inputCollection.mapReduce(map, reduce, "resultCollection", query);
		DBCollection resultColl = mapReduceOutput.getOutputCollection();
		DBCursor cursor = resultColl.find();
		System.out.println(map);
		System.out.println(reduce);
		while (cursor.hasNext()) {

			DBObject tObj = cursor.next();

			DBObject dbObj = (BasicDBObject) tObj.get("_id");
			dbObj.put("count", tObj.get("value"));

			System.out.println(dbObj);
		}
	}

	public static void gb() {
		StringBuffer sb = new StringBuffer();
		sb.append(" function() { ");
		sb.append(" 	emit({ ");
		sb.append(" 		userId : this.userId, ");
		sb.append(" 		nickname : this.nickname ");
		sb.append(" 	}, { ");
		sb.append(" 		price : this.price, ");
		sb.append(" 		count : this.count ");
		sb.append(" 	}); ");
		sb.append(" } ");

		StringBuffer sb2 = new StringBuffer();
		sb2.append(" function (key, values) { ");
		sb2.append(" 	var result = 0; ");
		sb2.append(" 	for (var i = 0; i < values.length; i++) { ");
		sb2.append(" 		result += values[i].price * values[i].count; ");
		sb2.append(" 	} ");
		sb2.append(" 	return result; ");
		sb2.append(" } ");

		DBCollection inputCollection = ds.getDB().getCollection("gift");
		String map = sb.toString();
		String reduce = sb2.toString();
		DBObject query = new BasicDBObject("messageId", new ObjectId("53d544a2c443f0470ea4d0cc"));

		MapReduceOutput mapReduceOutput = inputCollection.mapReduce(map, reduce, "resultCollection", query);
		DBCollection resultColl = mapReduceOutput.getOutputCollection();
		DBCursor cursor = resultColl.find();
		System.out.println(map);
		System.out.println(reduce);
		while (cursor.hasNext()) {
			System.out.println(cursor.next());
		}
	}

	public static void saveConfig() {
		BasicDBObject joA = new BasicDBObject();
		joA.put("disableVersion", "");
		joA.put("version", "");
		joA.put("versionRemark", "");
		joA.put("message", "");

		BasicDBObject joI = new BasicDBObject();
		joI.put("disableVersion", "");
		joI.put("version", "");
		joI.put("versionRemark", "");
		joI.put("message", "");

		BasicDBObject data = new BasicDBObject();
		data.put("android", joA);
		data.put("ios", joI);

		data.put("apiUrl", "http://192.168.1.240/api/v1/");
		data.put("uploadUrl", "http://192.168.1.240/");
		data.put("downloadUrl", "http://192.168.1.240/");
		data.put("downloadAvatarUrl", "http://192.168.1.240/");

		data.put("XMPPHost", "");
		data.put("XMPPDomain", "");
		data.put("ftpHost", "");
		data.put("ftpUsername", "");
		data.put("ftpPassword", "");

		ds.getDB().getCollection("config").insert(data);
	}

	public static void delete() {
		int[] userIds = { 100830, 100831, 100832, 100827, 0 };
		for (int val : userIds) {
			Query<User> q = ds.createQuery(User.class).field("userId").equal(val);
			ds.delete(q);
		}
	}

 
}
