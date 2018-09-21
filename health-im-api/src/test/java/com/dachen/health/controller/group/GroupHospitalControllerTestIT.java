package com.dachen.health.controller.group;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class GroupHospitalControllerTestIT  extends  BaseControllerTest {
	@Override
	public void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		//String access_token = jp.get("data.access_token");
		loginData=TestUtil.testGetLoginToken(TestConstants.telephone_doc,TestConstants.password_doc,TestConstants.userType_doc);
		// 获取登录token
		access_token =(String)((Map<String,Object>)loginData.get("data")).get("access_token"); 
		param.clear();
		param.put("access_token", access_token);
		RestAssured.basePath="/group/hospital";
	}
	
   ///@Test
    public void createGroupHospital() throws UnsupportedEncodingException {
          Response response= given(). param("access_token", access_token)
        		  			.param("hospitalId", "201306060055").param("doctorId", 128)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
        		  			.post("/createGroupHospital");
          System.out.println(response.asString());
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    //@Test
    public void groupHospitalList() throws UnsupportedEncodingException {
    	Response response= given(). param("access_token", access_token).param("pageIndex", 5).param("pageSize", 2)
    			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
    			.post("/groupHospitalList");
    	System.out.println(response.asString());
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
   // @Test
    public void updateHospitalRoot() throws UnsupportedEncodingException {
    	Response response= given(). param("access_token", access_token).param("hospitalId", 1459416476949l).param("doctorId", 144431)
    			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
    			.post("/updateHospitalRoot");
    	System.out.println(response.asString());
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),jp.get("resultCode")); 
    }
 
}
