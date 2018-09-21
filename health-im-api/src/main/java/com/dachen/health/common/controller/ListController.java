package com.dachen.health.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.JSONMessage;
import com.dachen.health.base.dao.IdxRepository;
import com.dachen.health.commons.dao.MsgListRepository;

@Deprecated
@RestController
public class ListController {

	@Autowired
	protected IdxRepository idxRepository;
	
	@RequestMapping(value = "/list/genId")
	public JSONMessage genMongoId(Integer count,Integer second){
		JSONObject json = new JSONObject();
		if(count==null){
			int idSize = 0;
			long startTime = System.currentTimeMillis();
			if(second==null){
				second = 1;
			}
		    long endTime =startTime +(second*1000);
		    while(System.currentTimeMillis() <= endTime) {
		      idxRepository.nextPayNoIdx(20);
		      idSize++;
		    }
		    json.put("count", idSize);
		    json.put("time", 1000);
		}else{
			long startTime = System.currentTimeMillis();
			for(int i=0;i<count;i++){
				idxRepository.nextPayNoIdx(20);
			}
			long endTime =System.currentTimeMillis();
			json.put("count", count);
		    json.put("time", endTime-startTime);
		}
		
		return JSONMessage.success(null,json);
	}

}
