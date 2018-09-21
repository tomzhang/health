package com.dachen.health.controller.group.schedule;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class OnlineControllerTestIT  extends  BaseControllerTest {
	
	@Override
	public void setUp() throws UnsupportedEncodingException {
		super.setUp();
		RestAssured.basePath="/schedule";
	}

	
	/**
     * 添加医生在线值班
     */
//    @Test
    public void testRegGroup() throws UnsupportedEncodingException {
        String data = "{\"departmentId\":\"55d59a4dcb827c15fc9d47b7\",\"clinicDate\":[{\"week\":1,\"period\":1,\"doctors\":[{\"doctorId\":569,\"doctorName\":\"皮敬伟\",\"startTime\":\"0900\",\"endTime\":\"1200\"},{\"doctorId\":530,\"doctorName\":\"李医生\",\"startTime\":\"0900\",\"endTime\":\"1200\"},{\"doctorId\":5,\"doctorName\":\"569\",\"startTime\":\"0900\",\"endTime\":\"1200\"}]}]}";
        
        Response response= given().param("access_token", access_token)
                                  .param("data", data)
                                  .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
                                  .post("/schedule/addOnline");
        JsonPath jp = new JsonPath(response.asString());  
          
        assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    }
    
    @Test
    public void testGetOfflines() throws UnsupportedEncodingException {
       // String data = "{\"departmentId\":\"55d59a4dcb827c15fc9d47b7\",\"clinicDate\":[{\"week\":1,\"period\":1,\"doctors\":[{\"doctorId\":569,\"doctorName\":\"皮敬伟\",\"startTime\":\"0900\",\"endTime\":\"1200\"},{\"doctorId\":530,\"doctorName\":\"李医生\",\"startTime\":\"0900\",\"endTime\":\"1200\"},{\"doctorId\":5,\"doctorName\":\"569\",\"startTime\":\"0900\",\"endTime\":\"1200\"}]}]}";
        
        Response response= given().param("access_token", access_token)
                                  //.param("doctorId", "11872")
        							.param("is_hospital_group", 1)//.param("lat", "11").param("lng", "11")
                                  .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
                                  .post("/getOfflines");
        JsonPath jp = new JsonPath(response.asString());  
        //System.out.println(jp.get("data").toString());
        assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    }
    
    
  

}
