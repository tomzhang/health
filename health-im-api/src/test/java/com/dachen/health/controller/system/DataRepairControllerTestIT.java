package com.dachen.health.controller.system;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;


public class DataRepairControllerTestIT { 

	public static String access_token;
	
	@BeforeClass
    public static void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc,TestConstants.password_doc,TestConstants.userType_doc);
		RestAssured.basePath="/dataRepair";
    }

    /**
     * 删除脏数据
     */
//    @Test
    public void testDeleteUserData() throws UnsupportedEncodingException {
    	
        Response response= given(). param("access_token", access_token)
      		  			 .get("/deleteUserData");
        JsonPath jp = new JsonPath(response.asString());  
        assertEquals(new Integer(1),jp.get("resultCode")); 
  }
    
   
}
