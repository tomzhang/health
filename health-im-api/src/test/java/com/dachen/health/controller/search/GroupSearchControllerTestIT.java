package com.dachen.health.controller.search;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class GroupSearchControllerTestIT {
	
	public static String access_token;
//	public static String access_token2;


	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc2,TestConstants.password_doc2,TestConstants.userType_doc2);
		RestAssured.basePath = "/groupSearch";
	 
//		access_token2=TestUtil.testGetToken(TestConstants.telephone_doc5,TestConstants.password_doc5,TestConstants.userType_doc5);
	}
	
	@Test
    public void testFindProDoctorByGroupId() throws UnsupportedEncodingException {
		RestAssured.basePath = "/groupSearch";
          Response response= given(). param("access_token", access_token)
        		  			.param("docGroupId", "55d588fccb827c15fc9d47b3")
        		  			.param("pageIndex", "0")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/findProDoctorByGroupId");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }
	
	@Test
    public void testFindDoctorByGroupId() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("docGroupId", "55d588fccb827c15fc9d47b3")
        		  			.param("pageIndex", "0")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/findDoctorByGroup");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
	
	@Test
    public void testFindDoctorOnlineByGroup() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("docGroupId", "55d588fccb827c15fc9d47b3")
        		  			.param("pageSize", 7)
        		  			.param("pageIndex", 0)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/findDoctorOnlineByGroup");
    	  JsonPath jp = new JsonPath(response.asString());
    	  assertEquals(new Integer(1),jp.get("resultCode"));
    }
	
	@Test
    public void testFindDoctorOnlineByGroupAndDetp() throws UnsupportedEncodingException {
          Response response= given(). param("access_token", access_token)
        		  			.param("docGroupId", "55d588fccb827c15fc9d47b3")
        		  			.param("deptName", "营养科")
        		  			.param("pageIndex", 0)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/findDoctorOnlineByGroupAndDept");
    	  JsonPath jp = new JsonPath(response.asString());
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }
	
	//@Test
    public void testFindGroupBaseInfo() throws UnsupportedEncodingException {
          Response response= given(). param("access_token", access_token)
        		  			.param("docGroupId", "55d588fccb827c15fc9d47b3")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/findGroupBaseInfo");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    
	
	
//	@Test
    public void testFindRecommDisease() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
//        		  			.param("pageIndex", "0")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/findRecommDisease");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
    @Test
    public void testFindDiseaseByGroup() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("pageIndex", "0")
        		  			.param("docGroupId", "55d588fccb827c15fc9d47b3")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/findDiseaseByGroup");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
    @Test
    public void testFindOnDutyToday() throws UnsupportedEncodingException {
    	RestAssured.basePath = "/groupSearch";
          Response response= given(). param("access_token", access_token)
        		  			.param("areaCode", 440300)
        		  			.param("specialistId", "")
        		  			.param("pageIndex", 0)
        		  			.param("pageSize", 200)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/findOnDutyToday");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
    //@Test
    public void testFindOrderDoctor() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("areaCode", 440300)
        		  			.param("specialistId", "")
        		  			.param("pageIndex", 0)
        		  			.param("pageSize", 200)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/findOrderDoctor");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
    //@Test
    public void testFindAppointDoctor() throws UnsupportedEncodingException {
    	RestAssured.basePath = "/groupSearch";
          Response response= given(). param("access_token", access_token)
        		  			.param("pageIndex", 0)
        		  			.param("pageSize", 10)
        		  			.param("lat", 10)
        		  			.param("lng", 10)
        		  			.param("doctorId", 12010)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/findAppointmentDoctor");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
    /**
     * 测试 搜索医生可加入的医生集团
     * @throws UnsupportedEncodingException
     *@author wangqiao
     *@date 2015年12月21日
     */
    @Test
    public void testFindGroupByName() throws UnsupportedEncodingException {
    	String access_tokens = TestUtil.testGetToken(TestConstants.telephone_doc,TestConstants.password_doc,TestConstants.userType_doc);
    	RestAssured.basePath = "/groupSearch";
          Response response= given(). param("access_token", access_tokens)
        		  			.param("areaCode", 440300)
        		  			.param("keyword", "皮敬") 
//        		  			.param("memberApply",false)
        		  			.param("pageIndex", 0)
        		  			.param("pageSize", 15)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/findGroupByName");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 

    }
    
    /**
     * 测试  查询医生与医生集团的关系
     * @throws UnsupportedEncodingException
     *@author wangqiao
     *@date 2015年12月21日
     */
    @Test
    public void testFindGroupDoctorStatus() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("docGroupId", "55d83a80f6fc141828074407") 

        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/findGroupDoctorStatus");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  assertNotNull(jp.get("data.doctorStatus")); 
    	  assertNotNull(jp.get("data.groupAdmin")); 
    }
    
    @Test
	public void testFindDoctorByDept() throws UnsupportedEncodingException {

		Response response = given().param("access_token", access_token).param("deptId", "NK")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/findDoctorByDept");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
    @Test
	public void testgetPatientDoctor() {
		try {
			access_token=TestUtil.testGetToken(TestConstants.telephone,TestConstants.password,TestConstants.userType);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		RestAssured.basePath = "/groupSearch";
		Response response = given().param("access_token", access_token)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getPatientDoctor");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
    
    //@Test
    public void testfindDoctoreByKeyWord() throws UnsupportedEncodingException{
    	String access_tokens=TestUtil.testGetToken(TestConstants.telephone,TestConstants.password,TestConstants.userType);
    	RestAssured.basePath = "/groupSearch";
		Response response = given().param("access_token", access_tokens).param("keyword", "号")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/findDoctoreByKeyWord");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
    }
    
    @Test
    public void testfindDoctorByGroupForPatient() throws UnsupportedEncodingException{
    	String access_tokens=TestUtil.testGetToken(TestConstants.telephone,TestConstants.password,TestConstants.userType);
    	RestAssured.basePath = "/groupSearch";
    	Map<String,String> map=new HashMap<String,String>();
    	map.put("pageSize","15");
    	map.put("docGroupId", "57034f6f4203f309e3abc69d");
		Response response = given().param("access_token", access_tokens).params(map)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/findDoctorByGroupForPatient");
		JsonPath jp = new JsonPath(response.asString());
		//System.out.println("json:"+jp.get("data").toString());
		assertEquals(new Integer(1), jp.get("resultCode"));
		
    }
    
}
