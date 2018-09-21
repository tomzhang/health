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

public class SigTestIT {

	public static String access_token;


	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc,TestConstants.password_doc,TestConstants.userType_doc2);
		RestAssured.basePath = "/sig";
	 
	}
	/**
	 * @apiParam {String}     		gid            		  会话(导医和患者)Id
     * @apiParam {String}     		orderId            	 电话咨询服务订单Id(orderId和gid不能同时为空。都不为空的时候以orderId为准)
     * @apiParam {Integer}     		userId            	患者用户id
	 * @throws UnsupportedEncodingException
	 */
//	@Test
	public void testGetSig() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token).param("userId", "12112")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getSig");
		System.out.println(response.asString());
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));

	}
	
	
}
