package com.dachen.health.controller.system;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;


/**
 * 意见反馈改为公众号方式实现，暂时屏蔽
 * @author Administrator
 *
 */
public class FeedBackControllerTestIT extends BaseControllerTest{ 

	//private String access_token;
//	@Before
    public void setUp() throws UnsupportedEncodingException {
		
		Date a=new Date(1441941733972L);
		super.setUp();
        RestAssured.basePath = "/feedback";
        //获取登录token
       // access_token=TestUtil.testGetLoginToken();
    }

    /**
     * 测试意见反馈新增
     */
//    @Test
    public void testCreate() throws UnsupportedEncodingException {
    	
        String content= "测试";
        Response response= given(). param("access_token", access_token)
      		  			.param("phoneModel", "华为integrate"). param("phoneSystem", "ddd"). param("clientVersion", "2.1"). param("content",content )
      		  			.get("/save");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  assertEquals(new Integer(1),jp.get("resultCode")); 
  }
    
    /**
     * 测试意见反馈查询
     * @throws Exception
     */
//    @Test 
	public void testQuery() throws Exception {
		Response response= given(). param("access_token", access_token)
						.param("pageIndex", 0).param("pageSize", 10).get("/query");
	   JsonPath jp = new JsonPath(response.asString());  
   	   assertEquals(new Integer(1),jp.get("resultCode")); 
   	   assertNotEquals(new Integer(1),jp.get("data.total")); 
   	   assertNotEquals(null,jp.get("data.pageData[0].userId")); 
	}
    
    /**
     * 测试获取单个意见反馈
     * @throws Exception
     */
//    @Test
	public void testGet() throws Exception {
    	
    	
    	
    	Response response= given(). param("access_token", access_token)
				.param("id", "559cee38577de6e4bfe3896d").get("/get");
		  JsonPath jp = new JsonPath(response.asString());  
		  assertEquals(new Integer(1),jp.get("resultCode")); 
		  assertEquals("559cee38577de6e4bfe3896d",jp.get("data.id")); 
		  assertNotEquals(null,jp.get("data.content")); 
	}

}
