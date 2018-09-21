package com.dachen.health.controller.group.fee;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class FeeControllerTestIT {

	public static String access_token;


	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc2,TestConstants.password_doc2,TestConstants.userType_doc2);
		RestAssured.basePath = "/group/fee";
	 
	}
	
	@Test
	public void testFeeSetting() {
		Response response= given(). param("access_token", access_token)
	  			.param("groupId", "55d588fccb827c15fc9d47b3")
	  			.param("phoneMin", 10)
	  			.param("phoneMax", 1000)
	  			.param("carePlanMin", 10)
	  			.param("carePlanMax", 1000)
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
	  			.post("/setting");
		JsonPath jp = new JsonPath(response.asString());  
		
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testGetFee() {
		Response response= given(). param("access_token", access_token)
	  			.param("groupId", "55d588fccb827c15fc9d47b3")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
	  			.get("/get");
		JsonPath jp = new JsonPath(response.asString());  
		
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
}
