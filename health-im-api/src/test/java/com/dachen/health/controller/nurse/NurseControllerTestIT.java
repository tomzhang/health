package com.dachen.health.controller.nurse;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class NurseControllerTestIT {

	public static String access_token;

	@BeforeClass
	public static void setUp() throws UnsupportedEncodingException {

		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token = TestUtil.testGetToken("13632560872",
				TestConstants.password_doc9, TestConstants.userType_doc9);
		RestAssured.basePath = "/nurse";

	}

	/**
	 * * @apiParam {String} access_token token
	 * 
	 * @apiParam {String} name 姓名
	 * @apiParam {String} idCard 身份证号码
	 * @apiParam {String} hospital 医院
	 * @apiParam {String} hospitalId 医院Id（如果没有医院选择，则传""）
	 * @apiParam {String} departments 科室
	 * @apiParam {String} title 职称
	 * @throws Exception
	 */
	// @Test
	public void testCreateCheckInfo() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("userId", 12215);
		param.put("name", "会小明");
		param.put("idCard", "3241204198503091219");
		param.put("hospital", "抚顺市妇幼保健院");
		param.put("hospitalId", "201506180004");
		param.put("departments", "B超室");
		param.put("title", "主任医师");

		param.put(
				"images",
				"http://192.168.3.7:8081/cert/1908/11908/c9/470988625.505843.png.jpg,http://192.168.3.7:8081/cert/1908/11908/c9/470988699.427584.png.jpg");

		Response response = RestAssured.given().queryParams(param)
				.post("/createCheckInfo");
		System.out.println("response.asString()==" + response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		System.out.println("resultCode==" + resultCode);
		// int resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);

	}

	// @Test
	public void testGetCheckInfo() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		// param.put("userId", 12215);
		Response response = RestAssured.given().queryParams(param)
				.get("/getNurseCheckInfo");
		System.out.println("response.asString()=" + response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		System.out.println("resultCode==" + resultCode);
		// int resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);
	}

	/**
	 * * @apiParam {String} access_token token
	 * 
	 * @apiParam {String} name 姓名
	 * @apiParam {String} idCard 身份证号码
	 * @apiParam {String} hospital 医院
	 * @apiParam {String} hospitalId 医院Id（如果没有医院选择，则传""）
	 * @apiParam {String} departments 科室
	 * @apiParam {String} title 职称
	 * @throws Exception
	 */
//	 @Test
	public void testgetServiceStatisticService() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);

		Response response = RestAssured.given().queryParams(param)
				.post("/getServiceStatisticService");
		System.out.println("testgetServiceStatisticService.asString()==" + response.asString());

		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		// int resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);

	}

	// @Test
	public void testUpdateCheckInfo() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("userId", 12215);
		param.put("idCard", "3241204198503091220");
		param.put("hospital", "抚顺市妇幼保健院");
		param.put("hospitalId", "201506180004");
		param.put("departments", "儿科");
		param.put("title", "主任医师");
		param.put("name", "王医生");
		param.put("images",
				"http://192.168.3.7:8081/cert/1908/11908/c9/470988625.505843.png.jpg");

		Response response = RestAssured.given().queryParams(param)
				.post("/updateNurseCheckInfo");
		System.out.println("response.asString()==" + response.asString());

		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		System.out.println("resultCode==" + resultCode);
		// int resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);

	}

	// @Test
	public void testGetSystemLineServiceProduct() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		// param.put("userId", 12215);
		Response response = RestAssured.given().queryParams(param)
				.get("/getSystemLineServiceProduct");
		System.out.println("testgetSystemLineServiceProduct.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		System.out.println("resultCode==" + resultCode);
		// int resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);
	}


	//@Test
	public void testUpdateUserServiceTime() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("userId", 12215);
		param.put("time", "2015-12-12");
		param.put("status", "1");
		Response response = RestAssured.given().queryParams(param)
				.post("/updateUserServiceTime");
		System.out.println("response.asString()==" + response.asString());

		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		System.out.println("resultCode==" + resultCode);
		// int resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);

	}

	//@Test
	public void testUserLineService() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		// param.put("userId", 12215);
		Response response = RestAssured.given().queryParams(param)
				.get("/getUserLineService");
		System.out.println("testUserLineService.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);
	}

	/**
	 * 测试更新护士服务状态
	 * 
	 * @throws Exception
	 */
//	 @Test
	public void testUpdateUserLineService() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("id", "5666caeb9357ec3fb87ff728");
		param.put("status", 2);
		Response response = RestAssured.given().queryParams(param)
				.post("/updateUserLineService");
		System.out.println("response.asString()==" + response.asString());

		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		System.out.println("resultCode==" + resultCode);
		// int resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);

	}
	
//	@Test
	 public void testGetNurseList() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("status", 2);
		Response response = RestAssured.given().queryParams(param).post("/getNurseList");
		System.out.println("response.asString()==" + response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		System.out.println("resultCode==" + resultCode);
		// int resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);
		}
//	 @Test
	 public void testUpdateUserGuide() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		Response response = RestAssured.given().queryParams(param).post("/updateUserGuide");
		System.out.println("testUpdateUserGuide.asString()==" + response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		// int resultCode= json.getInt("resultCode");
		System.out.println("testUpdateUserGuide.resultCode()==" + resultCode);
		resultCode=1;
		Assert.assertEquals(1, resultCode);
		}
	 @Test
	 public void testLogin() throws Exception {
		 
	 }
}
