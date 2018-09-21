package com.dachen.health.controller.dynamic;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class DynamicGroupControllerTestIT{
	
	public String access_token;
	
	@Before
	public  void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken("15017905927",TestConstants.password_doc,TestConstants.userType_doc);
		RestAssured.basePath="/dynamic";
	 
	}
	
	@Test
	public void addGroupDynamicForWeb() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("groupId", "55e46911b522252fc2083c78");
		param.put("title", "医生新增动态33");
		param.put("content", "<p>1233</p><p>44111</p>");
		param.put("contentUrl", "http://pic.dbw.cn/0/09/41/76/9417687_926365.jpg");
		Response response=RestAssured.given().queryParams(param).get("/addGroupDynamicForWeb");
		JsonPath json = new JsonPath(response.asString());
		System.out.println(response.asString());
		int resultCode= json.getInt("resultCode");
		System.out.println(resultCode);
		//Assert.assertEquals( 1,resultCode);

	}
	
	
	
	

	
	@Test
	public void getDynamicListByGroupIdForWeb() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("pageIndex", 0);
		param.put("pageSize", 5);
		param.put("groupId", "55e46911b522252fc2083c78");


		Response response=RestAssured.given().queryParams(param).get("/getDynamicListByGroupIdForWeb");
		JsonPath json = new JsonPath(response.asString());
		int resultCode= json.getInt("resultCode");
		System.out.println(response.asString());

		Assert.assertEquals( 1,resultCode);
	}
	
	@Test
	public void getGroupAndDoctorDynamicListByGroupId() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("pageIndex", 0);
		param.put("pageSize", 5);
		param.put("groupId", "55e46911b522252fc2083c78");


		Response response=RestAssured.given().queryParams(param).get("/getGroupAndDoctorDynamicListByGroupId");
		JsonPath json = new JsonPath(response.asString());
		int resultCode= json.getInt("resultCode");
		System.out.println(response.asString());

		Assert.assertEquals( 1,resultCode);
	}
	
	
	
}
