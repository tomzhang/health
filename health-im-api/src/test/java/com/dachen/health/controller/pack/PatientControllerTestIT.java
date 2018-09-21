package com.dachen.health.controller.pack;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class PatientControllerTestIT   {
	public static Map<String, Object> param = new HashMap<String, Object>();
	
	public static String access_token;
	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken("20009",TestConstants.password_doc,"1");
		param.clear();
		param.put("access_token", access_token);
		
		RestAssured.basePath="/packpatient";
	 
	}

	@Test
	public void create() {

		//Map<String, Object> param = new HashMap<String, Object>();
		param.put("userName", "用户");
		param.put("sex", 1);
		param.put("birthday", new Date().getTime());
		param.put("relation", "母子");
		param.put("area", "南山");
		param.put("diseaseInfo", "病情");
		param.put("telephone", "13632560875");
		//param.put("access_token", access_token);

		Response response = RestAssured.given().queryParams(param)
				.get("/create");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode=1;
		Assert.assertEquals(1, resultCode);

	}

//	@Test
	public void update() {

		param.put("userName", "用户1");
		param.put("sex", 2);
		param.put("birthday", new Date().getTime());
		param.put("relation", "母子");
		param.put("area", "南山");
		param.put("id",130);

		Response response = RestAssured.given().queryParams(param)
				.get("/update");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}



	@Test
	public void findByPk() {

		param.put("id", 921);

		Response response = RestAssured.given().queryParams(param)
				.get("/findById");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}

	@Test
	public void findByCreateUser() {

		//param.put("access_token", access_token);

		Response response = RestAssured.given().queryParams(param)
				.get("/findByCreateUser");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}
	
	//@Test
	public void deleteByPk() {
		param.put("id", 1);

		Response response = RestAssured.given().queryParams(param)
				.get("/deleteByPk");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}
	
	@Test
	public void exists() {
		param.put("id", 919);

		Response response = RestAssured.given().queryParams(param)
				.get("/existsBizData");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}
	
	@Test
	public void testGetDoctors() {

		RestAssured.basePath="/patient";

		Response response = RestAssured.given().param("access_token", access_token)
				.get("/getDoctors");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);
		RestAssured.basePath="/packpatient";
	}

}
