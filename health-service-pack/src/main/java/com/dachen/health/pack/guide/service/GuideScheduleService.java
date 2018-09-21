package com.dachen.health.pack.guide.service;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.im.server.data.request.PushMessageRequest;

@Component
public class GuideScheduleService {
	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;
	
     /**
      * 我的日程到时提醒（提前10分钟提醒）
      * 每10分钟执行一次
      */
//	 @Scheduled(cron = "0 0/1 * * * ?")
	 public void scheduleRemind() {
		 /**
		  *  凌晨0点到早上7点中不提醒
		  */
		 int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		 if (hour < 7){
			 return;
		 }
		 
		long currentTime = System.currentTimeMillis();
		System.out.println(new java.util.Date(currentTime));
		long startTime=  currentTime + getIntervalTime();
		long endTime=  startTime + 60*1000L;

		/**
		 * 条件1、预约开始时间  between {当前时间   and 当前时间+10分钟}
		 * 条件2、服务状态：服务中(state=2)
		 * 条件3、支付状态：已支付(payState=2)
		 */
		removeExpireCache();
//		String key = GuideUtils.genKey(GuideDAOImpl.Z_GUIDE_SCHEDULE);
//		String key = GuideDAOImpl.Z_GUIDE_SCHEDULE;
		
		/**
		 *  endTime <= 预约开始时间 <= endTime
		 */
//		Set<String> ids = jedisTemplate.zrangeByScore(key, endTime, endTime);
		
		/**
		 *  startTime < 预约开始时间 <= endTime
		 *  假如当前时间为9点整，则提醒预约时间为[9:10---9:11)
		 *  假如当前时间为9:01整，则提醒预约时间为[9:11---9:12)
		 */
//		Set<String> ids = jedisTemplate.zrangeByScore(key, ""+startTime, "("+endTime);
		
		Set<String>guideIdSet =new HashSet<String>();
		/*for(String id:ids)
		{
			String[]arrayStr = id.split(":");
			String guideId = arrayStr[0];
//			String orderId = arrayStr[1];
			guideIdSet.add(guideId);
			System.out.println(guideId);
			
		}*/
		this.push(guideIdSet);
	 }
	 
	 
	 private void push(Set<String>guideIdSet)
	 {
		 if(guideIdSet==null || guideIdSet.isEmpty())
		 {
			 return;
		 }
		 PushMessageRequest msg = new PushMessageRequest();
		 msg.setContent("温情提示：您的日程安排中有需要处理的订单将在10分钟后开始。");
		 msg.setPushUsers(guideIdSet);
		 msg.setBizType(0);
		 MsgHelper.push(msg);
	 }
	 
	 private void removeExpireCache() 
	 {
//		long currentTime = System.currentTimeMillis();
//		jedisTemplate.zremrangeByScore(GuideDAOImpl.Z_GUIDE_SCHEDULE,"-inf", "("+currentTime);
	 }
	 /**
	  * 间隔时间(ms) 默认10分钟
	  * @return
	  */
	 private long getIntervalTime()
	 {
		 return 10* 60 *1000L;
	 }
}
