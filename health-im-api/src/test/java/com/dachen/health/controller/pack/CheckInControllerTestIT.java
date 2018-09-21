package com.dachen.health.controller.pack;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class CheckInControllerTestIT {
    public static String access_token;
    @BeforeClass
    public  static void setUp() throws UnsupportedEncodingException   {
        
        RestAssured.baseURI = TestConstants.baseURI;
        RestAssured.port = TestConstants.port;
     
    }

	 /**
     * 患者报到
     */
    //@Test
    public void testAdd() throws UnsupportedEncodingException {
        access_token=TestUtil.testGetToken("20009",TestConstants.password_doc,"1");
        RestAssured.basePath="/pack/checkIn";
        
          Response response= given(). param("access_token", access_token)
        		  			.param("patientId", 940)
        		  			. param("doctorId", 216)
        		  			. param("phone", "13800000000")
        		  			. param("imageUrls","http://192.168.3.7:8081/af/201509/checkin/85a4050981364490acdbff242ae013e8.png" )
        		  			. param("hospital","协和医院" )
        		  			. param("recordNum","123456" )
        		  			. param("lastCureTime","1441883108086" )
        		  			. param("description","胃癌晚期" )
        		  			. param("message","李医生，救救我" )
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/add");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    

    /**
     * 报到列表
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testList() throws UnsupportedEncodingException {
        access_token=TestUtil.testGetToken("10010",TestConstants.password_doc,"3");
        RestAssured.basePath="/pack/checkIn";
          Response response= given(). param("access_token", access_token)
        		  			.param("status", 1)
        		  			.param("pageIndex", 0) 
        		  			.get("/list");
          
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    /**
     * 报到详情
     */
//    @Test
    public void testDetail() throws UnsupportedEncodingException {
        access_token=TestUtil.testGetToken("10010",TestConstants.password_doc,"3");
        RestAssured.basePath="/pack/checkIn";
          Response response= given(). param("access_token", access_token)
        		  			.param("checkInId", 144)
        		  			.post("/detail");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  assertNotNull(jp.get("data")); 
    }
    
    /**
     * 处理报到
     */
    @Test
    public void testUpdate() throws UnsupportedEncodingException {
        access_token=TestUtil.testGetToken("10010",TestConstants.password_doc,"3");
        RestAssured.basePath="/pack/checkIn";
        Response response= given(). param("access_token", access_token)
                        .param("checkInId", 68)
                        .param("status", 2)
                        .post("/update");
        JsonPath jp = new JsonPath(response.asString());  
        assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    /**
     * 处理报到
     */
    @Test
    public void testUpdateCase() throws UnsupportedEncodingException {
        access_token=TestUtil.testGetToken("10010",TestConstants.password_doc,"3");
        RestAssured.basePath="/pack/checkIn";
        Response response= given(). param("access_token", access_token)
                        .param("id", 128)
                        .param("message", "update case by caseId"+163)
                        .post("/updateCase");
        JsonPath jp = new JsonPath(response.asString());  
        assertEquals(new Integer(1),jp.get("resultCode")); 
    }

    /**
     * 报到详情
     */
    @Test
    public void testGetNewCheckInCount() throws UnsupportedEncodingException {
        access_token=TestUtil.testGetToken("10010",TestConstants.password_doc,"3");
        RestAssured.basePath="/pack/checkIn";
          Response response= given(). param("access_token", access_token)
        		  			.param("doctorId", 144)
        		  			.post("/getNewCheckInCount");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  assertNotNull(jp.get("data")); 
    }
}
