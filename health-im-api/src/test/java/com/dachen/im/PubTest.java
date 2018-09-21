package com.dachen.im;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.response.MsgGroupDetail;
import com.dachen.im.server.data.response.Result;
import com.dachen.pub.model.param.PubMsgParam;
import com.dachen.util.JSONUtil;

public class PubTest {
	
//    private final static String baseURL = "http://120.24.94.126:8091";
//    private final static String baseURL = "http://112.74.208.140:8091";
	private final static String baseURL = "http://localhost:8091";
    private final static String pid ="pub_001";
	
	public static void main(String[]args) throws Exception 
	{
		long time = System.currentTimeMillis();
//		String subId="245";
		System.out.println(time);
//		welcome("pub_001");
//		broadcast("欢迎您使用玄关健康");
//		Thread.sleep(1000);
//		
//		subscribe_reply(pid,"你们的产品真太好了。",subId);
//		Thread.sleep(1000);
//		
//		speaker_reply(pid,"谢谢亲。","100180");
//		sendDoctorPub();
		groupList("100266");
//		groupList("100186");
//		groupList("100180");
//		
//		msgList("pub_001","100185");
//		msgList(pid+"_"+subId,"100186");
//		msgList(pid+"_"+subId,"100180");
	}
	
	private static void welcome(String pubId)
	{
		if(pubId==null)
		{
			return;
		}
		List<ImgTextMsg>mpt = new ArrayList<ImgTextMsg>();
		ImgTextMsg msg = new ImgTextMsg();
		msg.setTitle("欢迎您使用玄关健康2");
		msg.setPic("http://120.24.94.126:8081/pub/welcome.jpg");
		msg.setDigest("如果您在使用过程中遇到了任何问题，可以随时在这里向我们反馈。");
		mpt.add(msg);
		
		PubMsgParam pubMsg = new PubMsgParam();
		pubMsg.setModel(2);//单图文消息
		pubMsg.setPubId(pubId);//公共号Id
		pubMsg.setSendType(2);//广播给指定用户
		pubMsg.setToAll(true);//不发送给所有人
		pubMsg.setToNews(false);//不往健康动态公共号转发
		pubMsg.setMpt(mpt);
		
		String action="/pub/sendMsg";
		String response = postJson(baseURL+action,JSON.toJSONString(pubMsg));
		
		System.out.println(response);
	}
	
	private static void broadcast(String content) 
	{
		String action="/pub/sendMsg";
		
		Map<String,Object>paramMap = new HashMap<>();
		paramMap.put("pubId", "pub_pub_55e46911b522252fc2083c78");
//		paramMap.put("userId", 216);
		paramMap.put("model", 2);
		paramMap.put("toAll", true);
		paramMap.put("toNews", true);
		paramMap.put("sendType",2);
		
//		paramMap.put("content", content);
		//guide_114
		List<ImgTextMsg>mpt = new ArrayList<ImgTextMsg>();
		ImgTextMsg msg = new ImgTextMsg();
		msg.setTitle("集团公告（大辰）");
//		msg.setTime(System.currentTimeMillis());
		msg.setPic("http://120.24.94.126:8081/af/201511/pubdoc/8bf5f20d543c410aa62c2ce8f5860df7.png");
		msg.setContent("100医生集团2群发100医生集团2群发100医生集团2群发100医生集团2群发100医生集团2群发100医生集团2群发100医生集团2群发100医生集团2群发100医生集团2群发100医生集团2群发");
//		msg.setUrl("http://www.baidu.com");
//		msg.setFooter("阅读全文");
		mpt.add(msg);
		paramMap.put("mpt", mpt);
		postJson(baseURL+action,JSON.toJSONString(paramMap));
	}
	
	private static void subscribe_reply(String pid,String content,String userId)
	{
		MessageVO msg = new MessageVO();
		msg.setType(1);
		msg.setGid(pid);
		msg.setContent(content);
		msg.setFromUserId(userId);
		String action="/pub/send";
		postJson(baseURL+action,JSON.toJSONString(msg));
	}
	
