package com.dachen.health.common.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.dachen.commons.JSONMessage;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.im.server.data.EventVO;

@RestController
@RequestMapping("/messageSign")
public class MessageSignController extends AbstractController{
	@Autowired
	private IMsgService msgService;
	@RequestMapping("find")
	public JSONMessage find(String userId,String type,String map){
		EventVO vo=new EventVO();
		Map<String,Object> param=new HashMap<String, Object>();
		if(StringUtils.isNotEmpty(map)){
			param=(Map)(JSONArray.parse(map));
		}
		param.put("from", userId);
		param.put("to", userId);
		vo.setEventType(type);
		vo.setUserId(userId);
		vo.setExpires(120L);
		vo.setParam(param);
		msgService.sendEvent(vo);
		return JSONMessage.success();
	}
	
}
