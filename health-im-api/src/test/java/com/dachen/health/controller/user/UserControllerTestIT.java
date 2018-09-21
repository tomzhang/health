package com.dachen.health.controller.user;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.dachen.util.Md5Util;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class UserControllerTestIT{
	
	public String access_token;
	
	@Before
	public  void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		//access_token=TestUtil.testGetToken("10008",TestConstants.password_doc,TestConstants.userType_doc);
		access_token=TestUtil.testGetToken("18900000001","123456",TestConstants.userType_admin);
		RestAssured.basePath="/user";
	 
	}
	
	public String login1() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("telephone", /*TestConstants.telephone_doc*/"18900000001");
		param.put("userType", /*TestConstants.userType_doc*/TestConstants.userType_admin);
		param.put("password", /*TestConstants.password_doc*/"123456");
		Response response=RestAssured.given().queryParams(param).get("/login");
		JsonPath json = new JsonPath(response.asString());
		boolean x= json.get("data")!=null;
		
		Assert.assertEquals(true, x);
		
		access_token=json.get("data.access_token");
		
		x=access_token!=null;
		
		Assert.assertEquals(true, x);
		
		return access_token;
		
	}

	
	@Test
	public void login() throws Exception {
		login1();
	}

	//@Test
	public void preResetPassword() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("phone", TestConstants.telephone);
		param.put("userType", TestConstants.userType);

		Response response=RestAssured.given().queryParams(param).get("/preResetPassword");
		JsonPath json = new JsonPath(response.asString());
		
		boolean x= json.get("data")!=null;
		
		//Assert.assertEquals(true, x);
		
		//String smsid = json.getString("data.smsid");

	//	System.out.print("smsid : " + smsid);
	}
	
	//@Test
	public void testSendRanCode() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("phone", "15814427800");
		param.put("userType", 3);

		Response response=RestAssured.given().queryParams(param).get("/sendRanCode");
		JsonPath json = new JsonPath(response.asString());
		
		boolean x= json.get("data")!=null;
		
		//Assert.assertEquals(true, x);
		
		//String smsid = json.getString("data.smsid");

	//	System.out.print("smsid : " + smsid);
	}

	@Test
	public void resetPassword() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("phone", TestConstants.telephone);
		param.put("userType", TestConstants.userType);
		param.put("password", TestConstants.password);
		param.put("ranCode", 3790);
		param.put("smsid", "5f5adcbd-410c-466f-8513-8cbe05b48de1");
		
		Response response=RestAssured.given().queryParams(param).get("/resetPassword");
		JsonPath json = new JsonPath(response.asString());
		
		int resultCode= json.getInt("resultCode");
		System.out.println(resultCode);
		//Assert.assertEquals( 1,resultCode);

	}
	
	//@Test
	public void setPassword() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("phone", "18538182601");
		param.put("userType", "1");
		param.put("password", "000000");
		param.put("ranCode", 3790);
		param.put("smsid", "5f5adcbd-410c-466f-8513-8cbe05b48de1");
		
		Response response=RestAssured.given().queryParams(param).get("/setPassword");
		JsonPath json = new JsonPath(response.asString());
		
		int resultCode= json.getInt("resultCode");
		System.out.println(resultCode);
		//Assert.assertEquals( 1,resultCode);

	}
	

	//@Test
	public void updateHeaderPic() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("headerPicName", "14527530114862.jpg");
		Response response=RestAssured.given().queryParams(param).get("/setUserHeaderPic");
		JsonPath json = new JsonPath(response.asString());
		int resultCode= json.getInt("resultCode");
		
		Assert.assertEquals( 1,resultCode);
	}
	
	//@Test
	public void testGetWeChatStatus() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("openid", "123qwe");
		Response response=RestAssured.given().queryParams(param).get("/getWeChatStatus");
		JsonPath json = new JsonPath(response.asString());
		int resultCode= json.getInt("resultCode");
		
		Assert.assertEquals( 1,resultCode);
	}
	
	//@Test
	public void testloginByWeChat() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("code", "0311l2up0HeNWb1Se0wp0rXVtp01l2u2");
		Response response=RestAssured.given().queryParams(param).get("/loginByWeChat");
		JsonPath json = new JsonPath(response.asString());
		int resultCode= json.getInt("resultCode");
		
		Assert.assertEquals( 1,resultCode);
	}

	//@Test
	public void update() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", "54ce0256e19f4eeb97fe0d9dab2fe50d");
		param.put("headPicFileName", "http://avatar.dev.file.dachentech.com.cn/69e4ee51587a4e919a04e9ee03288b02");
		Response response=RestAssured.given().queryParams(param).get("/update");
		JsonPath json = new JsonPath(response.asString());
		int resultCode= json.getInt("resultCode");
		
		Assert.assertEquals( 1,resultCode);
	}
	
	//@Test
	public void genQrCode() throws Exception {
		RestAssured.basePath="/qr";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("userId", "12010");
		param.put("userType", "3");
		Response response=RestAssured.given().queryParams(param).post("/createQRImage");
		JsonPath json = new JsonPath(response.asString());
		int resultCode= json.getInt("resultCode");
		
		Assert.assertEquals( 1,resultCode);
	}

