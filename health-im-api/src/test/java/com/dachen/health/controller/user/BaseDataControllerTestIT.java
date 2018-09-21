package com.dachen.health.controller.user;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class BaseDataControllerTestIT {

	public static String access_token;

	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc,TestConstants.password_doc,TestConstants.userType_doc2);
		RestAssured.basePath = "/base";
	}

	@Test
	public void saveMsgTemplate() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("id", "1");
		param.put("category", "短信111");
		// param.put("type","IM消息");
		param.put("title", "1111标题1{0},标题2{1}");
		param.put("content", "内容1{0},内容2{1}");
		param.put("usage", "用途");
		param.put("sample", "样例");
		Response response = RestAssured.given().queryParams(param).get("/saveMsgTemplate");

		param.remove("id");
		param.put("id","2");
		response = RestAssured.given().queryParams(param).get("/saveMsgTemplate");

		param.remove("id");
		response = RestAssured.given().queryParams(param).get("/saveMsgTemplate");

		param.remove("id");
		response = RestAssured.given().queryParams(param).get("/saveMsgTemplate");

		JsonPath jp = new JsonPath(response.asString());
		System.out.println("saveMsgTemplate:"+response.asString());
		assertEquals(new Integer(1),jp.get("resultCode"));
	}

	@Test
	public void queryMsgTemplate() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		
		param.put("pageIndex", "0");
		param.put("pageSize", "20");
		
		param.put("id", "564a8d17554abb1abca1a9ae");
		param.put("category", "短信111");
//		param.put("title", "aa");
//		param.put("content", "bb");

		Response response = RestAssured.given().queryParams(param).get("/queryMsgTemplate");

		JsonPath jp = new JsonPath(response.asString());
//		System.out.println("queryCopyWriterTemplate:"+response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1),jp.get("resultCode"));
	}

	@Test
	public void deleteMsgTemplate() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		
		//		param.put("ids","5649a96fa63d9d1cac3f6b09,5649a979a63d9d1cac3f6b0a");
		//		param.put("ids","1,2,5649a96fa63d9d1cac3f6b09");
		//		param.put("ids","5649a979a63d9d1cac3f6b0a,5649a96fa63d9d1cac3f6b09");
		param.put("ids","564a8776554abb083c8c7f00,564a8776554abb083c8c7f01");

		Response response = RestAssured.given().queryParams(param).get("/deleteMsgTemplate");

		JsonPath jp = new JsonPath(response.asString());
		System.out.println("deleteMsgTemplate:"+response.asString());
//		assertEquals(new Integer(1),jp.get("resultCode"));
	}
	
	@Test
	public void getAllAreas() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		Response response = RestAssured.given().queryParams(param).get("/getAllAreas");

		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1),jp.get("resultCode"));
	}

}
