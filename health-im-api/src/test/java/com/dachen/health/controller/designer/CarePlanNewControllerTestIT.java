package com.dachen.health.controller.designer;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class CarePlanNewControllerTestIT {

	public String access_token;

	@Before
	public void setUp() throws UnsupportedEncodingException {

		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token = TestUtil.testGetToken(TestConstants.telephone_doc, TestConstants.password_doc,
				TestConstants.userType_doc);

		RestAssured.basePath = "/pack/carenew";

	}
	
	//@Test
	public void testupdateStartTime() {
		Response response = given().param("access_token", access_token)
				.param("carePlanId", "09:30")
				.param("startDate", "xxxxxx")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/updateStartTime");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
	//@Test
	public void testupdateScheduleDate() {
		Response response = given().param("access_token", access_token)
				.param("schedulePlanId", "56d805164203f33f0b96350f")
				.param("oldDate", "2016-03-03")
				.param("newDate", "2016-03-04")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/updateScheduleDate");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
	//@Test
	public void testSubmitAnswerSheet() {
		Response response = given().param("access_token", access_token)
				.param("careItemId", "56f5f4ff4203f34b9d678d30")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/submitAnswerSheet");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
	@Test
	public void testSaveMedicalByPack() {
		Response response = given().param("access_token", access_token)
				.param("carePlanId", "5769fa0b4203f312bfbff36b")
				.param("jsonData", "jsonData=[{'imageUrl':'http://123.com.jpg','dateSeq':'5','goodsId':'57621ea4156a0141580d1835','generalName':'通用名222','title':'title22','manufacturer':'生产厂家222','packSpecification':'规格号222','totalQuantity':{'quantity':'4','unit':'','days':'27'},'usage':{'periodNum':'2','unit':'','periodUnit':'天','patients':'青少年','times':'5','quantity':'1','remarks':'备注222'}}]")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/saveMedicalByPack");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
}
