package com.dachen.health.controller.pack;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class OrderSessionControllerTestIT extends BaseControllerTest {

	@Override
	public void setUp() throws UnsupportedEncodingException {
		super.setUp();
		RestAssured.basePath = "/orderSession";
	}

//	@Test
	public void create() {

		//Map<String, Object> param = new HashMap<String, Object>();
		param.put("patientId", 2);
		param.put("needHelp", 1);
		param.put("createdTime", new Date().getTime());
		param.put("diseaseInfo", "病情");
		param.put("telephone", "130335");
	//	param.put("access_token", access_token);

		Response response = RestAssured.given().queryParams(param)
				.get("/create");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}

	//@Test
	public void update() {

	//	Map<String, Object> param = new HashMap<String, Object>();
		param.put("patientId", 2);
		param.put("needHelp", 1);
		param.put("createdTime", new Date().getTime());
		param.put("diseaseInfo", "病情1");
		param.put("id", 1);
	//	param.put("access_token", access_token);

		Response response = RestAssured.given().queryParams(param)
				.get("/update");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}


	//@Test
	public void findByPk() {

		param.put("id", 1);

		Response response = RestAssured.given().queryParams(param)
				.get("/findById");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}
	

//	@Test
	public void appointTime() {

		param.put("orderId", 701);
		param.put("appointTime", new Date().getTime());

		Response response = RestAssured.given().queryParams(param)
				.get("/appointTime");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}

	
	/**
	 * 测试套餐订单开始服务
	 */
	//@Test
	public void beginService() {

		param.put("orderId", 955);
		Response response = RestAssured.given().queryParams(param)
				.get("/beginService");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}
	
	//@Test
	public void beginService4Plan() {

		param.put("orderId", 4393);
		param.put("startDate", "2016-03-11");
		Response response = RestAssured.given().queryParams(param)
				.get("/beginService4Plan");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}
	
	/**
	 * 测试套餐订单结束服务
	 */
//	@Test
	public void finishService() {
		param.put("orderId",895);

		Response response = RestAssured.given().queryParams(param)
				.get("/finishService");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}
	
	/**
	 * 测试门诊订单开始服务
	 */
//	@Test
	public void beginServiceMenzhen() {

		param.put("orderId", 961);
		Response response = RestAssured.given().queryParams(param)
				.get("/beginService");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}
	
	/**
	 * 测试门诊订单结束服务
	 */
//	@Test
	public void finishServiceMenzhen() {
		param.put("orderId",962);

		Response response = RestAssured.given().queryParams(param)
				.get("/finishService");
		JsonPath json = new JsonPath(response.asString());
		int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);

	}


	
	 /**
     * 测试发关联订单的发送消息
     */
//    @Test 
    public void testSendMsg() throws UnsupportedEncodingException {
          Response response= given(). param("access_token", access_token)
        		  			.param("type", 1)
        		  			.param("fromUserId", 522)
        		  			.param("fromUserId", 559)
        		  			.param("gid", "W5VALVLN68")
        		  			.param("content", "发送者522,你好559-接收者！")
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
        		  			.post("/send");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    }
    
//    @Test
    public void testAbandon() throws UnsupportedEncodingException{
    	param.put("orderId",793);
    	 Response response= given(). param("access_token", access_token).queryParams(param).get("/abandonAdvisory");
    	 JsonPath json = new JsonPath(response.asString());
    	 int resultCode = json.getInt("resultCode");

 		Assert.assertEquals(1, resultCode);
    }
    
    
//    @Test
    public void testPrepareTreat() throws UnsupportedEncodingException{
    	param.put("orderId",957);
    	Response response= given(). param("access_token", access_token).queryParams(param).get("/prepareTreat");
    	JsonPath json = new JsonPath(response.asString());
    	int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);
    }
    
//  @Test
  public void testagreeAppointmentOrder() throws UnsupportedEncodingException{
  	param.put("orderId",3628);
  	Response response= given(). param("access_token", access_token).queryParams(param).get("/agreeAppointmentOrder");
  	JsonPath json = new JsonPath(response.asString());
  	int resultCode = json.getInt("resultCode");

		Assert.assertEquals(1, resultCode);
  }


}
