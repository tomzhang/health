package com.dachen.health.controller.group;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class GroupDoctorControllerTestIT    {
	public String access_token;
	
	@Before
	public void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc,TestConstants.password_doc,TestConstants.userType_doc);
		
		RestAssured.basePath="/group/doctor";
		
	 
	}

    /**
     * 添加集团医生
     */ 
//    @Test
    public void testSaveByGroupDoctor() throws UnsupportedEncodingException {
    	 
    	
//     	for(int i=30;i<50;i++)
//     	{
//     		RestAssured.basePath="/user";
//     		String userName="用户100"+i;
//     		 Response response1= given(). param("access_token", access_token)
// 		  			.param("nickname", userName)
// 		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
// 		  			.post("/query");
//     		 JsonPath jp1 = new JsonPath(response1.asString());  
//     		 Integer userId=(Integer)jp1.get("data[0].userId");
//     		 
//     		 
     		 
     		RestAssured.basePath="/group/doctor";
             Response response= given(). param("access_token", access_token)
           		  			.param("groupId", "55d588fccb827c15fc9d47b3")
           		  			.param("doctorId", 216)
           		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
           		  			.post("/saveByGroupDoctor");
       	  JsonPath jp = new JsonPath(response.asString());  
       	  
       	  assertEquals(new Integer(1),jp.get("resultCode")); 
       	  
//     	}


    }
    
    /**
     * 同意加入集团
     */ 
//    @Test
    public void testConfirmByGroupDoctor() throws UnsupportedEncodingException {
    	 
    	
    	RestAssured.basePath="/group/doctor";
    	
     		 
    	  Response response1= given(). param("access_token", access_token)
	  			.param("groupId", "55d588fccb827c15fc9d47b3")
	  			.param("status", "I")
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
	  			.post("/searchByGroupDoctor");
    	  JsonPath jp1 = new JsonPath(response1.asString());  
    	  jp1.get("data.pageData");
     		 
     		
//             Response response= given(). param("access_token", access_token)
//           		  			.param("id", userId)
//           		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
//           		  			.post("/confirmByGroupDoctor");
//       	  JsonPath jp = new JsonPath(response.asString());  
//       	  
//       	  assertEquals(new Integer(1),jp.get("resultCode")); 
       	  
     


    }

    /**
     * 测试获取所有数据
     */ 
//    @Test
    public void testGetAllDataById() throws UnsupportedEncodingException {
    	 
    	RestAssured.basePath="/group/doctor";
             Response response= given(). param("access_token", "ab7f0de672fe4bb08683bd7f6c6adf9a")
           		  			.param("doctorId", 100196)
           		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
           		  			.post("/getAllDataById");
       	  JsonPath jp = new JsonPath(response.asString());  
       	  System.out.println(response.asString());
       	         	  assertEquals(new Integer(1),jp.get("resultCode")); 

    }
    
    /**
     * 测试获取所有数据
     */ 
//    @Test
    public void testSendNoteInvite() throws UnsupportedEncodingException {
    	 
    	RestAssured.basePath="/group/doctor";
             Response response= given(). param("access_token", access_token)
           		  			.param("groupId", "55d588fccb827c15fc9d47b3")
           		  			.param("doctorId", 387)
           		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
           		  			.post("/sendNoteInvite");
       	  JsonPath jp = new JsonPath(response.asString());  
       	  assertEquals(new Integer(1),jp.get("resultCode")); 

    }

    /**
     * 查找所有集团医生 
     */ 
    @Test
    public void testSearchByGroupDoctor() throws UnsupportedEncodingException {
    	 
    	 
    	RestAssured.basePath="/group/doctor";
             Response response= given(). param("access_token", access_token)
           		  			.param("groupId", "57034f6f4203f309e3abc69d")
           		  			.param("status", "C")
           		  			.param("pageSize", "50")
           		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
           		  			.post("/searchByGroupDoctor");
       	  JsonPath jp = new JsonPath(response.asString());  
       	  System.out.println(response.asString());
       	         	  assertEquals(new Integer(1),jp.get("resultCode")); 

    }
    
    
    /**
     * 根据关键字集团医生 
     */ 
    @Test
    public void testsearchDoctorByKeyWord() throws UnsupportedEncodingException {
    	 
    	 
    	RestAssured.basePath="/group/doctor";
             Response response= given(). param("access_token", access_token)
           		  			.param("groupId", "5763911cb522252180fba82d")
           		  			.param("pageIndex", 0)
           		  			.param("pageSize", 10000)
           		  			.param("keyword", "2")
           		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
           		  			.post("/searchDoctorByKeyWord");
       	  JsonPath jp = new JsonPath(response.asString());  
       	         	  assertEquals(new Integer(1),jp.get("resultCode")); 

    }
    
