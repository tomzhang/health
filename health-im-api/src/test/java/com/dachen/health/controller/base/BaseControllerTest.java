package com.dachen.health.controller.base;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;

import com.jayway.restassured.RestAssured;
@SuppressWarnings("unchecked")
public class BaseControllerTest {
	public String access_token;
	public Map<String,Object> loginData;
	public Map<String,Object> data;
	
	public Map<String, Object> param = new HashMap<String, Object>();

	
	@Before
	public void setUp() throws UnsupportedEncodingException { 
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		//String access_token = jp.get("data.access_token");
		loginData=TestUtil.testGetLoginToken(TestConstants.telephone,TestConstants.password,TestConstants.userType);
		data=((Map<String,Object>)loginData.get("data"));
		// 获取登录token
		access_token =(String)((Map<String,Object>)loginData.get("data")).get("access_token"); 
		param.clear();
		param.put("access_token", access_token);
	}
}
