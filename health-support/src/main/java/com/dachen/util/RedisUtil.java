package com.dachen.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPubSub;

import com.dachen.commons.constants.Constants;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.commons.support.spring.SpringBeansUtils;

/**
 * 内存数据库Redis的辅助类，负责对内存数据库的所有操作
 * @version V1.0
 * @author fengjc
 */
public class RedisUtil {

    private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);
//    private static JedisPool jedisPool = SpringBeansUtils.getBean("jedisPool");
    private static JedisTemplate jedisTemplate = SpringBeansUtils.getBean("jedisTemplate");
    
    public static final String WAITING_QUEUE="wait";
    public static final String ADVISORY_QUEUE="advisory";


    /**
     * 删除keys对应的记录,可以是多个key
     * 
     * @param String
     *            ... keys
     * @return 删除的记录数
     * */
    public static void del(String... keys) {
//        Jedis jedis = jedisPool.getResource();
//        long count = 0;
        try {
             jedisTemplate.del(keys);
        } catch (Exception e) {
            logger.error("redis del失败，key:" + keys);
        } finally {
//            jedisPool.returnResource(jedis);
        }
//        return count;
    }
    
    public static String get(final String key) {
//        Jedis resource = jedisPool.getResource();
        String str = null;
        try {
            str = jedisTemplate.get(key);
        } catch (Exception e) {
            logger.error("redis get失败，key:" + key);
            e.printStackTrace();
        } finally {
//            jedisPool.returnResource(resource);
        }

        return str;
    }

    /**
     * 从hash中删除指定的存储
     * 
     * @param String
     *            key
     * @param String
     *            fieid 存储的名字
     * @return 状态码，1成功，0失败
     * */
    public void hdel(String key, final String... fields) {
//        Jedis jedis = jedisPool.getResource();
//        long count = 0;
        try {
            jedisTemplate.hdel(key, fields);
        } catch (Exception e) {
            logger.error("redis hdel失败，key:" + key + ",fields:" + fields);
        } finally {
//            jedisPool.returnResource(jedis);
        }
//        return count;
    }
    
    /**
     * 添加记录,如果记录已存在将覆盖原有的value
     * 
     * @param String
     *            key
     * @param String
     *            value
     * @return 状态码
     * */
    public static String set(String key, String value) {
        return set(key, value, Constants.Expire.HOUR1);
    }

    /**
     * 添加记录,如果记录已存在将覆盖原有的value
     * 
     * @param String
     *            key
     * @param String
     *            value
     * @return 状态码
     * */
    public static String set(String key, String value, int expire) {
//        Jedis jedis = jedisPool.getResource();
        String str = "";
        try {
        	jedisTemplate.set(key, value);
        	jedisTemplate.expire(key, expire);
        } catch (Exception e) {
            logger.error("redis set失败，key:" + key);
        } finally {
//            jedisPool.returnResource(jedis);
        }
        return str;
    }
    
    /**
     * 添加记录,如果记录不存在则添加，如果存在则不处理
     * 
     * @param String
     *            key
     * @param String
     *            value
     * @return 状态码
     * */
    public static Boolean setnx(String key, String value, int expire) {
//        Jedis jedis = jedisPool.getResource();
    	boolean i=false;
        try {
            i = jedisTemplate.setnx(key, value);
            if(expire>0){
            	jedisTemplate.expire(key, expire);
            }
        } catch (Exception e) {
            logger.error("redis setnx失败，key:" + key);
        } finally {
//            jedisPool.returnResource(jedis);
        }
        return i;
    }
    
    /**
     * 添加对应关系，如果对应关系已存在，则覆盖
     * 
     * @param Strin
     *            key
     * @param Map
     *            <String,String> 对应关系
     * @return 状态，成功返回OK
     * */
    public static String hmset(String key, Map<String, String> map) {
        return hmset(key, map, Constants.Expire.HOUR1);
    }

    /**
     * 添加对应关系，如果对应关系已存在，则覆盖
     * 
     * @param Strin
     *            key
     * @param Map
     *            <String,String> 对应关系
     * @return 状态，成功返回OK
     * */
    public static String hmset(String key, Map<String, String> map, int expire) {
//        Jedis jedis = jedisPool.getResource();
        String s = "";
        try {
        	jedisTemplate.hmset(key, map);
        	jedisTemplate.expire(key, expire);
        } catch (Exception e) {
            logger.error("redis hmset失败，key:" + key);
        } finally {
//            jedisPool.returnResource(jedis);
        }
        return s;
    }

    /**
     * 返回hash中指定存储位置的值
     * 
     * @param     key
     * @param     fieid
     ** 存储的名字
     * @return 存储对应的值
     * */
    public static String hget(String key, String field) {
//        Jedis jedis = jedisPool.getResource();
        String s = "";
        try {
            s = jedisTemplate.hget(key, field);
        } catch (Exception e) {
            logger.error("redis hget失败，key:" + key + ",field:" + field);
        } finally {
//            jedisPool.returnResource(jedis);
        }
        return s;
    }

    /**
     * 根据多个key，获取对应的value，返回List,如果指定的key不存在,List对应位置为null
     * 
     * @param String
     *            key
     * @param String
     *            ... fieids 存储位置
     * @return List<String>
     * */
    public static List<String> hmget(String key, String... fieids) {
//        Jedis jedis = jedisPool.getResource();
        List<String> list = null;
        try {
            list = jedisTemplate.hmget(key, fieids);
        } catch (Exception e) {
            logger.error("redis hmget失败，key:" + key);
        } finally {
//            jedisPool.returnResource(jedis);
        }
        return list;
    }

    /**
     * 以Map的形式返回hash中的存储和值
     * 
     * @param String
     *            key
     * @return Map<Strinig,String>
     * */
    public static Map<String, String> hgetAll(String key) {
//        Jedis jedis = jedisPool.getResource();
        Map<String, String> map = null;
        try {
            map = jedisTemplate.hgetAll(key);
        } catch (Exception e) {
            logger.error("redis hgetAll失败，key:" + key);
        } finally {
//            jedisPool.returnResource(jedis);
        }
        return map;
    }
    
    /**
     * 发布消息
     * */
    /*public static long publish(String channel,String message) {
        Jedis jedis = jedisPool.getResource();
        long count = 0;
        try {
        	jedis.publish(channel, message);
        } catch (Exception e) {
            logger.error("publish 失败，channel:" + channel+",message:"+message);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return count;
    }*/
    
    /**
     * 注册处理监听器
     * @param channel
     * @param message
     * @return
     */
    public static long psubscribe(String channel, JedisPubSub listener) {
        return 0;
    }
    
    /*public static Jedis getJedis(){
        return jedisPool.getResource();
    }
    
    public static void returnJedis(Jedis jedis){
        jedisPool.returnResource(jedis);
    }*/
    public static String generateKey(String type,String id){
    	if(type==null||type.trim().equals("")||id==null || id.trim().equals("")){
    		logger.error("type or id mybe is null" );
    		return null;
    	}
    	String key = null;;
    	try {
			key = type+"_"+id;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
    	return key;
    }
    
    /**在指定Key所关联的List Value的尾部插入参数中给出的所有Values*/
    public static  void rpush(final String key, final String... string){
    	jedisTemplate.rpush(key, string);
    }
    /**
     * #在指定Key所关联的List Value的头部插入参数中给出的所有Values*/
    public static void lpush(final String key, final String... string){
    	jedisTemplate.lpush(key, string);
    }
    
    /**
     * 返回并弹出指定Key关联的链表中的第一个元素，即头部元素。如果该Key不存，返回null
     */
    public static String  lpop(final String key){
    	return jedisTemplate.lpop(key);
    }
    
    /**
     * 返回并弹出指定Key关联的链表中的最后一个元素，即尾部元素。如果该Key不存，返回null
     */
    public static String  rpop(final String key){
    	
    	return jedisTemplate.rpop(key);
    }
    /**
     * 返回指定Key关联的链表中元素的数量，如果该Key不存在，则返回0。如果与该Key关联的Value的类型不是链表，则返回相关的错误信息。
     */
    public static long llen(final String key){
    	return jedisTemplate.llen(key);
    	
    }
    
    /**
     * 返回匹配到键
     */
    public static Set<String> getKeysByPattern(String pattern){
    	return jedisTemplate.keysPattern(pattern);
    }
    
    public static List<String> lrange(final String key,long start ,long end){
    	return jedisTemplate.lrange(key, start, end);
    }
    
    public static String lindex(final String key,int index){
    	return jedisTemplate.lindex(key, index);
    }
    public static boolean removeVal(String key,String val){
    	return jedisTemplate.lremOne(key, val);
    }
}

