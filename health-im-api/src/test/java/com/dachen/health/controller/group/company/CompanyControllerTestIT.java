package com.dachen.health.controller.group.company;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class CompanyControllerTestIT   {
	public String access_token;
	
	@Before
	public void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_com,TestConstants.password_com,TestConstants.userType_com);
		RestAssured.basePath="/company";
		
	 
	}


	
	  /**
     * 测试注册公司
     */
//    @Test
    public void testRegCompany() throws UnsupportedEncodingException {
    	 
          Response response= given(). param("access_token", "a8fcb535f9284df0985a0c8d1b54af8a")
        		  			.param("name", "100医生公司test")
        		  			.param("description", "100医生公司，用于测试")
        		  			.param("corporation", "dachen")
        		  			.param("license", "1234")
        		  			.param("bankAccount", "1234")
        		  			.param("bankNumber", "1234")
        		  			.param("openBank", "1234")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
        		  			.post("/regCompany");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    }
    
    /**
     * 测试获取公司信息
     */
//    @Test
    public void testGetCompany() {
    	Response response = given(). param("access_token", access_token)
    						.param("id", "55e698404203f371ec789855")
			    			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
				  			.post("/getCompanyById");
    	System.out.println(response.print());
		JsonPath jp = new JsonPath(response.asString());
		
		assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    
}
