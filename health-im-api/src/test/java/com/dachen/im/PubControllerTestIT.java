package com.dachen.im;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class PubControllerTestIT extends BaseControllerTest {
	@Before
    public void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		loginData=TestUtil.testGetLoginToken("18689208527","123456",TestConstants.userType_doc);
		data=((Map<String,Object>)loginData.get("data"));
		// 获取登录token
		access_token =(String)((Map<String,Object>)loginData.get("data")).get("access_token"); 
        RestAssured.basePath = "/im/msg";
    }
	
//	@Test 
    public void testGetMsg() throws UnsupportedEncodingException {
          Response response= given(). param("access_token", access_token)
        		  			.param("userId", 793)
        		  			.param("groupId","pub_793")
        		  			.param("type", 0)  //获取新消息
        		  			.param("cnt", "20")
        		  			.post("/get");
    	  JsonPath jp2 = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp2.get("resultCode")); 
    } 
	
//	@Test 
    public void testGetBusiness() throws UnsupportedEncodingException {
          Response response= given(). param("access_token", access_token)
        		  			.param("userId", "245")
        		  			.param("ts","1448443826200")
        		  			.param("cnt", 20)  
        		  			.post("/getBusiness");    	
          JsonPath jp = new JsonPath(response.asString());  
//    	  System.out.println(response.asString());
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    }
    
//    @Test 
    public void testDelMsg() throws UnsupportedEncodingException {
    	RestAssured.basePath = "/pub";
        Response response= given(). param("access_token", access_token)
      		  			.param("id", "5656872605bbfc2410285d4b")
      		  			.post("/delMsg");    	
        JsonPath jp = new JsonPath(response.asString());  
//  	  System.out.println(response.asString());
  	  assertEquals(new Integer(1),jp.get("resultCode")); 
  	  
  }
}
