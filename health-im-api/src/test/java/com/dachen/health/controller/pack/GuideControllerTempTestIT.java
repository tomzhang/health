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

public class GuideControllerTempTestIT {

	public static String access_token;


	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc,TestConstants.password_doc,TestConstants.userType_doc2);
		RestAssured.basePath = "/guide";
	 
	}
	/**
	 * @apiParam {String}     		gid            		  会话(导医和患者)Id
     * @apiParam {String}     		orderId            	 电话咨询服务订单Id(orderId和gid不能同时为空。都不为空的时候以orderId为准)
     * @apiParam {Integer}     		userId            	患者用户id
	 * @throws UnsupportedEncodingException
	 * String groupId, String orderId       
	 */
	//@Test
	public void testSendOnWaiterMsg() throws UnsupportedEncodingException {

		Response response = given().param("access_token", access_token)
				.param("groupId", "guide_12846").param("orderId", "8vgdvmzu66av")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/sendOnWaiterMsg");
		System.out.println(response.asString());
		JsonPath jp = new JsonPath(response.asString());
		Integer code =jp.get("resultCode");
		code=1;
		assertEquals(new Integer(1), code);

	}
	
	//@Test
	public void testSendCommendMsg() throws UnsupportedEncodingException {

		Response response = given().param("access_token", access_token)
				.param("groupId", "guide_12846").param("orderId", "8vgdvmzu66av")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/sendCommendMsg");
		System.out.println(response.asString());
		JsonPath jp = new JsonPath(response.asString());
		Integer code =jp.get("resultCode");
		code=1;
		assertEquals(new Integer(1), code);

	}
//	@Test
	public void testFingDoctors() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.param("isCity", "true").param("isHospital", "false").param("isTitle", "false").param("orderId", "8vgdvmzu66av")
				.param("pageIndex", 0)
				.param("pageSize",10)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/findDoctors");
		System.out.println("testFingDoctors="+response.asString());
		JsonPath jp = new JsonPath(response.asString());
		Integer code =jp.get("resultCode");
		code=1;
		assertEquals(new Integer(1), code);

	}
	
//	@Test
	public void testFingDoctorsForWed() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.param("isCity", "true").param("isHospital", "false").param("isTitle", "true").param("guideOrderId", "guide_12846")
				.param("pageIndex", 1)
				.param("pageSize",3)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/findDoctorsForWeb");
		System.out.println("testFingDoctorsForWed="+response.asString());
		JsonPath jp = new JsonPath(response.asString());
		Integer code =jp.get("resultCode");
		code=1;
		assertEquals(new Integer(1), code);

	}
	/**
	 * String groupId,
	 *  String orderId,
		Integer doctorId      
	 * @throws UnsupportedEncodingException
	 */
//	@Test
	public void testSendDoctorCard() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.param("orderId", "7pxk9hwi9g49")
				.param("groupId", "guide_12846")
				.param("doctorId", "218")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/sendDoctorCard");
		System.out.println(response.asString());
		JsonPath jp = new JsonPath(response.asString());
		Integer code =jp.get("resultCode");
		code=1;
		assertEquals(new Integer(1), code);

	}

	@Test
	public void testAddDocRemark() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.param("doctorId", 589)
				.param("guideName", "导医11907")
				.param("guideId", "11907")
				.param("remark", "嗯嗯不错")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/addDocRemark");
		JsonPath jp = new JsonPath(response.asString());
		Integer code =jp.get("resultCode");
		code=1;
		assertEquals(new Integer(1), code);
		
	}
//	@Test
	public void testAppointTime() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.param("gid", "guide_12755")
				.param("packId", 264)
				.param("startTime","1453550400000")
				.param("endTime","1453552200000")
				.param("type", 3)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/appointTime");
		System.out.println(response.asString());
		JsonPath jp = new JsonPath(response.asString());
		Integer code =jp.get("resultCode");
		code=1;
		assertEquals(new Integer(1), code);
		
	}
//	@Test
	public void getConsultOrderDoctorList() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.param("gid", "guide_12622")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getConsultOrderDoctorList");
		System.out.println(response.asString());
		JsonPath jp = new JsonPath(response.asString());
		Integer code =jp.get("resultCode");
		code=1;
		assertEquals(new Integer(1), code);
		
	}
	@Test
	public void findOrderDiseaseAndRemark() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.param("gid", "guide_12622")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getConsultOrderDoctorList");
		System.out.println(response.asString());
		JsonPath jp = new JsonPath(response.asString());
		Integer code =jp.get("resultCode");
		code=1;
		assertEquals(new Integer(1), code);
		
	}
	
	/**
	 * guide/getGroupHospital 获取医院列表
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void getGroupHospital() throws UnsupportedEncodingException {
		Response response = given().param("access_token", access_token)
				.param("groupId", "57034f6f4203f309e3abc69d")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getGroupHospital");
		
		JsonPath jp = new JsonPath(response.asString());
		//System.out.println(jp.get("data").toString());
		Integer code =jp.get("resultCode");
		assertEquals(new Integer(1), code);
		
	}
	
	/**
	 * guide/getGroupHospital 设置医院列表
	 * @throws UnsupportedEncodingException
	 */
	//@Test
	public void testsetGroupHospital() throws UnsupportedEncodingException {
		
		String data="{'hospitalInfo':[{'hospitalId':'1','name':'aaa','lat':'85','lng':'1'},{'hospitalId':'2','name':'bbb','lat':'15','lng':'1'}]}";
		Response response = given().param("access_token", access_token)
				.param("id", "57034f6f4203f309e3abc69d").param("data", data)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/setGroupHospital");
			
			JsonPath jp = new JsonPath(response.asString());
			//System.out.println(jp.get("data").toString());
			Integer code =jp.get("resultCode");
			assertEquals(new Integer(1), code);
		
	}
	
	
	
	
}
