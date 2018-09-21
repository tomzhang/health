package test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.dachen.util.DateUtil;

public class S6 {
	public static void join(Object roomId) throws Exception {
		DBCollection dbCollection = Samples1.dsForRW.getDB().getCollection("s_room_his");
		int userId = 123456;
		DBObject q = new BasicDBObject("_id", userId);

		BasicDBObject dbObj = new BasicDBObject();
		dbObj.put("roomId", roomId);
		dbObj.put("time", DateUtil.currentTimeSeconds());
		dbObj.put("type", new Random().nextInt(3) + 1);

		if (0 == dbCollection.find(q).count()) {
			DBObject o = new BasicDBObject();
			o.put("_id", userId);
			o.put("list", new DBObject[] { dbObj });
			o.put("roomCount", 1);

			dbCollection.insert(o);
		} else {
			DBObject o = new BasicDBObject();
			o.put("$addToSet", new BasicDBObject("list", dbObj));
			o.put("$inc", new BasicDBObject("roomCount", 1));

			dbCollection.update(q, o);
		}
	}

	public static void main(String[] args) throws Exception {
		// for (int i = 10000; i < 10004; i++) {
		// join(i);
		// }

		// BasicDBObject o = new BasicDBObject("_id", 123456);
		// o.put("list.type", 0);
		// DBCursor cursor =
		// Samples1.dsForRW.getDB().getCollection("s_room_his").find(o);
		// while (cursor.hasNext()) {
		// System.out.println(cursor.next());
		// }

		List<Object> objList = Lists.newArrayList();
		int userId = 123456;
		int type = 1;

		DBCollection coll = Samples1.dsForRW.getDB().getCollection("s_room_his");
		DBObject project = new BasicDBObject("$project", new BasicDBObject("list", 1));
		DBObject match = new BasicDBObject("$match", new BasicDBObject("_id", userId));
		DBObject unwind = new BasicDBObject("$unwind", "$list");
		DBObject matchChild = new BasicDBObject("$match", new BasicDBObject("list.type", type));
		List<DBObject> pipeline = Arrays.asList(project, match, unwind, matchChild);

		AggregationOutput output = coll.aggregate(pipeline);
		output.results().forEach(dbObj -> {
			objList.add(dbObj.get("list"));
		});

		for (Object obj : objList) {
			System.out.println(obj);
		}

		// long a = 1385827200 - 1370016000;
		// System.out.println(a/2592000);

		// FileInputStream in = new FileInputStream("E:\\陶文聪.mht");
		// ResumeV2 resume = CVReader.newReader(1, in).read();
		//
		// System.out.println(resume);
		//
		// for (Edu edu : resume.getE())
		// edu.setId(ObjectId.get());
		// for (Work work : resume.getW())
		// work.setId(ObjectId.get());
		// for (Project project : resume.getProjectList())
		// project.setId(ObjectId.get());
		// resume.setResumeName("我的简历");
		// resume.setUserId(100000);
		// resume.setCreateTime(System.currentTimeMillis() / 1000);
		// resume.setModifyTime(resume.getCreateTime());
		// Samples1.dsForRW.save(resume);

		// DBObject q = new BasicDBObject();
		// q.put("members.userId", 1001021);
		// q.put("_id", new ObjectId("54d49a846aa3b6ef83b60a46"));
		//
		// System.out.println(Samples1.dsForRW.getDB().getCollection("s_room").count(q));

		// q.put("id", new ObjectId("548127ef4826cd6abf8f0d0a"));

		// DBObject o = new BasicDBObject();
		// o.put("$pull", new BasicDBObject("members", new
		// BasicDBObject("userId", 10000001)));
		//
		// Samples1.dsForRW.getDB().getCollection("s_room").update(q, o);

	}

}
