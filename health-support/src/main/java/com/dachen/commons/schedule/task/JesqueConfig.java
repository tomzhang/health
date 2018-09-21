package com.dachen.commons.schedule.task;

import java.util.HashSet;
import java.util.Set;

import net.greghaines.jesque.Config;
import net.greghaines.jesque.ConfigBuilder;
import net.greghaines.jesque.utils.ResqueConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.jedis.JedisConfig;
import com.dachen.util.PropertiesUtil;

public class JesqueConfig {
	private static String NAMESPACE =null;
	private static final String REDIS_DBINDEX = "jesque.database";
	
	private static Pool<Jedis> JEDIS_POOL;
	private static Config REDIS_CONFIG;
	
	static Config getRedisConfig()
	{
		if(NAMESPACE==null){
			return null;
		}
		if(REDIS_CONFIG==null){
			final ConfigBuilder configBuilder = new ConfigBuilder();
			configBuilder.withDatabase(JesqueConfig.redisDbIndex());
			configBuilder.withPassword(JedisConfig.getPassword());
			configBuilder.withNamespace(NAMESPACE);
			REDIS_CONFIG  = configBuilder.build();
		}
		return REDIS_CONFIG;
	}
	
	static Pool<Jedis> getJedisPool()
	{
		if(JEDIS_POOL==null){
			synchronized (JesqueConfig.class) {
				if(JEDIS_POOL==null){
					JEDIS_POOL = getSentinelPool();
				}
			}
		}
        return JEDIS_POOL; 
	}
	
	private static int redisDbIndex()
	{
		String value = PropertiesUtil.getContextProperty(REDIS_DBINDEX);
    	if(StringUtils.isEmpty(value))
    	{
    		return 2;
    	}
    	return Integer.parseInt(value);
	}
	
	/**
	 * @return
	 */
	static Set<String>getAllRunQueue()
	{
		if(NAMESPACE==null){
			return null;
		}
		 Set<String>queueSet = new HashSet<String>();
		 Jedis xedis =  null;
		 try
		 {
			 xedis = getJedisPool().getResource(); 
			 String prefx = NAMESPACE+":"+ResqueConstants.QUEUE+":";
//				 
			 String suffix = ":"+ResqueConstants.FREQUENCY;
			 Set<String> scanResult = xedis.keys(prefx+"*");
			 String queue = null;
			 for (String key : scanResult) 
			 {
				 if(key.endsWith(suffix)){
					 continue;
				 }
				 queue = key.substring(prefx.length());
				 if(StringUtils.isNotEmpty(queue))
				 {
					 queueSet.add(queue);
				 }
			 }
		 } catch (Exception e) {
			e.printStackTrace();
		 } finally {
    		 getJedisPool().returnResource(xedis);
		 }
//		 if(queueSet==null || queueSet.size()==0){
//			 queueSet = DefaultQueueEnum.getQueueList();
//		 }
		 return queueSet;
	}
	
	 private static JedisSentinelPool getSentinelPool()
     {
        JedisSentinelPool sentinelPool = null;
         try{    
        	 
        	 String masterName = JedisConfig.getMasterName();
        	 Set<String> sentinels = JedisConfig.getSentinels();
        	 String password = JedisConfig.getPassword();
        	 int timeout = JedisConfig.getTimeOut();
        	 GenericObjectPoolConfig config = getDefaultPoolConfig();
        	 int database = redisDbIndex();
         	 sentinelPool = new JedisSentinelPool(masterName, sentinels,
         			config,timeout,password,database);
         } catch(Exception e) {  
             e.printStackTrace();  
             throw new ServiceException("redis配置错误!");
         }  
         
    	return sentinelPool;
    }
	 
	 private static GenericObjectPoolConfig getDefaultPoolConfig()
     {
       	 JedisPoolConfig config = new JedisPoolConfig();  
         config.setMaxTotal(100); 
         config.setLifo(true);
         config.setMinIdle(10);
         config.setMaxIdle(50);
         config.setMaxWaitMillis(10000);
         config.setTestOnBorrow(true);  
         config.setTestOnReturn(true); 
         
         //Idle时进行连接扫描
         config.setTestWhileIdle(true);
         //表示idle object evitor两次扫描之间要sleep的毫秒数
         config.setTimeBetweenEvictionRunsMillis(60*1000);
         //表示idle object evitor每次扫描的最多的对象数
         config.setNumTestsPerEvictionRun(10);
         //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
         config.setMinEvictableIdleTimeMillis(10*60*1000);
         return config;
	  }
	 
	 static void setNamespace(String namespace){
		 if(NAMESPACE==null){
			 NAMESPACE="jesque_job:"+namespace;
		 }
	 }
}
