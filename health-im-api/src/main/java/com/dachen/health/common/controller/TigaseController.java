package com.dachen.health.common.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.JSONMessage;
import com.dachen.util.ReqUtil;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@RestController
@RequestMapping("/tigase")
public class TigaseController extends AbstractController {


	/**
	 * 机器人群发消息--帐号默认为10005，可配置
	 * @param text
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/push")
	public JSONMessage push(@RequestParam String text, @RequestParam String body) {
		System.out.println("-----");
		List<Integer> userIdList = JSON.parseArray(text, Integer.class);
		return JSONMessage.failure("推送失败");
		// {userId:%1$s,toUserIdList:%2$s,body:'%3$s'}
	}
	
	/**
	 * 消息群发
	 * @param fromUserId
	 * @param toUserIds
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/massMsg")
	public JSONMessage massMsg(@RequestParam String fromUserId,@RequestParam String toUserIds, @RequestParam String body) {
		List<Integer> userIdList = JSON.parseArray(toUserIds, Integer.class);
		return JSONMessage.failure("消息发送失败");
		// {userId:%1$s,toUserIdList:%2$s,body:'%3$s'}
	}
	
	@RequestMapping("/dachen_msgs")
	public JSONMessage getMsgs(@RequestParam int receiver,
			@RequestParam(defaultValue = "0") long startTime,
			@RequestParam(defaultValue = "0") long endTime,
			@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "10") int pageSize) {
		int sender = ReqUtil.instance.getUserId();
		BasicDBObject q = new BasicDBObject();
		q.put("sender", sender);
		q.put("receiver", receiver);
		if (0 != startTime)
			q.put("ts", new BasicDBObject("$gte", startTime));
		if (0 != endTime)
			q.put("ts", new BasicDBObject("$lte", endTime));

		java.util.List<DBObject> list = Lists.newArrayList();


		return JSONMessage.success("", list);
	}

	@RequestMapping("/dachen_muc_msgs")
	public JSONMessage getMucMsgs(@RequestParam String roomId,
			@RequestParam(defaultValue = "0") long startTime,
			@RequestParam(defaultValue = "0") long endTime,
			@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "10") int pageSize) {

		BasicDBObject q = new BasicDBObject();
		q.put("room_jid_id", roomId);
		if (0 != startTime)
			q.put("ts", new BasicDBObject("$gte", startTime));
		if (0 != endTime)
			q.put("ts", new BasicDBObject("$lte", endTime));

		java.util.List<DBObject> list = Lists.newArrayList();


		return JSONMessage.success("", list);
	}

}
