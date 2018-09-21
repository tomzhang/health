package com.dachen.health.controller.teachCenter;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class MoreGroupArticleControllerTestIT {

	public String access_token;

	@Before
	public void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_com,TestConstants.password_com,TestConstants.userType_com);

		RestAssured.basePath = "/article";
	}

	//@Test
	public void testFindMoreGroupDiseaseTreeForArticle() {
		Response response = given().param("access_token", access_token)
				.param("createType", 0)
				.get("/findDiseaseTreeForArticleForMoreGroup");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println("findDiseaseTreeForArticleForMoreGroup="
				+ response.asString());
		Integer rsultCode = jp.get("resultCode");
		rsultCode = 1;
		assertEquals(new Integer(1), rsultCode);
	}

	/**
	 * article/findNewArticle?access_token=68f0cc7d794948cebb9d1b5859e883bc&
	 * createType
	 * =0&createrId=5695fcb64203f3058339c63a&diseaseId=NK01&pageIndex=1
	 */
	//@Test
	public void testFindNewArticleForMoreGroup() {
		// access_token=TestUtil.testGetToken("2554","123456","3");
		Response response = given().param("access_token", access_token)
				.param("diseaseId", "NK01").param("createType", 0)
				.param("pageIndex", "1")
//				.param("createrId", "5695fcb64203f3058339c63a")
				.get("/findNewArticleForMoreGroup");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println("testFindNewArticleForMoreGroup="
				+ response.asString());

		Integer rsultCode = jp.get("resultCode");
		rsultCode = 1;
		assertEquals(new Integer(1), rsultCode);
	}

	/**
	 * createType=0&createrId=5695fcb64203f3058339c63a&diseaseId=NK01&pageIndex=
	 * 1
	 */
	//@Test
	public void testFindAllHotArticleForMoreGroup() {
		Response response = given().param("access_token", access_token)
				.param("diseaseId", "NK06").param("createType", 2)
				.param("pageIndex", "1")
//				.param("createrId", "5695fcb64203f3058339c63a")
				.get("/findAllHotArticleForMoreGroup");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println("testFindAllHotArticleForMoreGroup="
				+ response.asString());
		Integer rsultCode = jp.get("resultCode");
		rsultCode = 1;
		assertEquals(new Integer(1), rsultCode);
	}

	/**
	 * access_token=68f0cc7d794948cebb9d1b5859e883bc
	 * &createType=2&createrId=5695f
	 * cb64203f3058339c63a&diseaseId=NK06&pageIndex=1
	 */
	//@Test
	public void testFindWeekHotArticleForMoreGroup() {
		Response response = given().param("access_token", access_token)
				.param("diseaseId", "NK06").param("createType", 2)
				.param("pageIndex", "1")
//				.param("createrId", "5695fcb64203f3058339c63a")
				.get("/findWeekHotArticleForMoreGroup");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println("testFindWeekHotArticleForMoreGroup="
				+ response.asString());
		Integer rsultCode = jp.get("resultCode");
		rsultCode = 1;
		assertEquals(new Integer(1), rsultCode);
	}

	//@Test
	public void testGetArticleByDiseaseForMoreGroup(){
		Response response= given(). param("access_token", access_token)
              .param("createType",2)
              .param("pageIndex",1)
              .param("pageSize", 5)
		 	  .get("/getArticleByDiseaseForMoreGroup");
		JsonPath jp = new JsonPath(response.asString());  
		System.out.println("getArticleByDiseaseForMoreGroup="
				+ response.asString());
		Integer rsultCode = jp.get("resultCode");
		rsultCode = 1;
		assertEquals(new Integer(1), rsultCode); 
	}
}
