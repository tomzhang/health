/**
 * @author Glan.duanyj
 * @date 2014-05-12
 * @project rest_demo
 */
package com.ucpaas.restsdk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.constant.DownTaskEnum;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.ucpaas.restsdk.client.AbsRestClient;
import com.ucpaas.restsdk.client.JsonReqClient;
import com.ucpaas.restsdk.client.XmlReqClient;
import com.ucpaas.restsdk.models.Member;

public class UcPaasRestSdk {
	private static String accountSid() {
        return PropertiesUtil.getContextProperty(
                "accountSid");
    }
	private static String authToken() {
        return PropertiesUtil.getContextProperty(
                "authToken");
    }
	private static String appId() {
        return PropertiesUtil.getContextProperty("appId");
    }
	private static String templateId() {
        return PropertiesUtil.getContextProperty(
                "templateId");
    }
	private static String fromSerNum_base() {
        return PropertiesUtil.getContextProperty(
                "from_ser_num");
    }
	private static String toSerNum_base() {
        return PropertiesUtil.getContextProperty(
                "to_ser_num");
    }
	public static String ringtoneId() {
        return PropertiesUtil.getContextProperty("xg_ringtongeId");
    }

	private static boolean json = true;
	
	private static String chargeType = "0";
	private static String charge = "0";

	static AbsRestClient InstantiationRestAPI(boolean enable) {
		if (enable) {
			return new JsonReqClient();
		} else {
			return new XmlReqClient();
		}
	}

