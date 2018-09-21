package com.dachen.health.controller.app;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class AppControllerTestIT extends BaseControllerTest {

	//private String access_token;
	@Before
    public void setUp() throws UnsupportedEncodingException {
        RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc,TestConstants.password_doc,TestConstants.userType_doc);
		RestAssured.basePath = "/appService";
    }
	/**
	 * 测试药品核查接口
	 */
	/*@Test
	public void testGetVersion() {
		  Response response= given(). param("access_token", access_token)
				.param("appCode", "com.bd.DGroupDoctor").get("/getVersion");
		  JsonPath jp = new JsonPath(response.asString());  
		  assertEquals(new Integer(1),jp.get("resultCode"));
	 }*/

}
