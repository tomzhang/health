package com.dachen.health.controller.pack;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class DiseaseControllerTestIT extends BaseControllerTest {

	@Override
	public void setUp() throws UnsupportedEncodingException {
		super.setUp();
		RestAssured.basePath = "/disease";
	}

	// @Test
	public void create() {

		// Map<String, Object> param = new HashMap<String, Object>();
		param.put("patientId", 2);
		param.put("needHelp", 1);
		param.put("createdTime", new Date().getTime());
		param.put("diseaseInfo", "病情");
		param.put("telephone", "130335");
		// param.put("access_token", access_token);

		Response response = RestAssured.given().queryParams(param)
				.get("/create");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}

	// @Test
	public void update() {

		// Map<String, Object> param = new HashMap<String, Object>();
		param.put("patientId", 130);
		param.put("needHelp", 1);
		param.put("createdTime", new Date().getTime());
		param.put("diseaseInfo", "病情1");
		param.put("id", 1360);
		// param.put("access_token", access_token);

		Response response = RestAssured.given().queryParams(param)
				.get("/update");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}

//	@Test
	public void findByPk() {

		param.put("id", 1360);

		Response response = RestAssured.given().queryParams(param)
				.get("/findById");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}

	@Test
	public void findByCreateUser() {

		// Map<String, Object> param = new HashMap<String, Object>();

		// param.put("access_token", access_token);

		Response response = RestAssured.given().queryParams(param)
				.get("/findByCreateUser");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}

	@Test
	public void findByPatient() {

		// Map<String, Object> param = new HashMap<String, Object>();
		param.put("patientId", 2);
		// param.put("access_token", access_token);

		Response response = RestAssured.given().queryParams(param)
				.get("/findByPatient");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}

	// @Test
	public void deleteByPk() {

		param.put("id", 2);

		Response response = RestAssured.given().queryParams(param)
				.get("/deleteByPk");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}

	//@Test
	public void testFindDeaseaseByOrderId() {

		param.put("orderId", 701);
		Response response = RestAssured.given().queryParams(param)
				.get("/findDeaseaseByOrderId");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		System.out.println("testFindDeaseaseByOrderId=" + response.asString());
		Assert.assertEquals(1, resultCode);

	}

	@Test
	public void testUpdateDoctorDisease() {
		param.put("id", 1361);
		param.put("marriage", "未婚");
		param.put("profession", "有力事假");
		param.put("weigth", "身高");

		Response response = RestAssured.given().queryParams(param)
				.get("/updateDoctorDisease");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		System.out.println("testFindDeaseaseByOrderId=" + response.asString());
		Assert.assertEquals(1, resultCode);

	}
	
	
}
