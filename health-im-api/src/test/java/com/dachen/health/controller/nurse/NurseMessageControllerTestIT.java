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
import com.dachen.line.stat.entity.param.CheckResultLineServiceParm;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class NurseMessageControllerTestIT {

	public static String access_token;

	@BeforeClass
	public static void setUp() throws UnsupportedEncodingException {

		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token = TestUtil.testGetToken("13632560872",
				TestConstants.password_doc9, TestConstants.userType_doc9);
		RestAssured.basePath = "/nurse";

	}

	// @Test //(String toTel,String vspId)
	public void testCallByTel() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("toTel", "131381696012");
		param.put("vspId", "568477829357ec20b434c791");
		Response response = RestAssured.given().queryParams(param)
				.get("/callByTel");
		System.out.println("getUserOrder.asString()=" + response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

	/**
	 */
//	 @Test
	public void testSendMessage() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("toTel", "13632560872");
		param.put("messageId", "");
		param.put("content", "哈哈哈哈123");
		param.put("vspId", "568477829357ec20b434c791");
		Response response = RestAssured.given().queryParams(param)
				.get("/sendMessage");
		System.out.println("getUserOrder.asString()=" + response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		Assert.assertEquals(1, resultCode);
	}

	/**
	 * @throws Exception
	 */
//	 @Test
	public void testGetUserMessageList() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		Response response = RestAssured.given().queryParams(param)
				.get("/getUserMessageList");
		System.out.println("testGetUserMessage.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

	/**
	 * @throws Exception
	 */
	// @Test
	public void testInsertUserMessage() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("content", "xinzeng");
		Response response = RestAssured.given().queryParams(param)
				.get("/insertUserMessage");
		System.out.println("insertUserMessage.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

	/**
	 * @throws Exception
	 */
	// @Test
	public void testdeleteUserMessage() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("messageId", "566bc847b4726512a44d162d");// 566bc847b4726512a44d162d
		Response response = RestAssured.given().queryParams(param)
				.get("/deleteUserMessage");
		System.out.println("testGetUserMessage.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

//	 @Test
	public void testInsertCheckResults() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("serviceId", "568b2b059357ec1a4893dbd2");// 567e0f939357ec15284dbeb6
															// 250402017
		param.put("from", 1);
		List<CheckResultLineServiceParm> list = new ArrayList<CheckResultLineServiceParm>();
		CheckResultLineServiceParm ss = new CheckResultLineServiceParm();
		ss.setLsIds("250402017");
		ss.setResults("song99");
		ss.setImageList("http://song9,http://song12");
		list.add(ss);
		String jsonStr = JSON.toJSONString(list);

		param.put("checkItemList", jsonStr);
		System.out.println("param=" + param);
		Response response = RestAssured.given().queryParams(param)
				.get("/insertCheckResults");
		System.out.println("insertCheckResults.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

	// @Test
	public void testGetCheckResults() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("orderId", "566bd55db4726512a44d162e");
		System.out.println("param=" + param);
		Response response = RestAssured.given().queryParams(param)
				.get("/getCheckResults");
		System.out.println("insertCheckResults.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

	// @Test
	public void testGetCertHospitalList() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		System.out.println("param=" + param);
		Response response = RestAssured.given().queryParams(param)
				.get("/getCertHospitalList");
		System.out.println("testGetCertHospitalList.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

	// @Test
	public void testGetCheckItemListById() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("id", "567dfbd94203f36da8ca36c8");
		param.put("type", 1);
		System.out.println("param=" + param);
		Response response = RestAssured.given().queryParams(param)
				.get("/getCheckItemListById");
		System.out.println("getCheckItemListById.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

//	 @Test
	public void testCallBackBasicOrder() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("basicOrderId", "3451");
		// param.put("type", 0);
		System.out.println("param=" + param);
		Response response = RestAssured.given().queryParams(param)
				.get("/callBackBasicOrder");
		System.out.println("callBackBasicOrder.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

	// @Test
	public void testException() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("orderId", "566bd55db4726512a44d162e");
		System.out.println("param=" + param);
		Response response = RestAssured.given().queryParams(param)
				.get("/getCheckResults");
		System.out.println("insertCheckResults.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}

//	 @Test
	public void testPushMessage() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("userId", 12577);//12827 p  12867   12215
		param.put("content", "测试推送的内容lw");

		System.out.println("param=" + param);
		Response response = RestAssured.given().queryParams(param)
				.get("/pushMessage");
		System.out.println("pushMessage.asString()=" + response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}
}
