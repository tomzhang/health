package com.dachen.health.controller.pack;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class GuideControllerTestIT {

	public static String access_token;


	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc,TestConstants.password_doc,TestConstants.userType_doc2);
		RestAssured.basePath = "/guide";
	 
	}
	

    /*@Test
	public void testGetOrders() throws UnsupportedEncodingException {

		Response response = given().param("access_token", access_token)
				.param("userId", 100850)
				.param("recordStatus", 0)
				.param("pageIndex", 0)
				.param("pageSize", 12)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getOrders");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));

	}
	
	
	@Test
	public void testConfirm() throws UnsupportedEncodingException {

		Response response = given().param("access_token", access_token)
				.param("orderId", 780)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/confirm");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));

	}*/
	/**
	 * @apiParam {String}     		gid            		  会话(导医和患者)Id
     * @apiParam {String}     		orderId            	 电话咨询服务订单Id(orderId和gid不能同时为空。都不为空的时候以orderId为准)
     * @apiParam {Integer}     		userId            	患者用户id
	 * @throws UnsupportedEncodingException
	 */
	//@Test
	public void testFindOrderDiseaseAndRemark() throws UnsupportedEncodingException {

		Response response = given().param("access_token", access_token)
				.param("gId", "guide_661").param("userId",661)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/findOrderDiseaseAndRemark");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));

	}
	
	@Test
	public void testAddDocRemark() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.param("doctorId", 4463).param("guideId",4463).param("guideName",11907).param("remark","8888888888888888")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/addDocRemark");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));

	}
	//@Test
	public void testFindDoctors() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.param("doctorId", "12010").param("guideId",11907).param("guideName",11907).param("remark","8888888888888888")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/addDocRemark");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
		
	}
	//@Test
	public void findDoctorsFromKeyWord() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.param("keyWord", "and").param("pageIndex", 0).param("pageSize", 200)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/findDoctorsFromKeyWord");
		System.out.println(response.asString());
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
		
	}
	///@Test
	public void getConsultOrderDoctorList() throws UnsupportedEncodingException {//
		Response response = given().param("access_token", access_token)
				.param("gid", "guide_12622")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getConsultOrderDoctorList");
		System.out.println(response.asString());
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
		
	}
	@Test
	public void getGuideNoServiceOrder() throws UnsupportedEncodingException {//
		Response response = given().param("access_token", access_token)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getGuideNoServiceOrder");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
		
	}
	
//	@Test
		public void heathWaitOrderList() throws UnsupportedEncodingException {//
			Response response = given().param("access_token", access_token)
					.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
					.post("/heathWaitOrderList");
			JsonPath jp = new JsonPath(response.asString());
			assertEquals(new Integer(1), jp.get("resultCode"));
			
		}
	//@Test
	public void receiveCareOrder() throws UnsupportedEncodingException {//
		Response response = given().param("access_token", access_token).param("orderId", 2608).param("careType", "help").param("careTemplateId", "ee0cdae273fc4b0ba78599e409ab74ee")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/receiveCareOrder");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
		
	}
	//@Test
	public void getDoctorTeam() throws UnsupportedEncodingException {//
		Response response = given().param("access_token", access_token).param("orderId", 2037)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getDoctorTeam");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
		
	}
	//@Test
	public void updateCareOrder() throws UnsupportedEncodingException {//
		Response response = given().param("access_token", access_token).param("orderId", 2608).param("careType", "help")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/updateCareOrder");
		System.out.println("更新状态："+response.asString());
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
		
	}
//	@Test
	public void getHandleCareOrder() throws UnsupportedEncodingException {//
		Response response = given().param("access_token", access_token).param("orderId", 2608).param("careType", "help")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getHandleCareOrder");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
		
	}
	
	@Test
	public void getHaveAppointmentListByDate() throws UnsupportedEncodingException {//
		Response response = given().param("access_token", access_token)
				.param("hospitalId", "201403290420")
				.param("date", 1466553600000L)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getHaveAppointmentListByDate");
		System.out.println("正在接单列表："+response.asString());
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
		
	}
	

	
	@Test
	public void getAppointmentListByCondition() throws UnsupportedEncodingException {//
			Response response = given().param("access_token", access_token)
					.param("doctorId", 793)
					.param("hospitalId", "201403290420")
					.param("oppointTime", 1466553600000L)
					.param("period", 1)
					.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
					.post("/getAppointmentListByCondition");
			System.out.println("正在接单列表："+response.asString());
			JsonPath jp = new JsonPath(response.asString());
			assertEquals(new Integer(1), jp.get("resultCode"));
			
		}
	

	@Test
	public void testgetAppointmentPaidOrders() throws UnsupportedEncodingException {//
		Response response = given().param("access_token", access_token)
				.param("hospitalId", "201403290420")
				.param("groupId", "57034f6f4203f309e3abc69d")
				.param("date", 1464615000000l)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getAppointmentPaidOrders");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), new Integer(1));
		
	}
	
	@Test
	public void testsearchAppointmentOrderByKeyword() throws UnsupportedEncodingException {//
		Response response = given().param("access_token", access_token)
				.param("keyword", "18664317008")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/searchAppointmentOrderByKeyword");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), new Integer(1));
		
	}
	
	
	@Test
	public void testgetPatientAppointmentByCondition() throws UnsupportedEncodingException {//
			Response response = given().param("access_token", access_token)
					.param("doctorId", 793)
					.param("hospitalId", "201403290420")
					.param("oppointTime", 1466553600000L)
					.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
					.post("/getPatientAppointmentByCondition");
			System.out.println("正在接单列表："+response.asString());
			JsonPath jp = new JsonPath(response.asString());
			assertEquals(new Integer(1), jp.get("resultCode"));
			
		}
	
//	@Test
	public void testupdateGroupConfigAndFee() throws UnsupportedEncodingException {//
		Response response = given().param("access_token", access_token)
				.param("type", 1)
				.param("groupId", "57034f6f4203f309e3abc69d")
				.param("openAppointment", true)
				.param("appointmentGroupProfit", 44)
				.param("appointmentParentProfit", 34)
				.param("appointmentDefault", 200000)
				.param("appointmentMin", 5100)
				.param("appointmentMax", 2000000)
//				.param("appointmentDefault", 1800)
				
//				.param("openAppointment", true)
//				.param("appointmentGroupProfit", 7)
//				.param("appointmentParentProfit", 8)
//				.param("appointmentMin", 1500)
//				.param("appointmentMax", 2000)
//				.param("appointmentDefault", 1800)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/updateGroupConfigAndFee");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
		
	}
	
}
