package com.dachen.health.controller.system;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class DoctorCheckControllerTestIT extends BaseControllerTest { 

//	private String access_token;
	
	@Before
    public void setUp() throws UnsupportedEncodingException {
//		super.setUp();
        RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc,TestConstants.password_doc,TestConstants.userType_doc);
		RestAssured.basePath = "/admin/check";
    }

    /**
     * 获取医生列表
     */
    @Test
    public void testGetDoctors() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("status", "2")
        		  			.param("name", "其他")
        		  			. param("pageIndex", "0"). param("pageSize", "20")
        		  			.get("/getDoctors");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    /**
     * 获取医生列表
     * 
     *  {int}   isAuthStatus                	    认证状态（1：已认证，2：未认证）
     *  {int}   pageIndex                	   	    页码，从0开始
     * 
     */
    @Test
    public void findDoctorByAuthStatus() throws UnsupportedEncodingException {
          Response response= given(). param("access_token", access_token)
        		  			.param("isAuthStatus", 1).param("pageIndex", 0)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.get("/findDoctorByAuthStatus");
    	  JsonPath jp = new JsonPath(response.asString());
    	  assertEquals(new Integer(1),jp.get("resultCode"));
    }
    
    /**
     * 新增一个医院名称
     */
//    @Test
    public void addHospital() throws UnsupportedEncodingException {
          Response response= given(). param("access_token", access_token)
        		  			.param("provinceId", 333)
        		  	        .param("cityId", 2231112)
        		  	        .param("countryId", 333)
        		  	        .param("name", "3医院名称123sfsesfesfefe")
        		  	        .param("level", "二级甲等")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.get("/addHospital");
    	  JsonPath jp = new JsonPath(response.asString());
    	  System.out.println("addHospital:\n"+response.asString());
    	  assertEquals(new Integer(1),jp.get("resultCode"));
    }
    
    @Test
    public void getHospitalLevel() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
      		  			.get("/getHospitalLevel");
  	  JsonPath jp = new JsonPath(response.asString());
  	  System.out.println("getHospitalLevel:\n"+response.asString());
  	  assertEquals(new Integer(1),jp.get("resultCode"));
  }
    
    /**
     * 获取医生
     * @throws Exception
     */
    @Test 
	public void testGetDoctor() throws Exception {
		Response response= given().param("access_token", access_token)
						.param("id", 90).get("/getDoctor");
	   JsonPath jp = new JsonPath(response.asString());  
   	   assertEquals(new Integer(1),jp.get("resultCode")); 
	}
    
    /**
     * 获取地区
     * @throws Exception
     */
    @Test
	public void testGetArea() throws Exception {
    	Response response= given().param("access_token", access_token).get("/getArea");
		  JsonPath jp = new JsonPath(response.asString());  
		  assertEquals(new Integer(1),jp.get("resultCode")); 
	}

    /**
     * 获取医院
     * @throws Exception
     */
    @Test
    public void testGetHospitals() throws Exception {
        Response response= given().param("access_token", access_token).
                param("id",330302).get("/getHospitals");
          JsonPath jp = new JsonPath(response.asString());  
          assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    /**
     * 获取科室
     * @throws Exception
     */
    @Test
    public void testGetDepts() throws Exception {
        Response response= given().param("access_token", access_token).get("/getDepts");
          JsonPath jp = new JsonPath(response.asString());  
          assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    /**
     * 获取职称
     * @throws Exception
     */
    @Test
    public void testGetTitles() throws Exception {
        Response response= given().param("access_token", access_token).get("/getTitles");
          JsonPath jp = new JsonPath(response.asString());  
          assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    /**
     * 获取职称
     * @throws Exception
     */
//    @Test
    public void testChecked() throws Exception {
        Response response= given().param("access_token", access_token)
                .param("userId", 94)
                .param("hospital", "扬州华东慧康医院")
                .param("hospitalId", "201502100013")
                .param("departments","皮肤科")
                .param("title","主任医师")
                .param("licenseNum","20140101")
                .param("licenseExpire","2025-01-01")
                .param("remark","太棒了")
                .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                .post("/checked");
          JsonPath jp = new JsonPath(response.asString());  
          assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    /**
     * 获取职称
     * @throws Exception
     */
//    @Test
    public void testFail() throws Exception {
        Response response= given().param("access_token", access_token)
                .param("userId", 99)
                .param("remark","证件不清晰")
                .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                .post("/fail");
          JsonPath jp = new JsonPath(response.asString());  
          assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    /**
     * 反审核一个医生
     * @throws Exception
     */
//  @Test
    public void testUncheck() throws Exception {
        Response response= given().param("access_token", access_token)
                .param("userId", 99)
                .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                .post("/uncheck");
          JsonPath jp = new JsonPath(response.asString());  
          assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    @Test
    public void testEdit() throws Exception {
        Response response= given().param("access_token", access_token)
                .param("userId", 144724)
                .param("headPicFileName", "http://avatar.dev.file.dachentech.com.cn/5930d8adec66492fbf84ba965c29f94d")
                .param("hospital", "扬州华东慧康医院")
                .param("hospitalId", "201502100013")
                .param("departments","康复医学科")
                .param("deptId","KF")
                .param("name","tanyf3")
                .param("forceQuitApp",1)
                .param("title","主任医师")
                .param("licenseNum","20140101")
                .param("licenseExpire","2025-01-01")
                .param("remark","太棒了")
                .header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
                .post("/edit");
          JsonPath jp = new JsonPath(response.asString());  
          assertEquals(new Integer(1),jp.get("resultCode")); 
    }
}
