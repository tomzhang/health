package com.dachen.health.controller.pack;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class FastandReplyControllerIT {
	
	public static String access_token;


	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc2,TestConstants.password_doc2,TestConstants.userType_doc2);
		RestAssured.basePath = "/pack/fastandReply";
	 
	}
	
	//@Test
    public void addFastandReply() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("replyContent", "测试数据")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/addFastandReply");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
	
	//@Test
    public void deleteFastandReply() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("replyId", "560289161ba32614d0c6d583")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/deleteFastandReply");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
	
	//@Test
    public void updateFastandReply() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("replyId", "560289161ba32614d0c6d583")
        		  			.param("replyContent", "12312123123")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/updateFastandReply");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
	
	
}
