package com.dachen.health.controller.income;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class IncomesControllerTestIT {

	private String access_token;
	@Before
    public void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8080;
		access_token=TestUtil.testGetToken("10011","123456","3");
		RestAssured.basePath="/income";
	}
	
	@Test
	public void testInfo(){
		 Response response= given(). param("access_token",access_token )
	                .param("doctorId", 13369)
				 	.post("/info");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testBalanceDetail(){
		 Response response= given(). param("access_token",access_token )
	                .param("doctorId", 13369)
				 	.post("/balanceDetail");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testIncomeList(){
		 Response response= given(). param("access_token",access_token )
	                .param("doctorId", 13369)
				 	.post("/incomeList");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testIncomeDetail(){
		 Response response= given(). param("access_token",access_token )
	                .param("doctorId", 13369)
	                .param("month", "2016年3月")
				 	.post("/incomeDetail");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testCashDetail(){
		Response response= given(). param("access_token",access_token )
				.param("id", 13369)
				.post("/cashDetail");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testgIncomeListNew(){
		 Response response= given(). param("access_token",access_token )
	                .param("groupId", "55d83f2cf6fc14181c6bdc56")
				 	.post("/gIncomeListNew");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testgIncomeDetail(){
		Response response= given(). param("access_token",access_token )
				.param("groupId", "55d83f2cf6fc14181c6bdc56")
				.param("month", "2016年3月")
				.post("/gIncomeDetail");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	@Test
	public void testgSettleYMList(){
		Response response= given(). param("access_token",access_token )
				.post("/gSettleYMList");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
//	@Test
	public void testAutoSettle(){
		 Response response= given(). param("access_token",access_token )
				 	.post("/autoSettle");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
//	@Test
	public void testdSettleYMList(){
		Response response= given(). param("access_token",access_token )
				.post("/dSettleYMList");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
//	@Test
	public void testdSettleMList(){
		Response response= given(). param("access_token",access_token )
				.param("groupId", "55d83f2cf6fc14181c6bdc56")
				.param("month", "2016年3月")
				.post("/dSettleMList");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	@Test
	public void testgSettleMList(){
		Response response= given(). param("access_token",access_token )
				.param("groupId", "55d83f2cf6fc14181c6bdc56")
				.param("month", "2016年3月")
				.post("/gSettleMList");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
//	@Test
	public void testgroupSettle(){
		Response response= given(). param("access_token",access_token )
				.param("id", 1)
				.param("settleMoney", 200)
				.param("expandMoney", 300)
				.post("/groupSettle");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
//	@Test
	public void testdoctorSettle(){
		Response response= given(). param("access_token",access_token )
				.param("id", 1)
				.param("settleMoney", 200)
				.param("expandMoney", 300)
				.post("/doctorSettle");
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
	
	
	
	
	
	
	
//	@Test
	public void testDetails(){
		 Response response= given(). param("access_token",access_token )
	                .param("doctorId", "12078")
	                .param("type", "1")
				 	.post("/details");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
//	@Test
	public void testsettleDetails(){
		Response response= given(). param("access_token",access_token )
                .param("userId", "793")
                .param("id", "13")
			 	.post("/settleDetails");
	 JsonPath jp = new JsonPath(response.asString());  
     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
//	@Test
	public void testHList(){
		 Response response= given(). param("access_token",access_token )
	                .param("doctorId", 641)
	                .param("type", 1)
				 	.post("/hList");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
//	@Test
	public void testhDetails(){
		 Response response= given(). param("access_token",access_token )
	                .param("doctorId", 641)
	                .param("time", "2015-03")
				 	.post("/hDetails");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
//	@Test
	public void testhsettleList(){
		 Response response= given(). param("access_token",access_token )
	                .param("userId", 641)
				 	.post("/hsettleList");
		 JsonPath jp = new JsonPath(response.asString());  
	     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
//	@Test 
	public void testgIncomeList(){
		Response response= given(). param("access_token",access_token )
                .param("upGroup", 641)
			 	.post("/gIncomeList");
	 JsonPath jp = new JsonPath(response.asString());  
     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
	
//	@Test
	public void testgIncomeDetails(){
		Response response= given(). param("access_token",access_token )
                .param("upGroup", "55d588fccb827c15fc9d47b3")
                .param("time", "2016-01")
			 	.post("/gIncomeDetails");
	 JsonPath jp = new JsonPath(response.asString());  
     assertEquals(new Integer(1),jp.get("resultCode")); 
	}
//	@Test
	public void testgdIncomeList(){
		Response response= given(). param("access_token",access_token )
                .param("upGroup", 641)
			 	.post("/gdIncomeList");
	 JsonPath jp = new JsonPath(response.asString());  
     assertEquals(new Integer(1),jp.get("resultCode")); 
	}

//	@Test
	public void testsettleList(){
		Response response= given(). param("access_token",access_token )
                .param("userType", 1)
			 	.post("/settleList");
	 JsonPath jp = new JsonPath(response.asString());  
     assertEquals(new Integer(1),jp.get("resultCode"));
	}
//	@Test
	public void testsettleIncomeList(){
		Response response= given(). param("access_token",access_token )
                .param("id", 1)
			 	.post("/settleIncomeList");
	 JsonPath jp = new JsonPath(response.asString());  
     assertEquals(new Integer(1),jp.get("resultCode"));
	}
	
//	@Test
	public void testsettleIncome(){
		Response response= given(). param("access_token",access_token )
			 	.post("/settleIncome");
	 JsonPath jp = new JsonPath(response.asString());  
     assertEquals(new Integer(1),jp.get("resultCode"));
	}
}
