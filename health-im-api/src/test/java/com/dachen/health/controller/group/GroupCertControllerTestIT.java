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

public class GroupCertControllerTestIT {

	public static String access_token;


	@BeforeClass
	public static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc2,TestConstants.password_doc2,TestConstants.userType_doc2);
		RestAssured.basePath = "/group/cert";
	 
	}
	
	@Test
	public void testSubmitCert() {
		Response response= given(). param("access_token", access_token)
	  			.param("groupId", "55dd1c924203f32e5c3ac5c5")
	  			.param("doctorId", 555)
	  			.param("companyName", "100医生集团，用于测试2")
	  			.param("orgCode", "888888")
	  			.param("license", "123456")
	  			.param("corporation", "lily")
	  			.param("accountName", "dfadsfsd")
	  			.param("openBank", "swdsfsdf")
	  			.param("bankAcct", "effsdfds")
	  			.param("adminName", "admin")
	  			.param("idNo", "430481198811111112")
	  			.param("adminTel", "13000000002")
	  			.param("idImage", "http://localhost")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
	  			.post("/submitCert");
		JsonPath jp = new JsonPath(response.asString());  
		
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	@Test
	public void testGetGroupCert() {
		Response response= given(). param("access_token", access_token)
	  			.param("groupId", "55d588fccb827c15fc9d47b3")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
	  			.post("/getGroupCert");
		JsonPath jp = new JsonPath(response.asString());  
		
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	@Test
	public void testGetGroupCerts() {
		Response response= given(). param("access_token", access_token)
	  			.param("status", "P")
	  			.param("keyword", "2")
	  			.param("pageIndex", 1)
	  			.param("pageSize", 1)
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
	  			.post("/getGroupCerts");
		JsonPath jp = new JsonPath(response.asString());  
		
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	//@Test
	public void testGetOtherGroupCerts() {
		Response response= given(). param("access_token", access_token)
				.param("groupId", "55dd1c924203f32e5c3ac5c5")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
	  			.post("/getOtherGroupCerts");
		JsonPath jp = new JsonPath(response.asString());  
		
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testUpdateRemarks() {
		Response response= given(). param("access_token", access_token)
				.param("groupId", "55d588fccb827c15fc9d47b3")
	  			.param("remarks", "资料全")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
	  			.post("/updateRemarks");
		JsonPath jp = new JsonPath(response.asString());  
		
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	//@Test
	public void testPassCert() {
		Response response= given(). param("access_token", access_token)
				.param("groupId", "55dd1c924203f32e5c3ac5c5")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
	  			.post("/passCert");
		JsonPath jp = new JsonPath(response.asString());  
		
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testNoPass() {
		Response response= given(). param("access_token", access_token)
				.param("groupId", "55d588fccb827c15fc9d47b3")
	  			.param("remarks", "资料不全")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
	  			.post("/noPass");
		JsonPath jp = new JsonPath(response.asString());  
		
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
}