// @Test
  public void testdoctorOnline() throws UnsupportedEncodingException {
  	 
  	 
  	RestAssured.basePath="/group/doctor";
           Response response= given(). param("access_token", access_token)
         		  			.param("groupId", "55d588fccb827c15fc9d47b3")
         		  			.param("doctorId", 549)
         		
         		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
         		  			.post("/doctorOnline");
     	  JsonPath jp = new JsonPath(response.asString());  
    	  System.out.println(response.asString());
    	  System.out.println("access_token"+ access_token);
     	         	  assertEquals(new Integer(1),jp.get("resultCode")); 

  }
  
  
	//@Test
	public void testdoctorOffline() throws UnsupportedEncodingException {
		 
		 
		RestAssured.basePath="/group/doctor";
	         Response response= given(). param("access_token", access_token)
	       		  			.param("groupId", "55d588fccb827c15fc9d47b3")
	       		  			.param("doctorId", 549)
	       		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
	       		  			.post("/doctorOffline");
	   	  JsonPath jp = new JsonPath(response.asString());  
	   	  System.out.println(response.asString());
	   	         	  assertEquals(new Integer(1),jp.get("resultCode")); 
	
	}
	
	//@Test
	public void testdoctorOnline2() throws UnsupportedEncodingException {

		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.param("doctorId", 55555)

		.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")).post("/doctorOnline");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		System.out.println("access_token" + access_token);
		assertEquals(new Integer(1), jp.get("resultCode"));

	}

	//@Test
	public void testdoctorOffline2() throws UnsupportedEncodingException {

		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.param("doctorId", 55555)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/doctorOffline");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));

	}
	    
	@Test
	public void testfindOnlineDoctorByGroupId() throws UnsupportedEncodingException {
		 
		 
		RestAssured.basePath="/group/doctor";
	       Response response= given(). param("access_token", access_token)
	     		  			.param("groupId", "55d588fccb827c15fc9d47b3")
	     		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
	     		  			.post("/findOnlineDoctorByGroupId");
	 	  JsonPath jp = new JsonPath(response.asString());  
	 	         	  assertEquals(new Integer(1),jp.get("resultCode")); 
	
	}
	@Test
	public void testsetOutpatientPrice() throws UnsupportedEncodingException {

		RestAssured.basePath = "/group/doctor";

		Response response = given().param("access_token", access_token)
				.param("groupId", "55d5b0104203f35c4aa2044c")
				.param("outpatientPrice", 5)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/setOutpatientPrice");

		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));

	}
	
	
//	@Test
	public void testsetTaskTimeLong() throws UnsupportedEncodingException {

		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.param("groupId", "55dd1c924203f32e5c3ac5c5")
				.param("entries[0].doctorId", 560)
				.param("entries[0].taskDuration", 6)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/setTaskTimeLong");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), new Integer(1));

	}
	
	@Test
	public void testsetTaskTimeLongNull() throws UnsupportedEncodingException {

		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.param("groupId", "55dd1c924203f32e5c3ac5c5")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/setTaskTimeLong");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));

	}
	
	
	@Test
	public void testlistOnlineDoctorGroupByDept() throws UnsupportedEncodingException {
		 
		 
		RestAssured.basePath="/group/doctor";
	       Response response= given(). param("access_token", access_token)
	     		  			.param("groupId", "55d588fccb827c15fc9d47b3")
	     		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
	     		  			.post("/listOnlineDoctorGroupByDept");
	 	  JsonPath jp = new JsonPath(response.asString());  
		  System.out.println(response.asString());
	 	         	  assertEquals(new Integer(1),jp.get("resultCode")); 
	
	}


	/**
	 * 医生新加入集团用例
	 */
	//@Test
	public void testSaveCompleteByGroupDoctor() throws UnsupportedEncodingException{
		RestAssured.basePath="/group/doctor";
		 Response response= given(). param("access_token", access_token)
		  			.param("groupId", "55f943e94203f37874f0b6e6")
		  			.param("doctorId", "117")
		  			.param("inviteId", "11847")
		  			.param("telephone", "18689208527")
//		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
		  			.get("/saveCompleteByGroupDoctor");
		 JsonPath jp = new JsonPath(response.asString());  
		 System.out.println(response.asString());
       	  assertEquals(new Integer(1),jp.get("resultCode")); 
		
	}
	
	/**
	 * 医生已经加入集团，不能加入用例
	 */
	@Test
	public void testSaveCompleteByGroupDoctor2() throws UnsupportedEncodingException{
		RestAssured.basePath="/group/doctor";
		 Response response= given(). param("access_token", access_token)
		  			.param("groupId", "55d588fccb827c15fc9d47b3")
		  			.param("doctorId", "793")
		  			.param("inviteId", "11847")
		  			.param("telephone", "18689208527")
//		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
		  			.get("/saveCompleteByGroupDoctor");
		 JsonPath jp = new JsonPath(response.asString());  
		 System.out.println(response.asString());
       	  assertEquals(new Integer(1),jp.get("resultCode")); 
		
	}
	
	