//	@Test
	public void updatePassword() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("userId",data.get("userId"));
		param.put("oldPassword", TestConstants.password);
		param.put("newPassword", TestConstants.password);
		param.put("access_token", access_token);
		
		
		Response response=RestAssured.given().queryParams(param).get("/updatePassword");
		JsonPath json = new JsonPath(response.asString());
		
		int resultCode= json.getInt("resultCode");
		
		Assert.assertEquals( 1,resultCode);

	}
	
	
	/**
	 * 涉及验证码，不参与用例
	 */
//	@Test
	public void verifyResetPassword() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("phone", "13751132072");
		param.put("userType", 3);
		param.put("smsid","122");
		param.put("ranCode", "dfdfd");
		
		Response response=RestAssured.given().queryParams(param).get("/verifyResetPassword");
		JsonPath json = new JsonPath(response.asString());
		
		int resultCode= json.getInt("resultCode");
		
		Assert.assertEquals( 1,resultCode);

	}

	//暂时取消
	//@Test
	public void register() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("telephone", "15527177731");
		param.put("password", 123456);
		param.put("userType", 6);
		param.put("name", "cqycqy");
		
		Response response=RestAssured.given().queryParams(param).get("/register");
		JsonPath json = new JsonPath(response.asString());
		
		int resultCode= json.getInt("resultCode");
		
		Assert.assertEquals(resultCode,1);
	}
	
	//@Test
	public void registerDoctor() throws Exception {
		for(int i=10000;i<10100;i++){
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("telephone", i);
			param.put("password", 123456);
			param.put("userType", 3);
			
			Response response=RestAssured.given().queryParams(param).get("/registerImUser");
			JsonPath json = new JsonPath(response.asString());
			
			int resultCode= json.getInt("resultCode");
			if(resultCode!=1){
				System.out.println("phone : "+i+"\t resultCode :"+resultCode);
			}
			//Assert.assertEquals( 1,resultCode);
		}
		
	}
	
	//@Test
	public void registerPatient() throws Exception {
		for(int i=20000;i<20100;i++){
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("telephone", i);
			param.put("password", 123456);
			param.put("userType", 1);
			
			Response response=RestAssured.given().queryParams(param).get("/registerImUser");
			JsonPath json = new JsonPath(response.asString());
			
			int resultCode= json.getInt("resultCode");
			if(resultCode!=1){
				System.out.println("phone : "+i+"\t resultCode :"+resultCode);
			}
			//Assert.assertEquals( 1,resultCode);
		}
		
	}
	public String createCustomerAccount(int i){
		NumberFormat nf=NumberFormat.getInstance();
		nf.setGroupingUsed(false);
		nf.setMaximumIntegerDigits(2);
		nf.setMinimumIntegerDigits(2);
		nf.format(i);
		return nf.format(i);
		
	}
	
	//@Test
	public void registerCustomer() throws Exception {
		for(int i=1;i<11;i++){
			Map<String, Object> param = new HashMap<String, Object>();
			String telephone="189000000"+createCustomerAccount(i);
			param.put("telephone", telephone);
			param.put("password", 123456);
			param.put("userType", 4);
			
			Response response=RestAssured.given().queryParams(param).get("/register");
			JsonPath json = new JsonPath(response.asString());
			
			int resultCode= json.getInt("resultCode");
			if(resultCode!=1){
				System.out.println("phone : "+i+"\t resultCode :"+resultCode);
			}
			//Assert.assertEquals( 1,resultCode);
		}
		
	}
	
	/*public static void main(String[] args) {
		String hex = "123";
		System.out.println(Md5Util.md5Hex(hex));
		System.out.println("80c9ef0fb86369cd25f90af27ef53a9e");
	}*/

	//@Test
	public void md5() {
		String hex = "123";
		System.out.println(Md5Util.md5Hex(hex));
		System.out.println("80c9ef0fb86369cd25f90af27ef53a9e");

	}

	/**
	 * 获取我的好友资料
	 * @throws Exception
	 */
	@Test
	public void testGet() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		
		param.put("access_token", access_token);

		Response response = RestAssured.given().queryParams(param).get("/get");

		JsonPath jp = new JsonPath(response.asString());

		assertEquals(new Integer(1), jp.get("resultCode"));

	}
	
	
	@Test
	public void testGetDoctorInfo() throws Exception {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("access_token", access_token);
			Response response=RestAssured.given().queryParams(param).get("/getGroupDoctorInfo");
			
			  JsonPath jp = new JsonPath(response.asString());  
	    	  System.out.println(response.asString());
	    	  assertEquals(new Integer(1),jp.get("resultCode")); 
		
	}
	/**
	 * 用于调试错误
	 * @throws Exception
	 */
//	@Test
	public void testRegisterGroup() throws Exception {
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token",  access_token);
		param.put("telephone", "13751132073");
		param.put("name", "屈");
		param.put("password", "123456");
		param.put("groupId", "null");
		param.put("inviteId", "null"); 
		
		
		Response response=RestAssured.given().queryParams(param).get("/registerGroup");
		JsonPath json = new JsonPath(response.asString());
		
		int resultCode= json.getInt("resultCode");
		Assert.assertEquals( 1,resultCode);
	}
		

