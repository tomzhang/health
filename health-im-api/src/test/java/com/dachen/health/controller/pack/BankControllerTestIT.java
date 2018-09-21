package com.dachen.health.controller.pack;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class BankControllerTestIT {
	 public static String access_token;
	    @BeforeClass
	    public  static void setUp() throws UnsupportedEncodingException   {
	        
	        RestAssured.baseURI = TestConstants.baseURI;
	        RestAssured.port = TestConstants.port;
	     
	    }
		

	    /**
	     * 从xls文件导入银行数据到银行字典表中
	     * @throws UnsupportedEncodingException
	     */
	   // @Test
	    public void testSaveBankBinData() throws UnsupportedEncodingException {
	        access_token=TestUtil.testGetToken("10010",TestConstants.password_doc,"3");
	        RestAssured.basePath="/pack/bank";
	          Response response= given(). param("access_token", access_token)
	        		  			.get("/saveBankBinData");
	          
	    	  JsonPath jp = new JsonPath(response.asString());  
	    	  assertEquals(new Integer(1),jp.get("resultCode")); 
	    }
	    
	    
	    /**
	     * 获取开户行名称
	     * @throws UnsupportedEncodingException
	     */
	    @Test
	    public void testfindBankBinData() throws UnsupportedEncodingException {
	        access_token=TestUtil.testGetToken("10010",TestConstants.password_doc,"3");
	        RestAssured.basePath="/pack/bank";
	          Response response= given().param("access_token", access_token)
	        		  .param("bankCard", "6222024000061555351")			
	        		  .get("/findBankName");
	          
	    	  JsonPath jp = new JsonPath(response.asString());  
	    	  assertEquals(new Integer(1),jp.get("resultCode")); 
	    }
	  
	    @Test
	    public void testbankNameList() throws UnsupportedEncodingException{
	    	access_token=TestUtil.testGetToken("10010",TestConstants.password_doc,"3");
	        RestAssured.basePath="/pack/bank";
	          Response response= given().param("access_token", access_token)
	        		  .param("bankName", "工")
	        		  .get("/searchNameByKeyword");
	          
	    	  JsonPath jp = new JsonPath(response.asString());  
	    	  assertEquals(new Integer(1),jp.get("resultCode")); 
	    }
	    
	   
}
