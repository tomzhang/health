package com.dachen.health.controller.user;

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

/**
 * 用例规划
 * 1、
 * @author Administrator
 *
 */
public class FriendsControllerTestIT   {
	public String access_token;
	
	@Before
	public void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		access_token=TestUtil.testGetToken("10012",TestConstants.password_doc2,TestConstants.userType_doc3);
		RestAssured.basePath="/friends";
	}

   /**
   * 测试发送好友申请
   * 
   */

  //@Test
  public void testSendApply() throws UnsupportedEncodingException {
	  System.out.println("testSendApply");
        Response response= given(). param("access_token", access_token)
      		  			.param("toUserId", 12078)
      		  			.param("applyContent", "add apply update")
      		  			.post("/sendApply");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  
  	  assertEquals(new Integer(1),jp.get("resultCode")); 
  	
  }
  
  
  /**
  * 测试处理好友申请
  */
// @Test
 public void testReplyAddFriend() throws UnsupportedEncodingException {
	 
       Response response= given(). param("access_token", access_token)
     		  			.param("id", "55ddad5a4cf9de26fc713247")
     		  			.param("result", 2)
     		  			.post("/replyAdd");
 	  JsonPath jp = new JsonPath(response.asString());  
 	  
 	  assertEquals(new Integer(1),jp.get("resultCode")); 
 	
 }
 
 /**
 * 测试获取好友请求列表
 */
//@Test
public void testGetFriendReq() throws UnsupportedEncodingException {
	  System.out.println("testGetFriendReq");
      Response response= given(). param("access_token", access_token)
    		  			.param("pageIndex", 0)
    		  			.post("/getFriendReq");
	  JsonPath jp = new JsonPath(response.asString());  
	  
	  assertEquals(new Integer(1),jp.get("resultCode")); 
	
}
	
	 /**
     * 测试加好友
     * 
     */
//    @Test
    public void testApplyAddFriend() throws UnsupportedEncodingException {
          Response response= given(). param("access_token", access_token)
        		  			.param("toUserId", 355)
        		  			.post("/applyAdd");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	
    } 
    

  /**
   * 测试删好友
   */
//  @Test
  public void testDelFriend() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			.param("toUserId", 355)
      		  			.post("/delete");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  
  	  assertEquals(new Integer(1),jp.get("resultCode")); 
  
  }
  
  /**
   * 测试添加手机联系人
   * 
   */
  @Test
  public void testAddPhoneFriend() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			.param("phone", "18689208527")
      		  			.post("/addPhoneFriend");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  
  	  assertEquals(new Integer(1),jp.get("resultCode")); 
  	
  } 
  
  
  /**
   * 发送邀请短信
   * 
   */
  //TODO 所有发短信要通过系统记录
  
//  @Test
  public void testSendInviteMsg() throws UnsupportedEncodingException {
        Response response= given(). param("access_token", access_token)
      		  			.param("phone", "13751132072")
      		  			.post("/sendInviteMsg");
  	  JsonPath jp = new JsonPath(response.asString());  
  	  
  	  assertEquals(new Integer(1),jp.get("resultCode")); 
  	
  } 
  
	@Test
	public void testAddDelGroupFriend() {
		Response response = given().param("access_token", access_token)
				.param("userId", 11111)
				.param("toUserId", 22222)
				.post("/addGroupFriend");
		JsonPath jp = new JsonPath(response.asString());
		
		response = given().param("access_token", access_token)
				.param("userId", 11111)
				.param("toUserId", 22222)
				.post("/delGroupFriend");
		jp = new JsonPath(response.asString());

		assertEquals(new Integer(1), jp.get("resultCode"));
	}

}
