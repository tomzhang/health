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
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class OrderControllerTestIT  {


	public static String access_token;


	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc,TestConstants.password_doc,TestConstants.userType_doc);
		RestAssured.basePath = "/pack/order";
	 
	}
	
	 /**
     * 测试处理
     */
   // @Test
    public void testHandleOrder() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", "1c99110fa41b49729d8e54ee5390a3c6")
        		  			.param("orderId", 610)
        		  			. param("userId ", 245)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/handelOrder");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
    
    /**
     * 套餐订单测试用例
     */
//    @Test
    public void testTakeOrder() throws UnsupportedEncodingException {
    	
    	 access_token=TestUtil.testGetToken("20009",TestConstants.password_doc,"1");
    	 RestAssured.basePath = "/pack/order";
    	 
          Response response= given(). param("access_token", access_token)
        		  			.param("doctorId", 793)
        		  			.param("orderType", 1)
        		  			.param("packId", 84)
        		  			.param("patientId", 940)
        		  			.param("diseaseDesc", "test unit2")
        		  			.param("imagePaths","/af/201509/checkin/7a9acfb6f5614fc2976674d476b8a4d6.png")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/takeOrder");   
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  assertEquals(new Integer(1),jp.get("data.orderStatus")); 
    	
    }
    
    
    /**
     * https://192.168.3.7/health/pack/order/createOrder?
     * patientId=664&
     * packId=2425&
     * doctorId=100846&
     * expectAppointmentId=
     * &illCaseInfoId=
     * &seeDoctorMsg=&imagePaths=()
     * &isSee=0
     * &orderType=1&
     * telephone=18689208527&
     * device=iPhone&
     * diseaseDesc=1&
     * access_token=2bfb9d0354fb4556a0fc16eb1314bedc
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testcreateOrder() throws UnsupportedEncodingException {
   	 access_token=TestUtil.testGetToken("13145835257","123456","1");
   	 RestAssured.basePath = "/pack/order";
   	 
         Response response= given(). param("access_token", access_token)
       		  			.param("doctorId", 101564)
       		  			.param("orderType", 1)
       		  			.param("packId", 765)
       		  			.param("patientId", 1248)
       		  			.param("diseaseDesc", "test unit2")
       		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
       		  			.post("/createOrder");
   	  JsonPath jp = new JsonPath(response.asString());  
   	  System.out.println(jp);
   	
   }
    
    
    //@Test
    public void testThroughTrainOrder() throws UnsupportedEncodingException {
    	
   	 access_token=TestUtil.testGetToken("20009",TestConstants.password_doc,"1");
   	 RestAssured.basePath = "/pack/order";
   	 
         Response response= given(). param("access_token", access_token)
       		  			.param("doctorId", 793)
       		  			.param("patientId", 940)
       		  			.param("packType", 7)
       		  			.param("price", 1000)
       		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
       		  			.post("/throughTrainOrder");   
   	  JsonPath jp = new JsonPath(response.asString());  
   	  assertEquals(new Integer(1),jp.get("resultCode")); 
   	  assertEquals(new Integer(1),jp.get("data.orderStatus")); 
   	
   }
    
    /**
     * 门诊订单测试用例
     */
   //@Test
    public void testTakeOrder2() throws UnsupportedEncodingException {
    	
    	 access_token=TestUtil.testGetToken("20009",TestConstants.password_doc,"1");
    	 RestAssured.basePath = "/pack/order";
    	 
          Response response= given(). param("access_token", access_token)
        		  			.param("doctorId", 793)
        		  			.param("orderType", 3)
//        		  			.param("packId", 107)
        		  			.param("patientId", 940)
        		  			.param("diseaseDesc", "test unit2")
        		  			.param("imagePaths","/af/201509/checkin/7a9acfb6f5614fc2976674d476b8a4d6.png")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/takeOrder");   
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }

    /**
     *套餐订单测试用例
     * @throws UnsupportedEncodingException
     */
    //@Test
    public void testTakeOrderPack() throws UnsupportedEncodingException{
   	 access_token=TestUtil.testGetToken("20009",TestConstants.password_doc,"1");
   	 RestAssured.basePath = "/pack/order";
     Response response= given(). param("access_token", access_token)
   		  			.param("doctorId", 549)
//   		  			.param("orderType", 1)
       		  			.param("packId", 130)
   		  			.param("patientId", 940)
   		  			.param("diseaseDesc", "test unit2")
   		  			.param("imagePaths","/af/201509/checkin/7a9acfb6f5614fc2976674d476b8a4d6.png")
   		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
   		  			.post("/takeOrder");   
	  JsonPath jp = new JsonPath(response.asString());  
	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
  
    
    /**
     * 测试查询
     */
//    @Test
    public void testQuery() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("orderId", 704)
        		  			.post("/detail");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  assertNotNull(jp.get("data")); 
    }
    
    /**2
     * 测试查询
     */
    @Test
    public void testFindOrders() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			 //.param("userId", 602)
        		  			.param("doctorId", 101453)
        		  			//.param("orderType", 1)
        		  			//.param("userName", "")
        		  			//.param("diseaseTel", "12000000000")
        		  			.param("orderStatus", 2)
        		  			//.param("pageIndex", 0)
        		  			.post("/findOrders");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  assertNotNull(jp.get("data")); 
    }
    
    
    @Test
    public void testtransferRecords() throws UnsupportedEncodingException {
          Response response= given(). param("access_token", access_token)
        		  			.param("transferRecordType", 1)
        		  			.post("/transferRecords");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  assertNotNull(jp.get("data")); 
    }
    
    
    /**
     * 测试订单详情
     */
	//@Test
    public void testOrderDetail() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			. param("orderId", 752)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/detail");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }
	
	/**
     * 测试订单支付
     */
	//@Test
    public void testPayOrder() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", "4ca30a984b674598bc7b5b0cde602015")
        		  			. param("orderId", 5127)
        		  			. param("payType", 2)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/payOrder");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    /**
     * 测试订单支付
     */
	//@Test
    public void testOrderDisease() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			. param("orderId", 715)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/orderDisease");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }
	
	/*@Test
    public void testPayHadler() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			. param("orderId", 715)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/pay");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }*/
	
	//	@Test
    public void testOrderCancel() throws UnsupportedEncodingException {
    	
		  Response response= given(). param("access_token", "322d1d17e8dc4896b2714438e56c6a30")
		  			.param("doctorId", 410)
		  			.param("packId", 107)
		  			.param("patientId", 940)
		  			.param("diseaseDesc", "test unit2")
		  			.param("imagePaths","/af/201509/checkin/7a9acfb6f5614fc2976674d476b8a4d6.png")
		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
		  			.post("/takeOrder");   
		JsonPath jp = new JsonPath(response.asString());  
		assertEquals(new Integer(1),jp.get("resultCode")); 
		Integer orderId=jp.get("data.orderId");

          Response response2= given(). param("access_token", access_token)
        		  			. param("orderId", orderId)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/cancel");
    	  JsonPath jp2 = new JsonPath(response2.asString());  
    	  assertEquals(new Integer(1),jp2.get("resultCode")); 
    }
    /**
     * 测试医生服务中的订单
     */
    //@Test
    public void testGetServingOrder() throws UnsupportedEncodingException{
    	 Response response= given(). param("access_token", access_token)
    			 	. param("doctorId", 793)
		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
		  			.post("/serviceOrder");
    	 System.err.println(response.asString());
    	 JsonPath jp = new JsonPath(response.asString());  
   	  	assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    //@Test
    public void testTakePreChargeOrder() throws UnsupportedEncodingException{
   	 access_token=TestUtil.testGetToken("20009",TestConstants.password_doc,"1");
   	 RestAssured.basePath = "/pack/order";
     Response response= given(). param("access_token", access_token)
    		 			.param("doctorId", 410)
		 				.param("orderType", 3)
   		  			.param("patientId", 940)
   		  			.param("diseaseDesc", "test unit2")
   		  			.param("imagePaths","/af/201509/checkin/7a9acfb6f5614fc2976674d476b8a4d6.png")
   		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
   		  			.post("/takePreChargeOrder");   
	  JsonPath jp = new JsonPath(response.asString());  
	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    
   // @Test
    public void testGetOrderKeyInfoByOrderId() throws UnsupportedEncodingException{
    	 Response response= given(). param("access_token", access_token)
		  			.param("ids", 1646)
		  			.param("ids", 1641)
		  			.param("ids", 957)
		  			.get("/getOrderKeyInfoByOrderId"); 
    	 JsonPath jp = new JsonPath(response.asString());  
    	 assertEquals(new Integer(1),jp.get("resultCode"));
    }
    
    @Test
    public void testCancelOrderBySystem() throws UnsupportedEncodingException{
    	 Response response= given(). param("access_token", access_token)
		  			.param("orderId", 1323)
		  			.get("/cancelOrderBySystem"); 
 	 JsonPath jp = new JsonPath(response.asString());  
 	 assertEquals(new Integer(1),jp.get("resultCode"));
    }
    
    @Test
    public void testGetOrderByDoctorAndUser() throws UnsupportedEncodingException{
    	Response response= given(). param("access_token", access_token)
	  			.param("doctorId", 3387)
	  			.param("userId", 599)
	  			.get("/getOrderByDoctorAndUser"); 
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),jp.get("resultCode"));
    }
    
    /**
     * 关怀计划订单测试用例
     */
   // @Test
    public void testTakeOrderCare() throws UnsupportedEncodingException {
    	
    	 access_token=TestUtil.testGetToken("20009",TestConstants.password_doc,"1");
    	 RestAssured.basePath = "/pack/order";
    	 
          Response response= given(). param("access_token", access_token)
        		  			.param("doctorId", 793)
        		  			.param("orderType", 4)
        		  			.param("packId", 332)
        		  			.param("patientId", 940)
        		  			.param("diseaseDesc", "test unit2")
        		  			.param("imagePaths","/af/201509/checkin/7a9acfb6f5614fc2976674d476b8a4d6.png")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/takeOrder");   
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    /**
     * 健康关怀
     */
    //@Test
    public void testAutoSendHealthCare() {
    	Response response= given(). param("access_token", access_token)
	  			.get("/autoSendHealthCare"); 
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),jp.get("resultCode"));
    }
    
    /**
     * 随访订单测试用例
     */
