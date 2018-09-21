//package test;
//
//import java.io.FileOutputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.poi.hssf.usermodel.HSSFCell;
//import org.apache.poi.hssf.usermodel.HSSFRichTextString;
//import org.apache.poi.hssf.usermodel.HSSFRow;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//
//public class TestPoi {
//	public static class User {
//		private int id;
//		private String name;
//		private int age;
//
//		public User(int id, String name, int age) {
//			super();
//			this.id = id;
//			this.name = name;
//			this.age = age;
//		}
//
//		public int getId() {
//			return id;
//		}
//
//		public void setId(int id) {
//			this.id = id;
//		}
//
//		public String getName() {
//			return name;
//		}
//
//		public void setName(String name) {
//			this.name = name;
//		}
//
//		public int getAge() {
//			return age;
//		}
//
//		public void setAge(int age) {
//			this.age = age;
//		}
//	}
//
//	public static void main(String[] args) throws Exception {
//		String[] headers = new String[] { "编号", "姓名", "年龄" };
//		List<User> userList = new ArrayList<TestPoi.User>();
//		userList.add(new User(1, "张三1", 121));
//		userList.add(new User(2, "张三2", 122));
//		userList.add(new User(3, "张三3", 123));
//		userList.add(new User(4, "张三4", 124));
//		userList.add(new User(5, "张三5", 125));
//
//		// 声明一个工作薄
//		HSSFWorkbook wb = new HSSFWorkbook();
//		// 生成一个表格
//		HSSFSheet sheet = wb.createSheet("test");
//		// 产生表格标题行
//		HSSFRow row = sheet.createRow(0);
//		for (int i = 0; i < headers.length; i++) {
//			HSSFCell cell = row.createCell(i);
//			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
//			cell.setCellValue(text);
//		}
//
//		int rownum = 1;
//		for (User user : userList) {
//			row = sheet.createRow(rownum);
//			HSSFCell cell1 = row.createCell(0);
//			cell1.setCellValue(user.getId());
//			HSSFCell cell2 = row.createCell(1);
//			cell2.setCellValue(user.getName());
//			HSSFCell cell3 = row.createCell(2);
//			cell3.setCellValue(user.getAge());
//
//			rownum++;
//		}
//		// 写入到文件
//		FileOutputStream out = new FileOutputStream("d:\\a.xls");
//		// 写入到下载
//		// HttpServletResponse response;
//		// response.getOutputStream().write(wb.getBytes());
//		// response.setContentType("application/vnd.ms-excel");
//		// response.addHeader("Content-Disposition", "attachment;   filename=\""
//		// + "123.xls" + "\"");
//		// response.getOutputStream().write(wb.getBytes());
//		// out = response.getOutputStream();
//
//		wb.write(out);
//		wb.close();
//	}
//
//}
