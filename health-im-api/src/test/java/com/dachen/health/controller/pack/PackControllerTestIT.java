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

public class PackControllerTestIT  {

	public static String access_token2;
	
	public static String access_token;
	

	@BeforeClass
	public  static void setUp() throws UnsupportedEncodingException   {
		
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken(TestConstants.telephone_doc,TestConstants.password_doc,TestConstants.userType_doc);
		access_token2=TestUtil.testGetToken(TestConstants.telephone_doc2,TestConstants.password_doc2,TestConstants.userType_doc2);
		 RestAssured.basePath = "/pack/pack";
	 
	}
 

	 /**
     * 测试套餐新增
     */
//    @Test
    public void testCreate() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("name", "test套餐")
        		  			. param("price", 100)
        		  			. param("packType", 2)
        		  			. param("status",1 )
        		  			. param("timeLimit",100 )
        		  			. param("description","test" )
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/add");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  assertNotNull(jp.get("data.packId")); 
    	  assertNotNull(jp.get("data.price")); 
//    	  assertEquals(new Integer(100),jp.get("data.price")); 
    	  
    	  //新增之后马上删除，避免下次测试时因为多个电话套餐导致新增失败
          response= given(). param("access_token", access_token)
		  			.param("id", jp.get("data.packId").toString())
		  			.post("/delPack");
          jp = new JsonPath(response.asString());  
          assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    }
    
    /**
     * 测试套餐新增（图文和电话资讯套餐，只允许有一个开启状态的套餐）
     * @throws UnsupportedEncodingException
     *@author wangqiao
     *@date 2015年12月14日
     */
    //@Test
    public void testCreateRepeat() throws UnsupportedEncodingException {
    	//doctorid=215  图文套餐有2条status=1的记录
        Response response= given(). param("access_token", access_token)
	  			.param("name", "test只允许一个开启的图文套餐")
	  			. param("price", 100)
	  			. param("packType", 1)
	  			. param("status",1 )
	  			. param("timeLimit",100 )
	  			. param("description","test" )
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/add");
        JsonPath jp = new JsonPath(response.asString());  
        assertEquals(new Integer(0),jp.get("resultCode")); //新增失败
        assertEquals("已存在开启状态的同类型套餐！",jp.get("resultMsg")); 
        
    }
    
    /**
     * 测试套餐新增（图文和电话资讯套餐，价格必须在集团范围之内）
     * @throws UnsupportedEncodingException
     *@author wangqiao
     *@date 2015年12月14日
     */
//    @Test
    public void testCreateCheckPrice() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token2)
	  			.param("name", "test电话资讯套餐价格必须在集团范围之内")
	  			. param("price", 10000)
	  			. param("packType", 2)//电话资讯，集团设置的价格区间是200 ~2000
	  			. param("status",1 )
	  			. param("timeLimit",100 )
	  			. param("description","test" )
	  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
	  			.post("/add");
        JsonPath jp = new JsonPath(response.asString());  
        assertEquals(new Integer(1),jp.get("resultCode")); 
        assertEquals(new Integer(2000),jp.get("data.price")); //价格自动取集团价格的上下限
        assertNotNull(jp.get("data.packId")); 

        //新增之后马上删除，避免下次测试时因为多个电话套餐导致新增失败
        response= given(). param("access_token", access_token2)
        		.param("id", jp.get("data.packId").toString())
        		.post("/delPack");
        jp = new JsonPath(response.asString());  
        assertEquals(new Integer(1),jp.get("resultCode")); 
    }
    
    /**
     * 测试修改套餐（图文和电话资讯套餐，价格必须在集团范围之内）
     * @throws UnsupportedEncodingException
     *@author wangqiao
     *@date 2015年12月14日
     */
//    @Test
    public void testUpateCheckPrice() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token2)
	  			.param("id", 668)
	  			. param("price", 5700) 
	  			.get("/update");

        JsonPath jp = new JsonPath(response.asString());  
        assertEquals(new Integer(1),jp.get("resultCode")); 
        assertEquals(new Integer(3000),jp.get("data.price"));//价格不能超过集团的范围 300~3000
        
    }
    
    @Test
	public void testQueryPack() {
		Response response = given().param("access_token", access_token)
				.param("doctorId", 793)
				.param("status", 1)
				.post("/queryPack");
		JsonPath jp = new JsonPath(response.asString());
		assertEquals(new Integer(1), jp.get("resultCode"));
		assertNotNull(jp.get("data"));
	}
  

    /**
     * 测试修改套餐
     * @throws UnsupportedEncodingException
     */
//    @Test
    public void testUpate() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  .param("id", 200)
		  			. param("price", 5700) 
//		  			. param("timeLimit", 21)
        		  			.get("/update");
          

          
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  assertEquals(new Integer(5700),jp.get("data.price")); //新增的返回值 price
    	  
    	  Response response2= given(). param("access_token", access_token)
		  			.param("id", 200)
		  			.get("/get");
    	  JsonPath jp2 = new JsonPath(response2.asString());  
    	  assertEquals(new Integer(1),jp2.get("resultCode")); 
    	  assertEquals(new Integer(5700),jp2.get("data.price")); 
    	  assertEquals(new Integer(21),jp2.get("data.timeLimit")); 
    }
    
    /**
     * 测试套餐查询
     */
    @Test
    public void testQuery() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("doctorId", 511)
        		  			. param("status", 1)
        		  			.post("/query");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
//    	  assertNotNull(jp.get("data")); 
    }
    
    /**
     * 测试关怀计划套餐新增
     */
    //@Test
    public void testCareCreate() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			. param("careTemplateId", "56c533174203f30dcc023af5")
        		  			. param("packType", 3)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/add");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  assertNotNull(jp.get("data.packId")); 
    }
    
    /**
     * 测试随访套餐新增
     */
    //@Test
    public void testFollowCreate() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			. param("followTemplateId", "049adb904fc04ad1a836dab6c6332a68")
        		  			. param("packType", 4)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/add");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  assertNotNull(jp.get("data.packId")); 
    }
    
    /**
     * 测试关怀计划医生分成设置
     */
    //@Test
    public void testSavePackDoctor() throws UnsupportedEncodingException {
    	
    	 String data="{'packId':'927','packDoctorList':[{'doctorId':'11807','splitRatio':'85','receiveRemind':'1'},{'doctorId':'216','splitRatio':'10','receiveRemind':'0'},{'doctorId':'217','splitRatio':'5','receiveRemind':'0'}]}";
          Response response= given(). param("access_token", access_token)
        		  			.param("data", data)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
        		  			.post("/savePackDoctor");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }

    
    /**
     * 测试关怀计划套餐查询 
     */
    @Test
    public void testQueryCare() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("doctorId", 511)
        		  			.param("packType", 3)
        		  			. param("status", 1)
        		  			.post("/query");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  assertNotNull(jp.get("data")); 
    }
    
    
    /**
     * 测试套餐删除
     */
//    @Test
    public void testDel() throws UnsupportedEncodingException {
    	
          Response response= given(). param("access_token", access_token)
        		  			.param("id", 12)
        		  			.post("/delPack");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    }
}
