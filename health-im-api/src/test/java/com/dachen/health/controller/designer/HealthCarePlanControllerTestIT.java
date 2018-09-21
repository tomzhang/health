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

public class HealthCarePlanControllerTestIT {

	public String access_token;

	@Before
	public void setUp() throws UnsupportedEncodingException {

		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token = TestUtil.testGetToken(TestConstants.telephone_doc, TestConstants.password_doc,
				TestConstants.userType_doc);

		RestAssured.basePath = "/designer";

	}

	// @Test
	public void testSaveCheckItem() {
		Response response = given().param("access_token", access_token).param("sendTime", "09:30")
				.param("schedulePlanId", "xxxxxx").param("type", 5).param("checkItem.checkIds", "vvv")
				.param("checkItem.checkIds", "www").param("otherRemind.content", "复查")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/saveCheckItem");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}

	//@Test
	public void testSaveIllnessTrack() {
		Response response = given().param("access_token", access_token)
				.param("sendTime", "09:30")
				.param("carePlanId", "56c532da4203f30dcc023af4").param("dateSeq", 1)
				.param("jsonData",
						"{'name':'伤口渗液', 'options':[{'seq':'1','name':'有','level':'2', 'appendQuestions':[{'seq':'1','name':'渗液颜色', 'options':[{'seq':'1','name':'浅淡'}, {'seq':'2','name':'脓液'}],  }],  }, {'seq':'2','name':'无','level':'1'}] }")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/saveIllnessTrack");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}

	//@Test
	public void testSaveMedicalCare() {
		Response response = given().param("access_token", access_token).param("sendTime", "09:00")
				.param("carePlanId", "56c532da4203f30dcc023af4").param("dateSeq", 2)
				.param("jsonData",
						"{'medicalInfos':[{'medicalId':'fffId','general_name':'药品名','title':'药品标题，显示全部用这个',"
								+ "'manufacturer':'生产厂商','pack_specification':'包装规格',"
								+ "'totalQuantity': {'quantity':'2','unit':'盒'},"
								+ "'usage':{'period':{'number':'1','unit':'Day','text':'1天'},"
								+ "'patients':'青年','times':'3','quantity':'1','remarks':'口服','days':'4'},"
								+ "'reminder':{'gapDay':'1','duration':'4'} }]}")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/saveMedicalCare");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}

	//@Test
	public void testDeleteCareItem() {
		Response response = given().param("access_token", access_token).param("careItemId", "56c5975513d0d623047e5992")
				.post("/deleteCareItem");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}

	// @Test
	public void testFindCarePlan() {
		Response response = given().param("access_token", access_token).param("carePlanId", "56c532da4203f30dcc023af4")
				.post("/findCarePlan");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}

	//@Test
	public void testSaveDoctorReply() {
		Response response = given().param("access_token", access_token)
				.param("carePlanId", "56c6b79513d0d630501096ab")
				.post("/saveDoctorReply");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
	//@Test
	public void testSaveRepeatAsk() {
		Response response = given().param("access_token", access_token)
				.param("questionId", "56c6b79513d0d630501096ab")
				.param("jsonData", "{'repeats':[{'repeatSeq':'1','dateSeq':'3','sendTime':'10:30'}],  "
						+ "'endCondition':{'continueDays':'2','level':'1'} }")
				.post("/saveRepeatAsk");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}

}