	private static void sendDoctorPub() 
	{
		String action="/pub/sendMsg";
		
		Map<String,Object>paramMap = new HashMap<>();
		paramMap.put("pubId", "pub_doctor_100266");
		paramMap.put("model", 2);
		paramMap.put("toAll", true);
		paramMap.put("toNews", true);
		paramMap.put("sendType",2);
		
//		paramMap.put("content", content);
		//guide_114
		List<ImgTextMsg>mpt = new ArrayList<ImgTextMsg>();
		ImgTextMsg msg = new ImgTextMsg();
		msg.setTitle("医生动态_test001");
		msg.setDigest("人民币“入篮”有多重意义 专家称利大");
		msg.setPic("http://120.24.94.126:8081/af/201511/pubdoc/8bf5f20d543c410aa62c2ce8f5860df7.png");
		msg.setUrl("http://www.baidu.com");
		mpt.add(msg);
		paramMap.put("mpt", mpt);
		String response = postJson(baseURL+action,JSON.toJSONString(paramMap));
		System.out.println(response);
	}
	
	private static void speaker_reply(String pid,String content,String userId) 
	{
		
	}
	
	private static void groupList(String userId)
	{
		Map<String,String>paramMap = new HashMap<>();
		paramMap.put("userId",userId);
		paramMap.put("cnt", "1000");
		paramMap.put("ts", "1448936552950");
//		paramMap.put("access_token", "c80ab1c001eb46fbbda83d9042b56ee3");
		
		String action="/im/msg/getBusiness";
		String response = get(baseURL+action,paramMap);
		Result result  = JSONUtil.parseObject(Result.class, response);
		JSON json= (JSON)result.getData();
//		GroupListResult msgGroupList = JSON.toJavaObject(json,GroupListResult.class);
//		List<MsgGroupDetail> list = msgGroupList.getList();
//		for(MsgGroupDetail group:list)
//		{
//			
//		}
		System.out.println(response);
	}
	
	private static void msgList(String gid,String userId)
	{
		Map<String,String>paramMap = new HashMap<>();
		paramMap.put("userId",userId);
		paramMap.put("groupId", gid);
		paramMap.put("type", "0");
		paramMap.put("cnt", "20");
//		paramMap.put("access_token", "c80ab1c001eb46fbbda83d9042b56ee3");
		
		String action="/im/msg/get";
		String response = get(baseURL+action,paramMap);
		Result result  = JSONUtil.parseObject(Result.class, response);
		result.getData();
		System.out.println(response);
	}
	private static String postJson(String url,String jsonParam) 
	{
		   String respContent=null;
	       HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
	       CloseableHttpClient closeableHttpClient = httpClientBuilder.build();  
	       HttpPost httpPost = new HttpPost(url); 

	       httpPost.setHeader("access_token", "c80ab1c001eb46fbbda83d9042b56ee3");
	       StringEntity entity = new StringEntity(jsonParam.toString(),"utf-8");//解决中文乱码问题    
           entity.setContentEncoding("UTF-8");    
           entity.setContentType("application/json");    
	       try {  
	           httpPost.setEntity(entity);  
	           HttpResponse httpResponse = closeableHttpClient.execute(httpPost);  
		       	if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
		       	{
		       		 respContent = EntityUtils.toString( httpResponse.getEntity(), "UTF-8").trim();
		       	}
	       }  
	       catch (Exception e) {
				e.printStackTrace();
			}  
	       finally {  
	            try {
					closeableHttpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}  
	       }
	       return respContent;
	}
	
	public static String get(String url,Map<String,String> paramMap) 
	{
		   String respContent=null;
	       HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
	       CloseableHttpClient closeableHttpClient = httpClientBuilder.build();  
	      
	       int i=0;
	       for (Map.Entry<String, String> entry : paramMap.entrySet()) 
	       {
	    	   if(i==0)
	    	   {
	    		   url=url+"?"+entry.getKey()+"="+entry.getValue();
	    	   }
	    	   else
	    	   {
	    		   url=url+"&"+entry.getKey()+"="+entry.getValue();   
	    	   }
	    	   i++;
	    	   
	       }
	       HttpGet httpGet = new HttpGet(url); 
	       try {  
	           HttpResponse httpResponse = closeableHttpClient.execute(httpGet);  
		       	if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
		       	{
		       		 respContent = EntityUtils.toString( httpResponse.getEntity(), "UTF-8").trim();
		       	}
	       }  
	       catch (Exception e) {
				e.printStackTrace();
			}  
	       finally {  
	            try {
					closeableHttpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}  
	       }
	       return respContent;
	}
}
