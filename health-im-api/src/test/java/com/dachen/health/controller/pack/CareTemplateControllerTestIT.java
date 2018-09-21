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

public class CareTemplateControllerTestIT {
	
	public static String access_token;
	
	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc2,TestConstants.password_doc2,TestConstants.userType_doc2);
		RestAssured.basePath = "/pack/care";
	 
	}
	
	//@Test
	public void testSaveCareTemplate() {
		
		Response response= given().param("access_token", access_token)
	  			.param("categoryId", "NK0101")
	  			.param("careName", "测试关怀计划")
	  			.param("careDesc", "测试案例数据")
	  			.param("price", "10000~20000")
	  			.param("scales", "392652f422de4e1abb535168b6c3071a,c72ad4be39d347a18ee518046015d73d")
	  			.param("remind", "d8ad78e4cb6745c6aef173785680fd3b")
	  			.param("tracks", "256202e4da2c48ceab2c32951dd3905d")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/saveCareTemplate");
				JsonPath jp = new JsonPath(response.asString());  
				assertEquals(new Integer(1),jp.get("resultCode")); 
		
	}
		
	
	//@Test
	public void testSaveCareTemplateByCare() {
		
		Response response= given().param("access_token", access_token)
	  			.param("categoryId", "EK0101")
	  			.param("groupId", "55d588fccb827c15fc9d47b3")
	  			.param("tmpType", 2)
	  			.param("careName", "病患随访表1")
	  			.param("cares", "2476c8a3fd20400aa88305faac6411c0")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/saveCareTemplateByCare");
				JsonPath jp = new JsonPath(response.asString());  
				assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	//@Test
	public void testQueryCareTemplateItemDetailByCare() {
		
		Response response= given(). param("access_token", access_token)
	  			.param("templateId", "a7aada40b80e4622a5826d0e32231b76")
	  			.param("groupId", "2476c8a3fd20400aa88305faac6411c0")
	  			.param("tmpType", 2)
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/queryCareTemplateItemDetailByCare");
				JsonPath jp = new JsonPath(response.asString());  
				assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	//@Test
	public void testdeleteCareTempateByCare() {
		Response response= given(). param("access_token", access_token)
	  			.param("templateId", "a7aada40b80e4622a5826d0e32231b76")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/deleteCareTempateByCare");
				JsonPath jp = new JsonPath(response.asString());  
				assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	//@Test
	public void testQueryCareTemplateItem() {
		
		Response response= given(). param("access_token", access_token)
	  			.param("categoryId", "CR0713")
	  			.param("type", "3")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/queryCareTemplateItem");
				JsonPath jp = new JsonPath(response.asString());  
				assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	//@Test
	public void testQueryCareTemplateDetail() {
		Response response= given(). param("access_token", access_token)
	  			.param("templateId", "17c0dee09a614abab8d11d0516b11b81")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/queryCareTemplateDetail");
				JsonPath jp = new JsonPath(response.asString());  
				assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	//@Test
	public void testFindCareTemplateById() {
		Response response= given(). param("access_token", access_token)
	  			.param("careId", "392652f422de4e1abb535168b6c3071a")
	  			.param("type", "1")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/findCareTemplateById");
				JsonPath jp = new JsonPath(response.asString());  
				assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	//@Test
	public void testQueryCareTemplate() {
		
		Response response= given(). param("access_token", access_token)
				.param("categoryId", "CR0101")
				.param("groupId", "564be9a44203f35eda0868ed")
				.param("tmpType", 1)
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/queryCareTemplate");
				JsonPath jp = new JsonPath(response.asString());  
				assertEquals(new Integer(1),jp.get("resultCode")); 
		
	}
	
	@Test
	public void testQueryCareTemplateStore() {
		
		Response response= given(). param("access_token", access_token)
				.param("doctorId", "784")
				.param("groupId", "564be9a44203f35eda0868ed")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/queryCareTemplateStore");
				JsonPath jp = new JsonPath(response.asString());  
				assertEquals(new Integer(1),jp.get("resultCode")); 
		
	}
	
	//@Test
	public void testDelCareTemplate() {
		Response response= given(). param("access_token", access_token)
	  			.param("templateId", "6763ae0abfd2464ebf9bbe3705cc1c91")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/delCareTemplate");
				JsonPath jp = new JsonPath(response.asString());  
				assertEquals(new Integer(1),jp.get("resultCode")); 
		
	}
	
	

	//@Test
	public void testQueryCarePlanItem() {
		Response response= given(). param("access_token", access_token)
	  			.param("templateId", "56c533174203f30dcc023af5")
	  			.param("type", "3")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/queryCarePlanItem");
				JsonPath jp = new JsonPath(response.asString());  
				assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	
	
}
