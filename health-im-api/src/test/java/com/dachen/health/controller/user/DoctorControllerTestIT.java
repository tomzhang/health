package com.dachen.health.controller.user;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

/**
 * 医生服务测试
 * @author Administrator
 *
 */
public class DoctorControllerTestIT  {
	public static String access_token;
	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc,TestConstants.password_doc,TestConstants.userType_doc);
		RestAssured.basePath="/doctor";
	 
	}
	  /**
     * 测试加好友
     */
    //@Test
	public void testSetIntroduction() throws UnsupportedEncodingException {

		Response response = given().param("access_token", access_token).param("introduction", "new introduction")
				.post("/setIntro");
		JsonPath jp = new JsonPath(response.asString());

		assertEquals(new Integer(1), jp.get("resultCode"));

	}
    
    @Test
	public void testGetFriends() throws Exception {
		Response response = given().param("access_token", access_token).post("/getFriends");
		JsonPath jp = new JsonPath(response.asString());

		assertEquals(new Integer(1), jp.get("resultCode"));
	}
    
    @Test
	public void testGetPatients() throws Exception {
		Response response = given().param("access_token", access_token).post("/getPatients");
		JsonPath jp = new JsonPath(response.asString());

		assertEquals(new Integer(1), jp.get("resultCode"));
	}
    
    @Test
	public void testUpdatePatientTag() throws Exception {
		Response response = given().param("access_token", access_token)
				.param("id", 12663).post("/updatePatientTag");
		JsonPath jp = new JsonPath(response.asString());

		assertEquals(new Integer(1), jp.get("resultCode"));
	}
    
    //@Test
	public void testAddPatient() throws Exception {
		Response response = given().param("access_token", access_token)
				.param("telephones", "15555551111")
				.param("telephones", "15555551114")
				.post("/addPatient");
		JsonPath jp = new JsonPath(response.asString());

		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
//	@Test
	public void testSendSms() throws Exception {
		Response response = given().param("access_token", access_token).param("toUserId", 12305).param("smsType", 1)
				.post("/sendSms");
		JsonPath jp = new JsonPath(response.asString());

		assertEquals(new Integer(1), jp.get("resultCode"));
	}

    //@Test
	public void testsetExpertise() throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);

		param.put("expertises", "ABAA002");

		Response response = RestAssured.given().queryParams(param).get("/setExpertise");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println("access_token : " + access_token);
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
    
    @Test
	public void testdeleteExpertise() throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);

		param.put("expertises", "ABAA002");

		Response response = RestAssured.given().queryParams(param).get("/deleteExpertise");

		JsonPath jp = new JsonPath(response.asString());

		assertEquals(new Integer(1), jp.get("resultCode"));
	}
    
    
    @Test
	public void testGetExpertise() throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);

		Response response = RestAssured.given().queryParams(param).get("/getExpertise");
		System.out.println(response.asString());
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(" access_token " + access_token);
		assertEquals(new Integer(1), jp.get("resultCode"));
	}

    @Test
	public void testGetIntro() throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("userId", 555);
		param.put("groupId", "55d588fccb827c15fc9d47b3");

		Response response = RestAssured.given().queryParams(param).get("/getIntro");
		System.out.println(response.asString());
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(" access_token " + access_token);
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
    
    //@Test
	public void testSetWork() throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("departments", "病理科");

		Response response = RestAssured.given().queryParams(param).get("/setWork");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
    
    //@Test
	public void testUpdateMsgDisturb() throws Exception {
    	 
    	Map<String, Object> param = new HashMap<String, Object>();
    	param.put("access_token", access_token);
    	param.put("doctorUserId", 15);
    	param.put("troubleFree", "2");
 		
 		Response response=RestAssured.given().queryParams(param).get("/updateMsgDisturb");
 		
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
    
    @Test
	public void testBasicInfo() throws Exception {
    	 
    	Map<String, Object> param = new HashMap<String, Object>();
    	param.put("access_token", access_token);
    	param.put("doctorId", 531);
 		
 		Response response=RestAssured.given().queryParams(param).get("/basicInfo");
 		
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
    
    @Test
   	public void getDoctorInfoDetails() throws Exception {
       	 
       	Map<String, Object> param = new HashMap<String, Object>();
       	param.put("access_token", access_token);
       	param.put("groupId", "5756256bbae14d2c9956a5bc");
       	param.put("doctorId", 144724);
       	
       	Response response=RestAssured.given().queryParams(param).get("/getDoctorInfoDetails");
   		JsonPath jp = new JsonPath(response.asString());
   		assertEquals(new Integer(1),jp.get("resultCode"));
   	}
    @Test
   	public void searchDoctorListTest() throws Exception {
       	 
       	Map<String, Object> param = new HashMap<String, Object>();
       	param.put("access_token", access_token);
       	param.put("doctorName", "");
       	param.put("hospitalId", "201504080012");
    	param.put("pageIndex", 0);
    	param.put("pageSize", 14);
       	
       	Response response=RestAssured.given().queryParams(param).get("/searchDoctorList");
   		JsonPath jp = new JsonPath(response.asString());
   		System.out.println(response.asString());
   		assertEquals(new Integer(1),new Integer(1));
   	}
}
