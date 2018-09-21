package com.dachen.health.group.common.util;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.dachen.util.RedisUtil;

import redis.clients.jedis.JedisPubSub;

@Service
public class SubListener extends JedisPubSub implements ApplicationListener<ContextRefreshedEvent> {
	// 取得订阅的消息后的处理
	public void onMessage(String channel, String message) {
		System.out.println(channel + "=" + message);
	}

	// 初始化订阅时候的处理
	public void onSubscribe(String channel, int subscribedChannels) {
		// System.out.println(channel + "=" + subscribedChannels);
	}

	// 取消订阅时候的处理
	public void onUnsubscribe(String channel, int subscribedChannels) {
		// System.out.println(channel + "=" + subscribedChannels);
	}

	// 初始化按表达式的方式订阅时候的处理
	public void onPSubscribe(String pattern, int subscribedChannels) {
		// System.out.println(pattern + "=" + subscribedChannels);
	}

	// 取消按表达式的方式订阅时候的处理
	public void onPUnsubscribe(String pattern, int subscribedChannels) {
		// System.out.println(pattern + "=" + subscribedChannels);
	}

	// 取得按表达式的方式订阅的消息后的处理
	public void onPMessage(String pattern, String channel, String message) {
		System.out.println(pattern + "=" + channel + "=" + message);
	}

 
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		 if(event.getApplicationContext().getParent() == null)
		 {
			 final SubListener listener = new SubListener();
				new Thread(new Runnable() {
					public void run() {
						 RedisUtil.psubscribe("PROFILE_CHANGE",listener);
					}
				}).start();
		 }
		
		
	}
}
