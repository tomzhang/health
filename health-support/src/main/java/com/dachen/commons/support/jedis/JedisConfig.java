package com.dachen.commons.support.jedis;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.util.StringUtils;

import com.dachen.util.PropertiesUtil;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class JedisConfig {
//	public static final String DEFAULT_HOST = "localhost";
//	public static final int DEFAULT_PORT = Protocol.DEFAULT_PORT;
	public static final int DEFAULT_TIMEOUT = Protocol.DEFAULT_TIMEOUT;
	public static final int DEFAULT_DATABASE = Protocol.DEFAULT_DATABASE;
	
	private static final String REDIS_PWD = "redis.password";
	private static final String REDIS_DBINDEX = "redis.database";
	private static final String REDIS_TIMEOUT = "redis.timeout";//redis.master=mymaster
	private static final String REDIS_SENTINELS = "redis.sentinels";
	private static final String REDIS_MASTER = "redis.master";
	
	public static String getMasterName()
	{
    	 String masterName = PropertiesUtil.getContextProperty(REDIS_MASTER);
    	 if(masterName==null || masterName.trim().length()==0)
    	 {
    		 masterName ="mymaster";
    	 }
		return masterName;
	}
	
	public static Set<String> getSentinels()
	{
		
		String v = PropertiesUtil.getContextProperty(REDIS_SENTINELS);
		if(v==null)
		{
			return null;
		}
		Set<String> sentinels = new HashSet<String>();
		String[]arrs = v.split(",");
		for(String one:arrs)
		{
			sentinels.add(one);
		}
		return sentinels;
	}
	public static int getDbIndex()
    {
    	String value = PropertiesUtil.getContextProperty(REDIS_DBINDEX);
    	if(StringUtils.isEmpty(value))
    	{
    		return DEFAULT_DATABASE;
    	}
    	return Integer.parseInt(value);
    }
    
	public static int getTimeOut()
    {
    	String value = PropertiesUtil.getContextProperty(REDIS_TIMEOUT);
    	if(StringUtils.isEmpty(value))
    	{
    		return DEFAULT_TIMEOUT;
    	}
    	return Integer.parseInt(value);
    }
    
    public static String getPassword()
    {
    	String pwd = PropertiesUtil.getContextProperty(REDIS_PWD);
    	if(StringUtils.isEmpty(pwd))
    	{
    		return null;
    	}
    	return pwd;
    }
   /* *//**
   	 * 快速设置JedisPoolConfig, 不执行idle checking。
   	 *//*
    private static JedisPoolConfig createPoolConfig(int maxIdle, int maxTotal) {
   		JedisPoolConfig poolConfig = new JedisPoolConfig();
   		poolConfig.setMaxIdle(maxIdle);
   		poolConfig.setMaxTotal(maxTotal);
   		poolConfig.setTimeBetweenEvictionRunsMillis(-1);
   		return poolConfig;
   	}

   	*/
    /**
   	 * 快速设置JedisPoolConfig, 设置执行idle checking的间隔和可被清除的idle时间.
   	 * 默认的checkingIntervalSecs是30秒，可被清除时间是60秒。
   	 */
    /*
    private static JedisPoolConfig createPoolConfig(int maxIdle, int maxTotal,
   			int checkingIntervalSecs, int evictableIdleTimeSecs) {
   		JedisPoolConfig poolConfig = createPoolConfig(maxIdle, maxTotal);
   		poolConfig.setTimeBetweenEvictionRunsMillis(checkingIntervalSecs * 1000);
   		poolConfig.setMinEvictableIdleTimeMillis(evictableIdleTimeSecs * 1000);
   		return poolConfig;
   	}
    */
    public static GenericObjectPoolConfig getDefaultPoolConfig()
    {
       	 JedisPoolConfig config = new JedisPoolConfig();  
         config.setMaxTotal(300); 
         config.setLifo(true);
         config.setMinIdle(30);
         config.setMaxIdle(100);
         config.setMaxWaitMillis(10000);
         config.setTestOnBorrow(true);  
         config.setTestOnReturn(true); 
         
         //Idle时进行连接扫描
         config.setTestWhileIdle(true);
         //表示idle object evitor两次扫描之间要sleep的毫秒数
         config.setTimeBetweenEvictionRunsMillis(30000);
         //表示idle object evitor每次扫描的最多的对象数
         config.setNumTestsPerEvictionRun(10);
         //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
         config.setMinEvictableIdleTimeMillis(60000);
         return config;
    }
}
