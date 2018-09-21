package com.dachen.commons.support.jedis;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;

import com.dachen.commons.exception.ServiceException;

public class JedisUtil  {  
    protected Logger log = LoggerFactory.getLogger(getClass());  
    private static ConcurrentHashMap<String,Pool<Jedis>> maps  
    					= new ConcurrentHashMap<String,Pool<Jedis>>();  
    /** 
     * 私有构造器. 
     */  
    private JedisUtil() {  
    	
    }
    
    public static JedisUtil getInstance() {  
        return RedisUtilHolder.instance;  
    } 
    
    /** 
     *类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例 
     *没有绑定关系，而且只有被调用到时才会装载，从而实现了延迟加载。 
     */  
    private static class RedisUtilHolder
    {  
        /** 
         * 静态初始化器，由JVM来保证线程安全 
         */  
        private static JedisUtil instance = new JedisUtil();  
    }  
  
    private JedisSentinelPool getSentinelPool(int dbIndex)
    {
    	String key = "dc_sentinel_pool_"+dbIndex; 
        if(maps.containsKey(key)) {  
        	return (JedisSentinelPool)maps.get(key);
        }
         try{    
        	 synchronized(this){
        		 if(maps.containsKey(key)){
        			 return (JedisSentinelPool)maps.get(key);
        		 }
        		 String masterName = JedisConfig.getMasterName();
    			 Set<String> sentinels = JedisConfig.getSentinels();
    			 GenericObjectPoolConfig config = JedisConfig.getDefaultPoolConfig();
    			 String password = JedisConfig.getPassword();
    			 int timeout = JedisConfig.getTimeOut();
    			 JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinels,
    					 config,timeout,password,dbIndex);
    			 maps.putIfAbsent(key,sentinelPool);
    			 return sentinelPool;
        	 }
         } catch(Exception e) {  
             e.printStackTrace();  
             throw new ServiceException("redis配置错误!");
         }  
    }
      
    public Pool<Jedis> getPool(int dbIndex)
    {
    	Pool<Jedis> pool = getSentinelPool(dbIndex);
    	return pool;
    }
    /** 
     * 获取Redis实例. 
     * @return Redis工具类实例 
     */  
    public Jedis getJedis(int dbIndex) {  
        Jedis jedis  = null;  
        int count =0;  
        do{  
            try{ 
                jedis = getPool(dbIndex).getResource();  
            } catch (Exception e) {  
            	log.error("redis connect fail...");
            	e.printStackTrace();
                 // 销毁对象    
            	if(jedis!=null)
            	{
            		jedis.close();    
            	}
            }  
            count++;  
        }while(jedis==null&&count<3);  
        return jedis;  
    }  
  
    /** 
     * 释放redis实例到连接池. 
     * @param jedis redis实例 
     */  
    public void closeJedis(Jedis jedis) {  
        if(jedis != null) {  
        	jedis.close();  
        }  
    } 
    
    public void destoryPool(int dbIndex) {  
    	getPool(dbIndex).destroy();
    } 

} 
