package test;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class ConfigBuild {
	public static final String uri = "mongodb://121.37.30.25:27017";
	public static MongoClient mongoClient;

	public static void init() throws Exception {
		mongoClient = new MongoClient(new MongoClientURI(uri));

	}

	public static void main(String[] args) throws Exception {
		System.out.println(DigestUtils.md5Hex("123456"));
		// init();
		// DBObject jo = new BasicDBObject();
		// jo.put("isFirstLogin", 1);
		// jo.put("loginTime", DateUtil.currentTimeSeconds());
		// jo.put("apiVersion", "");
		// jo.put("osVersion", "");
		// jo.put("model", "");
		// jo.put("serial", "");
		// jo.put("latitude", 0);
		// jo.put("longitude", 0);
		// jo.put("location", "");
		// jo.put("address", "");
		// DBCollection dbColl =
		// mongoClient.getDB("health").getCollection("user");
		// dbColl.update(new BasicDBObject(), new BasicDBObject("$set", new
		// BasicDBObject("loginLog", jo)), false, true);

		// String s =
		// "[\"5461df5aa3108b8e792e3fbd\",\"5461df5aa3108b8e792e3fbd\"]";
		// List<ObjectId> r2 = new ObjectMapper().readValue(s,
		// TypeFactory.defaultInstance().constructCollectionType(List.class,
		// ObjectId.class));
		// System.out.println(r2);
		//
		// for (ObjectId a : r2) {
		// System.out.println(a);
		// }
		// List<MyBean> result = mapper.readValue(src,
		// TypeFactory.collectionType(ArrayList.class, MyBean.class));
	}

	public static void tt() throws Exception {
		// DBCollection dbCollection =
		// TestMorphia.dsForRW.getDB().getCollection("config");
		// DBObject dbObject = dbCollection.findOne();
		// System.out.println(dbObject);
		//
		MongoClient mongoClient = new MongoClient(new MongoClientURI(uri));
		// mongoClient.getDB("health").getCollection("config").save(dbObject);

		DBCollection dbCollection = mongoClient.getDB("health").getCollection(
				"config");

		// DBObject entity = new BasicDBObject();
		// entity.put("apiUrl", "http://192.168.1.240/api/v1/");
		// entity.put("uploadUrl", "http://192.168.1.240/");
		// entity.put("downloadUrl", "http://192.168.1.240/");
		// entity.put("downloadAvatarUrl", "http://192.168.1.240/");

		DBObject entity = new BasicDBObject();
		entity.put("apiUrl", "http://121.37.30.25:8090/api/v1/");
		entity.put("uploadUrl", "http://121.37.30.25/");
		entity.put("downloadUrl", "http://121.37.30.25/");
		entity.put("downloadAvatarUrl", "http://121.37.30.25/");
		entity.put("freeswitch", "121.37.30.25");

		DBObject q = new BasicDBObject();
		q.put("_id", new ObjectId("53d0d7c945c9af43903a0c66"));

		DBObject o = new BasicDBObject();
		o.put("$set", entity);

		dbCollection.update(q, o);
	}
}
