package com.dachen.health.controller.pack;

import static com.jayway.restassured.RestAssured.given;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class EvaluationControllerTestIT extends BaseControllerTest {

	@Override
	public void setUp() throws UnsupportedEncodingException {
		super.setUp();
		RestAssured.basePath = "/pack/evaluate";
	}
	
	//@Test
	public void TestGetEvaluationItem() {

		param.put("orderId", 1099);
		Response response = RestAssured.given().queryParams(param)
				.get("/getEvaluationItem");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);
	}
	
	//@Test
	public void TestAddEvaluation() {

		Response response = given().param("access_token", access_token)
				.param("orderId", 1748)
				.param("itemIds", "568f6d2431008b47dfa85341")
				.param("itemIds", "568f6d2431008b47dfa85343")
				.param("itemIds", "568f6d2431008b47dfa85341")
				.post("/addEvaluation");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);
	}
	
	@Test
	public void TestIsEvaluated() {

		Response response = given().param("access_token", access_token)
				.param("orderId", 710)
				.post("/isEvaluated");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);
	}
	
	@Test
	public void TestGetTopSix() {

		Response response = given().param("access_token", access_token)
				.param("doctorId", 550)
				.post("/getTopSix");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);
	}
	
	@Test
	public void TestGetEvaluationDetail() {

		Response response = given().param("access_token", access_token)
				.param("doctorId", 550)
				.post("/getEvaluationDetail");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);
	}
}
