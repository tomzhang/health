package com.dachen.health.controller.pack;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class VoipControllerTestIT  {


	public static String access_token;


	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone,TestConstants.password,TestConstants.userType_doc);
		RestAssured.basePath = "/voip";
	 
	}
	
	 /**
     * 测试处理
     */
//    @Test
    public void testCall() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("toUserId", 573)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/call");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
	 /**
     * 测试处理
     */
//    @Test
    public void testCallByOrder() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("orderId", 850)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/callByOrder");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
    @Test
    public void getAuthcationInfo(){
    	RestAssured.basePath = "/voip";

		String body = (new StringBuilder("<?xml version='1.0'?><request>")
	            .append(" <event>confCreatess</event>")
	            .append("<accountid>ab529121937c1da5b0e14249b1d97fee</accountid>")
	            .append("<appid>6feff93230654ff889754d0532f71a4f</appid>")
	            .append("</request>")).toString();
		
		Response response=RestAssured.given()
				.header("Content-Type", "application/xml")
				.body(body)
				.post("/getAuthcationInfo");

		JsonPath jp = new JsonPath(response.asString());  
		System.err.println(response.asString());
    }
    
}
