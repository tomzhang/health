package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * 修复部分数据
 *@author wangqiao
 *@date 2016年1月6日
 *
 */
public class TestDataClear {

	public static MongoClient m = null;  
	public static MongoDatabase  db = null;  
	
	/**
	 * 链接数据库
	 *@author wangqiao
	 *@date 2016年1月6日
	 */
	public static void initMongo(){
        m = new MongoClient("192.168.3.7", 27017); 
        db  = m.getDatabase("health");
//        db.getCollection("b_disease_type");
	}
	
	/**
	 * 
	 * 关闭数据库
	 *@author wangqiao
	 *@date 2016年1月6日
	 */
	public static void closeMongo(){
        m.close();
	}
	
	public static void main(String[] args) throws Exception {
		
		initMongo();
		
//		long count = db.getCollection("b_checkup").count();
//		System.out.println("count = "+count);
		int count = 0;
		
//		Pattern p=Pattern.compile("1|2|3|4|5|6|7|8|9|0|．");
//		Pattern p=Pattern.compile("1|2|3|4|5|6|7|8|9|0．|\"|”|、");
//		Pattern p=Pattern.compile("1|2|3|4|5|6|7|8|9|0|．|\"|”|、");
		Pattern p=Pattern.compile("\"|”");
		
		FindIterable find = db.getCollection("b_checkup").find().sort(new BasicDBObject("_id",1));
		MongoCursor cursor = find.iterator();
		while(cursor.hasNext()) {
			Document obj = (Document)cursor.next();
			String id = obj.getString("_id");
			String name = obj.getString("name");
			//利用正则表达式，修复name中的 空格，数字， 点， 双引号，顿号
			if(StringUtils.isEmpty(name)){
				continue;
			}

			Matcher m=p.matcher(name);
			boolean result=m.find();
			if(result){
				System.out.println("name = "+name +"    id="+id);
				count++;
			}
			
			
			//先打印出来
			
			
			//再更新
//			BasicDBObject query = new BasicDBObject("_id", id);
//			BasicDBObject update = new BasicDBObject("name", name);
//			db.getCollection("b_checkup").updateOne(query, update);
			
		}
		System.out.println("count = "+count);
		closeMongo();
		
		
	}
	
	
}

