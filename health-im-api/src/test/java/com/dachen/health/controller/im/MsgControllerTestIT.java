package com.dachen.health.controller.im;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class MsgControllerTestIT  extends BaseControllerTest {

	@Before
    public void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		//String access_token = jp.get("data.access_token");
//		loginData=TestUtil.testGetLoginToken(TestConstants.doc_telephone,TestConstants.doc_password,TestConstants.doc_userType);
		loginData=TestUtil.testGetLoginToken("13168737738","123456",TestConstants.userType_doc);
		data=((Map<String,Object>)loginData.get("data"));
		// 获取登录token
		access_token =(String)((Map<String,Object>)loginData.get("data")).get("access_token"); 
        RestAssured.basePath = "/im/msg";
    }
	
	 /**
     * 测试发送消息 (522,559)
     */
   // @Test 
    public void testSendMsg() throws UnsupportedEncodingException {
          Response response= given(). param("access_token", access_token)
        		  			.param("type", 1)
        		  			.param("fromUserId", 522)
//        		  			.param("fromUserId", 559)
        		  			.param("gid", "W5VALVLN68")
        		  			.param("content", "发送者522,你好559-接收者！")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
        		  			.post("/send");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    }
    
    /**
     * 测试发送消息 (522,559)
     */
    @Test 
    public void testSendOvertimeMsg() throws UnsupportedEncodingException {
          Response response= given(). param("access_token", access_token)
        		  			.param("gid", "8E6DNN3YV5")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
        		  			.post("/sendOvertimeMsg");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    }
    
    /**
     * 测试发送消息 (522,559)
     */
    @Test 
    public void testSendOvertimeMsgForGuide() throws UnsupportedEncodingException {
          Response response= given(). param("access_token", access_token)
        		  			.param("gid", "guide_245")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
        		  			.post("/sendOvertimeMsg");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    }
    
    /**
     * 测试获取消息
     */
//    @Test 
    public void testGetMsg() throws UnsupportedEncodingException {
    	
//    	  Response response1= given(). param("access_token", access_token)
//		  			.param("type", 1)
//		  			.param("fromUserId", 522)
////		  			.param("fromUserId", 559)
//		  			.param("groupId", "W5VALVLN68")
//		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
//		  			.post("/send");
//    	  JsonPath jp = new JsonPath(response1.asString());  
//    	  String gid=jp.get("data.gid");

          Response response= given(). param("access_token", access_token)
        		  			.param("userId", 27)
        		  			.param("groupId","IB0DALMEAN")
        		  			.param("type", 0)  //获取新消息
//        		  			.param("msgId", "N5SSTL25H5")
        		  			.param("cnt", "20")
        		  			.post("/get");
    	  JsonPath jp2 = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp2.get("resultCode")); 
    	  
    } 
//    @Test 
    public void testCreateGroup() throws UnsupportedEncodingException {
    	
//  	  Response response1= given(). param("access_token", access_token)
//		  			.param("type", 1)
//		  			.param("fromUserId", 522)b
////		  			.param("fromUserId", 559)
//		  			.param("groupId", "W5VALVLN68")
//		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
//		  			.post("/send");
//  	  JsonPath jp = new JsonPath(response1.asString());  
//  	  String gid=jp.get("data.gid");

        Response response= given(). param("access_token", access_token)
      		  			.param("toUserId", 410)
      		  			.param("fromUserId",374)
      		  			.param("type", 1)  //获取新消息
      		  			.param("gtype", "3_3,")
      		  			.post("/createGroup");
  	  JsonPath jp2 = new JsonPath(response.asString());  
  	  assertEquals(new Integer(1),jp2.get("resultCode")); 
  	  
  } 
    
    /**
     * 测试业务轮询
     */
//    @Test 
    public void testGetBusiness() throws UnsupportedEncodingException {
    	 
          Response response= given(). param("access_token", access_token)
        		  			.param("userId", 511)
        		  			.param("ts",0)
        		  			.param("cnt", 50)  
        		  			.post("/getBusiness");    	
          JsonPath jp = new JsonPath(response.asString());  
//    	  System.out.println(response.asString());
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    }
    
    /**
     * 测试业务轮询
     */
//    @Test 
    public void testGetGroupInfo() throws UnsupportedEncodingException {
    	 
          Response response= given(). param("access_token", access_token)
        		  			.param("userId", 559)
        		  			.param("gid", "W5VALVLN68")
        		  			.post("/groupInfo");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    }

}
