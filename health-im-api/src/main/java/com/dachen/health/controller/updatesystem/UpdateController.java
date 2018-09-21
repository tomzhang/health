package com.dachen.health.controller.updatesystem;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.support.jedis.JedisCallbackVoid;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.health.base.constant.JobQueueName;
import com.dachen.health.base.utils.HealthJesqueSwitch;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

@RestController
@RequestMapping("upatedate")
public class UpdateController {

	private static Logger logger = LoggerFactory.getLogger(UpdateController.class);
	
	@Autowired
	private JedisTemplate jedisTemplate;
	
	@Autowired
	HealthJesqueSwitch healthJesqueSwitch;
	
	@RequestMapping(value="/jobname/transfer")
	public JSONMessage jobnameTransfer(){
		String jesqueSpace = healthJesqueSwitch.getJesqueSpace();
		logger.info("update jesque job namespace is "+jesqueSpace);
		if(StringUtils.isNoneBlank(jesqueSpace)){
			dataTransfer(jesqueSpace);
		}
		return JSONMessage.success();
	}
	
	private boolean isHealthQueue(String key){
		for(JobQueueName jq : JobQueueName.values()){
			if(key.contains(jq.name()))
				return true;
		}
		return false;
	}
	
	public void dataTransfer(String jesqueSpace){
		jedisTemplate.execute(new JedisCallbackVoid(){
			@Override
			public void execute(Jedis jedis) {
				jedis.select(2);
				Pipeline pipeline = jedis.pipelined();
				try{
					String prefx = "jesque_job:";
					String newPrefx=prefx+jesqueSpace+":";
					Set<String> scanResult = jedis.keys(prefx+"*");
					String newKey=null;
					for (String key:scanResult) 
					{
						if(key.startsWith(newPrefx) || !isHealthQueue(key)){
							continue;
						}
						newKey = key.replaceFirst(prefx, newPrefx);
						pipeline.rename(key, newKey);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally
				{
					pipeline.sync();
				}
			}
		});
	}
	
}
