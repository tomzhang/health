package com.dachen.health.controller.wx;

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

public class WXControllerTestIT {

	public static String access_token;
	
	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken("10010",TestConstants.password_doc,TestConstants.userType_doc);
		RestAssured.basePath="/wx";
	}
	
	//@Test
	public void testGetConfig() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("url", "http://192.168.3.47:8081/wechat/");
		Response response=RestAssured.given().queryParams(param).get("/getConfig");
		JsonPath json = new JsonPath(response.asString());
		int resultCode= json.getInt("resultCode");
		
		Assert.assertEquals( 1,resultCode);
	}
}
