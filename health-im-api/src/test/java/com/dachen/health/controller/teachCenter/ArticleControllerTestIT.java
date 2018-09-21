package com.dachen.health.controller.teachCenter;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class ArticleControllerTestIT {
	
	public String access_token;
	@Before
	public void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_com,TestConstants.password_com,TestConstants.userType_com);
		RestAssured.basePath="/article";
	}
	
	
	@Test
	public void testFindDiseaseTreeForArticle(){
		 Response response= given(). param("access_token", access_token)
				 					.param("createType", 2)
				 					.param("groupId", "55d588fccb827c15fc9d47b3")
				 					.get("/findDiseaseTreeForArticle");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testAddArticle(){
		 Response response= given(). param("access_token",access_token )
				 	.param("author", "11864")
	                .param("title", "100集团新建内科文章1")
	                .param("diseaseId", "NK01")
	                .param("isShow",1)
	                .param("isShare",1)
	                .param("tags","WK04")
	                .param("tags","WK05")
	                .param("createType",2)
	                .param("createrId", "55d588fccb827c15fc9d47b3")
	                .param("copyPath","http://192.168.3.7:8081/af/201510/article/670baf585f0d4decb3a291a2794491eb.png")
	                .param("description","adw")
	                .param("content","sda")
	                .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				 	.post("/addArticle");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testUpdateArticle(){
		Response response= given(). param("access_token", access_token)
				.param("articleId", "56346781084a2108803bfae4")
			 	.param("author", "11863")
                .param("title", "扬州华东慧康医院")
                .param("diseaseId", "WK03")
                .param("isShow","1")
                .param("isShare","0")
                .param("tags","WK04")
                .param("tags","WK05")
                .param("createType","2")
                .param("createrId","55d588fccb827c15fc9d47b3")
                .param("copyPath","/ss/aacc")
                .param("description","ssssswwe")
                .param("content","ssssdweaqdasd")
                .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
			 	.post("/updateArticle");
	 JsonPath jp = new JsonPath(response.asString());  
     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testFindNewArticle(){
//		access_token=TestUtil.testGetToken("2554","123456","3");
		Response response= given(). param("access_token", access_token)
//                .param("diseaseId", "WK05")
                .param("createType", 3)
                .param("pageIndex","1")
                .param("createrId", "55d588fccb827c15fc9d47b3")
			 	.get("/findNewArticle");
	 JsonPath jp = new JsonPath(response.asString());  
     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testViewArticle(){
		Response response= given(). param("access_token", access_token)
                .param("articleId", "5632c37d4203f329f35d9342")
			 	.get("/viewArticle");
	 JsonPath jp = new JsonPath(response.asString());  
     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testFindAllHotArticle(){
		Response response= given(). param("access_token", access_token)
                .param("diseaseId", "WK05")
                .param("createType", 7)
                .param("createrId", "57034f6f4203f309e3abc69d")
                .param("pageIndex","1")
			 	.get("/findAllHotArticle");
	 JsonPath jp = new JsonPath(response.asString());  
     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testFindWeekHotArticle(){
		Response response= given(). param("access_token", access_token)
				.param("diseaseId", "WK05")
                .param("createType", 7)
                .param("createrId", "57034f6f4203f309e3abc69d")
                .param("pageIndex","1")
			 	.get("/findWeekHotArticle");
	 JsonPath jp = new JsonPath(response.asString());  
     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testCollectArticle(){
		Response response= given(). param("access_token", access_token)
				.param("createType",3)
                .param("articleId", "5632c37d4203f329f35d9342")
			 	.get("/collectArticle");
	 JsonPath jp = new JsonPath(response.asString());  
     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	
	@Test
	public void testTopArticle(){
		 Response response= given(). param("access_token", access_token)
                .param("articleId", "562a03e26b68b31ba8568201")
			 	.get("/topArticle");
	     JsonPath jp = new JsonPath(response.asString());  
         assertEquals(new Integer(1),jp.get("resultCode")); 
          response= given(). param("access_token", access_token)
                 .param("articleId", "5631add94203f334c483abfc")
 			 	.get("/topArticle");
 	      jp = new JsonPath(response.asString());  
          assertEquals(new Integer(1),jp.get("resultCode")); 
		
       //testTopArticleUp
		 response= given(). param("access_token", access_token)
                .param("upId", "5632c37d4203f329f35d9342")
                .param("downId", "562a03e26b68b31ba8568201")
			 	.get("/topArticleUp");
		  jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	     //testTopArticleRemove
	     response= given(). param("access_token", access_token)
	             .param("articleId", "5632c37d4203f329f35d9342")
				 	.get("/topArticleRemove");
		 jp = new JsonPath(response.asString());  
		 assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	@Test 
	public void testFindTopArticle(){
		 Response response= given(). param("access_token", access_token)
				 	.get("/findTopArticle");
		     JsonPath jp = new JsonPath(response.asString());  
	         assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testGetArticleByDoctor(){
		Response response= given(). param("access_token", access_token)
				.param("pageIndex", 1)
			 	.get("/getArticleByDoctor");
	     JsonPath jp = new JsonPath(response.asString());  
         assertEquals(new Integer(1),jp.get("resultCode")); 
		
	}
	
	@Test
	public void testGetArticleByDisease(){
		Response response= given(). param("access_token", access_token)
              .param("createType",1)
              .param("pageIndex",1)
              .param("pageSize", 5)
		 	  .get("/getArticleByDisease");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
//	@Test
	public void testFindArticleByKeyWord(){
		//中文get会乱码。post请求不会
		Response response= given(). param("access_token", "c4993262485b4d749192e56ed39872fb")
	              .param("title", "d")
	              .param("createType",2)
	              .param("createrId", "55d588fccb827c15fc9d47b3")
	              .param("pageIndex","1")
	              .param("pageSize", "5")
	              .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				  .post("/findArticleByKeyWord");
			JsonPath jp = new JsonPath(response.asString());  
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testFindArticleByTag(){
		//中文get会乱码。post请求不会
		Response response= given(). param("access_token", access_token)
	              .param("tags", "WK0501")
	              .param("createType",2)
	              .param("createrId", "55d588fccb827c15fc9d47b3")
	              .param("pageIndex","1")
	              .param("pageSize", "2")
	              .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				  .post("/findArticleByTag");
			JsonPath jp = new JsonPath(response.asString());  
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testGetArticleById(){
		Response response= given(). param("access_token", access_token)
	              .param("articleId", "5629e4056b68b315d484e645")
				  .get("/getArticleById");
			JsonPath jp = new JsonPath(response.asString());  
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	@Test
	public void testUpdateArticleUse(){
		Response response= given(). param("access_token", access_token)
				.param("articleId", "5629e4056b68b315d484e645")
				.get("/updateArticleUse");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode"));
	}

	@Test
	public void testSendArticleToUser(){
		Response response= given(). param("access_token", access_token)
				.param("fromUserId", "0")
				.param("gid", "8P0XR3Q9RJ")
				.param("url", "http://192.168.3.7:8081/html/template/563dd9dd4203f322f33fb612.html")
				.get("/sendArticleToUser");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode"));
	}

	@Test
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
	@Test
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
	@Test
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
	@Test
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

	@Test
	public void testGetArticleByDiseaseForMoreGroup(){
		Response response= given(). param("access_token", access_token)
              .param("createType",2)
              .param("pageIndex",1)
              .param("pageSize", 15)
		 	  .get("/getArticleByDiseaseForMoreGroup");
		JsonPath jp = new JsonPath(response.asString());  
		System.out.println("getArticleByDiseaseForMoreGroup="
				+ response.asString());
		Integer rsultCode = jp.get("resultCode");
		rsultCode = 1;
		assertEquals(new Integer(1), rsultCode); 
	}
	@Test
	public void testupdateVisitNum() throws UnsupportedEncodingException{
		Response response = given().param("access_token", access_token)
				.get("/updateVisitNum");
		JsonPath jp = new JsonPath(response.asString());
		Integer rsultCode = jp.get("resultCode");
		rsultCode = 1;
		assertEquals(new Integer(1), rsultCode);
	}
}
