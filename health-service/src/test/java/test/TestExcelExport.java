package test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;


/**
 * 生成excel文件
 *@author wangqiao
 *@date 2016年1月6日
 *
 */
public class TestExcelExport {
	public static class Item {
		private String id1;
		private String id2;
		private String id3;
		private String name1;
		private String name2;
		private String name3;
		private String remark;

		public Item(String id1,String id2,String id3,String name1, String name2, String name3,String remark) {
			super();
			this.id1 = id1;
			this.id2 = id2;
			this.id3 = id3;
			this.name1 = name1;
			this.name2 = name2;
			this.name3 = name3;
			this.remark = remark;
		}

		public String getName1() {
			return name1;
		}

		public void setName1(String name1) {
			this.name1 = name1;
		}

		public String getName2() {
			return name2;
		}

		public void setName2(String name2) {
			this.name2 = name2;
		}

		public String getName3() {
			return name3;
		}

		public void setName3(String name3) {
			this.name3 = name3;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String toString(){
			return "name1="+name1+"; name2="+name2+"; name3="+name3+"; remark="+remark;
		}

		public String getId1() {
			return id1;
		}

		public void setId1(String id1) {
			this.id1 = id1;
		}

		public String getId2() {
			return id2;
		}

		public void setId2(String id2) {
			this.id2 = id2;
		}

		public String getId3() {
			return id3;
		}

		public void setId3(String id3) {
			this.id3 = id3;
		}
		
		

	}
	
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
	
	/**
	 * 
	 * 生成excel 文件
	 *@author wangqiao
	 * @throws IOException 
	 * @date 2016年1月5日
	 */
	public static void generateFile(List<Item> itemList) throws IOException{
		
		String[] headers = new String[] { "教程ID","教程","一级分类ID", "一级分类","最后级分类ID", "最后级分类","症状" };
//		List<Item> userList = new ArrayList<TestPoi.Item>();


		// 声明一个工作薄
		HSSFWorkbook wb = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = wb.createSheet("疾病症状录入");
		// 产生表格标题行
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}

		int rownum = 1;
		for (Item item : itemList) {
			row = sheet.createRow(rownum);
			HSSFCell cell1 = row.createCell(0);
			cell1.setCellValue(item.getId1());
			
			HSSFCell cell2 = row.createCell(1);
			cell2.setCellValue(item.getName1());
			
			HSSFCell cell3 = row.createCell(2);
			cell3.setCellValue(item.getId2());
			
			HSSFCell cell4= row.createCell(3);
			cell4.setCellValue(item.getName2());
			
			HSSFCell cell5 = row.createCell(4);
			cell5.setCellValue(item.getId3());
			
			HSSFCell cell6 = row.createCell(5);
			cell6.setCellValue(item.getName3());
			
			HSSFCell cell7 = row.createCell(6);
			cell7.setCellValue(item.getRemark());

			rownum++;
		}
		// 写入到文件
		FileOutputStream out = new FileOutputStream("e:\\a.xls");
		// 写入到下载
		// HttpServletResponse response;
		// response.getOutputStream().write(wb.getBytes());
		// response.setContentType("application/vnd.ms-excel");
		// response.addHeader("Content-Disposition", "attachment;   filename=\""
		// + "123.xls" + "\"");
		// response.getOutputStream().write(wb.getBytes());
		// out = response.getOutputStream();

		wb.write(out);
//		wb.close();
		
	}

	public static void main(String[] args) throws Exception {
//		generateFile();
		List<Item> itemList = new ArrayList<Item>();
		initMongo();
		
		//查询一级分类
		BasicDBObject query_1 = new BasicDBObject("parent", "0");
//		db.getCollection("b_disease_type");
		FindIterable find_1 = db.getCollection("b_disease_type").find(query_1).sort(new BasicDBObject("weight",-1));
		MongoCursor cursor_1 = find_1.iterator();
		while(cursor_1.hasNext()) {
				Document obj_1 = (Document)cursor_1.next();
				String id_1 = obj_1.getString("_id");
				String name_1 = obj_1.getString("name");
				//查询二级分类
				BasicDBObject query_2 = new BasicDBObject("parent", id_1);
				FindIterable find_2 = db.getCollection("b_disease_type").find(query_2).sort(new BasicDBObject("_id",1));
				MongoCursor cursor_2 = find_2.iterator();
				
				while(cursor_2.hasNext()){
					Document obj_2 = (Document)cursor_2.next();
					String id_2 = obj_2.getString("_id");
					String name_2 = obj_2.getString("name");
					//查询三级分类
					BasicDBObject query_3 = new BasicDBObject("parent", id_2);
					FindIterable find_3 = db.getCollection("b_disease_type").find(query_3).sort(new BasicDBObject("_id",1));
					MongoCursor cursor_3 = find_3.iterator();
					
					while(cursor_3.hasNext()){
						Document obj_3 = (Document)cursor_3.next();
						String id_3 = obj_3.getString("_id");
						String name_3 = obj_3.getString("name");
						String remark = obj_3.getString("remark");
						
						Item item = new Item(id_1,id_2,id_3,name_1,name_2,name_3,remark);
						System.out.println(item);
						itemList.add(item);
					}
					
				}
				
				
		   }
		
		generateFile(itemList);
		
		closeMongo();
	}
}

