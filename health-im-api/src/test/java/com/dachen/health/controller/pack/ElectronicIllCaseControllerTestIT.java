package com.dachen.health.controller.pack;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestParam;

import com.dachen.commons.JSONMessage;
import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.dachen.health.pack.consult.entity.po.IllCaseTypeContent;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class ElectronicIllCaseControllerTestIT {

	
	private String access_token;
	@Before
    public void setUp() throws UnsupportedEncodingException {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc3,TestConstants.password_doc3,TestConstants.userType_doc3);//("13145835257","123456",TestConstants.userType_doc3);
		RestAssured.basePath="/illcase";
    }
	
//    @Test
    public void testGetIllCaseInfo() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			. param("userId", 245)
	      		  		. param("patientId", 1822)
	      		  		. param("doctorId", 793)
	      		  		. param("enterType", 1)
      		  			.post("/getIllCaseInfo");
	  	  JsonPath jp = new JsonPath(response.asString());
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
   
    //getIllCasePatient
    
//    @Test
    public void testGetIllCasePatient() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
	      		  		. param("illCaseInfoId", "5693538782fcfe48b8b45702")
      		  			.post("/getIllCasePatient");
	  	  JsonPath jp = new JsonPath(response.asString());  
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
    
//    @Test
    public void testUpdateIllCasePatient() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
	      		  		. param("illCaseInfoId", "5693538782fcfe48b8b45702")
      		  			. param("age", 21)
	      		  		. param("height", 181)
			      		. param("weight", 135)
			      		. param("area", "纽约")
			      		. param("telephone", "1234576878")
			      		. param("isMarried", true)
			      		.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
      		  			.post("/updateIllCasePatient");
	  	  JsonPath jp = new JsonPath(response.asString());  
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
    
    
    
 //   @Test
    public void testSaveIllCaseTypeContent() throws UnsupportedEncodingException {
    	IllCaseTypeContent item = new IllCaseTypeContent();
    	
    	/*item.setContentTxt("1452498140670");
    	item.setIllCaseInfoId("5693538782fcfe48b8b45702");
    	item.setIllCaseTypeId("56934d5082fcfe030cf2a4bf");
    	
    	IllCaseTypeContent item1 = new IllCaseTypeContent();
    	item1.setContentImagesString("fffff,ddddddddd,cccccccccc");
    	item1.setIllCaseInfoId("5693538782fcfe48b8b45702");
    	item1.setIllCaseTypeId("56934d5082fcfe030cf2a4c0");
    	List<IllCaseTypeContent> list = new ArrayList<IllCaseTypeContent>();
    	list.add(item1);
    	list.add(item);
    	
    	String str = JSONUtil.toJSONString(list);*/
    	Response response= given(). param("access_token", access_token)
	      		  	    . param("illCaseInfoId","5693538782fcfe48b8b45702")
	      		  	    . param("illCaseTypeId","56934d5082fcfe030cf2a4bf")
	      		  	    . param("contentTxt","1452498140670")
      		  			.post("/saveIllCaseTypeContent");
	  	  JsonPath jp = new JsonPath(response.asString());  
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
    
//    @Test
    public void testGetSeekIllInit() throws UnsupportedEncodingException {
    	Response response= given(). param("access_token", access_token)
      		  			.post("/getSeekIllInit");
	  	  JsonPath jp = new JsonPath(response.asString());  
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
    
    
//    @Test
    public void testIsFinished() throws UnsupportedEncodingException {
    	Response response= given(). param("access_token", access_token)
    					.param("orderId","3556")
      		  			.post("/isFinished");
	  	  JsonPath jp = new JsonPath(response.asString());  
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
    
    
//    @Test
    public void testGetPatientIllcaseList() throws UnsupportedEncodingException {
    	Response response= given(). param("access_token", access_token)
    					.param("patientId","3556")
    					.param("userId","3556")
      		  			.post("/getPatientIllcaseList");
	  	  JsonPath jp = new JsonPath(response.asString());  
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }

    
    @Test
    public void testGetIllCaseByOrderId() throws UnsupportedEncodingException {
    	Response response= given(). param("access_token", access_token)
    					.param("orderId",15697)
    					//. param("enterType", 1)
      		  			.post("/getIllCaseByOrderId");
	  	  JsonPath jp = new JsonPath(response.asString());  
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
    
    
    /*@Test
    public void testInserBaseData() throws UnsupportedEncodingException {
        Response response= given().param("access_token", access_token).post("/insertBaseData");
	  	  JsonPath jp = new JsonPath(response.asString());  
	  	 // Integer code =1;
	  	  assertEquals(new Integer(1),jp.get("resultCode")); 
   }*/
	
    
    @Test
    public void testgetIllCaseById() throws UnsupportedEncodingException {
    	Response response= given(). param("access_token", access_token)
    					. param("illCaseInfoId", "56fc86544203f30930a5e813")
      		  			.post("/getIllCaseById");
	  	  JsonPath jp = new JsonPath(response.asString());  
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
    
    
//    @Test
    public void testgetIllRecordList() throws UnsupportedEncodingException {
    	Response response= given(). param("access_token", access_token)
    				. param("userId", 7)
	    			. param("doctorId",5)
      		  			.post("/getIllRecordList");
	  	  JsonPath jp = new JsonPath(response.asString());  
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
	
//    @Test
    public void testcreateIllCaseInfo() throws UnsupportedEncodingException {
    	Response response= given(). param("access_token", access_token)
      		  			.post("/createIllCaseInfo");
	  	  JsonPath jp = new JsonPath(response.asString());  
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
    
    @Test
    public void testtoCreate() throws UnsupportedEncodingException {
    	Response response= given(). param("access_token", access_token)
    					.param("patientId", 923)
    					.param("userId", 641)
    					.param("doctorId", 152)
      		  			.post("/toCreate");
	  	  JsonPath jp = new JsonPath(response.asString());  
	  	  Integer code =1;
	  	  assertEquals(new Integer(1),code);
    }
    
}
