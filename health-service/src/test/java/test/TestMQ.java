package test;

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
import com.alibaba.fastjson.JSONObject;

public class TestMQ {

	public static void main(String[] args) {
		sendMsg("11872");
		
//		Integer i = Integer.parseInt("12a3");
//		System.out.println(1);
	}
	
	
	public static String postJson(String url, String jsonParam) {
		String respContent = null;
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
		HttpPost httpPost = new HttpPost(url);

		StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");// 解决中文乱码问题
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		try {
			httpPost.setEntity(entity);
			httpPost.setHeader("access_token", "adfaa06601d3422c9cbc349a496708cf");
			HttpResponse httpResponse = closeableHttpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				respContent = EntityUtils.toString(httpResponse.getEntity(),
						"UTF-8").trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeableHttpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return respContent;
	}
	
	public static void sendMsg(String fromUserId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", "9");
		params.put("fromUserId", fromUserId);
		params.put("fileId", "569623ace95217262cc555f2");
		
		List<Integer> userList = new ArrayList<Integer>();
		userList.add(12345);
		userList.add(54321);
		params.put("userList", userList);
		
		params.put("gid", "ILBHWV7QFA");
		params.put("key", "569623ace95217262cc555f2");
		params.put("name", "569623ace95217262cc555f2");
		params.put("size", "810KB");
		params.put("format", "zip");
//		 params.put("param.bizId", "dsexfefgd");
		params.put("isPush", "false");
		
		postJson("send.action", params);
	}
	
	private static void postJson(String action, Map<String, Object> params) {
//		String url = "http://localhost:8090/im/convers/" + action;
		String url = "http://192.168.3.7:8090/im/convers/" + action;
		String result = postJson(url, JSON.toJSONString(params));
		JSONObject map = JSON.parseObject(result);
//		System.out.println(result);
	}
	
}

