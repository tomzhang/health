package com.dachen.health.controller.pack;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class DoctorAssistantControllerTestIT {

	public static String access_token;


	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken("18689208527","123456","1");
		//access_token=TestUtil.testGetToken(TestConstants.telephone_doc7,TestConstants.password_doc7,TestConstants.userType_doc7);
		//access_token="7ddae005cf8244149c91500dbe7509c8";
		RestAssured.basePath = "/doctorAssistant";
	 
	}
	

   
	
	@Test
	public void testsetConsultationTime() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.param("orderId", 4463).param("oppointTime",123456L)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/setConsultationTime");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));

	}

	@Test
	public void testisOverPayTimeTwoDays() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.param("orderId", 4463)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/isOverPayTimeTwoDays");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));

	}
	
	@Test
	public void testcancelOrder() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.param("orderId", 15722).param("msg","aaaaaaaaaaaaaaa").param("cancelType",2)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/cancelOrder");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));

	}
	

	
	@Test
	public void testqueryOrderByConditions() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.param("orderStatus", 2).param("packType",1).param("doctorId",100196).param("pageIndex",0)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/queryOrderByConditions");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));

	}
	
	
	@Test
	public void testqueryDoctorListFromOrder() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/queryDoctorListFromOrder");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));

	}

	@Test
	public void testqueryDoctorList() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/queryDoctorList");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));

	}
}
