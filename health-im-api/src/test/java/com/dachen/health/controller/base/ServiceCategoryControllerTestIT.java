package com.dachen.health.controller.base;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class ServiceCategoryControllerTestIT {

	public String access_token;
	
	@Before
    public void setUp() throws UnsupportedEncodingException {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = 8080;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc3,TestConstants.password_doc3,TestConstants.userType_doc3);
		RestAssured.basePath="/serviceCate";
    }
	
	@Test
	public void testgetById()  throws UnsupportedEncodingException{
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		Response response= given(). param("access_token", access_token)
		  			. param("Id", "5721afe7f95c43d41203d233")
		  			.post("/getById");
		JsonPath jp = new JsonPath(response.asString());  
		Integer code =1;
		assertEquals(new Integer(1),code); 
	}
}