	public static void findAccount(boolean json, String accountSid,
			String authToken) {
		try {
			String result = InstantiationRestAPI(json).findAccoutInfo(
					accountSid, authToken);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static JSONObject createClient(boolean json, String accountSid,
			String authToken, String appId, String clientName,
			String chargeType, String charge, String mobile) {
		try {
			String result = InstantiationRestAPI(json).createClient(accountSid,
					authToken, appId, clientName, chargeType, charge, mobile);
			return JSON.parseObject(result);
		} catch (Exception e) {
			// TODO: handle exception
			throw new ServiceException(20001, "ucpaas server error");
		}
	}
	
	public static JSONObject createClient(String clientName,
			String chargeType, String charge, String mobile) {
		return createClient(json, accountSid(), authToken(), appId(), clientName, chargeType, charge, mobile);
	}
	
	public static JSONObject createClient(String clientName,
			 String mobile) {
		return createClient( clientName, chargeType, charge, mobile);
	}

	public static void findClients(boolean json, String accountSid,
			String authToken, String appId, String start, String limit) {
		try {
			String result = InstantiationRestAPI(json).findClients(accountSid,
					authToken, appId, start, limit);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void findClientByNbr(boolean json, String accountSid,
			String authToken, String clientNumber, String appId) {
		try {
			String result = InstantiationRestAPI(json).findClientByNbr(
					accountSid, authToken, clientNumber, appId);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void closeClient(boolean json, String accountSid,
			String authToken, String clientNumber, String appId) {
		try {
			String result = InstantiationRestAPI(json).closeClient(accountSid,
					authToken, clientNumber, appId);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void chargeClient(boolean json, String accountSid,
			String authToken, String clientNumber, String chargeType,
			String charge, String appId) {
		try {
			String result = InstantiationRestAPI(json).charegeClient(
					accountSid, authToken, clientNumber, chargeType, charge,
					appId);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void billList(boolean json, String accountSid,
			String authToken, String appId, String date) {
		try {
			String result = InstantiationRestAPI(json).billList(accountSid,
					authToken, appId, date);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void clientBillList(boolean json, String accountSid,
			String authToken, String appId, String clientNumber, String date) {
		try {
			String result = InstantiationRestAPI(json).clientBillList(
					accountSid, authToken, appId, clientNumber, date);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static JSONObject callback(String fromClient, String to,
			String fromSerNum, String toSerNum,String orderId) {
		return callback(json, accountSid(), authToken(), appId(), fromClient, to,
				fromSerNum_base(), toSerNum_base(),orderId);
	}

	public static JSONObject callback(boolean json, String accountSid,
			String authToken, String appId, String fromClient, String to,
			String fromSerNum, String toSerNumm,String orderId) {
		try {
			String rtId = ringtoneId();
			if(ReqUtil.instance.isBDJL()){
				rtId = PropertiesUtil.getContextProperty("bd_ringtongeId");
			}
			String result = InstantiationRestAPI(json).callback(accountSid,
					authToken, appId, fromClient, to, fromSerNum, toSerNumm,orderId,rtId);
			return JSON.parseObject(result);
		} catch (Exception e) {
			throw new ServiceException(20001, e.getMessage());
		}
	}

	public static String voiceCode(boolean json, String accountSid,
			String authToken, String appId, String to, String verifyCode) {
		String result = null;
		try {
			result = InstantiationRestAPI(json).voiceCode(accountSid,
					authToken, appId, to, verifyCode);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
	
	public static String voiceNotify(boolean json, String accountSid,
			String authToken, String appId, String to, String content, String toSerNum) {
		String result = null;
		try {
			result = InstantiationRestAPI(json).voiceNotify(accountSid, authToken, appId, content, to, "0", toSerNum, "1");
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	public static void templateSMS(boolean json, String accountSid,
			String authToken, String appId, String templateId, String to,
			String param) {
		try {
			String result = InstantiationRestAPI(json).templateSMS(accountSid,
					authToken, appId, templateId, to, param);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static String createConference(boolean json, String accountSid,String authToken, String appId,
			Integer maxMember, Integer duration, Integer playTone){
		String result = null;
		try {
			
			result = InstantiationRestAPI(json).createConference(accountSid, authToken, appId, String.valueOf(maxMember), duration, playTone);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
		}
		return result;
	}
	
	public static String removeConference(boolean json,String accountSid,String authToken, String appId,String confId,String callId){
		String result = null;
		try {
			result = InstantiationRestAPI(json).removeConference(accountSid, authToken, appId, confId,callId);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
		}
		return result;
	}
	
	public static String dismissConference(boolean json,String accountSid,String authToken, String appId,String confId){
		String result = null;
		try {
			result = InstantiationRestAPI(json).dismissConference(accountSid, authToken, appId,confId);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
		}
		return result;
	}
	
	public static String inviteConference(boolean json,String accountSid,String authToken, String appId,String confId,List<Member> list){
		String result = null;
		try {
			result = InstantiationRestAPI(json).inviteConference(accountSid, authToken, appId,confId, list);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
		}
		return result;
	}
	
	public static String muteConference(boolean json,String accountSid,String authToken, String appId,String confId,String callId){
		String result = null;
		try {
			result = InstantiationRestAPI(json).muteConference(accountSid, authToken, appId,confId, callId);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
		}
		return result;
	}
	
	public static String unMuteConference(boolean json,String accountSid,String authToken, String appId,String confId,String callId){
		String result = null;
		try {
			result = InstantiationRestAPI(json).unMuteConference(accountSid, authToken, appId,confId, callId);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
		}
		return result;
	}
	public static String deafConference(boolean json,String accountSid,String authToken, String appId,String confId,String callId){
		String result = null;
		try {
			result = InstantiationRestAPI(json).deafConference(accountSid, authToken, appId,confId, callId);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
		}
		return result;
	}
	
	public static String unDeafConference(boolean json,String accountSid,String authToken, String appId,String confId,String callId){
		String result = null;
		try {
			result = InstantiationRestAPI(json).unDeafConference(accountSid, authToken, appId,confId, callId);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
		}
		return result;
	}
	/**
	 * 语音用于 提醒患者用户
	 * @param content 在云之讯审核通过的语音ID
	 * @param to 需要通知对法的手机号
	 */
	public static String sendVoiceNotify(String content,String to){
		try {
			String toSerNum = PropertiesUtil.getContextProperty("from_ser_num");//回拨显示的电话号码
			String accountSid=PropertiesUtil.getContextProperty("accountSid");
			String authToken = PropertiesUtil.getContextProperty("authToken");
			String appId=PropertiesUtil.getContextProperty("appId");
			String type="1";//发送固定语音回拨 0：文字转语音，1：直接语音ID
			String playTimes ="1";//重复次数
			String result=InstantiationRestAPI(false).voiceNotify(accountSid, authToken, appId, to, content, type, toSerNum, playTimes);
			System.out.println("Response content is: " + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	public static String queryConference(boolean json,String accountSid,String authToken, String appId,String confId){
		String result = null;
		try {
			result = InstantiationRestAPI(json).queryConference(accountSid, authToken, appId,confId);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
		}
		return result;
	}
	
	public static String recordConference(boolean json,String accountSid,String authToken, String appId,String confId){
		String result = null;
		try {
			result = InstantiationRestAPI(json).recordConference(accountSid, authToken, appId,confId);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
		}
		return result;
	}
	
	public static String stopRecordConference(boolean json,String accountSid,String authToken, String appId,String confId){
		String result = null;
		try {
			result = InstantiationRestAPI(json).stopRecordConference(accountSid, authToken, appId,confId);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
		}
		return result;
	}
	
	public static JSONObject findClientByMobile(boolean json, String accountSid,
			String authToken, String mobile, String appId) {
		try {
			String result = InstantiationRestAPI(json).findClientByMobile(
					accountSid, authToken, mobile, appId);
			//System.out.println("Response content is: " + result);
			return JSON.parseObject(result);
		} catch (Exception e) {
			throw new ServiceException(20001, "ucpaass error");
		}
	}
	
	/**
	 * 释放client
	 * @param json
	 * @param accountSid
	 * @param authToken
	 * @param appId
	 * @return
	 */
	public static JSONObject closeClientByMobile(boolean json, String accountSid,
			String authToken, String clientId, String appId) {
		try {
			String result = InstantiationRestAPI(json).closeClient(
					accountSid, authToken, clientId, appId);
			//System.out.println("Response content is: " + result);
			return JSON.parseObject(result);
		} catch (Exception e) {
			// TODO: handle exception
			throw new ServiceException(20001, "ucpaass error");
		}
	}
	
	public static JSONObject findClientByMobile( String mobile) {
		return findClientByMobile(json,accountSid() , authToken(), mobile, appId());
	}
	
	
	

	public static void dispalyNumber(boolean json, String accountSid,
			String authToken, String appId, String clientNumber, String display) {
		try {
			String result = InstantiationRestAPI(json).dispalyNumber(
					accountSid, authToken, appId, clientNumber, display);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 只对accountSid, authToken, appId鉴定
	 * @param accountSid
	 * @param appId
	 * @return
	 */
	public static boolean authVoip(String accountSid ,String appId){
		boolean result = false;
		if(accountSid.trim().equals(UcPaasRestSdk.accountSid())  && appId.trim().equals(UcPaasRestSdk.appId())){
			result = true;
		}
		return result;
	}
	
	public static Map<String,Object> downTask(String url,String recordId){
		Map<String,Object> map = new HashMap<String, Object>();
		//MD5加密
		EncryptUtil encryptUtil = new EncryptUtil();
		// 构造请求URL内容 
		String sig = accountSid() + recordId + authToken();
		boolean result = false;
		try {
			String signature = encryptUtil.md5Digest(sig);
			url = url + "?sig=" + signature;
			InputStream in = new URL(url).openConnection().getInputStream(); // 创建连接、输入流
			String osName = System.getProperty("os.name").toLowerCase();
			String path = DownTaskEnum.DOWN_PATH_LINUX ;
			if(osName.startsWith("windows")){
				path = DownTaskEnum.DOWN_PATH_WINDOW;
			}
			String uuid = UUID.randomUUID().toString().replace("-", "");
			File file = new File(path);
			if(!file.exists()){
				file.mkdirs();
			}
			path = path + uuid;
			file = new File(path);
			if(file.exists()){
				file.delete();
			}else{
				file.createNewFile();
			}
			FileOutputStream f = new FileOutputStream(file);// 创建文件输出流
			byte[] bb = new byte[1024]; // 接收缓存
			int len;
			while ((len = in.read(bb)) > 0) { // 接收
				f.write(bb, 0, len); // 写入文件
			}
			f.close();
			in.close();
			result = true;
			map.put("path", path);
			map.put("uuid", uuid);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		map.put("result", result);
		return map;
	}
	
	
	
	/**
	 * 从网络Url中下载文件
	 * @param urlStr
	 * @throws IOException
	 */
	public static Map<String,Object>  downLoadFromUrl(String urlStr,String recordId,String type) {
		Map<String,Object> map = new HashMap<String, Object>();
		
		
		String osName = System.getProperty("os.name").toLowerCase();
		String path = DownTaskEnum.DOWN_PATH_LINUX ;
		if(osName.startsWith("windows")){
			path = DownTaskEnum.DOWN_PATH_WINDOW;
		}
		boolean result = false;
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容 
			String sig = accountSid() + recordId + authToken();
			String signature = encryptUtil.md5Digest(sig);
			urlStr = urlStr + "?sig=" + signature;
			URL url = new URL(urlStr);  
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
			//设置超时间为3秒
			conn.setConnectTimeout(3*1000);
			//防止屏蔽程序抓取而返回403错误
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

			//得到输入流
			InputStream inputStream = conn.getInputStream();  
			//获取自己数组
			byte[] getData = readInputStream(inputStream);    

			//文件保存位置
			File saveDir = new File(path);
			if(!saveDir.exists()){
				saveDir.mkdir();
			}
			String uuid = UUID.randomUUID().toString().replace("-", "");
			if(type.equalsIgnoreCase("B")){
				uuid += ".mp3";
			}
			
			path = path + uuid;
			File file = new File(path);
			if(file.exists()){
				file.delete();
			}else{
				file.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream(file);     
			fos.write(getData); 
			if(fos!=null){
				fos.close();  
			}
			if(inputStream!=null){
				inputStream.close();
			}
			result = true;
			map.put("path", path);
			map.put("uuid", uuid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("result", result);
		return map;
	}
	
	/**
	 * 从输入流中获取字节数组
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static  byte[] readInputStream(InputStream inputStream) throws IOException {  
		byte[] buffer = new byte[1024];  
		int len = 0;  
		ByteArrayOutputStream bos = new ByteArrayOutputStream();  
		while((len = inputStream.read(buffer)) != -1) {  
			bos.write(buffer, 0, len);  
		}  
		bos.close();  
		return bos.toByteArray();  
	}  
	

	/**
	 * 测试说明 参数顺序，请参照各具体方法参数名称 参数名称含义，请参考rest api 文档
	 * 
	 * @author Glan.duanyj
	 * @date 2014-06-30
	 * @return void
	 * @throws IOException
	 * @method main
	 */
	public static void main(String[] args) throws IOException {
		
	}

	private static void test() throws IOException {
		
		
		UcPaasRestSdk.sendVoiceNotify("214976","18938856957");
		
		// String jsonStr="{\"client\":\"1\"}";
		// JSONObject obj=JSONObject.fromObject(jsonStr);
		// System.out.println(obj.getInt("client"));
		/*System.out.println("请输入参数，以空格隔开...");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String param = br.readLine();
		String[] params = param.split(" ");
		String method = params[0];
		boolean json = true;
		if (params[1].equals("xml")) {
			json = false;
		}
		if (method.equals("1")) {
			// String accountSid="";
			// String token="";
			findAccount(json, accountSid, authToken);
		} else if (method.equals("2")) {
			createClient(json, params[2], params[3], params[4], params[5],
					params[6], params[7], params[8]);
		} else if (method.equals("3")) {
			
			 * String accountSid=""; String token=""; String appId="";
			 
			findClients(json, accountSid, authToken, appId, "0", "5");
		} else if (method.equals("4")) {
			findClientByNbr(json, params[2], params[3], params[4], params[5]);
		} else if (method.equals("5")) {
			closeClient(json, params[2], params[3], params[4], params[5]);
		} else if (method.equals("6")) {
			chargeClient(json, params[2], params[3], params[4], params[5],
					params[6], params[7]);
		} else if (method.equals("7")) {
			billList(json, params[2], params[3], params[4], params[5]);
		} else if (method.equals("8")) {
			clientBillList(json, params[2], params[3], params[4], params[5],
					params[6]);
		} else if (method.equals("9")) {
			// String accountSid = "";// 主账户Id
			// String authToken="";
			// String appId="";
			// accountSid="";
			// authToken="";
			// appId="";
			String fromClient = "";
			String to = "";
			String fromSerNum = "";
			String toSerNum = "";
			callback(json, accountSid, authToken, appId, fromClient, to,
					fromSerNum, toSerNum,"");
		} else if (method.equals("10")) {
			String to = "";
			String accountSid = "";
			String token = "";
			String appId = "";
			String para = "";
			voiceCode(json, accountSid, token, appId, to, para);
		} else if (method.equals("11")) { // 短信验证码
			String accountSid = "";
			String token = "";
			String appId = "";
			String templateId = "";
			String to = "";
			String para = "";
			templateSMS(json, accountSid, token, appId, templateId, to, para);
		} else if (method.equals("12")) {
			findClientByMobile(json, params[2], params[3], params[4], params[5]);
		} else if (method.equals("13")) {
			String accountSid = "";
			String token = "";
			String clientNumber = "";
			String appId = "";
			String display = "1";
			dispalyNumber(json, accountSid, token, appId, clientNumber, display);
		}*/
	}
	
	
	
	
}
