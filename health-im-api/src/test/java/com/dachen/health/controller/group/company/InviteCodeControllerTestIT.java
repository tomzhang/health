package com.dachen.health.controller.group.company;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class InviteCodeControllerTestIT  extends BaseControllerTest {
	
	@Override
	public void setUp() throws UnsupportedEncodingException {
		super.setUp();
		RestAssured.basePath="/invite/code";
		
	 
	}

	 /**
     * 测试获取邀请码
     */
//    @Test
    public void testGetInviteCode() throws UnsupportedEncodingException {
    	 
          Response response= given(). param("access_token", access_token)
		        		    .param("companyId", "55d58633cb827c15fcdf7d44")
				  		 	.param("doctorId", "539")
				  			.param("telephone", "13751132072")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
        		  			.post("/getInviteCode");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    }
}
