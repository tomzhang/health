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

public class CarePlanContollerTestIT {
	
public static String access_token;
	
	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc2,TestConstants.password_doc2,TestConstants.userType_doc2);
		RestAssured.basePath = "/pack/carePlan";
	 
	}
	//@Test
	public void testQueryCarePlanByOne() {
		
		Response response= given(). param("access_token", access_token)
	  			.param("packId", "927")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/queryCarePlanByOne");
				JsonPath jp = new JsonPath(response.asString());  
				assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	//@Test
	public void testGetCareQuestionList() {
		
		Response response= given(). param("access_token", access_token)
	  			.param("dateTime", "2015-10-30")
	  			.param("type", "3")
	  			.param("orderId", "2066")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/getCareQuestionList");
				JsonPath jp = new JsonPath(response.asString());  
				assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	//@Test
	public void testQueryCarePlanByOrder() {
		
		Response response= given(). param("access_token", access_token)
	  			.param("orderId", "2066")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/queryCarePlanByOrder");
				JsonPath jp = new JsonPath(response.asString());  
				assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	//@Test
	public void testFindCareOrderGroupByDate() {
		
		Response response= given(). param("access_token", access_token)
	  			.param("orderId", "2066")
	  			.param("type", "1")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/findCareOrderGroupByDate");
				JsonPath jp = new JsonPath(response.asString());  
				assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	

}
