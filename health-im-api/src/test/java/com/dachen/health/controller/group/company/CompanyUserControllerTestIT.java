package com.dachen.health.controller.group.company;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.junit.Before;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class CompanyUserControllerTestIT   {
	public Map<String, Object> map;
	private static String access_token = null;
	private static Integer doctorId = null;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		map = TestUtil.testGetLoginToken(TestConstants.telephone_com,TestConstants.password_com,TestConstants.userType_com);
		access_token = (String) ((Map<String,Object>) map.get("data")).get("access_token");
		doctorId = (Integer) ((Map<String,Object>) map.get("data")).get("userId");
		RestAssured.basePath="/company/user";
		
	 
	}

	/**
     * 测试公司账户登录
     */
//	@Test
    public void testLogoCompany() {
    	Response response = given(). param("access_token", map.get("access_token").toString())
				.param("id", "55e698404203f371ec789855")
    			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
	  			.post("/getCompanyById");
				System.out.println(response.print());
				JsonPath jp = new JsonPath(response.asString());
				
				assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    
    
    
    
}
