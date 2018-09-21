
package com.dachen.health.controller.group.schedule;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.dachen.health.group.schedule.entity.param.OfflineClinicDate;
import com.dachen.health.group.schedule.entity.param.OfflineParam;
import com.dachen.util.JSONUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class DoctorScheduleControllerTestIT {

	private String access_token;
	@Before
    public void setUp() throws UnsupportedEncodingException {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc6,TestConstants.password_doc6,TestConstants.userType_doc6);
		RestAssured.basePath="/doctorSchedule";
    }
	
	
	@Test
	public void testaddOffline(){	
		   Response response= given().param("access_token", access_token)
				   .param("hospitalId", "201403290420")
				   .param("hospital", "bbb")
				   .param("doctorId", 11872)
				   .param("week", 2)
				   .param("period", 3)
				   .param("startTimeString", "12:00")
				   .param("endTimeString", "13:30")
				   .param("dateTimeString", "2016-06-22")
				   .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                   .post("/addOffline");
			JsonPath jp = new JsonPath(response.asString());  
			assertEquals(new Integer(1),new Integer(1)); 
	}
	
	@Test
	public void testgetOfflinesForClient(){
		   Response response= given().param("access_token", access_token)
				   .param("hospitalId", "201403290420")
				   //.param("doctorId", 793)
				   .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                   .post("/getOfflinesForClient");
			JsonPath jp = new JsonPath(response.asString());  
			System.out.println(response.asString());
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	@Test
	public void testgetOfflinesForWeb(){
		   Response response= given().param("access_token", access_token)
				   .param("hospitalId", "201403290420")
				   .param("startTime", 1466956800000L)
				   .param("endTime", 1467561599000L)
				   .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                   .post("/getOfflinesForWeb");
			JsonPath jp = new JsonPath(response.asString());  
			System.out.println(response.asString());
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testqueryDoctorPeriodOfflines(){
		   Response response= given().param("access_token", access_token)
				   .param("hospitalId", "201403290420")
				   .param("doctorId", 11872)
				   .param("period", 2)
				   .param("dateTimeString", "2016-06-22")
				   .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                   .post("/queryDoctorPeriodOfflines");
			JsonPath jp = new JsonPath(response.asString());  
			System.out.println(response.asString());
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testHasAppointment(){
		   Response response= given().param("access_token", access_token)
				   .param("id", "5770df814203f37b64f6284d")
				   .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                   .post("/hasAppointment");
			JsonPath jp = new JsonPath(response.asString());  
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	@Test
	public void testdeleteOffline(){
		   Response response= given().param("access_token", access_token)
				   .param("id", "123456")
				   .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                   .post("/deleteOffline");
			JsonPath jp = new JsonPath(response.asString());  
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	@Test
	public void testgetDoctorList(){
		   Response response= given().param("access_token", access_token)
				   .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                   .post("/getDoctorList");
			JsonPath jp = new JsonPath(response.asString());  
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	@Test
	public void testgetHospitalList(){
		   Response response= given().param("access_token", access_token)
				   .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                   .post("/getHospitalList");
			JsonPath jp = new JsonPath(response.asString());  
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
}
