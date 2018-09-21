package com.dachen.health.controller.dynamic;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class DynamicPatientControllerTestIT{
	
	public String access_token;
	
	@Before
	public  void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken("13751132072",TestConstants.password_doc,"1");
		RestAssured.basePath="/dynamic";
	 
	}
	@Test
	public void getPatientRelatedDynamicList() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("pageSize", 3);
		param.put("createTime", 1469772999357l);

		

		Response response=RestAssured.given().queryParams(param).get("/getPatientRelatedDynamicList");
		JsonPath json = new JsonPath(response.asString());
		int resultCode= json.getInt("resultCode");
		System.out.println(response.asString());

		Assert.assertEquals( 1,resultCode);
	}
	
	
}
