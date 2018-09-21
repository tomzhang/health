package com.dachen.health.controller.sig;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class SigcheckControllerTestIT  { 

	private String access_token;
	@Before
    public void setUp() throws UnsupportedEncodingException {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc3,TestConstants.password_doc3,TestConstants.userType_doc3);
		RestAssured.basePath="/sig";
    }
//	@Test
    public void testupdateCheckbill() throws UnsupportedEncodingException {
     Response response= given(). param("access_token", access_token).post("/getSig");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  System.out.println(jp.get("resultCode").toString());
  	  assertEquals(new Integer(1),jp.get("resultCode"));
   }
}
