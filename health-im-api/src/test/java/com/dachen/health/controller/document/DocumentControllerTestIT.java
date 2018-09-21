package com.dachen.health.controller.document;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.dachen.health.document.constant.DocumentEnum;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class DocumentControllerTestIT {
	private String access_token;
	
	@Before
	public void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc3,TestConstants.password_doc3,TestConstants.userType_doc3);
		RestAssured.basePath="/document";
	}
	@Test
	public void testDelDocument(){
		 Response response= given(). param("access_token",access_token )
	                .param("id", "5668f11b6b68b31874c9d224")
				 	.post("/delDocument");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testAddDocument(){
		 Response response= given(). param("access_token",access_token )
	                .param("title", "100集团新建内科文章1")
	                .param("documentType", DocumentEnum.DocumentType.adv.getIndex())
//	                .param("contentType","WK")
	                .param("isShowImg",1)
	                .param("copyPath","http://192.168.3.7:8081/af/201510/article/670baf585f0d4decb3a291a2794491eb.png")
//	                .param("description","adwqweqweqwefvrwr1255")
	                .param("content","sdaasdqwe	qwedadfwqef")
	                .param("isShow",DocumentEnum.DocumentShowStatus.show.getIndex())
	                .param("isTop",DocumentEnum.DocumentTopStatus.unTop.getIndex())
	                .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				 	.post("/createDocument");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	@Test
	public void testGetDocumentDetail(){
		 Response response= given(). param("access_token",access_token )
	                .param("id", "56692af64203f323252e484b")
				 	.post("/getDocumentDetail");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	@Test
	public void testGetDocumentList(){
		 Response response= given(). param("access_token",access_token )
				 .param("documentType", 2)
//	                .param("contentType", "QT")
	                .param("pageIndex", "1")
				 	.post("/getDocumentList");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testViewDocumentDetail(){
		 Response response= given(). param("access_token",access_token )
	                .param("id", "56692af64203f323252e484b")
				 	.post("/viewDocumentDetail");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testSetTopScience(){
		 Response response= given(). param("access_token",access_token )
	                .param("id", "5669121a6b68b31ee436c402")
	                .param("isTop", "1")
				 	.post("/setTopScience");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	@Test
	public void testGetTopScienceList(){
		 Response response= given(). param("access_token",access_token )
				 	.post("/getTopScienceList");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testSetAdverShowStatus(){
		 Response response= given().param("access_token",access_token )
				 	.param("isShow",1 )
				 	.param("id","56692af64203f323252e484b" )
				 	.post("/setAdverShowStatus");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testUpOrDownWeight(){
		 Response response= given().param("access_token",access_token )
				 	.param("type","+" )
				 	.param("id","56692af64203f323252e484b" )
				 	.post("/upOrDownWeight");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	@Test
	public void testUpdateDocument(){
		 Response response= given().param("access_token",access_token )
				 	.param("id","5669121a6b68b31ee436c402" )
				 	.param("title", "wqeq")
				 	.param("isShowImg",1)
	                .param("contentType","FC")
	                .param("copyPath","http://192.168.3.7:8081/af/201510/article/670baf585f0d4decb3a291a2794491eb.png")
	                .param("description","adwqweqweqwefvrwr1255")
	                .param("content","sdaasdqwe	qwedadfwqef")
	                .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				 	.post("/updateDocument");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testGetContentType(){
		Response response= given().param("access_token",access_token )
			 	.param("documentType","2" )
			 	.post("/getContentType");
	 JsonPath jp = new JsonPath(response.asString());  
     assertEquals(new Integer(1),jp.get("resultCode"));
	}
	
	
	
}
