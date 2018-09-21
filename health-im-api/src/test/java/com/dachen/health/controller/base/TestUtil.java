package com.dachen.health.controller.base;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class TestUtil {
	
	/**
	 * 使用一个用户登录，得到用户的acces_token
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String,Object> testGetLoginToken(String telephone,String password,String userType)
			throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		RestAssured.basePath="/user";

		Map<String, String> param = new HashMap<String, String>();
		param.put("telephone", telephone);
		param.put("password", password);
		param.put("userType", userType);
		
		Response response1 =given().queryParameters(param).get("/login") ;//get("/user/login?telephone=15587037745&password=123&userType=2");
		assertEquals(200, response1.getStatusCode());
		String json = response1.asString();
		JsonPath jp = new JsonPath(json);
		Map<String,Object> ret=jp.get();
		return ret;
	}
	
	/**
	 * 使用一个用户登录，得到用户的acces_token
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static String testGetToken(String telephone,String password,String userType)
			throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		RestAssured.basePath="/user";

		Map<String, String> param = new HashMap<String, String>();
		param.put("telephone", telephone);
		param.put("password", password);
		param.put("userType", userType);
		
		Response response1 =given().queryParameters(param).get("/login") ;//get("/user/login?telephone=15587037745&password=123&userType=2");
		assertEquals(200, response1.getStatusCode());
		String json = response1.asString();
		JsonPath jp = new JsonPath(json);
		Map<String,Object> loginData=jp.get();
		// 获取登录token
		System.out.println("loginData="+loginData.get("data"));
		String access_token =(String)((Map<String,Object>)loginData.get("data")).get("access_token"); 
		return access_token;
	}
}