//	@Test
	public void testSetRemindVoice() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token",  access_token);
		param.put("remindVoice", "sound_bell01.mp3");
		
		Response response=RestAssured.given().queryParams(param).get("/setRemindVoice");
		
		JsonPath json = new JsonPath(response.asString());
		
		int resultCode= json.getInt("resultCode");
		Assert.assertEquals( 1,resultCode);
		
	}
	
	@Test
	public void testGetRemindVoice() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token",  access_token);
		
		Response response=RestAssured.given().queryParams(param).get("/getRemindVoice");
		
		JsonPath json = new JsonPath(response.asString());
		
		int resultCode= json.getInt("resultCode");
		Assert.assertEquals( 1,resultCode);
		
		String voiceName = json.getString("data.userConfig.remindVoice");
		Assert.assertEquals("sound_bell01.mp3", voiceName);
	}
	
	@Test
	public void testSetRemarks() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token",  access_token);
		param.put("userId",  27);
		param.put("remarks", "33333");
		
		Response response=RestAssured.given().queryParams(param).get("/setRemarks");
		
		JsonPath json = new JsonPath(response.asString());
		
		int resultCode= json.getInt("resultCode");
		Assert.assertEquals( 1,resultCode);
		
	}
	
	@Test
	public void testGetRemarks() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token",  access_token);
		param.put("userId",  27);
		
		Response response=RestAssured.given().queryParams(param).get("/getRemarks");
		
		JsonPath json = new JsonPath(response.asString());
		
		int resultCode= json.getInt("resultCode");
		Assert.assertEquals( 1,resultCode);
	}
	
	@Test
	public void testGetHeaderByUserIds() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("ids", "277");
		Response response = RestAssured.given().queryParams(param).get("/getHeaderByUserIds");

		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));

	}
	
	//@Test
	public void testUpdateStatus() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("userId", 568);
		Response response = RestAssured.given().queryParams(param).get("/updateStatus");

		JsonPath jp = new JsonPath(response.asString());
		System.out.println(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
	}
	
	//@Test
	public void testRegisterByGroupAdmin() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("telephone", "12033333327");
		param.put("name", "lily");
		param.put("doctor.departments", "急诊科");
		param.put("doctor.deptId", "JZ");
		param.put("doctor.hospital", "上海协和医院");
		param.put("doctor.hospitalId", "200510190003");
		param.put("doctor.title", "主治医师");
		param.put("doctor.skill", "CSCF");
		param.put("platform", "1");
		param.put("headPicFileName", "http://avatar.test.file.dachentech.com.cn/a88997d59e1b4bbfbbdd685ffcde1c90");
		param.put("groupId", "571ecdf24203f37d78f7926c");
		
		Response response=RestAssured.given().queryParams(param).get("/registerByAdmin");
		JsonPath json = new JsonPath(response.asString());
		
		int resultCode= json.getInt("resultCode");
		
		Assert.assertEquals( 1,resultCode);
	}
	
	//@Test
	public void testRegisterByGroupNotify() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("telephone", "18538182601");

		Response response=RestAssured.given().queryParams(param).get("/registerByGroupNotify");
		JsonPath json = new JsonPath(response.asString());
		
		int resultCode= json.getInt("resultCode");
		
		Assert.assertEquals( 1,resultCode);
	}
	
	/*@Test
	public void testtest() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		Response response = RestAssured.given().queryParams(param).get("/test");
	}*/
	
	@Test
	public void updateEnterUserName() throws UnsupportedEncodingException { 
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("userId", 11855);
		param.put("newName", "王一");
		Response response = RestAssured.given().queryParams(param).get("/updateUserName");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println("response.asString()" + jp.get("data").toString());
		Integer resultCode = jp.get("resultCode");
		Assert.assertEquals(new Integer(1), resultCode);
	}
	
	@Test
	public void testGetGuideDoctorList() throws UnsupportedEncodingException { 
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("groupId", "55e12d4d4203f3033b8d3d96");
		param.put("userName", null);
		param.put("telephone", null);
		param.put("pageIndex", "0");
		param.put("pageSize", "9999");
		Response response = RestAssured.given().queryParams(param).get("/getGuideDoctorList");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println("response.asString()" + jp.get("data").toString());
		Integer resultCode = jp.get("resultCode");
		Assert.assertEquals(new Integer(1), resultCode);
	}
	@Test
	public void testAddDoctorCheckImageList() throws UnsupportedEncodingException { 
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("access_token", access_token);
		param.put("userId", 13204);
		param.put("doctorsImage", "http://group.dev.file.dachentech.com.cn/o_1alohvjo61un0kp82ri1k9e1d6o2f");
		Response response = RestAssured.given().queryParams(param).get("/addDoctorCheckImage");
		JsonPath jp = new JsonPath(response.asString());
		System.out.println("response.asString()" + jp.get("data").toString());
		Integer resultCode = jp.get("resultCode");
		Assert.assertEquals(new Integer(1), resultCode);
	}
}
