package com.dachen.health.controller.pack;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class CureRecordControllerTestIT  {



    public static String access_token;
    public Map<String, Object> param = new HashMap<String, Object>();

	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc,TestConstants.password_doc,TestConstants.userType_doc2);
		 RestAssured.basePath = "/cureRecord";
		
	}
	
	/**
	 * 因为涉及到医药平台的调用，取消自动化测试
	 */
//	@Test
	public void create() {

		param.put("access_token", access_token);
		param.put("orderId", 701);
		param.put("groupId", "55d588fccb827c15fc9d47b3");
		param.put("drugAdvise", "001");
		param.put("attention", "attention");
		param.put("treatAdvise", "treatAdvise");
		param.put("consultAdviseDiseases", "KQ06,CR0501,PF0201");
		
	    param.put("drugAdviseJson", "[{\"drug\":\"ac0db1bc875946268674b59183f31c81\",\"period\":\"4 Day\",\"times\":\"2\",\"quantity\":\"6238db2effdd44218bef8c03fc4cead8\",\"patients\":\"1f5cc770b7b0414eb26573110d220557\",\"method\":\"d95b3747b6074232b56afabc81b6ca36\"}]");
		
			
		
		String url1="htt://124.5.5.5/defaut.jpg?timeLong=4"			;
		//String url2="htt://124.5.5.5/defaut.jpg?timeLong=5";
		
		param.put("voices", url1);

		Response response = RestAssured.given().queryParams(param)
				.get("/createCurrecord");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}
	/**
	 * 因为涉及到医药平台的调用，取消自动化测试
	 */
	//@Test
	public void update() {

		 param.put("access_token", access_token);
		param.put("orderId", 4462);
		param.put("id", 604);
		param.put("groupId", "55d588fccb827c15fc9d47b3");
		param.put("drugAdvise", "002");
		param.put("attention", "attention");
		param.put("treatAdvise", "treatAdvise");
		param.put("consultAdviseDiseases", "KQ06,CR0501,PF0201,PF0201");
		param.put("drugAdviseJson", "[{\"drug\":\"bcdfd672ed5945aaaa7043bcb1ab52e1\",\"period\":\"1 Day\",\"times\":\"3\",\"quantity\":\"1包\",\"patients\":\"爱成人\",\"method\":\"口服\",\"requires_quantity\":\"1\"},{\"drug\":\"5dce8cd484054306b82971ccccb86e37\",\"period\":\"1 Day\",\"times\":\"3\",\"quantity\":\"3粒\",\"patients\":\"消化性溃疡\",\"method\":\"口服，不可咀嚼\",\"requires_quantity\":\"1\"}]");

		Response response = RestAssured.given().queryParams(param)
				.get("/updateCurrecord");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}

	/**
	 * 因为涉及到医药平台的调用，取消自动化测试
	 */
//	@Test
	public void findByPk() {

		 param.put("access_token", access_token);
		param.put("id", 73);

		Response response = RestAssured.given().queryParams(param)
				.get("/findById");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}
	
	
	//@Test
	public void deleteByPk() {

		 param.put("access_token", access_token);
		param.put("id",2);

		Response response = RestAssured.given().queryParams(param)
				.get("/deleteByPk");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}

	/**
	 * 因为涉及到医药平台的调用，取消自动化测试
	 */
//	@Test
	public void testFindByOrder() {

		 param.put("access_token", access_token);
		param.put("orderId", 701);

		Response response = RestAssured.given().queryParams(param)
				.get("/findByOrder");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}
	
	//@Test
	public void findByPatientAndDoctor() {

		 param.put("access_token", access_token);
		param.put("orderId", 701);

		Response response = RestAssured.given().queryParams(param)
				.get("/findByPatientAndDoctor");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}

	//@Test
	public void getUsageByDrugId() {

		 param.put("access_token", access_token);
		param.put("drugId", "ac0db1bc875946268674b59183f31c81");

		Response response = RestAssured.given().queryParams(param)
				.get("/getUsageByDrugId");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}
	
	@Test
	public void pushMessageToDoctor() throws UnsupportedEncodingException {//
		Response response = given().param("access_token", access_token).param("doctorId", 13115)
				.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
				.post("/pushMessageToDoctor");
		System.out.println(response.asString());
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
		
	}
}
