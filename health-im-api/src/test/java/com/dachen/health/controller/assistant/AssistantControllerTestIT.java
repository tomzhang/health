package com.dachen.health.controller.assistant;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class AssistantControllerTestIT extends BaseControllerTest {

	//private String access_token;
	@Before
    public void setUp() throws UnsupportedEncodingException {
		super.setUp();
        RestAssured.basePath = "/assistant";
        //获取登录token
       // access_token=TestUtil.testGetLoginToken();
    }
	/**
	 * 测试药品核查接口
	 */
//	@Test
	public void testVerifydrug() {
		  Response response= given(). param("access_token", access_token)
				.param("drugCode", "81357870004071873393").param("latitude", 10).param("longitude", 10).get("/verifydrug");
		  JsonPath jp = new JsonPath(response.asString());  
		  //无该药监码信息
		  if(jp.get("resultCode").equals(new Integer(1000105))) 
		  {
			  assertEquals(new Integer(1000105),jp.get("resultCode")); 
		  }
		  //查询到药监码
		  else
		  {
			  assertEquals(new Integer(1),jp.get("resultCode")); 	
			  assertEquals("81357870004071873393", jp.get("data.drugCode"));
		  }
		  
	 }

}
