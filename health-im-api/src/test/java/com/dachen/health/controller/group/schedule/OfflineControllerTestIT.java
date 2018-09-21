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

public class OfflineControllerTestIT {

	private String access_token;
	@Before
    public void setUp() throws UnsupportedEncodingException {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc6,TestConstants.password_doc6,TestConstants.userType_doc6);
		RestAssured.basePath="/schedule";
    }
	
	@Test
	public void testgetDoctorOfflineItems(){
		   Response response= given().param("access_token", access_token)
				   .param("doctorId", 123456)
				   .param("hospitalId", "123456")
				   .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                   .post("/getDoctorOfflineItems");
			JsonPath jp = new JsonPath(response.asString());  
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testaddOffline(){
		/**
		 * {
			  "hospital" : "深圳仁康医院",
			  "clinicDate" : [
			    {
			      "week" : "3",
			      "endTimeString" : "08:30",
			      "period" : [
			        "1"
			      ],
			      "startTimeString" : "08:00"
			    }
			  ],
			  "hospitalId" : "201403290420"
			}
		 */
		OfflineParam param = new OfflineParam();
		param.setHospital("深圳仁康医院");
		param.setHospitalId("201403290420");
		List<OfflineClinicDate> ss = new ArrayList<OfflineClinicDate>();
		OfflineClinicDate ddd = new OfflineClinicDate();
		ddd.setWeek(3);
		ddd.setEndTimeString("09:00");
		ddd.setStartTimeString("08:00");
		List<Integer> pers = new ArrayList<Integer>();
		pers.add(1);
		ddd.setPeriod(pers);
		ss.add(ddd);
		param.setClinicDate(ss);
		
		String data = JSONUtil.toJSONString(param);
		System.out.println(data);
		
		   Response response= given().param("access_token", access_token)
				   .param("data", data)
				   .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                   .post("/addOffline");
			JsonPath jp = new JsonPath(response.asString());  
			assertEquals(new Integer(1),new Integer(1)); 
	}
	
	@Test
	public void testHasAppointment(){
		   Response response= given().param("access_token", access_token)
				   .param("id", "576120954e6e92e0ab5960c2")
				   .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                   .post("/hasAppointment");
			JsonPath jp = new JsonPath(response.asString());  
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	@Test
	public void testgetOfflines(){
		   Response response= given().param("access_token", access_token)

				   .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                   .post("/getOfflines");
			JsonPath jp = new JsonPath(response.asString());  
			System.out.println(response.asString());
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	@Test
	public void testgetOffline(){
		   Response response= given().param("access_token", access_token)
				   .param("id", 123456)
				   .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                   .post("/getOffline");
			JsonPath jp = new JsonPath(response.asString());  
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	@Test
	public void testupdateOffline(){
		   Response response= given().param("access_token", access_token)
				   .param("id", "576120ae4e6e92e0ab5960c5")
				   .param("price", 200)
				   .param("week", 4)
				   .param("period", 3)
				   .param("hospitalId", "201403290420")
				   .param("hospital", "深圳仁康医院")
				   .param("startTimeString", "18:00")
				   .param("endTimeString", "19:30")
				   .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                   .post("/updateOffline");
			JsonPath jp = new JsonPath(response.asString());  
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	@Test
	public void testdeleteOffline(){
		   Response response= given().param("access_token", access_token)
				   .param("id", "123456")
				   .param("clinicType", 1)
				   .param("hospital", "")
				   .param("hospitalId", 123456)
				   .param("price", 200)
				   .param("week", 1)
				   .param("period", 1)
				   .param("startTime", 1464613200000L)
				   .param("endTime", 1464615200000L)
				   .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                   .post("/deleteOffline");
			JsonPath jp = new JsonPath(response.asString());  
			assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
}
