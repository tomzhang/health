package test;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.QueryBuilder;
//import com.dachen.commons.cv.CVReader;
import com.dachen.util.DateUtil;

public class Samples1 {
	public static Datastore dsForRW;
	public static final Morphia morphia;

	static {
		morphia = new Morphia();
		morphia.mapPackage("com.dachen.health.commons.vo", true);
		try {
			MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://114.119.6.150:27017"));
			dsForRW = morphia.createDatastore(mongoClient, "imapi");
			dsForRW.ensureIndexes();
			dsForRW.ensureCaps();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void add() {
		DBObject obj = new BasicDBObject();
		obj.put("examList", new Object[] { 1, 2, 3 });
		obj.put("results", new Object[] {});

		dsForRW.getDB().getCollection("test").save(obj);
	}

	public static void addScore() {
		DBObject q = new BasicDBObject();
		q.put("_id", new ObjectId("546ef79aa310fa8807842029"));
		q.put("results.examId", 1);

		DBObject score = new BasicDBObject();
		score.put("userId", 1);
		score.put("score", 90);
		score.put("time", DateUtil.currentTimeSeconds());

		DBObject score1 = new BasicDBObject();
		score1.put("userId", 2);
		score1.put("score", 80);
		score1.put("time", DateUtil.currentTimeSeconds());

		DBObject score2 = new BasicDBObject();
		score2.put("userId", 3);
		score2.put("score", 101);
		score2.put("time", DateUtil.currentTimeSeconds());

		DBObject o = new BasicDBObject();
		o.put("$addToSet", new BasicDBObject("results.$.scores", new BasicDBObject("$each", new Object[] { score, score1, score2 })));

		DBCollection coll = dsForRW.getDB().getCollection("a_aft");
		coll.update(q, o);
	}

	public static void dddd() {
		QueryBuilder qb = QueryBuilder.start();
		qb.and("_id").is(new ObjectId("546d9bd74826e94f8975c50e"));
		qb.and("results.examId").is(1);

		DBObject score = new BasicDBObject();
		score.put("userId", 12123123);
		score.put("score", 1000);
		score.put("time", DateUtil.currentTimeSeconds());

		// ObjectId id = new ObjectId("546d9bd74826e94f8975c50e");
		// DBObject q = new BasicDBObject("_id", id);
		// q.put("results.examId", 3);

		DBObject o = new BasicDBObject("$inc", new BasicDBObject("results.$.score", 1000));
		o.put("$addToSet", new BasicDBObject("results.$.scores", new BasicDBObject("$each", new Object[] { score })));
		dsForRW.getDB().getCollection("test").update(qb.get(), o);
	}

	public static int getAvgScore(ObjectId aftId, int examId, int score) {
		int total = score;

		DBCollection coll = dsForRW.getDB().getCollection("a_aft");
		DBObject project = new BasicDBObject("$project", new BasicDBObject("results", 1));
		DBObject match = new BasicDBObject("$match", new BasicDBObject("_id", aftId));
		DBObject unwind = new BasicDBObject("$unwind", "$results");
		DBObject matchChild = new BasicDBObject("$match", new BasicDBObject("results.examId", examId));
		DBObject unwindChild = new BasicDBObject("$unwind", "$results.scores");
		List<DBObject> pipeline = Arrays.asList(project, match, unwind, matchChild, unwindChild);
		AggregationOutput output = coll.aggregate(pipeline);
		List<DBObject> objList = Lists.newArrayList(output.results());

		for (DBObject obj : objList) {
			DBObject results = (DBObject) obj.get("results");
			BasicDBObject scores = (BasicDBObject) results.get("scores");
			total += scores.getInt("score");
		}

		int avg = total / (objList.size() + 1);
		System.out.println(total);
		System.out.println(objList.size() + 1);
		System.out.println(total / objList.size() + 1);
		return avg;
	}

	public static boolean getSumAnswer(ObjectId aftId) {
		DBCollection coll = dsForRW.getDB().getCollection("a_aft");
		DBObject project = new BasicDBObject("$project", new BasicDBObject("results", 1));
		DBObject match = new BasicDBObject("$match", new BasicDBObject("_id", aftId));
		DBObject unwind = new BasicDBObject("$unwind", "$results");
		List<DBObject> pipeline = Arrays.asList(project, match, unwind);
		AggregationOutput output = coll.aggregate(pipeline);
		List<DBObject> objList = Lists.newArrayList(output.results());

		int completed = 0;
		for (DBObject obj : objList) {
			BasicDBObject results = (BasicDBObject) obj.get("results");
			int status = results.getInt("status");
			if (2 == status) {
				completed++;
			}
		}

		return objList.size() == completed;
	}

	public static void main(String[] args) throws Exception {
		// int score = getAvgScore(new ObjectId("546ef79aa310fa8807842029"), 1,
		// 0);
		// System.out.println(score);

		// String s =
		// "[{\"questionId\" : 1,\"correct\" : 1,\"score\" : 10,\"answer\" : \"0\"},{\"questionId\" : 2,\"correct\" : 1,\"score\" : 20,\"answer\" : \"0\"}]";
		// Object obj = com.mongodb.util.JSON.parse(s);
		// System.out.println(obj);
		// DBObject o = new BasicDBObject("_id", new BasicDBObject("$in", new
		// Object[] { new ObjectId("5473f7f3a7c4ea8652fa21c0") }));
		// DBObject o = new BasicDBObject("_id", new
		// ObjectId("5473f7f3a7c4ea8652fa21c0"));
		// DBCursor cursor = dsForRW.getDB().getCollection("a_aft").find(o, new
		// BasicDBObject("results.answer", 0));
		// // DBCursor cursor = dsForRW.getDB().getCollection("a_aft").find(o);
		// while (cursor.hasNext()) {
		// System.out.println(cursor.next());
		// }
		// cursor.close();
		// getSumAnswer(new ObjectId("5473f7f3a7c4ea8652fa21c0"));

//		FileInputStream in = new FileInputStream("C:\\Users\\Administrator\\Desktop\\闄舵枃鑱�.mht");
//		ResumeV2 resume = CVReader.newReader(1, in).read();
//
//		System.out.println(resume);
//
//		resume.setResumeName("鎴戠殑绠�鍘�");
//		resume.setUserId(300000);
//		resume.setCreateTime(System.currentTimeMillis() / 1000);
//		resume.setModifyTime(resume.getCreateTime());
//		dsForRW.save(resume);

	}

	public static void mapReduce() {
		List<DBObject> objList = Lists.newArrayList();

		StringBuffer sbMap = new StringBuffer();
		sbMap.append(" function() { ");
		sbMap.append(" 	emit({ ");
		sbMap.append(" 		id : this.id ");
		sbMap.append(" 	}, { ");
		sbMap.append(" 		count : this.count ");
		sbMap.append(" 	}); ");
		sbMap.append(" } ");

		StringBuffer sbReduce = new StringBuffer();
		sbReduce.append(" function (key, values) { ");
		sbReduce.append(" 	var total = 0; ");
		sbReduce.append(" 	for (var i = 0; i < values.length; i++) { ");
		sbReduce.append(" 		total += values[i].count; ");
		sbReduce.append(" 	} ");
		sbReduce.append(" 	return total; ");
		sbReduce.append(" } ");

		DBCollection inputCollection = dsForRW.getDB().getCollection("a_aft");
		String map = sbMap.toString();
		String reduce = sbReduce.toString();

		QueryBuilder q = QueryBuilder.start();
		q.and("_id").is(new ObjectId("546ef79aa310fa8807842029"));
		q.and("results.examId").is(4);
		DBObject query = q.get();

		DBCursor cursor = inputCollection.mapReduce(map, reduce, "resultCollection", query).getOutputCollection().find();

		while (cursor.hasNext()) {
			DBObject tObj = cursor.next();

			DBObject dbObj = (BasicDBObject) tObj.get("_id");
			dbObj.put("count", tObj.get("value"));

			objList.add(dbObj);
		}
	}

	public static BasicDBObject newDBObject(Object... kvs) {
		if (0 != kvs.length % 2)
			throw new RuntimeException("鍙彉鍙傛暟闀垮害涓哄彧鑳戒负鍋舵暟");
		BasicDBObject dbObj = new BasicDBObject();
		for (int i = 0; i < kvs.length / 2; i++) {
			dbObj.put(kvs[i].toString(), kvs[i + 1]);
		}

		return dbObj;
	}

	public static void t2() {
		QueryBuilder q = QueryBuilder.start();
		q.and("_id").is(new ObjectId("546ef79aa310fa8807842029"));
		q.and("results.examId").is(4);
		DBObject ooo = new BasicDBObject();
		ooo.put("results.score", 1);
		ooo.put("results.examId", 1);
		Cursor cursor = dsForRW.getDB().getCollection("a_aft").find(q.get(), ooo);
		while (cursor.hasNext()) {
			System.out.println(cursor.next());

		}
		cursor.close();
		// System.out.println(dsForRW.getDB().getCollection("a_aft").findOne(q.get()));
	}

	public static void test() throws Exception {
		HashSet<Integer> as = Sets.newHashSet(1, 2, 3, 4, 5);
		HashSet<Integer> bs = Sets.newHashSet(1, 2, 3);
		// HashSet<ObjectId> as = Sets.newHashSet(new
		// ObjectId("5469d543a310b457963c8957"));
		// HashSet<ObjectId> bs = Sets.newHashSet(new
		// ObjectId("5469d543a310b457963c8957"));
		System.out.println(Sets.union(as, bs));
		System.out.println(Sets.difference(as, bs));
		System.out.println(Sets.intersection(as, bs));

//		long a = System.currentTimeMillis();
//
//		List<ObjectId> jobIdList = Lists.newArrayList(new ObjectId("5469d543a310b457963c8957"));
//
//		Query<JobApply> q = dsForRW.createQuery(JobApply.class);
//		q.filter("jobId in", jobIdList);
//		q.field("userId").equal(100000);
//		q.filter("time >", System.currentTimeMillis() / 1000 - 60 * 60 * 24 * 7);
//
//		for (Key<JobApply> apply : q.asKeyList()) {
//			System.out.println(apply.getId());
//		}
//
//		HashSet<ObjectId> as1 = Sets.newHashSet(jobIdList);
//		System.out.println(as1);
//		System.out.println(System.currentTimeMillis() - a);
	}

	public static void update() {
		ObjectId id = new ObjectId("546d9bd74826e94f8975c50e");

		DBObject result = new BasicDBObject();
		result.put("examId", 3);
		result.put("examType", 111);
		result.put("answer", new Object[] {});

		DBObject result2 = new BasicDBObject();
		result2.put("examId", 4);
		result2.put("examType", 124);
		result2.put("answer", new Object[] {});

		DBObject q = new BasicDBObject("_id", id);
		DBObject o = new BasicDBObject();
		o.put("$addToSet", new BasicDBObject("results", new BasicDBObject("$each", new Object[] { result, result2 })));

		dsForRW.getDB().getCollection("test").update(q, o);

	}

}
