package com.dachen.health.controller.group.department;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import com.dachen.health.controller.base.BaseControllerTest;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class DepartmentControllerTestIT  extends BaseControllerTest {
	
	@Override
	public void setUp() throws UnsupportedEncodingException {
		super.setUp();
		RestAssured.basePath="/department";
		
	 
	}


	
	  /**
     * 测试增加部门
     */
//    @Test
    public void testRegCompany() throws UnsupportedEncodingException {
    	 
          Response response= given(). param("access_token", access_token)
        		  			.param("groupId", "55d588fccb827c15fc9d47b3")
//        		  			.param("parentId", "55d58a12cb827c15fc9d47b4")
        		  			.param("name", "内科")
        		  			.param("description", "内科")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
        		  			.post("/saveByDepart");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    }
    
    


}
