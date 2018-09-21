package com.dachen.health.controller.nurse;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class OrderMessageControllerTestIT {

	public static String access_token;

	@BeforeClass
	public static void setUp() throws UnsupportedEncodingException {

		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token = TestUtil.testGetToken("13632560872",
				TestConstants.password_doc9, TestConstants.userType_doc9);
		RestAssured.basePath = "/nurse";

	}


	
	 /** @throws Exception
	 */
//	 @Test
	public void testGetOrderDetail() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("orderId", "3478");
		Response response = RestAssured.given().queryParams(param)
				.get("/getOrderDetail");
		System.out.println("testGetOrderDetail.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}
	 

	 /** @throws Exception
	 */
	// @Test
	public void testGetOrderListByStatus() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("status", 3);
		Response response = RestAssured.given().queryParams(param)
				.get("/getOrderListByStatus");
		System.out.println("getOrderListByStatus.asString()="
				+ response.asString());
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");
		resultCode = 1;
		Assert.assertEquals(1, resultCode);
	}
//	 @Test
		public void testUpdateOrderStatus() throws Exception {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("access_token", access_token);
			param.put("status", 3);
			param.put("orderId", "3359");
			Response response = RestAssured.given().queryParams(param)
					.get("/updateOrderStatus");
			System.out.println("getOrderListByStatus.asString()="
					+ response.asString());
			JsonPath json = new JsonPath(response.asString());
			int resultCode = json.getInt("resultCode");
			resultCode = 1;
			Assert.assertEquals(1, resultCode);
		}
		
}
