/**
 * @author Glan.duanyj
 * @date 2014-05-12
 * @project rest_demo
 */
package com.ucpaas.restsdk.client;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.dachen.health.base.constant.CallEnum;
import com.google.gson.Gson;
import com.ucpaas.restsdk.DateUtil;
import com.ucpaas.restsdk.EncryptUtil;
import com.ucpaas.restsdk.UcPaasRestSdk;
import com.ucpaas.restsdk.models.AppBill;
import com.ucpaas.restsdk.models.Callback;
import com.ucpaas.restsdk.models.ClientBill;
import com.ucpaas.restsdk.models.Conf;
import com.ucpaas.restsdk.models.Member;
import com.ucpaas.restsdk.models.TemplateSMS;
import com.ucpaas.restsdk.models.VoiceCode;
import com.ucpaas.restsdk.models.VoiceNotify;
@SuppressWarnings("deprecation")
public class JsonReqClient extends AbsRestClient {
	private static Logger logger=Logger.getLogger(JsonReqClient.class);
	@Override
	public String findAccoutInfo(String accountSid, String authToken)
			throws NoSuchAlgorithmException, KeyManagementException {
		// TODO Auto-generated method stub
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			//构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),DateUtil.DATE_TIME_NO_SLASH);
			String signature=getSignature(accountSid, authToken,timestamp,encryptUtil);
			String url = getStringBuffer().append("/").append(version()).
					append("/Accounts/").append(accountSid).append("")
					.append("?sig=").append(signature).toString();
			logger.info(url);
			HttpResponse response=get("application/json",accountSid,authToken,timestamp,url,httpclient,encryptUtil);
			//获取响应实体信息
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			// 确保HTTP响应内容全部被读出或者内容流被关闭
			EntityUtils.consume(entity);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	@Override
	public String createClient(String accountSid, String authToken, 
			String appId,String clientName,String chargeType
			,String charge,String mobile) {
		// TODO Auto-generated method stub
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			//构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_NO_SLASH);
			String signature=getSignature(accountSid, authToken,timestamp,encryptUtil);
			String url = getStringBuffer().append("/").append(version()).
					append("/Accounts/").append(accountSid)
					.append("/Clients")
					.append("?sig=").append(signature).toString();
			Client client=new Client();
			client.setAppId(appId);
			client.setFriendlyName(clientName);
			client.setClientType(chargeType);
			client.setCharge(charge);
			client.setMobile(mobile);
			Gson gson = new Gson();
			String body = gson.toJson(client);
			body="{\"client\":"+body+"}";
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			//获取响应实体信息
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			// 确保HTTP响应内容全部被读出或者内容流被关闭
			EntityUtils.consume(entity);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	@Override
	public String findClients(String accountSid, String authToken,
			String appId, String start, String limit) {
		// TODO Auto-generated method stub
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			//构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_NO_SLASH);
			String signature=getSignature(accountSid, authToken,timestamp,encryptUtil);
			String url = getStringBuffer().append("/").append(version())
					.append("/Accounts/").append(accountSid)
					.append("/clientList")
					.append("?sig=").append(signature).toString();
			Client client=new Client();
			client.setAppId(appId);
			client.setStart(start);
			client.setLimit(limit);
			Gson gson=new Gson();
			String body = gson.toJson(client);
			body="{\"client\":"+body+"}";
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			//获取响应实体信息
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			// 确保HTTP响应内容全部被读出或者内容流被关闭
			EntityUtils.consume(entity);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	@Override
	public String findClientByNbr(String accountSid, String authToken,String clientNumber,String appId) {
		// TODO Auto-generated method stub
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			//构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_NO_SLASH);
			String signature=getSignature(accountSid, authToken,timestamp,encryptUtil);
			String url = getStringBuffer().append("/").append(version())
					.append("/Accounts/").append(accountSid)
					.append("/Clients")
					.append("?sig=").append(signature)
					.append("&clientNumber=").append(clientNumber)
					.append("&appId=").append(appId)
					.toString();
			HttpResponse response=get("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil);
			//获取响应实体信息
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	@Override
	public String closeClient(String accountSid, String authToken, String clientId,String appId) {
		// TODO Auto-generated method stub
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
			String url = getStringBuffer().append("/").append(version())
					.append("/Accounts/").append(accountSid)
					.append("/dropClient")
					.append("?sig=").append(signature).toString();
			Client client=new Client();
			client.setClientNumber(clientId);
			client.setAppId(appId);
			Gson gson = new Gson();
			String body = gson.toJson(client);
			body="{\"client\":"+body+"}";
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			//获取响应实体信息
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			// 确保HTTP响应内容全部被读出或者内容流被关闭
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	@Override
	public String charegeClient(String accountSid, String authToken,
			String clientNumber, String chargeType, String charge,String appId) {
		// TODO Auto-generated method stub
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
			String url = getStringBuffer().append("/2014-06-30/Accounts/").append(accountSid)
					.append("/chargeClient")
					.append("?sig=").append(signature).toString();
			Client client=new Client();
			client.setClientNumber(clientNumber);
			client.setChargeType(chargeType);
			client.setCharge(charge);
			client.setAppId(appId);
			Gson gson = new Gson();
			String body = gson.toJson(client);
			body="{\"client\":"+body+"}";
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			//获取响应实体信息
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			// 确保HTTP响应内容全部被读出或者内容流被关闭
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	@Override
	public String billList(String accountSid, String authToken,
			String appId,String date) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				String result = "";
				DefaultHttpClient httpclient=getDefaultHttpClient();
				try {
					//MD5加密
					EncryptUtil encryptUtil = new EncryptUtil();
					// 构造请求URL内容
					String timestamp = DateUtil.dateToStr(new Date(),
							DateUtil.DATE_TIME_NO_SLASH);// 时间戳
					String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
					String url = getStringBuffer().append("/").append(version())
							.append("/Accounts/").append(accountSid)
							.append("/billList")
							.append("?sig=").append(signature).toString();
					AppBill appBill=new AppBill();
					appBill.setAppId(appId);
					appBill.setDate(date);
					Gson gson = new Gson();
					String body = gson.toJson(appBill);
					body="{\"appBill\":"+body+"}";
					logger.info(body);
					HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
					//获取响应实体信息
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						result = EntityUtils.toString(entity, "UTF-8");
					}
					// 确保HTTP响应内容全部被读出或者内容流被关闭
					EntityUtils.consume(entity);
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					// 关闭连接
				    httpclient.getConnectionManager().shutdown();
				}
				return result;
	}
	@Override
	public String clientBillList(String accountSid, String authToken,
			String appId,String clientNumber,String date) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				String result = "";
				DefaultHttpClient httpclient=getDefaultHttpClient();
				try {
					//MD5加密
					EncryptUtil encryptUtil = new EncryptUtil();
					// 构造请求URL内容
					String timestamp = DateUtil.dateToStr(new Date(),
							DateUtil.DATE_TIME_NO_SLASH);// 时间戳
					String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
					String url = getStringBuffer().append("/").append(version())
							.append("/Accounts/").append(accountSid)
							.append("/Clients/billList")
							.append("?sig=").append(signature).toString();
					ClientBill clientBill=new ClientBill();
					clientBill.setAppId(appId);
					clientBill.setClientNumber(clientNumber);
					clientBill.setDate(date);
					Gson gson = new Gson();
					String body = gson.toJson(clientBill);
					body="{\"clientBill\":"+body+"}";
					logger.info(body);
					HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
					//获取响应实体信息
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						result = EntityUtils.toString(entity, "UTF-8");
					}
					// 确保HTTP响应内容全部被读出或者内容流被关闭
					EntityUtils.consume(entity);
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					// 关闭连接
				    httpclient.getConnectionManager().shutdown();
				}
				return result;
	}
	@Override
	public String callback(String accountSid, String authToken, String appId,
			String fromClient, String to,String fromSerNum,String toSerNum,String orderId,String ringtoneID) {
		// TODO Auto-generated method stub
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
			String url = getStringBuffer().append("/").append(version())
					.append("/Accounts/").append(accountSid)
					.append("/Calls/callBack")
					.append("?sig=").append(signature).toString();
			Callback callback=new Callback();
			callback.setAppId(appId);
			callback.setFromClient(fromClient);
			callback.setTo(to);
			callback.setFromSerNum(fromSerNum);
			callback.setToSerNum(toSerNum);
			callback.setMaxallowtime(CallEnum.MAX_ALLOW_TIME);
			callback.setOrderId(orderId);
			callback.setRingtoneID(ringtoneID);
			Gson gson = new Gson();
			String body = gson.toJson(callback);
			body="{\"callback\":"+body+"}";
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	@Override
	public String voiceCode(String accountSid, String authToken, String appId,
			String to, String verifyCode) {
		// TODO Auto-generated method stub
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
			String url = getStringBuffer().append("/").append(version())
					.append("/Accounts/").append(accountSid)
					.append("/Calls/voiceCode")
					.append("?sig=").append(signature).toString();
			VoiceCode voiceCode=new VoiceCode();
			voiceCode.setAppId(appId);
			voiceCode.setVerifyCode(verifyCode);
			voiceCode.setTo(to);
			Gson gson = new Gson();
			String body = gson.toJson(voiceCode);
			body="{\"voiceCode\":"+body+"}";
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	@Override
	public String voiceNotify(String accountSid, String authToken,
			String appId, String content, String to, String type,
			String toSerNum, String playTimes) {
		// TODO Auto-generated method stub
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
			String url = getStringBuffer().append("/").append(version())
					.append("/Accounts/").append(accountSid)
					.append("/Calls/voiceNotify")
					.append("?sig=").append(signature).toString();
			VoiceNotify voiceNotify=new VoiceNotify();
			voiceNotify.setAppId(appId);
			voiceNotify.setTo(to);
			voiceNotify.setContent(content);
			voiceNotify.setToSerNum(toSerNum);
			voiceNotify.setType(type);
			voiceNotify.setPlayTimes(playTimes);
			Gson gson = new Gson();
			String body = gson.toJson(voiceNotify);
			body="{\"voiceNotify\":"+body+"}";
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	@Override
	public String createConference(String accountSid, String authToken, String appId, String maxMember,
			Integer duration, Integer playTone) {
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
//			/2015-06-30/Accounts/6f959b38f6f5e291e309d497afe659cb/Conference/create?sig=2781F328C28440D9A48EB5938DA99174 
			String url = getUrl("2015-06-30",accountSid,"/Conference/create",signature);
			Conf conf=new Conf();
			conf.setAppId(appId);
			conf.setDuration(String.valueOf(duration));
			conf.setMaxMember(maxMember);
			conf.setMediaType(String.valueOf(1));
			conf.setPlayTone(String.valueOf(playTone));
			
			Gson gson = new Gson();
			String body = gson.toJson(conf);
			body="{\"conf\":"+body+"}";
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	@Override
	public String removeConference(String accountSid, String authToken, String appId, String confId, String callId) {
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
			String url = getUrl("2015-06-30",accountSid,"/Conference/remove",signature);
			Conf conf=new Conf();
			conf.setAppId(appId);
			conf.setConfId(confId);
			conf.setCallId(callId);
			Gson gson = new Gson();
			String body = gson.toJson(conf);
			body="{\"conf\":"+body+"}";
			System.err.println(body);
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	@Override
	public String dismissConference(String accountSid,String authToken, String appId,String confId) {
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
//			/2015-06-30/Accounts/6f959b38f6f5e291e309d497afe659cb/Conference/dismiss?sig=E247809A899AEEAD7B630AA886B9E192 
			String url = getUrl("2015-06-30",accountSid,"/Conference/dismiss",signature);
			Conf conf=new Conf();
			conf.setAppId(appId);
			conf.setConfId(confId);
			Gson gson = new Gson();
			String body = gson.toJson(conf);
			body="{\"conf\":"+body+"}";
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	@Override
	public String inviteConference(String accountSid,String authToken, String appId,String confId,List<Member> list) {
//		1	appId	String	必选	应用Id
//		2	confId	String	必选	会议标识ID，由8为纯数字组成。
//		3	memberList	Array	必选	会议成员列表，json数组，成员属性分别为：nickName、number、role；nickName：成员昵称（目前只支持英文字母和数字组成的字符串，长度为30字节）；
//		number：成员号码：client、固话、手机号码； role: 1: 普通与会者; 2: 主持人.
//		如：[{"nickName":tone188","number":"18800015233","role":"1",”}]
//		注：会议中可以无主持人角色，也支持1个或多个主持人存在；若当前会议中无主持人，则不能使用成员静音、禁听、会议放音、录音等会控功能；若需要调用这些接口进行会议控制管理时，则必须保证会议中至少存在一个主持人角色的成员；
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
			String url = getUrl("2015-06-30",accountSid,"/Conference/invite",signature);
			Conf conf=new Conf();
			conf.setAppId(appId);
			conf.setConfId(confId);
			
			Gson gson = new Gson();
			String memberString = gson.toJson(list);
			System.err.println(memberString);
			conf.setMemberList(memberString);
			String body = gson.toJson(conf);
			body="{\"conf\":"+body+"}";
			System.err.println(body);
			logger.info(body);
//			body = body.replace("[", "\"[").replace("]", "]\"");
//			"{\"conf\":{\"appId\":\"5e8ccec79dc04140bb7fef21144a4320\",\"confId\":\"66667603\",\"memberList\":\"[{\"nickName\":\"屈军利\",\"number\":\"13751132072\",\"role\":\"2\"},{\"nickName\":\"小患\",\"number\":\"15000000001\",\"role\":\"1\"}]\"}}"
			System.err.println(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	@Override
	public String muteConference(String accountSid, String authToken, String appId, String confId, String callId) {
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
			String url = getUrl("2015-06-30",accountSid,"/Conference/mute",signature);
			Conf conf=new Conf();
			conf.setAppId(appId);
			conf.setConfId(confId);
			conf.setCallId(callId);
			Gson gson = new Gson();
			String body = gson.toJson(conf);
			body="{\"conf\":"+body+"}";
			System.err.println(body);
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	@Override
	public String unMuteConference(String accountSid, String authToken, String appId, String confId, String callId) {
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
			String url = getUrl("2015-06-30",accountSid,"/Conference/unmute",signature);
			Conf conf=new Conf();
			conf.setAppId(appId);
			conf.setConfId(confId);
			conf.setCallId(callId);
			Gson gson = new Gson();
			String body = gson.toJson(conf);
			body="{\"conf\":"+body+"}";
			System.err.println(body);
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	@Override
	public String deafConference(String accountSid, String authToken, String appId, String confId, String callId) {
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
			String url = getUrl("2015-06-30",accountSid,"/Conference/deaf",signature);
			Conf conf=new Conf();
			conf.setAppId(appId);
			conf.setConfId(confId);
			conf.setCallId(callId);
			Gson gson = new Gson();
			String body = gson.toJson(conf);
			body="{\"conf\":"+body+"}";
			System.err.println(body);
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	@Override
	public String unDeafConference(String accountSid, String authToken, String appId, String confId, String callId) {
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
			String url = getUrl("2015-06-30",accountSid,"/Conference/undeaf",signature);
			Conf conf=new Conf();
			conf.setAppId(appId);
			conf.setConfId(confId);
			conf.setCallId(callId);
			Gson gson = new Gson();
			String body = gson.toJson(conf);
			body="{\"conf\":"+body+"}";
			System.err.println(body);
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	@Override
	public String queryConference(String accountSid, String authToken, String appId, String confId) {
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
			String url = getUrl("2015-06-30",accountSid,"/Conference/query",signature);
			Conf conf=new Conf();
			conf.setAppId(appId);
			conf.setConfId(confId);
			Gson gson = new Gson();
			String body = gson.toJson(conf);
			body="{\"conf\":"+body+"}";
			System.err.println(body);
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	@Override
	public String recordConference(String accountSid, String authToken, String appId, String confId) {
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
			String url = getUrl("2015-06-30",accountSid,"/Conference/record",signature);
			Conf conf=new Conf();
			conf.setAppId(appId);
			conf.setConfId(confId);
			Gson gson = new Gson();
			String body = gson.toJson(conf);
			body="{\"conf\":"+body+"}";
			System.err.println(body);
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	@Override
	public String stopRecordConference(String accountSid, String authToken, String appId, String confId) {
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
			String url = getUrl("2015-06-30",accountSid,"/Conference/stopRecord",signature);
			Conf conf=new Conf();
			conf.setAppId(appId);
			conf.setConfId(confId);
			Gson gson = new Gson();
			String body = gson.toJson(conf);
			body="{\"conf\":"+body+"}";
			System.err.println(body);
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	@Override
	public String templateSMS(String accountSid, String authToken,
			String appId, String templateId, String to, String param) {
		// TODO Auto-generated method stub
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);// 时间戳
			String signature =getSignature(accountSid,authToken,timestamp,encryptUtil);
			String url = getStringBuffer().append("/").append(version())
					.append("/Accounts/").append(accountSid)
					.append("/Messages/templateSMS")
					.append("?sig=").append(signature).toString();
			TemplateSMS templateSMS=new TemplateSMS();
			templateSMS.setAppId(appId);
			templateSMS.setTemplateId(templateId);
			templateSMS.setTo(to);
			templateSMS.setParam(param);
			Gson gson = new Gson();
			String body = gson.toJson(templateSMS);
			body="{\"templateSMS\":"+body+"}";
			logger.info(body);
			HttpResponse response=post("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil, body);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	@Override
	public String findClientByMobile(String accountSid, String authToken,
			String mobile, String appId) {
		// TODO Auto-generated method stub
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			//构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_NO_SLASH);
			String signature=getSignature(accountSid, authToken,timestamp,encryptUtil);
			String url = getStringBuffer().append("/").append(version())
					.append("/Accounts/").append(accountSid)
					.append("/ClientsByMobile")
					.append("?sig=").append(signature)
					.append("&mobile=").append(mobile)
					.append("&appId=").append(appId)
					.toString();
			HttpResponse response=get("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil);
			//获取响应实体信息
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	@Override
	public String dispalyNumber(String accountSid, String authToken,
			String appId, String clientNumber, String display) {
		// TODO Auto-generated method stub
		String result = "";
		DefaultHttpClient httpclient=getDefaultHttpClient();
		try {
			//MD5加密
			EncryptUtil encryptUtil = new EncryptUtil();
			//构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_NO_SLASH);
			String signature=getSignature(accountSid, authToken,timestamp,encryptUtil);
			String url = getStringBuffer().append("/").append(version())
					.append("/Accounts/").append(accountSid)
					.append("/dispalyNumber")
					.append("?sig=").append(signature)
					.append("&appId=").append(appId)
					.append("&clientNumber=").append(clientNumber)
					.append("&display=").append(display)
					.toString();
			HttpResponse response=get("application/json",accountSid, authToken, timestamp, url, httpclient, encryptUtil);
			//获取响应实体信息
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			EntityUtils.consume(entity);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			// 关闭连接
		    httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	private String getUrl(String version,String accountSid,String function,String signature){
		
		
//		http://api1.ucpaas.com/2015-06-30/Accounts/ab529121937c1da5b0e14249b1d97fee/Conference/create?sig=46C282C7BF163B003BA3524AB0AC2861
//		StringBuffer sf = new StringBuffer();
//		String url = sf.append("https://api1.ucpaas.com/").append(version)
//				.append("/Accounts/").append(accountSid)
//				.append(function)
//				.append("?sig=").append(signature).toString();
		
		String	url = getStringBuffer().append("/").append(version)
				.append("/Accounts/").append(accountSid)
				.append(function)
				.append("?sig=").append(signature).toString();
		return url;
	}
}
