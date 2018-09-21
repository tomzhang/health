package com.dachen.health.controller.group;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.dachen.health.controller.base.BaseControllerTest;
import com.dachen.health.controller.base.TestConstants;
import com.dachen.health.controller.base.TestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public class GroupControllerTestIT  extends  BaseControllerTest {
	
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws UnsupportedEncodingException {
		RestAssured.baseURI = TestConstants.baseURI;
		RestAssured.port = TestConstants.port;
		//String access_token = jp.get("data.access_token");
		loginData=TestUtil.testGetLoginToken(TestConstants.telephone_com,TestConstants.password_com,TestConstants.userType_com);
		data=((Map<String,Object>)loginData.get("data"));
		// 获取登录token
		access_token =(String)((Map<String,Object>)loginData.get("data")).get("access_token"); 
		param.clear();
		param.put("access_token", access_token);
		
		RestAssured.basePath="/group";
		
	 
	}


	
	  /**
     * 测试注册集团
     */
//    @Test
    public void testRegGroup() throws UnsupportedEncodingException {
    	 
          Response response= given(). param("access_token", access_token)
        		  			.param("companyId", "55d58633cb827c15fcdf7d44")
        		  			.param("name", "100医生集团")
        		  			.param("introduction", "100医生集团，用于测试")
        		  			.param("config.memberInvite", "true")
        		  			.param("config.passByAudit", "true")
        		  			.param("config.parentProfit", 10)
        		  			.param("config.groupProfit", 10)
        		  			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")) 
        		  			.post("/regGroup");
    	  JsonPath jp = new JsonPath(response.asString());  
    	  
    	  assertEquals(new Integer(1),jp.get("resultCode")); 
    	  
    }
    
    /**
     * 测试修改集团信息
     */
    @Test
    public void testUpdateGroup() {
    	Response response = given(). param("access_token", access_token)
			    			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
			    			.param("id", "55d588fccb827c15fc9d47b3")
			    			.param("introduction", "100医生集团测试")
				  			.post("/updateByGroup");
    	JsonPath jp = new JsonPath(response.asString());  
  	  
  	    assertEquals(new Integer(1),jp.get("resultCode")); 
    	assertNotNull(jp.get("data.id")); 
    }
    
    
//    @Test
    public void testGroupApply() {
    	Response response = given(). param("access_token", access_token)
			    			.header(new Header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"))
			    			.param("name", "test集团申请")
			    			.param("introduction", "test医生集团测试")
			    			.param("logoUrl", "test医生集团测试")
			    			.param("applyUserId", 12869)
				  			.post("/groupApply");
    	JsonPath jp = new JsonPath(response.asString());
  	    assertEquals(1,1); 
    }
    
//    @Test
    public void testGroupApplyInfo() {
    	Response response = given(). param("access_token", access_token)
			    			.param("groupId", "1234567")
				  			.post("/groupApplyInfo");
    	JsonPath jp = new JsonPath(response.asString());  
  	    assertEquals(1,1); 
    }
    
//    @Test
    public void testGroupAudit() {
    	Response response = given(). param("access_token", access_token)
			    			.param("groupApplyId", "123456789")
			    			.param("auditId", "1234567")
			    			.param("status", "1234567")
				  			.post("/processGroupApply");
    	JsonPath jp = new JsonPath(response.asString());  
  	    assertEquals(1,1); 
    }
    
    
    @Test //参数不够
    public void testApplyList() {
    	Response response = given(). param("access_token", access_token)
			    			.param("pageIndex", "0")
			    			.param("pageSize", "10")
			    			.param("status", "P")
//			    			.param("groupActive", "active")
				  			.post("/applyList");
    	JsonPath jp = new JsonPath(response.asString());
//    	System.out.println(jp.prettyPrint());
  	    assertEquals(1,1); 
    }
    
//    @Test
    public void testApplyDetail() {
    	Response response = given(). param("access_token", access_token)
    						.param("groupApplyId", "1234567")
				  			.post("/applyDetail");
    	JsonPath jp = new JsonPath(response.asString());
  	    assertEquals(1,1); 
    }
    
    
    
//    @Test
    public void testApplyTransfer() {
    	Response response = given(). param("access_token", access_token)
    						.param("groupId", "1234567")
    						.param("inviteUserId", "1234567")
    						.param("confirmUserId", "1234567")
				  			.post("/applyTransfer");
    	JsonPath jp = new JsonPath(response.asString());
  	    assertEquals(1,1); 
    }
    
//    @Test
    public void testConfirmTransfer() {
    	Response response = given(). param("access_token", access_token)
    						.param("groupId", "1234567")
    						.param("inviteUserId", "1234567")
    						.param("confirmUserId", "1234567")
				  			.post("/confirmTransfer");
    	JsonPath jp = new JsonPath(response.asString());
  	    assertEquals(1,1); 
    }
    
//    @Test
    public void testGetTransferInfo() {
    	Response response = given(). param("access_token", access_token)
    						.param("groupUserApplyId", "1234567")
				  			.post("/getTransferInfo");
    	JsonPath jp = new JsonPath(response.asString());
  	    assertEquals(1,1); 
    }
    
	  @Test
	  public void testFindGroupByKeyword() {
	  	Response response = given(). param("access_token", access_token)
	  						.param("keyword", "jason")
					  		.post("/findGroupByKeyword");
	  	JsonPath jp = new JsonPath(response.asString());
	  	System.out.println(response.asString());
		    assertEquals(new Integer(1),jp.get("resultCode")); 
	  }
    
    
/*    @Test
    public void testtest() {
    	Response response = given(). param("access_token", access_token)
				  			.post("/testtest");
    	JsonPath jp = new JsonPath(response.asString());
  	    assertEquals(1,1); 
    }
    */
	  @Test
	  public void testActiveGroup() {
	    	Response response = given(). param("access_token", access_token)
	    						.param("groupApplyId", "5746defcbae14d6da06fbeed")
					  			.post("/activeGroup");
	    	JsonPath jp = new JsonPath(response.asString());
	  	    assertEquals(1,1); 
	    }
	  @Test
//	  @Ignore
	  public void testBlockGroup() {
	    	Response response = given(). param("access_token", access_token)
	    						.param("groupId", "56d96a1c4203f367c308c0f8")
					  			.post("/blockGroup");
	    	JsonPath jp = new JsonPath(response.asString());
	  	    assertEquals(1,1); 
	  }
	  @Test
//	  @Ignore
	  public void testUnBlockGroup() {
	    	Response response = given(). param("access_token", access_token)
	    						.param("groupId", "56d96a1c4203f367c308c0f8")
					  			.post("/unBlockGroup");
	    	JsonPath jp = new JsonPath(response.asString());
	  	    assertEquals(1,1); 
	  }
	  
	  @Test
	  public void testGetDeptsOfDoctors() {
	    	Response response = given(). param("access_token", access_token)
	    						.param("groupId", "55d588fccb827c15fc9d47b3")
					  			.post("/getDepartments");
	    	JsonPath jp = new JsonPath(response.asString());
	  	    assertEquals(1,1); 
	  }
	  
	  @Test
	  public void testGetServiceType() {
	    	Response response = given(). param("access_token", access_token)
	    						.param("groupId", "55d588fccb827c15fc9d47b3")
					  			.post("/getServiceType");
	    	JsonPath jp = new JsonPath(response.asString());
	  	    assertEquals(1,1); 
	  }
	  
	  //获取推荐集团列表
	  @Test
	  public void testGetGroupRecommendedList() {
	    	Response response = given(). param("access_token", access_token)
					  			.post("/getGroupRecommendedList");
	    	JsonPath jp = new JsonPath(response.asString());
	  	    assertEquals(1,1); 
	  }
	  
	  //设置集团推荐
	  @Test
	  public void testSetGroupToRecommended() {
	    	Response response = given(). param("access_token", access_token)
	    						.param("groupId", "55ed39a9b5222536f535515c")
					  			.post("/setGroupToRecommended");
	    	JsonPath jp = new JsonPath(response.asString());
	  	    assertEquals(1,1); 
	  }
	 
	  //移除集团推荐
	  @Test
	  public void testRemoveGroupRecommended() {
	    	Response response = given(). param("access_token", access_token)
	    						.param("groupId", "55ed39a9b5222536f535515c")
					  			.post("/removeGroupRecommended");
	    	JsonPath jp = new JsonPath(response.asString());
	  	    assertEquals(1,1); 
	  }
	  
	  //上升集团推荐排名
	  @Test
	  public void testRiseRecommendedOfGroup() {
	    	Response response = given(). param("access_token", access_token)
	    						.param("groupId", "55ed39a9b5222536f535515c")
					  			.post("/riseRecommendedOfGroup");
	    	JsonPath jp = new JsonPath(response.asString());
	  	    assertEquals(1,1); 
	  }
	  
	  //搜索集团
	  @Test
	  public void testSearchGroupByName() {
	    	Response response = given(). param("access_token", access_token)
	    						.param("name", "集团")
	    						.param("pageIndex", 0)
	    						.param("pageSize", 20)
					  			.post("/searchGroupByName");
	    	JsonPath jp = new JsonPath(response.asString());
	  	    assertEquals(1,1); 
	  }
}
