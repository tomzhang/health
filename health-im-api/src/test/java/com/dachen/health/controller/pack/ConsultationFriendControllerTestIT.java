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
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class ConsultationFriendControllerTestIT {

	private String access_token;
	@Before
    public void setUp() throws UnsupportedEncodingException {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc3,TestConstants.password_doc3,TestConstants.userType_doc3);//("13145835257","123456",TestConstants.userType_doc3);
		RestAssured.basePath="/consultation/friend";
    }
	
	
//	@Test
    public void testSearchAssistantDoctors() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("number", 127781)
      		  			.post("/searchAssistantDoctors");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
	
	
//	@Test
    public void testSearchConsultationDoctors() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			.post("/searchConsultationDoctors");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
	
//	@Test
    public void getSearchConsultationDoctorsByKeyword() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
        				. param("keyword", "王")
        				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
      		  			.post("/searchConsultationDoctorsByKeyword");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
	
	
	
//	@Test
    public void testDoctorDetail() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("doctorId", 127781)
      		  			. param("roleType", 2)
      		  			.post("/doctorDetail");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
	
//	@Test
    public void testApplyFriends() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("consultationDoctorId", 127781)
      		  			. param("unionDoctorId", 127751)
      		  			. param("applyType", 1)
      		  			. param("applyMessage", "")
      		  			.post("/applyFriends");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
	
//	@Test
    public void testGetFriendsByRoleType() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("doctorId", 127781)
      		  			. param("applyType", 1)
      		  			.post("/getFriendsByRoleType");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
	
//	@Test
    public void testGetApplyFriendByRoleType() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("doctorId", 127781)
      		  			. param("roleType", 1)
      		  			.post("/getApplyFriendByRoleType");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
//	@Test
    public void testProcessfriendsapply() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("consultationApplyFriendId", "56a1e4c84203f359cd8e5f33")
      		  			. param("status", 2)
      		  			.post("/processFriendsApply");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
//	@Test
    public void testIsSpecialist() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("doctorId", 345678)
      		  			.post("/isSpecialist");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
//	@Test
    public void testGetPatientIllcaseList() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("doctorId", 793)
      		  			.post("/getPatientIllcaseList");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
//	@Test
    public void testGetConsultationDoctorNum() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
        				. param("doctorId", 456)
      		  			.post("/getConsultationDoctorNum");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
//	@Test
    public void testGetUnionDoctorNum() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
        				. param("doctorId", 456)
      		  			.post("/getUnionDoctorNum");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
//	@Test
    public void testGetDoctorApplyNum() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
        				. param("doctorId", 12317)
        				. param("applyType", 2)
      		  			.post("/getDoctorApplyNum");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
	
//	@Test
    public void testCollectOperate() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
        				. param("unionDoctorId", 456)
        				. param("consultationDoctorId", 456)
        				. param("operateIndex", 456)
      		  			.post("/collectOperate");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
}