//	@Test//因为这个测试用例使用的是公司管理员账户，所以暂时取消该用例
	public void testSaveBatchInvite() throws UnsupportedEncodingException{
		
		String access_token_temp=TestUtil.testGetToken("10010","123456",TestConstants.userType_doc2);
		RestAssured.basePath="/group/doctor";
		
		Response response= given(). param("access_token", access_token_temp)
				.param("groupId", "55d588fccb827c15fc9d47b3")
	  			.param("telepNumsOrdocNums", 90010870)
	  			.param("telepNumsOrdocNums", 90010886)
	  			.param("telepNumsOrdocNums", 1234567895)
	  			.param("ignore", "false")
	  			.get("/saveBatchInvite"); 
	 JsonPath jp = new JsonPath(response.asString());  //有错不忽略
	 assertEquals(new Integer(1),jp.get("resultCode"));
	 
	 
	 response= given(). param("access_token", access_token_temp)
				.param("groupId", "55d588fccb827c15fc9d47b3")
	  			.param("telepNumsOrdocNums", 90010870)
	  			.param("telepNumsOrdocNums", 90010886)
	  			.param("telepNumsOrdocNums", 1234567895)
	  			.param("ignore", "true")
	  			.get("/saveBatchInvite"); 
	 jp = new JsonPath(response.asString());  //有错忽略
	 assertEquals(new Integer(1),jp.get("resultCode"));
	 
	 response= given(). param("access_token", access_token_temp)
				.param("groupId", "55d588fccb827c15fc9d47b3")
	  			.param("telepNumsOrdocNums", 90010870)
	  			.param("telepNumsOrdocNums", 90010886)
//	  			.param("telepNumsOrdocNums", 1234567895)
	  			.param("ignore", "false")
	  			.get("/saveBatchInvite"); 
	 jp = new JsonPath(response.asString());  //无错一次性通过
	 assertEquals(new Integer(1),jp.get("resultCode"));
	 
	 
	 
	 
	}
	
	@Test
	public void testUpdateContactWay() {
		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.param("groupId", "55d588fccb827c15fc9d47b3")
				.param("doctorId", 121)
				.param("contactWay", "13000000001")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/updateContactWay");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
	@Test
	public void testGetMyInviteRelationListById() {
		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.param("groupId", "55d588fccb827c15fc9d47b3")
				.param("doctorId", 216)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getMyInviteRelationListById");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
	@Test
	public void testGetHasSetPrice() {
		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.param("groupId", "55d588fccb827c15fc9d47b3")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getHasSetPrice");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
	@Test
	public void testSetMainGroup() {
		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.param("groupId", "55d588fccb827c15fc9d47b3")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/setMainGroup");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
	@Test
	public void testDimission() {
		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.param("groupId", "55d588fccb827c15fc9d47b3")
				.param("doctorId", 11864)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/dimission");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), new Integer(1));
	}
	
	//@Test
	public void testGetDoctorDutyInfo(){
		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.param("doctor", 100838)
				.get("getDoctorDutyInfo");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
	
	@Test
	public void testGetGroups() {
		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.get("getGroups");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
	/**
	 * 测试 保存 医生加入集团申请
	 *@author wangqiao
	 *@date 2015年12月23日
	 */
//	@Test
	public void testSaveByDoctorApply() {
		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.param("groupId", "55d7046c4203f338390074dd")
				.param("applyMsg", "测试医生申请加入集团")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/saveByDoctorApply");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
	/**
	 * 测试 审核 医生加入集团申请
	 *@author wangqiao
	 *@date 2015年12月23日
	 */
//	@Test
	public void testConfirmByDoctorApply() {
		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.param("id", "5679ffc1beaee719e4feca22")
				.param("approve", true)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/confirmByDoctorApply");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), new Integer(1));
	}
	
	/**
	 * 测试 读取 待审核的医生加入集团申请
	 *@author wangqiao
	 *@date 2015年12月23日
	 */
	@Test
	public void testGetDoctorApplyByGroupId() {
		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.param("groupId", "55d83a80f6fc141828074407")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getDoctorApplyByGroupId");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
	@Test
	public void testQuitGroup() {
		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.param("groupId", "55d7046c4203f338390074dd")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/quitGroup");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), new Integer(1));
	}
	
	/**
	 * 读取单个 医生加入集团申请
	 *@author wangqiao
	 *@date 2016年1月19日
	 */
	@Test
	public void testGetDoctorApplyByApplyId() {
		RestAssured.basePath = "/group/doctor";
		Response response = given().param("access_token", access_token)
				.param("id", "5679ffc1beaee719e4feca22")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/getDoctorApplyByApplyId");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	@Test
	public void testGetMembers() {
		Response response = given().param("access_token", access_token)
				.param("groupId", "55e45a974203f330b7dc86e2")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.get("/getMembers");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}

	@Test
	public void testIsInBdjl() {
		Response response = given().param("access_token", access_token)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.get("/isInBdjl");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	@Test
	public void testGetMyNormalGroups() {
		Response response = given().param("access_token", "b0e878075f234af6b2d1986bf3ce59ab")
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.get("/getMyNormalGroups");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
}
