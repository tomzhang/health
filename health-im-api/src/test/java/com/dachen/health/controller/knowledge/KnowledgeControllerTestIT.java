package com.dachen.health.controller.knowledge;

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

public class KnowledgeControllerTestIT {

	public String access_token;
	@Before
	public void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken("13316827932",TestConstants.password_com,TestConstants.userType_doc3);
		RestAssured.basePath="/knowledge";
	}
	
	@Test
	public void testFindKnowledgeListByKeys(){
		Response response= given(). param("access_token", access_token)
					.param("keywords", "张")
					.param("groupId", "56f9da3eb522256dcd06be2d")
					.get("/findKnowledgeListByKeys");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	@Test
	public void testGetKnowledgeListByCategoryId(){
		Response response= given(). param("access_token", access_token)
					.param("categoryId", "56f9da3eb522256dcd06be2d")
					.get("/getKnowledgeListByCategoryId");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	@Test
	public void testGetCategoryList(){
		Response response= given(). param("access_token", access_token)
					.param("groupId", "56f9da3eb522256dcd06be2d")
					.get("/getCategoryList");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	@Test
	public void testUpdateCategoryById(){
		Response response= given(). param("access_token", access_token)
					.param("id", "57996eab4203f35ca798b917")
					.param("name", "56561")
					.get("/updateCategoryById");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	@Test
	public void testDelCategoryById(){
		Response response= given(). param("access_token", access_token)
					.param("id", "57996e674203f35ca798b915")
					.get("/delCategoryById");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	@Test
	public void testAddGroupCategory(){
		Response response= given(). param("access_token", access_token)
					.param("name", "AAA")
					.param("groupId", "56f9da3eb522256dcd06be2d")
					.get("/addGroupCategory");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	@Test
	public void testDelKnowledgeById(){
		Response response= given(). param("access_token", access_token)
					.param("id", "579f0ad24203f32344f994c5")
					.get("/delKnowledgeById");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testGetGroupMedicalKnowledgeList(){
		Response response= given(). param("access_token", access_token)
					.param("id", "57996e674203f35ca798b915")
					.param("groupId", "56dab739b522256c670000d9")
					.get("/getGroupMedicalKnowledgeList");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test 
	public void testGetDoctorMedicalKnowledgeList(){
		Response response = given().param("access_token", access_token)
				.param("id", "57996e674203f35ca798b915")
				.param("doctorId", 102137)
				.get("/getDoctorMedicalKnowledgeList");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	@Test 
	public void testGetUrlById(){
		Response response = given().param("access_token", access_token)
				.param("id", "579af62e4203f320b85d8dbe")
				.get("/getUrlById");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	@Test 
	public void testGetDetailById(){
		Response response = given().param("access_token", access_token)
				.param("id", "579af62e4203f320b85d8dbe")
				.get("/getDetailById");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	@Test 
	public void testSetTop(){
		Response response = given().param("access_token", access_token)
				.param("id", "579af4d74203f320b85d8dbd")
				.param("bizId", "56dab739b522256c670000d9")
				.get("/setTop");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	@Test 
	public void testCancelTop(){
		Response response = given().param("access_token", access_token)
				.param("id", "579af62e4203f320b85d8dbe")
				.param("bizId", "56dab739b522256c670000d9")
				.get("/cancelTop");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	@Test 
	public void testUpKnowledge(){
		Response response = given().param("access_token", access_token)
				.param("id", "579b0fcc4203f320b85d8e14")
				.param("bizId", "56dab739b522256c670000d9")
				.get("/upKnowledge");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
}
