package com.dachen.health.controller.group;

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

public class GroupProfitControllerTestIT {
	public static String access_token;


	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc2,TestConstants.password_doc2,TestConstants.userType_doc2);
		RestAssured.basePath = "/group/profit";
	 
	}
	
	//@Test
	public void testGetList() {
		Response response = given().param("access_token", access_token)
				.param("groupId", "55d588fccb827c15fc9d47b3")
				.param("parentId", 0)
				.param("start", 0)
				.param("pageSize", 20)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getList");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
//    @Test
    public void testSearchByKeyword() {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("groupId", "55d588fccb827c15fc9d47b3")
        		  			.param("keyword", "户")
        		  			.param("pageIndex", 0)
        		  			.param("pageSize", 10)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/searchByKeyword");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    	
    }
}
