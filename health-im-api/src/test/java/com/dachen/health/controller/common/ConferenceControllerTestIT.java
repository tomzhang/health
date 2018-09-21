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

public class ConferenceControllerTestIT extends BaseControllerTest{
	
	@Override
	public void setUp() throws UnsupportedEncodingException {
		super.setUp();
	}
	
//	@Test
	public void testCreate(){
		RestAssured.basePath="/conference";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("orderId", 2731);
		Response response=RestAssured.given().queryParams(param).get("/createConference");
		JsonPath jp = new JsonPath(response.asString());  
   	  	System.out.println(response.asString());
   	  	assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
//	@Test
	public void testDismiss(){
		RestAssured.basePath="/conference";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", "7c59faf7bba943398c56791279db4124");
		param.put("callRecordId", "565923df3a21a90a5c32d4b5");
		Response response=RestAssured.given().queryParams(param).get("/dismissConference");
		JsonPath jp = new JsonPath(response.asString());  
   	  	System.out.println(response.asString());
   	  	assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
//	@Test
	public void testRemove(){
		RestAssured.basePath="/conference";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("callRecordId", "564ef46a3a21a9208cb42c1e");
		param.put("userId", "11907");
		Response response=RestAssured.given().queryParams(param).get("/removeConference");
		JsonPath jp = new JsonPath(response.asString());  
   	  	System.out.println(response.asString());
   	  	assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
//	@Test
	public void testInvite(){
		RestAssured.basePath="/conference";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("callRecordId", "565827273a21a91f14bd5260");
		param.put("userId", "921");
		Response response=RestAssured.given().queryParams(param).get("/inviteMember");
		JsonPath jp = new JsonPath(response.asString());  
   	  	System.out.println(response.asString());
   	  	assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
//	@Test
	public void testMuteAndUmute(){
		RestAssured.basePath="/conference";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("callRecordId", "565827273a21a91f14bd5260");
		param.put("userId", "921");
		Response response=RestAssured.given().queryParams(param).get("/unDeafConference");
		JsonPath jp = new JsonPath(response.asString());  
   	  	System.out.println(response.asString());
   	  	assertEquals(new Integer(1),jp.get("resultCode")); 
//   	  	try {unDeafConference
//			Thread.sleep(10*1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}unDeafConference
//   	    response=RestAssured.given().queryParams(param).get("/deafConference");
//		jp = new JsonPath(response.asString());  
//	  	System.out.println(response.asString());
//	  	assertEquals(new Integer(1),jp.get("resultCode")); 
	}
//	@Test
	public void testEndCall(){
		RestAssured.basePath="/conference";
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("access_token", access_token);
		String body = (new StringBuilder("<?xml version='1.0'?><request>")
	            .append(" <event>confCreate</event>")
	            .append("<appId>3f3f6b8f1f4143198ee65511dbb77daf</appId>")
	            .append("<confId>22222223</confId>")
	            .append("</request>")).toString();
		
		Response response=RestAssured.given()
				.header("Host", "127.0.0.1")
//				.header("Accept", "application/xml")
				.header("Content-Type", "application/xml")
				.queryParams(param)
				.body(body)
				.post("/endCall");
		JsonPath jp = new JsonPath(response.asString());  
   	  	System.out.println(response.asString());
   	  	assertEquals(new Integer(1),jp.get("resultCode")); 
	}
//	@Test
	public void testGetCallRecord(){
		RestAssured.basePath="/conference";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("orderId", 705);
		Response response=RestAssured.given().queryParams(param).get("/getCallRecord");
		JsonPath jp = new JsonPath(response.asString());  
   	  	System.out.println(response.asString());
   	  	assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
//	@Test
	public void testGetCommentByConfId(){
		RestAssured.basePath="/conference";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("confId", "5655b9a03a21a9215c8a90a8");
		Response response=RestAssured.given().queryParams(param).get("/getCommentByConfId");
		JsonPath jp = new JsonPath(response.asString());  
   	  	System.out.println(response.asString());
   	  	assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
//	@Test
	public void testGetStatus(){
		RestAssured.basePath="/conference";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("callRecordId", "5655b9a03a21a9215c8a90a8");
		Response response=RestAssured.given().queryParams(param).get("/getStatus");
		JsonPath jp = new JsonPath(response.asString());  
   	  	System.out.println(response.asString());
   	  	assertEquals(new Integer(1),jp.get("resultCode")); 
	}
//	@Test
	public void testGetLastConference(){
		RestAssured.basePath="/conference";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", "7c59faf7bba943398c56791279db4124");
		param.put("orderId", 2731);
		Response response=RestAssured.given().queryParams(param).get("/getLastConference");
		JsonPath jp = new JsonPath(response.asString());  
   	  	System.out.println(response.asString());
   	  	assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
//	@Test
	public void testGetConferenceRecordStatus(){
		RestAssured.basePath="/conference";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("orderId", 3467);
		Response response=RestAssured.given().queryParams(param).get("/getConferenceRecordStatus");
		JsonPath jp = new JsonPath(response.asString());  
   	  	System.out.println(response.asString());
   	  	assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
}
