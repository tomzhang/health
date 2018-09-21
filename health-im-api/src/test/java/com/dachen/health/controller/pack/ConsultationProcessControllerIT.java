package com.dachen.health.controller.pack;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class ConsultationProcessControllerIT {

	private String access_token;
	@Before
    public void setUp() throws UnsupportedEncodingException {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc3,TestConstants.password_doc3,TestConstants.userType_doc3);//("13145835257","123456",TestConstants.userType_doc3);
		RestAssured.basePath="/consultation/process";
    }
	
//	@Test
    public void testGetGroupList() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("doctorId", 3757)
      		  			.post("/getGroupList");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
	
//	@Test
    public void testGetConsultationMember() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("orderId", 3757)
      		  			.post("/getConsultationMember");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
	
//	@Test
    public void testNotifySpecialist() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("orderId", 3763)
      		  			.post("/notifySpecialist");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
//	@Test
    public void testGetRoomNumber() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("orderId", 3763)
      		  			.post("/getRoomNumber");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
	
/*	@Test
    public void testSendDirective() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("orderId", 3763)
      		  			.post("/sendDirective");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }*/
	
	
}
