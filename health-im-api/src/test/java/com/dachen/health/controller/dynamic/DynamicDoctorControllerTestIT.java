package com.dachen.health.controller.dynamic;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class DynamicDoctorControllerTestIT{
	
	public String access_token;
	
	@Before
	public  void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
//		access_token=TestUtil.testGetToken("15017905927",TestConstants.password_doc,TestConstants.userType_doc);
		access_token=TestUtil.testGetToken("18680388042",TestConstants.password_doc,TestConstants.userType_doc);

		
		RestAssured.basePath="/dynamic";
	 
	}
	
	
	
	@Test
	public void addDoctorDynamicForWeb() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);

		param.put("title", "医生新增动态221111");
		param.put("content", "<p>hellooo</p><p>hell333</p>");
		param.put("contentUrl", "http://pic.dbw.cn/0/09/41/76/9417687_926365.jpg");

		param.put("imageList", "http://112.jpg,http://113.jpg,http://112.jpg,http://113.jpg,http://112.jpg,http://113.jpg,http://112.jpg,http://113.jpg");
		Response response=RestAssured.given().queryParams(param).get("/addDoctorDynamicForWeb");
		JsonPath json = new JsonPath(response.asString());
		System.out.println(response.asString());
		int resultCode= json.getInt("resultCode");
		System.out.println(resultCode);
		//Assert.assertEquals( 1,resultCode);`

	}
	

	@Test
	public void addDoctorDynamic() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);

		param.put("content", "8888");
		param.put("imageList", "http://112.jpg,http://113.jpg,http://112.jpg,http://113.jpg,http://112.jpg,http://113.jpg,http://112.jpg,http://113.jpg");
		Response response=RestAssured.given().queryParams(param).get("/addDoctorDynamic");
		JsonPath json = new JsonPath(response.asString());
		System.out.println(response.asString());
		int resultCode= json.getInt("resultCode");
		System.out.println(resultCode);
		//Assert.assertEquals( 1,resultCode);

	}
	

	@Test
	public void deleteDoctorDynamic() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("id", "579af387e8135e4e640664e0");
		Response response=RestAssured.given().queryParams(param).get("/deleteDoctorDynamic");
		JsonPath json = new JsonPath(response.asString());
		int resultCode= json.getInt("resultCode");
		System.out.println(response.asString());

		Assert.assertEquals( 1,resultCode);
	}
	
	@Test
	public void getDoctorDynamicList() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("pageIndex", 0);
		param.put("pageSize", 5);

		Response response=RestAssured.given().queryParams(param).get("/getDoctorDynamicList");
		JsonPath json = new JsonPath(response.asString());
		int resultCode= json.getInt("resultCode");
		System.out.println(response.asString());

		Assert.assertEquals( 1,resultCode);
	}
	
	
}