//    @Test  liwei
    public void testTakeOrderFollow() throws UnsupportedEncodingException {
    	
    	 access_token=TestUtil.testGetToken("20009",TestConstants.password_doc,"1");
    	 RestAssured.basePath = "/pack/order";
    	 
          Response response= given(). param("access_token", access_token)
        		  			.param("doctorId", 793)
        		  			.param("orderType", 5)
        		  			.param("packId", 338)
        		  			.param("patientId", 940)
        		  			.param("diseaseDesc", "test unit2")
        		  			.param("imagePaths","/af/201509/checkin/7a9acfb6f5614fc2976674d476b8a4d6.png")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/takeOrder");   
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
    @Test
    public void testSetRemarks() throws UnsupportedEncodingException {
    	
    	 access_token=TestUtil.testGetToken("20009",TestConstants.password_doc,"1");
    	 RestAssured.basePath = "/pack/order";
    	 
          Response response= given(). param("access_token", access_token)
        		  			.param("orderId", 703)
        		  			.param("remarks", "sssssss")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/setRemarks");   
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
    //@Test
    public void testfindExeitOrderByStatus() throws UnsupportedEncodingException {
    	
    	 access_token=TestUtil.testGetToken("15017905927",TestConstants.password_doc,"3");
    	 RestAssured.basePath = "/pack/order";
    	 
          Response response= given(). param("access_token", access_token)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/findOrderSchedule");   
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
    
//    @Test
    public void testGetOrderDetailById() throws UnsupportedEncodingException{
    	Response response= given(). param("access_token", access_token)
	  			.param("orderId", 3344)
	  			.get("/getOrderDetailById"); 
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(1,1);
    }
    
    //@Test
    public void testTakeConsultation() {
    	Response response= given(). param("access_token", access_token)
	  			.param("illCaseId", "569efcb24203f348df4f1d82")
	  			.param("conDoctorId", 793)
	  			.get("/takeConsultation"); 
    	JsonPath jp = new JsonPath(response.asString());  
  	 	assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    }
    
    //@Test
    public void testconfirmConsultation() {
    	Response response= given(). param("access_token", access_token)
	  			.param("orderId", 3759)
	  			.param("groupId", "568257034203f36b9fb1a2b8")
	  			.param("conDoctorId", 11865)
	  			.param("oppointTime", "1453455000000")
	  			.get("/confirmConsultation"); 
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),jp.get("resultCode"));
    	
    }
    
    @Test
    public void testorderDetail() {
    	Response response= given(). param("access_token", access_token)
	  			.param("orderId", 3759)
	  			.get("/orderDetail"); 
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),new Integer(1));
    	
    }
    
    @Test
    public void testhasIllCase() {
    	Response response= given(). param("access_token", access_token)
	  			.param("orderId", 3759)
	  			.get("/hasIllCase");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),new Integer(1));
    	
    }
    
