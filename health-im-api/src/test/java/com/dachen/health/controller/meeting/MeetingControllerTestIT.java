package com.dachen.health.controller.meeting;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class MeetingControllerTestIT {

	private String access_token;
	@Before 
    public void setUp() throws UnsupportedEncodingException {
		
		RestAssured.baseURI = TestConstants.baseURI; 
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc3,TestConstants.password_doc3,TestConstants.userType_doc3);//("13145835257","123456",TestConstants.userType_doc3);
		RestAssured.basePath="/meeting";
    }
	
//	@Test
    public void testcreate() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
        				. param("company", "大辰科技公司")
        				. param("subject", "研发会议")
        				. param("startDate", System.currentTimeMillis())
        				. param("startTime", System.currentTimeMillis())
        				. param("endTime", System.currentTimeMillis() + 24 * 60 * 60 * 1000)
        				. param("attendeesCount", 30)
        				. param("price", 2000)
        				. param("organizerToken", "123456")
        				. param("panelistToken", "789456")
        				. param("attendeeToken", "123789")
        				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
      		  			.post("/create");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
	
//	@Test
    public void testupdate() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
        				. param("id", "56df9d21e1e8a027e886f840")
        				. param("company", "大辰NB公司a")
        				. param("subject", "第N次骷髅大会")
        				. param("startDate", System.currentTimeMillis())
        				. param("startTime", System.currentTimeMillis())
        				. param("endTime", System.currentTimeMillis() + 24 * 60 * 60 * 1000)
        				. param("attendeesCount", 5)
        				. param("price", 2000)
        				. param("organizerToken", "123456")
        				. param("panelistToken", "789456")
        				. param("attendeeToken", "123789")
        				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
      		  			.post("/update");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
//	@Test
    public void teststop() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
        				. param("meetingId", "56dfaaeae1e8a03498480c07")
      		  			.post("/stop");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
//	@Test
    public void testlist() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			.post("/list");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
}
