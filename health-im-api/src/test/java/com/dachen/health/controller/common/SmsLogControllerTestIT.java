package com.dachen.health.controller.common;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class SmsLogControllerTestIT extends BaseControllerTest {
	
	@Override
	public void setUp() throws UnsupportedEncodingException {
		super.setUp();
		RestAssured.basePath="/smsLog";
	}

	
	@Test
	public void testFind() throws Exception {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("access_token", access_token);
		//	param.put("access_token","dfc24a1515bc4bb5af44af5ca224e96f");
			Response response=RestAssured.given().queryParams(param).get("/find");
			
			  JsonPath jp = new JsonPath(response.asString());  
	    	  assertEquals(new Integer(1),jp.get("resultCode")); 
		
	}
}