//    @Test
    public void testupdateCareCorder() {
    	Response response= given(). param("access_token", access_token)
	  			.param("orderId", 3759)
	  			.get("/updateCareCorder");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),new Integer(1));
    	
    }
    
    @Test
    public void testgetGroupNameByOrderId() {
    	Response response= given(). param("access_token", access_token)
	  			.param("orderId", 3759)
	  			.get("/getGroupNameByOrderId");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),new Integer(1));
    }
    
    @Test
    public void testGetRefundOrders() {
    	Response response= given(). param("access_token", access_token)
	  			.get("/getRefundOrders");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),jp.get("resultCode"));
    }
    
    //@Test
    public void testCancelPaidOrder() {
    	Response response= given(). param("access_token", access_token)
    			.param("orderIds", 5127)
	  			.get("/cancelPaidOrder");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),jp.get("resultCode"));
    }
    
    //@Test
    public void testRefund() {
    	Response response= given(). param("access_token", access_token)
    			.param("orderId", 5127)
	  			.get("/refund");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),jp.get("resultCode"));
    }
    
    @Test
    public void testconsultationMember() {
    	Response response= given(). param("access_token", access_token)
    			.param("orderId", 5127)
    			.param("roleType", 1)
	  			.get("/consultationMember");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),jp.get("resultCode"));
    }
    
    @Test
    public void testgetIllCaseCardInfo() {
    	Response response= given(). param("access_token", access_token)
    			.param("orderId", 5127)
    			.param("illCaseInfoId", "3214565789")
    			.param("msgId", "4569780")
	  			.get("/sendIllCaseCardInfo");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),new Integer(1));
    }
    
    
    @Test
    public void testsendDoctorCardInfo() {
    	Response response= given(). param("access_token", access_token)
    			.param("doctorId", 5127)
    			.param("msgId", "3245978")
	  			.get("/sendDoctorCardInfo");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),new Integer(1));
    }
    
    @Test
    public void testtakeOrderByIllCase() {
    	Response response= given(). param("access_token", access_token)
    			.param("doctorId", 100808)
    			.param("orderType", 1)
	  			.param("packId", 277)
    			.param("illCaseInfoId", "5768a4bd4203f3521fb68836")
	  			.get("/takeOrderByIllCase");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),new Integer(1));
    }
    
    //@Test
    public void testupdateRemark() {
    	Response response= given(). param("access_token", access_token)
    			.param("orderId", 3480)
    			.param("remark", "DDDDD")
	  			.param("isSend", "")
	  			.get("/updateRemark");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),new Integer(1));
    }
    
    
  @Test
    public void testcancelOrderByAdmin() {
    	Response response= given(). param("access_token", access_token)
    			.param("orderId", 395)
    			.param("cancelReason", "呵呵哒")
	  			.get("/cancelOrderByAdmin");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),jp.get("resultCode"));
    }
    
  @Test
    public void testqueryOrderByConditions() {
    	Response response= given(). param("access_token", access_token)
    			//.param("doctorName", "大白菜")
    			//.param("startCreateTime", "1474128000000")
    			//.param("orderStatus", 5)
    			//.param("packType", 0)
	  			.get("/queryOrderByConditions");
    	JsonPath jp = new JsonPath(response.asString());  
    	assertEquals(new Integer(1),jp.get("resultCode"));
    }
    
}
