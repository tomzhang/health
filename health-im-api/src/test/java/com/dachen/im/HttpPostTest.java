package com.dachen.im;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.dachen.im.server.data.ImgTextMsg;

public class HttpPostTest {
	
	private final static String baseURL = "http://localhost:8091";
	
	public static void main(String[]args) 
	{
		sendMsg("sendMsg");
	}
	public static void sendMsg(int model) 
	{
		Map<String,Object>paramMap = new HashMap<>();
		paramMap.put("pubId", "pub_793");
		paramMap.put("model", model);
		paramMap.put("sendType", 1);
		paramMap.put("fromUserId", "0");
		if(model==1)
		{
			paramMap.put("content", "吞吞吐吐hahahh");
		}
		else
		{
			List<ImgTextMsg>mpt = new ArrayList<ImgTextMsg>();
			ImgTextMsg msg = new ImgTextMsg();
			msg.setTitle("没有逻辑，就远离这个市场！没有逻辑，就远离这个市场没有逻辑，就远离这个市场");
			msg.setTime(System.currentTimeMillis());
			msg.setPic("http://192.168.3.7:8081/avatar/t/511/144043425.jpg");
			msg.setDigest("陶阳：上海筑金投资公司董事长兼投资部总经理");
//			msg.setDigest("陶阳：上海筑金投资公司董事长兼投资部总经理，以下内容根据陶阳在鄂尔多斯高端论坛上的讲话整理");
			msg.setUrl("http://www.baidu.com");
			msg.setFooter("阅读全文");
			mpt.add(msg);
			paramMap.put("mpt", mpt);
		}
		
//		paramMap.put("", "");
		boolean toAll = true;
		paramMap.put("toAll", toAll);
		if(!toAll)
		{
			List<String>userIds = new ArrayList<>();
			userIds.add("259");
			userIds.add("261");
			userIds.add("265");
			userIds.add("266");
			userIds.add("267");
			paramMap.put("to", userIds);
		}
		postJson(baseURL+"/pub/sendMsg",JSON.toJSONString(paramMap));
	}
	
	private static void sendMsg(String groupId)
	{
		//guide_114
		List<ImgTextMsg>mpt = new ArrayList<ImgTextMsg>();
		ImgTextMsg msg = new ImgTextMsg();
		msg.setTitle("没有逻辑，就远离这个市场！没有逻辑，就远离这个市场没有逻辑，就远离这个市场就远离这个市场没有逻辑，就远离这个市场");
		msg.setTime(System.currentTimeMillis());
		msg.setPic("http://192.168.3.7:8081/avatar/t/511/144043425.jpg");
		msg.setDigest("陶阳：上海筑金投资公司董事长兼投资部总经理没有逻辑，就远离这个市场！没有逻辑，就远离这个市场没有逻辑，就远离这个市场就远离这个市场没有逻辑，就远离这个市场没有逻辑，就远离这个市场！没有逻辑，就远离这个市场没有逻辑，就远离这个市场就远离这个市场没有逻辑，就远离这个市场");
//		msg.setContent("陶阳：上海筑金投资公司董事长兼投资部总经理，以下内容根据陶阳在鄂尔多斯高端论坛上的讲话整理陶阳：上海筑金投资公司董事长兼投资部总经理，以下内容根据陶阳在鄂尔多斯高端论坛上的讲话整理");
		msg.setUrl("http://www.baidu.com");
		msg.setFooter("阅读全文");
		mpt.add(msg);
		
		Map<String,Object>paramMap = new HashMap<>();
		paramMap.put("gid", "U1DZ7FOYJN");//guide_114
		paramMap.put("type", 16);
		paramMap.put("fromUserId", "0");
		paramMap.put("mpt", mpt);
		postJson("http://localhost:8090/im/convers/send.action",JSON.toJSONString(paramMap));
	}
	private static String postJson(String url,String jsonParam) 
	{
		   String respContent=null;
	       HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
	       CloseableHttpClient closeableHttpClient = httpClientBuilder.build();  
	       HttpPost httpPost = new HttpPost(url); 

	       httpPost.setHeader("access_token", "0009b2a1012b4fc49cbd5416b550e0bc");
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
}
