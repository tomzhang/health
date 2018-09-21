package com.dachen.health.controller.group;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class GroupSearchControllerTestIT  extends  BaseControllerTest {
	
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		//String access_token = jp.get("data.access_token");
		//loginData=TestUtil.testGetLoginToken(TestConstants.telephone_com,TestConstants.password_com,TestConstants.userType_com);
		loginData=TestUtil.testGetLoginToken(TestConstants.telephone_doc,TestConstants.password_doc,TestConstants.userType_doc);
		data=((Map<String,Object>)loginData.get("data"));
		// 获取登录token
		access_token =(String)((Map<String,Object>)loginData.get("data")).get("access_token"); 
		param.clear();
		param.put("access_token", access_token);
		
		RestAssured.basePath="/groupSearch";
		
	 
	}


	
	  /**
     * 测试获取全部医生集团
     */
    @Test
    public void testFindAllGroup() throws UnsupportedEncodingException {
    	 
          Response response= given(). param("access_token", access_token)
        		  			.param("pageIndex", "0")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
        		  			.post("/findAllGroup");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    }
    
    /**
     * 测试搜索医生集团（集团名／医生名／病种 ）
     */
//    @Test
    public void testFindGroupByKeyWord() {
    	Response response = given(). param("access_token", access_token)
			    			.param("keyword", "集团")
			    			.param("pageIndex", "0")
			    			.param("pageSize", "15")
			    			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				  			.post("/findGroupByKeyWord");
    	JsonPath jp = new JsonPath(response.asString());  
  	  
  	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    /**
     * 测试搜索医生（集团名／医生名／病种 ）
     */
    //@Test
    public void testFindDoctoreByKeyWord() {
        Response response = given(). param("access_token", access_token)
                            .param("keyword", "集团")
                            .param("pageIndex", "0")
                            .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                            .post("/findDoctoreByKeyWord");
        JsonPath jp = new JsonPath(response.asString());  
      
      assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    /**
     * 测试根据病种搜索医生集团
     */
    @Test
    public void testFindGroupByDisease() {
        Response response = given(). param("access_token", access_token)
                            .param("disease", "脑出血")
                            .param("pageIndex", "0")
                            .param("pageSize", "15")
                            .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                            .post("/findGroupByDisease");
        JsonPath jp = new JsonPath(response.asString());  
      
      assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    
    /**
     * 测试根据病种搜索医生
     */
    @Test
    public void testFindDoctorByDisease() {
        Response response = given(). param("access_token", access_token)
                            .param("diseaseId", "YK11")
                            .param("pageIndex", "0")
                            .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                            .post("/findDoctorByDisease");
        JsonPath jp = new JsonPath(response.asString());  
      
      assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    
    /**
     * 获取预约医生
     */
    @Test
    public void testfindAppointmentDoctor() {
        Response response = given(). param("access_token", access_token).param("lat", "22.554315").param("lng", "113.948725").param("sort", "1")
                            .post("/findAppointmentDoctor");
        JsonPath jp = new JsonPath(response.asString());  
        //System.out.println(jp.get("data").toString());
      assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    

}
