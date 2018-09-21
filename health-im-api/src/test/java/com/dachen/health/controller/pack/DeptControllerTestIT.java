package com.dachen.health.controller.pack;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class DeptControllerTestIT extends BaseControllerTest {

	@Override
	public void setUp() throws UnsupportedEncodingException {
		super.setUp();
		RestAssured.basePath = "/dept";
	}

	

	@Test
	public void findByParent() {

		param.put("parentId", 2);

		Response response = RestAssured.given().queryParams(param)
				.get("/findByParent");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		System.out.println("access_token : "+access_token);
		Assert.assertEquals(1, resultCode);

	}



}
