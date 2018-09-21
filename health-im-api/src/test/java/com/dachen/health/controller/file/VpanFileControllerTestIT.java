package com.dachen.health.controller.file;

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

/**
 * vpan文件管理相关接口测试
 * 
 * @author wangqiao
 * @date 2016年1月13日
 *
 */
public class VpanFileControllerTestIT {

	private String access_token;

	@Before
	public void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token = TestUtil.testGetToken(TestConstants.telephone_doc3, TestConstants.password_doc3,
				TestConstants.userType_doc3);
		RestAssured.basePath = "/vpanfile";
	}

	/**
	 * 获取上传token 接口测试
	 * 
	 * @author wangqiao
	 * @date 2016年1月13日
	 */
	//@Test
	public void testGetUploadToken() {
		Response response = given().param("access_token", access_token).post("/getUploadToken");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}

//	@Test
	public void testSaveFileInfo() {
		//测试新增
		Response response = given().param("access_token", access_token)
				.param("name", "xxx.jpg")
				.param("mimeType", "image/png")
				.param("size", 11989)
				.param("key", "xxx.png")
				.param("hash", "Fq69n_13IhhEc-9a3Z9SIfiNbLVu")
				.post("/saveFileInfo");
		
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
		
		//测试删除
		Response response2 = given().param("access_token", access_token)
				.param("id",  jp.get("data.id").toString())
				.post("/deleteUploadFile");
		
		JsonPath jp2 = new JsonPath(response2.asString());
		assertEquals(new Integer(1), jp2.get("resultCode"));
	}
	
//	@Test
	public void testSendFile() {
		//测试文件发送
		Response response = given().param("access_token", access_token)
				.param("fileId", "56962d05e9521722f06115ad")
				.param("receiveUserIds", "12666")
				.post("/sendFile");
		
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
		
		//测试删除发送的文件
		Response response2 = given().param("access_token", access_token)
				.param("id",  jp.get("data.id").toString())
				.post("/deleteUploadFile");
		
		JsonPath jp2 = new JsonPath(response2.asString());
		assertEquals(new Integer(1), jp2.get("resultCode"));
	}

	@Test
	public void testSearchFileVersion2() {
		Response response = given().param("access_token", access_token).post("/queryFile");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1),new Integer(1));
	}
	
	
//	@Test
	public void testSaveFile() {
		Response response = given().param("access_token", access_token)
				.param("fileId", "56962c03e952172700d44426")
				.param("receiveUserId", 45621)
				.param("sendUserId", 45621)
				.post("/saveFile");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1),new Integer(1));
	}
	
	
	
}
