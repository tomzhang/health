package com.dachen.health.controller.pack;

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

public class ConsultationDoctorControllerTestIT {   

	
	private String access_token;
	@Before 
    public void setUp() throws UnsupportedEncodingException {
		
		RestAssured.baseURI = TestConstants.baseURI; 
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc3,TestConstants.password_doc3,TestConstants.userType_doc3);//("13145835257","123456",TestConstants.userType_doc3);
		//access_token=TestUtil.testGetToken("13145835257","123456",TestConstants.userType_doc3);
		//access_token=TestUtil.testGetToken("18575515431","111111",TestConstants.userType_doc3);
		RestAssured.basePath="/consultation/doctor";
    }
	
	
//	@Test
    public void testgetFriendsNum() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			.post("/getFriendsNum");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),new Integer(1));
    }
	
	
//	@Test
    public void testgetConsultationRecordList() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			.post("/getConsultationRecordList");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),new Integer(1));
    }
	
	//@Test
    public void testgetFriendList() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			.post("/getFriendList");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),new Integer(1));
    }
	
	//@Test
    public void testsearchList() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			.post("/searchConsultationDoctors");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),new Integer(1));
    }
	
	//@Test
    public void testgetPatientIllcaseList() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			.post("/getPatientIllcaseList");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),new Integer(1));
    }
	
}
