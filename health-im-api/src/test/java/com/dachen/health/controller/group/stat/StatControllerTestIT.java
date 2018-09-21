package com.dachen.health.controller.group.stat;

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

public class StatControllerTestIT {
	public static String access_token;
	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken("10029",TestConstants.password_doc,TestConstants.userType_doc);
		RestAssured.basePath="/group/stat";
	 
	}
	
	@Test
	public void testGetNewDiseaseTypeTree() {
		Map<String, Object> param = new HashMap<String, Object>();
		
		param.put("access_token", access_token);
		param.put("groupId",  "564be9a44203f35eda0868ed");
		param.put("tmpType",  6);
		
		Response response=RestAssured.given().queryParams(param).get("/getNewDiseaseTypeTree");
		
		JsonPath json = new JsonPath(response.asString());
		
		int resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);
	}
	

	@Test
	public void doctorArea() {
		Map<String, Object> param = new HashMap<String, Object>();
		
		param.put("access_token", access_token);
		param.put("groupId",  "55d588fccb827c15fc9d47b3"); // 55d588fccb827c15fc9d47b3
//		param.put("areaId",  2);
		
		Response response=RestAssured.given().queryParams(param).get("/doctorArea");
		
		JsonPath json = new JsonPath(response.asString());
		System.out.println(response.asString());
		
		int resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);
	}
	
	@Test
	public void TestDisease() {
		Map<String, Object> param = new HashMap<String, Object>();
		
		param.put("access_token", access_token);
		param.put("groupId",  "55d588fccb827c15fc9d47b3"); // 55d588fccb827c15fc9d47b3
		param.put("diseaseId",  "FC");
		
		Response response=RestAssured.given().queryParams(param).get("/disease");
		
		JsonPath json = new JsonPath(response.asString());
		System.out.println(response.asString());
		
		int resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);
	}
	
	@Test
	public void testOrderMoney() {
		Map<String, Object> param = new HashMap<String, Object>();
		
		param.put("access_token", access_token);
		param.put("groupId",  "55d588fccb827c15fc9d47b3"); // 55d588fccb827c15fc9d47b3
//		param.put("doctorId",  793);
		
		Response response=RestAssured.given().queryParams(param).get("/orderMoney");
		
		JsonPath json = new JsonPath(response.asString());
		System.out.println(response.asString());
		
		int resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);
	}
	
	@Test
	public void testStatDoctor() {
		Map<String, Object> param = new HashMap<String, Object>();
		//病种
		param.put("access_token", access_token);
		param.put("groupId",  "55d588fccb827c15fc9d47b3"); // 55d588fccb827c15fc9d47b3
		param.put("type",  1);
		param.put("typeId",  "AB");
		
		Response response=RestAssured.given().queryParams(param).get("/statDoctor");
		
		JsonPath json = new JsonPath(response.asString());
		System.out.println(response.asString());
		
		int resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);
		
		//职称
		param.put("access_token", access_token);
		param.put("groupId",  "55d588fccb827c15fc9d47b3"); // 55d588fccb827c15fc9d47b3
		param.put("type",  2);
		param.put("typeId",  "医师");
		
		response=RestAssured.given().queryParams(param).get("/statDoctor");
		
		json = new JsonPath(response.asString());
		System.out.println(response.asString());
		
		resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);
		
		//区域
		param.put("access_token", access_token);
		param.put("groupId",  "55d588fccb827c15fc9d47b3"); // 55d588fccb827c15fc9d47b3
		param.put("type",  3);
		param.put("typeId",  "440300");
		
		response=RestAssured.given().queryParams(param).get("/statDoctor");
		
		json = new JsonPath(response.asString());
		System.out.println(response.asString());
		
		resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);
	}
	
	
	@Test
	public void testPatient() {
		Map<String, Object> param = new HashMap<String, Object>();
		//病种
		param.put("access_token", access_token);
		param.put("groupId",  "55d588fccb827c15fc9d47b3"); // 55d588fccb827c15fc9d47b3
		param.put("type",  1);
		param.put("id",  0);
		param.put("pageIndex", 0);
		param.put("pageSize", 500);
		
		Response response=RestAssured.given().queryParams(param).get("/patient");
		
		JsonPath json = new JsonPath(response.asString());
		System.out.println(response.asString());
		
		int resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);
	
	}
	
	@Test
	public void testInviteDoctor() {
		Map<String, Object> param = new HashMap<String, Object>();
		//病种
		param.put("access_token", access_token);
		param.put("keyword",  "屈军利"); // 55d588fccb827c15fc9d47b3
		param.put("groupId",  "57034f6f4203f309e3abc69d"); // 55d588fccb827c15fc9d47b3
		param.put("pageIndex", 0);
		param.put("pageSize", 500);
		
		Response response=RestAssured.given().queryParams(param).get("/inviteDoctor");
		
		JsonPath json = new JsonPath(response.asString());
		System.out.println(response.asString());
		
		int resultCode= json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);
	
	}
}
