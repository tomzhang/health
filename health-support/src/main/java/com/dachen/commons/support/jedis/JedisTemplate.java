package com.dachen.commons.support.jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Pool;

@Component
public class JedisTemplate {

	private static Logger logger = LoggerFactory.getLogger(JedisTemplate.class);

	private void close(Jedis jedis) {
		try {
//			Pool<Jedis> poll = JedisUtil.getInstance().getPool(JedisConfig.getDbIndex());
//			poll.returnResource(jedis);
//			JedisUtil.getInstance().closeJedis(jedis);
			jedis.close();
		} catch (Exception e) {
		}
	}

	private Jedis getJedis() {
		Jedis jedis = JedisUtil.getInstance().getJedis(JedisConfig.getDbIndex());
		if (jedis == null) {
			throw new JedisConnectionException("redis disconnect!");
		}
		return jedis;
	}

	@PreDestroy
	public void destory(){
		JedisUtil.getInstance().destoryPool(JedisConfig.getDbIndex());
	}
	
	public <T> T execute(JedisCallback<T> callback) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return callback.execute(jedis);
		} catch (Exception e) {
			throw e;
		} finally {
			close(jedis);
		}
	}

	public void execute(JedisCallbackVoid callback) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			callback.execute(jedis);
		} catch (Exception e) {
			throw e;
		} finally {
			close(jedis);
		}
	}

	public Long expire(final String key, final int seconds) {
		Jedis jedis = getJedis();
		try {
			return jedis.expire(key, seconds);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public Long ttl(final String key) {
		Jedis jedis = getJedis();
		try {
			return jedis.ttl(key);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public Long decr(final String key) {
		Jedis jedis = getJedis();
		try {
			return jedis.decr(key);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public void del(final String... keys) {
		Jedis jedis = getJedis();
		try {
			jedis.del(keys);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public Boolean keyExists(final String key) {

		Jedis jedis = getJedis();
		try {
			return jedis.exists(key);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public void flushDB() {
		Jedis jedis = getJedis();
		try {
			jedis.flushDB();
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}

	}

	public String get(final String key) {

		Jedis jedis = getJedis();
		try {
			return jedis.get(key);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public Integer getAsInt(final String key) {
		String result = get(key);
		return result != null ? Integer.valueOf(result) : null;
	}

	public Long getAsLong(final String key) {
		String result = get(key);
		return result != null ? Long.valueOf(result) : null;
	}

	public void hdel(final String key, final String... fieldsName) {

		Jedis jedis = getJedis();
		try {
			jedis.hdel(key, fieldsName);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public String hget(final String key, final String field) {
		Jedis jedis = getJedis();
		try {
			return jedis.hget(key, field);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}

	}

	public Set<String> hkeys(final String key) {

		Jedis jedis = getJedis();
		try {
			return jedis.hkeys(key);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public List<String> hmget(final String key, final String[] fields) {
		Jedis jedis = getJedis();
		try {
			return jedis.hmget(key, fields);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}

	}

	public void hmset(final String key, final Map<String, String> map) {
		Jedis jedis = getJedis();
		try {
			jedis.hmset(key, map);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public void hset(final String key, final String field, final String value) {

		Jedis jedis = getJedis();
		try {
			jedis.hset(key, field, value);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}
	
	public Set<String> keysPattern(String pattern){
		Jedis jedis = getJedis();
		try {
			return jedis.keys(pattern);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public Long incr(final String key) {

		Jedis jedis = getJedis();
		try {
			return jedis.incr(key);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public Long llen(final String key) {

		Jedis jedis = getJedis();
		try {
			return jedis.llen(key);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}
	
	
	public List<String> lrange(final String key,long start, long end){
		Jedis jedis = getJedis();
		try {
			return jedis.lrange(key, start, end);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public void lpush(final String key, final String... values) {

		Jedis jedis = getJedis();
		try {
			jedis.lpush(key, values);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}
	
	

	public void rpush(final String key, final String... values) {

		Jedis jedis = getJedis();
		try {
			jedis.rpush(key, values);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}
	
	

	public Boolean lremAll(final String key, final String value) {
		Jedis jedis = getJedis();
		try {
			Long count = jedis.lrem(key, 0, value);
			return (count > 0);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public String lindex(final String key,  int index){
		Jedis jedis = getJedis();
		try {
			String str = jedis.lindex(key, index);
			return str;
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}
	
	public Boolean lremOne(final String key, final String value) {
		Jedis jedis = getJedis();
		try {
			Long count = jedis.lrem(key, 1, value);
			return (count == 1);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public String rpop(final String key) {
		Jedis jedis = getJedis();
		try {
			return jedis.rpop(key);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public String lpop(final String key) {
		Jedis jedis = getJedis();
		try {
			return jedis.lpop(key);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public void set(final String key, final String value) {

		Jedis jedis = getJedis();
		try {
			jedis.set(key, value);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public void setex(final String key, final String value, final int seconds) {

		Jedis jedis = getJedis();
		try {
			jedis.setex(key, seconds, value);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public Boolean setnx(final String key, final String value) {
		Jedis jedis = getJedis();
		try {
			return jedis.setnx(key, value) == 1 ? true : false;
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}

	}
	
	public Long sadd(String key, String...members) {
		Jedis jedis = getJedis();
		try {
			return jedis.sadd(key, members);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}
	
	public Set<String> smembers(String key) {
		Jedis jedis = getJedis();
		try {
			return jedis.smembers(key);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public Long zadd(final String key, final String member, final double score) {
		Jedis jedis = getJedis();
		try {
			return jedis.zadd(key, score, member);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public Long zcard(final String key) {
		Jedis jedis = getJedis();
		try {
			return jedis.zcard(key);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}

	}

	public Boolean zrem(final String key, final String member) {
		Jedis jedis = getJedis();
		try {
			return jedis.zrem(key, member) == 1 ? true : false;
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public Double zscore(final String key, final String member) {
		Jedis jedis = getJedis();
		try {
			return jedis.zscore(key, member);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}

	}

	public Long zrank(final String key, final String member) {
		Jedis jedis = getJedis();
		try {
			return jedis.zrank(key, member);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public Set<String> zrangeByScore(final String key, final double min, final double max) {
		Jedis jedis = getJedis();
		try {
			return jedis.zrangeByScore(key, min, max);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}
	public Set<String> zrangeByScore(final String key, final String min, final String max) {
		Jedis jedis = getJedis();
		try
		{
			return jedis.zrangeByScore(key, min, max);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		}
		finally
		{
			close(jedis);
		}
	}
	
	public Set<String> zrangeByScore(final String key, final String min, final String max,
		      final int offset, final int count) {
		Jedis jedis = getJedis();
		try
		{
			return jedis.zrangeByScore(key, min, max,offset,count);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		}
		finally
		{
			close(jedis);
		}
	}
	public Set<String> zrangeByScore(final String key, final double min, final double max,
		      final int offset, final int count)
	{
		Jedis jedis = getJedis();
		try
		{
			return jedis.zrangeByScore(key, min, max,offset,count);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		}
		finally
		{
			close(jedis);
		}
    }
	public Set<String> zrevrangeByScore(final String key, final String min, final String max,
		      final int offset, final int count) {
		Jedis jedis = getJedis();
		try
		{
			return jedis.zrevrangeByScore(key, min, max,offset,count);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		}
		finally
		{
			close(jedis);
		}
	}
	public Set<String> zrevrangeByScore(final String key, final String min, final String max) {
		Jedis jedis = getJedis();
		try
		{
			return jedis.zrevrangeByScore(key, min, max);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		}
		finally
		{
			close(jedis);
		}
	}
	public long zcount(final String key , final double min , final double max)
	{
		Jedis jedis = getJedis();
		try
		{
			return jedis.zcount(key, min, max);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		}
		finally
		{
			close(jedis);
		}
	}
	
	public long zcount(final String key , final String min , final String max)
	{
		Jedis jedis = getJedis();
		try
		{
			return jedis.zcount(key, min, max);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		}
		finally
		{
			close(jedis);
		}
	}
	public Long zadd(final String key, final double score, final String member) {
		Jedis jedis = getJedis();
		try {
			return jedis.zadd(key, score, member);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public Map<String, String> hgetAll(final String key) {
		Jedis jedis = getJedis();
		try {
			return jedis.hgetAll(key);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}
	
	
	public Long zremrangeByScore(final String key, final String start, final String end) {
		Jedis jedis = getJedis();
		try {
			return jedis.zremrangeByScore(key, start, end);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}
	
	public Long zremrangeByScore(final String key, final double start, final double end) {
		Jedis jedis = getJedis();
		try {
			return jedis.zremrangeByScore(key, start, end);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}
	
	public void watch(final String key) {
		Jedis jedis = getJedis();
		try {
			jedis.watch(key);
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}

	}

	public void unwatch() {

		Jedis jedis = getJedis();
		try {
			jedis.unwatch();
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}

	public Transaction multi() {

		Jedis jedis = getJedis();
		try {
			return jedis.multi();
		} catch (JedisConnectionException ex) {
			throw ex;
		} catch (ClassCastException ex) {
			throw ex;
		} finally {
			close(jedis);
		}
	}
	
	public ScanResult<String> scan(String cursor, ScanParams scanParams) {
		Jedis jedis = getJedis();
        try {
            return jedis.scan(cursor, scanParams);
        } catch (JedisConnectionException ex) {
            throw ex;
        } catch (ClassCastException ex) {
            throw ex;
        } finally {
        	close(jedis);
        }
    }
}
