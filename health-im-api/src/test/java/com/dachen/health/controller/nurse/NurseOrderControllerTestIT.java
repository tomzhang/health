package com.dachen.health.controller.nurse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class NurseOrderControllerTestIT {

	public static String access_token;

	@BeforeClass
	public static void setUp() throws UnsupportedEncodingException {

		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token = TestUtil.testGetToken(TestConstants.telephone_doc9,
				TestConstants.password_doc9, TestConstants.userType_doc9);
		RestAssured.basePath = "/nurse";

	}

//	 @Test
	public void testGetUserOrder() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		Response response = RestAssured.given().queryParams(param)
				.get("/getNurseAvialOrder");
		System.out.println("getNurseAvialOrder.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

//	 @Test
	public void testGetTheOrder() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("orderId", "568c6f469357ec1a903fd73f");
		Response response = RestAssured.given().queryParams(param)
				.get("/getTheOrder");
		System.out.println("getUserOrder.asString()=" + response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

//	 @Test
	public void testGetVServiceProcessList() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		Response response = RestAssured.given().queryParams(param)
				.get("/getVServiceProcessList");
		System.out.println("testGetVServiceProcessList.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}
	
//	 @Test
//	public void testGetHistoryVServiceProcessListPage() throws Exception {
//		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("access_token", access_token);
//		param.put("pageIndex", 1);
//		param.put("pageSize", 15);
//		Response response = RestAssured.given().queryParams(param)
//				.get("/getHistoryVServiceProcessListForPage");
//		System.out.println("getHistoryVServiceProcessListForPage.asString()="
//				+ response.asString());
//		JsonPath json = new JsonPath(response.asString());
//		int resultCode = json.getInt("resultCode");
//		resultCode = 1;
//		Assert.assertEquals(1, resultCode);
//	}

//	 @Test
	public void testGetHistoryVServiceProcessList() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		Response response = RestAssured.given().queryParams(param)
				.get("/getHistoryVServiceProcessList");
		System.out.println("testGetHistoryVServiceProcessList.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

//	 @Test
	public void testUpdateNurseServiceStatus() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("serviceId", "568c703f9357ec190c87812a");
		// param.put("status", 2);
		Response response = RestAssured.given().queryParams(param)
				.get("/updateNurseServiceStatus");
		System.out.println("testGetVServiceProcessList.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}



//	 @Test //(String toTel,String vspId)
	public void testEndAppraise() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("orderId", "568b290e9357ec1a4893dbce");
		Response response = RestAssured.given().queryParams(param)
				.get("/endAppraise");
		System.out.println("endAppraise.asString()=" + response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

//	 @Test //
	public void testCheckCancleUserOrder() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("orderId", "568b81839357ec1114be5bde");
		Response response = RestAssured.given().queryParams(param)
				.get("/checkCancleUserOrder");
		System.out.println("checkCancleUserOrder.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

//	 @Test //(String toTel,String vspId)
	public void testCancleUserOrder() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("orderId", "568c6f469357ec1a903fd73f");
		Response response = RestAssured.given().queryParams(param)
				.get("/cancleUserOrder");
		System.out.println("cancleUserOrder.asString()=" + response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

//	@Test
	public void testCallBackBasicOrder() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("basicOrderId", "3479");
		Response response = RestAssured.given().queryParams(param)
				.get("/callBackBasicOrder");
		System.out.println("testCallBackBasicOrder.asString()=" + response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

	/**
	 * params : departments=[""] &patientTel=12000000000 &patientId=972
	 * &productId=567dfbd94203f36da8ca36c8 &checkId=567dfbd94203f36da8ca36c8
	 * &doctorId=&
	 * checkItems=["567dfbd94203f36da8ca36cb","567dfbd94203f36da8ca36cc"
	 * ,"567dfbd94203f36da8ca36ca"] &appointmentTime=2016-01-01 07:30
	 * &access_token=c3646edf38f043cea7df7291598a7dba
	 * &hospitals=["201506180004"] &remark=大哥哥 &doctorName=
	 */
//	 @Test
	public void testInsertUserOrder() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("productId", "567dfbd94203f36da8ca36c8");
		param.put("checkId", "567dfbd94203f36da8ca36c8");
		param.put("patientId", 917);
		param.put("patientName", "");
		param.put("patientTel", "13632560872");

		param.put("doctorName", "");
		param.put("doctorId", 0);
		param.put("appointmentTime", "2016-01-21 16:10");

		List<String> hospitals = new ArrayList<String>();
		hospitals.add("200509200006");
		hospitals.add("200108223124");
		List<String> departments = new ArrayList<String>();
		departments.add("WKAH");
		List<String> checkItems = new ArrayList<String>();
		checkItems.add("250402013");
		checkItems.add("250203031");
		checkItems.add("2503060022");
		checkItems.add("2504030192");
		checkItems.add("250101015c");
		param.put("hospitals", JSON.toJSONString(hospitals));
		param.put("departments", JSON.toJSONString(departments));
		param.put("checkItems", JSON.toJSONString(checkItems));
		 param.put("remark", "订单测试001 ");
		System.out.println("param=" + param);
		Response response = RestAssured.given().queryParams(param)
				.get("/insertUserOrder");
		System.out.println("insertUserOrder.asString()=" + response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

//	@Test
	public void testdetail() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("orderId", "3491");
		System.out.println("param=" + param);
		Response response = RestAssured.given().queryParams(param)
				.get("/getOrderDetail");
		System.out.println("testdetail.asString()=" + response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

//	 @Test
	public void tesGetCheckBillService() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("checkId", "567d2b334203f375dfb0022c");
		param.put("checkBillStatus", 4);
		System.out.println("param=" + param);
		Response response = RestAssured.given().queryParams(param)
				.get("/getCheckBillService");
		System.out.println("etCheckBillService.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}
	
	
//	 @Test
	public void testConfirmNurseService() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("serviceId", "567a1428f7aaa10900fa79e3");
		System.out.println("param=" + param);
		Response response = RestAssured.given().queryParams(param)
				.get("/confirmNurseService");
		System.out.println("endNurseService.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}
	
}
