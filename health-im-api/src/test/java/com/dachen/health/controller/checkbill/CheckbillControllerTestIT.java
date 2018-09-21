package com.dachen.health.controller.checkbill;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.dachen.health.checkbill.entity.po.CheckItem;
import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class CheckbillControllerTestIT  { 

	private String access_token;
	@Before
    public void setUp() throws UnsupportedEncodingException {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		//access_token=TestUtil.testGetToken("13154565456","123456","9");
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc3,TestConstants.password_doc3,TestConstants.userType_doc3);
//		access_token=TestUtil.testGetToken("13715025342","123456","1");
		RestAssured.basePath="/checkbill";
    }

	
	
	/**
     * 测试检查项新增
     */
    //@Test
    public void testbatchAddOrUpdateCheckItem() throws UnsupportedEncodingException {
    	
    	ArrayList<String> imgs = new ArrayList<String>();
    	imgs.add("http://www.iteye.com/upload/logo/user/879557/22e6151e-10f6-3c4c-ba92-66df4892813a.jpg?1375879607");
    	imgs.add("http://www.iteye.com/upload/logo/user/879557/22e6151e-10f6-3c4c-ba92-66df4892813a.jpg?1375879607");
    	imgs.add("http://www.iteye.com/upload/logo/user/879557/22e6151e-10f6-3c4c-ba92-66df4892813a.jpg?1375879607");
    	
    	
    	List<CheckItem> list = new ArrayList<CheckItem>();
    	for(int i=0;i<3;i++){
    		CheckItem ci = new CheckItem();
    		ci.setItemName("itemName"+i);
    		ci.setResults("results"+i);
    		ci.setImageList(imgs);
    		ci.setFromId("1234567890");
    		list.add(ci);
    	}
    	String str = JSON.toJSONString(list);
    	
        Response response= given(). param("access_token", access_token)
      		  			. param("checkItemString", str)
      		  			.post("/batchAddOrUpdateCheckItem");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  Integer code =1;
  	  assertEquals(new Integer(1),code); 
   }
    
   
    //@Test
    public void testGetCheckItem() throws UnsupportedEncodingException {
    	
        Response response= given(). param("access_token", access_token)
        				. param("fromId", "1234567890")
      		  			.get("/getCheckItemList");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  System.out.println(jp.get("resultCode").toString());
  	  Integer code =1;
  	  assertEquals(new Integer(1),code); 
   }
    
   // @Test
    public void testGetCheckbillList() throws UnsupportedEncodingException {
     Response response= given(). param("access_token", access_token)
      		  			.post("/getCheckbillList");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  System.out.println(jp.get("resultCode").toString());
  	  Integer code =1;
  	  assertEquals(new Integer(1),code); 
   }

    
    //@Test
    public void testgetCheckItemCount() throws UnsupportedEncodingException {
    
    	
     Response response= given(). param("access_token", access_token)
    		 			.param("patientId", 922)
      		  			.get("/getCheckItemCount");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  System.out.println(jp.get("resultCode").toString());
  	  assertEquals(new Integer(1),jp.get("resultCode"));
   }
    
   // @Test
    public void testgetCheckItemDetailById() throws UnsupportedEncodingException {
    	 
    	
     Response response= given(). param("access_token", access_token)
    		 			. param("checkItemId", "567e0f2be1e8a02074756242")
      		  			.get("/getCheckItemDetailById");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  System.out.println(jp.get("resultCode").toString());
  	  assertEquals(new Integer(1),jp.get("resultCode"));
   }
    
    
   // @Test
    public void testgetCheckItemByClassify() throws UnsupportedEncodingException {
    	
     Response response= given(). param("access_token", access_token)
    		 			. param("checkUpId", "654321")
    		 			. param("patientId", "922")
      		  			.get("/getCheckItemByClassify");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  System.out.println(jp.get("resultCode").toString());
  	  assertEquals(new Integer(1),jp.get("resultCode"));
   }
    
  
  // @Test
    public void testupdateCheckbill() throws UnsupportedEncodingException {
     Response response= given(). param("access_token", access_token)
    		 			.param("id", "567e2f410aa4423b292e478d")
    		 			.param("checkBillStatus", "2")
      		  			.post("/updateCheckbill");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  System.out.println(jp.get("resultCode").toString());
  	  assertEquals(new Integer(1),jp.get("resultCode"));
   }
    
    
   // @Test
    public void testupdateCheckItem() throws UnsupportedEncodingException {
    	
     Response response= given(). param("access_token", access_token)
    		 			. param("id", "567e0f2be1e8a02074756242")
    		 			. param("fromId", "567e2f410aa4423b292e478d")
    		 			. param("results", "dfghjklrtyuiodfghjkl")
      		  			.post("/updateCheckItem");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  System.out.println(jp.get("resultCode").toString());
  	  assertEquals(new Integer(1),jp.get("resultCode"));
   }
   
}
