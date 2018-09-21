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

public class OrderExpandControllerIT {
	
	public static String access_token;


	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc2,TestConstants.password_doc2,TestConstants.userType_doc2);
		RestAssured.basePath = "/pack/orderExpand";
	 
	}
	
   // @Test
    public void testGetSchedule() throws UnsupportedEncodingException {
    	
        Response response= given(). param("access_token", access_token)
      		  			.param("searchDate", "2015-09-22")
      		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
      		  			.post("/getSchedule");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  assertEquals(new Integer(1),jp.get("resultCode")); 
  	
    }
    
    @Test
    public void testScheduleDetail() throws UnsupportedEncodingException {
    	
        Response response= given(). param("access_token", access_token)
      		  			.param("searchDate", "2016-01-14")
      		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
      		  			.post("/scheduleDetail");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  assertEquals(new Integer(1),jp.get("resultCode")); 
  	
    }
    
    //@Test
    public void testScheduleTime() throws UnsupportedEncodingException {
    	
    	Response response= given(). param("access_token", access_token)
    			.param("searchDate", "2015-09-22")
    			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
    			.post("/scheduleTime");
    	JsonPath jp = new JsonPath(response.asString());
    	//System.out.println(jp.get("data").toString());
    	assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
   // @Test
    public void testGetSchedules() throws UnsupportedEncodingException {
    	
    	Response response= given(). param("access_token", access_token)
    			.param("searchDate", "2015-11-11")
    			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
    			.post("/getSchedules");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
   // @Test
    public void testGet3Schedule() throws UnsupportedEncodingException {
    	
    	Response response= given(). param("access_token", access_token)
    			.param("startDate", 1449572809379L)
    			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
    			.post("/get3Schedule");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
    //@Test
    public void testAutoSendSchedule() throws UnsupportedEncodingException {
    	
    	Response response= given(). param("access_token", "b462ee30c1e44d2eaba631709166540a")
    			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
    			.post("/autoSendSchedule");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
}
