package com.dachen.health.controller.pack;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class RechargeControllerTestIT extends BaseControllerTest {
	@Before
	public void setUp() throws UnsupportedEncodingException {
		super.setUp();
		RestAssured.basePath = "/pack/recharge";
	}


//	@Test
	public void update() {
    	
        Response response= given(). param("access_token", access_token)
      		  			 .param("id", 51)
      		  			. param("rechargeStatus", 2) 
      		  			.get("/update");
        

        
  	  JsonPath jp = new JsonPath(response.asString());  
  	  assertEquals(new Integer(1),jp.get("resultCode")); 
  	  
  	 
  }



}
