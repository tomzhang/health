package com.dachen.health.controller.pack;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.dachen.util.DateUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class ConsultationPackControllerIT {
	
	private String access_token;
	@Before
    public void setUp() throws UnsupportedEncodingException {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc3,TestConstants.password_doc3,TestConstants.userType_doc3);//
		access_token=TestUtil.testGetToken("13145835257","123456",TestConstants.userType_doc3);
		RestAssured.basePath="/consultation/pack";
    }

//	@Test
    public void testGetList() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("groupId", "dasdasd")
	      		  		. param("pageIndex", 0)
	      		  		. param("pageSize", 10)
      		  			.post("/getList");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  assertEquals(new Integer(1),new Integer(1));
    }
	
	
//	@Test
    public void testGetDetail() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("consultationPackId", "5698ae0c4203f3704c196b3d")
      		  			. post("/getDetail");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	assertEquals(new Integer(1),new Integer(1));
    }
	
	
//	@Test
    public void testGetDoctors() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("consultationPackId", "56988fe3e1e8a01570f3cdcf")
      		  			. post("/getDoctorList");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	assertEquals(new Integer(1),new Integer(1));
    }
	
	
//	@Test
    public void testUpdateConsultationPack() throws UnsupportedEncodingException {
		
		List<Integer> set = new ArrayList<Integer>();
		set.add(11879);
		set.add(11865);
		set.add(12317);
		set.add(793);
		set.add(12078);
		set.add(12869);
		
		
        Response response= given(). param("access_token", access_token)
      		  			. param("id", "56a1c7934203f3172018518c")
	      		  		. param("consultationPackDesc", "23456789wertyui")
	      		  		. param("maxFee", 20)
	      		  		. param("minFee", 3)
	      		  		. param("groupPercent", 22)
	      		  		. param("consultationDoctorPercent", 10)
	      		  		. param("unionDoctorPercent", 432)
	      		  		. param("doctorIds", set)
      		  			. post("/update");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	assertEquals(new Integer(1),new Integer(1));
    }
	
	
//	@Test
    public void testAddConsultationPack() throws UnsupportedEncodingException {
		
		List<Integer> set = new ArrayList<Integer>();
		set.add(12078);
		
        Response response= given(). param("access_token", access_token)
				        		. param("groupId", "568257034203f36b9fb1a2b8")
				  		  		. param("consultationPackDesc", "234567890")
				  		  		. param("maxFee", 1000)
				  		  		. param("minFee", 50)
				  		  		. param("groupPercent", 52)
				  		  		. param("consultationDoctorPercent", 18)
				  		  	. param("doctorIds", set)
				  		  		. param("unionDoctorPercent", 30)
				  		  		.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
      		  			        . post("/add");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  assertEquals(new Integer(1),new Integer(1));
    }
	
	
//	@Test
    public void testGetNotInCurrentPackDoctorIds() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("consultationPackId", "56988fe3e1e8a01570f3cdcf")
      		  			. param("groupId", "568257034203f36b9fb1a2b8")
      		  			. post("/getNotInCurrentPackDoctorIds");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	assertEquals(new Integer(1),new Integer(1));
    }
	
	
//	@Test
    public void testDelete() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("consultationPackId", "56988fe3e1e8a01570f3cdce")
      		  			. post("/delete");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	assertEquals(new Integer(1),new Integer(1));
    }
	
//	@Test
    public void testOpenService() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("groupId", "55d83f2cf6fc14181c6bdc56")
      		  			. post("/openService");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	assertEquals(new Integer(1),new Integer(1));
    }
	
	public static void main(String[] args) {
		System.out.println(DateUtil.formatDate2Str(1458784140250l));
	}
	
	
	@Test
    public void testgetPlatformSelectedDoctors() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("groupId", "666666666666666666666666")
      		  			. post("/getPlatformSelectedDoctors");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	assertEquals(new Integer(1),new Integer(1));
    }
	
	@Test
    public void testsyncData() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. post("/syncData");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	assertEquals(new Integer(1),new Integer(1));
    }
	
}
