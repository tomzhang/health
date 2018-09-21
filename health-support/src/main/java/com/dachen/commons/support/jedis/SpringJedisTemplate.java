package com.dachen.commons.support.jedis;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 暂时废弃不用
 * @author Administrator
 * @deprecated
 */
public class SpringJedisTemplate {
	
	@Resource(name = "redisTemplate")
	private StringRedisTemplate redisTemplate;
	
	public <T> T execute(RedisCallback<T> action)
	{
		return redisTemplate.execute(action);
	}
	public List<Object> executePipelined(RedisCallback<?> action)
	{
		return redisTemplate.executePipelined(action);
	}
	
	public Boolean expire(final String key, final int seconds) {
		return redisTemplate.expire(key, seconds,TimeUnit.SECONDS);
		/*return execute(new JedisCallback<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) {
				return redisTemplate.expire(key, seconds,TimeUnit.SECONDS);
			}
		});*/
		
	}

	public Long ttl(final String key) {
		return redisTemplate.getExpire(key);
		/*return redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.ttl(key.getBytes());
            }
        });*/
		/*return execute(new JedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.ttl(key);
			}
		});*/
	}

	public Long decr(final String key) {
		return redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.decr(key.getBytes());
            }
        });
//		return execute(new JedisCallback<Long>() {
//			@Override
//			public Long execute(Jedis jedis) {
//				return jedis.decr(key);
//			}
//		});
	}

	public void del(final String... keys) {
		List<String>keyList = Arrays.asList(keys);
		redisTemplate.delete(keyList);
		/*return execute(new JedisCallback<Boolean>() {

			@Override
			public Boolean execute(Jedis jedis) {
				return jedis.del(keys) == 1 ? true : false;
			}
		});*/
	}

	public Boolean keyExists(final String key) {
		return redisTemplate.hasKey(key);
		/*return execute(new JedisCallback<Boolean>() {

			@Override
			public Boolean execute(Jedis jedis) {
				return jedis.exists(key);
			}
		});*/
	}

	

	public void flushDB() {
		redisTemplate.execute(new RedisCallback<String>() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return "ok";
            }
        });
		/*execute(new JedisCallbackVoid() {

			@Override
			public void execute(Jedis jedis) {
				jedis.flushDB();
			}
		});*/
	}

	public String get(final String key) {
		return redisTemplate.opsForValue().get(key);
		/*return execute(new JedisCallback<String>() {

			@Override
			public String execute(Jedis jedis) {
				return jedis.get(key);
			}
		});*/
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
		redisTemplate.opsForHash().delete(key, fieldsName);
//		return execute(new JedisCallback<Long>() {
//			@Override
//			public Long execute(Jedis jedis) {
//				return jedis.hdel(key, fieldsName);
//			}
//		});
	}

	public String hget(final String key, final String field) {
		return (String)redisTemplate.opsForHash().get(key, field);
		/*return execute(new JedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.hget(key, field);
			}
		});*/
	}

	public Set<Object> hkeys(final String key) {
		return redisTemplate.opsForHash().keys(key);
//		return execute(new JedisCallback<Set<String>>() {
//			@Override
//			public Set<String> execute(Jedis jedis) {
//				return jedis.hkeys(key);
//			}
//		});
	}

	public List<String> hmget(final String key, final String[] fields) {
		HashOperations<String,String,String>opt = redisTemplate.opsForHash();
		return opt.multiGet(key, Arrays.asList(fields));
//		return execute(new JedisCallback<List<String>>() {
//			@Override
//			public List<String> execute(Jedis jedis) {
//				return jedis.hmget(key, fields);
//			}
//		});
	}

	public void hmset(final String key, final Map<String, String> map) {
		redisTemplate.opsForHash().putAll(key, map);
		/*execute(new JedisCallbackVoid() {

			@Override
			public void execute(Jedis jedis) {
				jedis.hmset(key, map);
			}
		});*/
	}

	public void hset(final String key, final String field, final String value) {
		redisTemplate.opsForHash().put(key, field, value);
		/*execute(new JedisCallbackVoid() {

			@Override
			public void execute(Jedis jedis) {
				jedis.hset(key, field, value);
			}
		});*/
	}

	public Long incr(final String key) {
		return redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) {
				return connection.incr(key.getBytes());
			}
		});
		/*return execute(new JedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.incr(key);
			}
		});*/
	}

	public Long llen(final String key) {
		return redisTemplate.opsForList().size(key);
		/*return execute(new JedisCallback<Long>() {

			@Override
			public Long execute(Jedis jedis) {
				return jedis.llen(key);
			}
		});*/
	}

	public void lpush(final String key, final String... values) {
		redisTemplate.opsForList().leftPushAll(key, values);
//		execute(new JedisCallbackVoid() {
//			@Override
//			public void execute(Jedis jedis) {
//				jedis.lpush(key, values);
//			}
//		});
	}

	public Boolean lremAll(final String key, final String value) {
		Long count = redisTemplate.opsForList().remove(key, 0, value);
		return (count > 0);
//		return execute(new JedisCallback<Boolean>() {
//			@Override
//			public Boolean execute(Jedis jedis) {
//				Long count = jedis.lrem(key, 0, value);
//				return (count > 0);
//			}
//		});
	}

	public Boolean lremOne(final String key, final String value) {
		Long count = redisTemplate.opsForList().remove(key, 1, value);
		return (count == 1);
//		return execute(new JedisCallback<Boolean>() {
//			@Override
//			public Boolean execute(Jedis jedis) {
//				Long count = jedis.lrem(key, 1, value);
//				return (count == 1);
//			}
//		});
	}

	public String rpop(final String key) {
		return redisTemplate.opsForList().rightPop(key);
//		return execute(new JedisCallback<String>() {
//
//			@Override
//			public String execute(Jedis jedis) {
//				return jedis.rpop(key);
//			}
//		});
	}

	public void set(final String key, final String value) {
		redisTemplate.opsForValue().set(key, value);
//		execute(new JedisCallbackVoid() {
//
//			@Override
//			public void execute(Jedis jedis) {
//				jedis.set(key, value);
//			}
//		});
	}

	public void setex(final String key, final String value, final int seconds) {
		redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
//		execute(new JedisCallbackVoid() {
//
//			@Override
//			public void execute(Jedis jedis) {
//				jedis.setex(key, seconds, value);
//			}
//		});
	}

	public Boolean setnx(final String key, final String value) {
		return redisTemplate.opsForValue().setIfAbsent(key, value);
//		return execute(new JedisCallback<Boolean>() {
//
//			@Override
//			public Boolean execute(Jedis jedis) {
//				return jedis.setnx(key, value) == 1 ? true : false;
//			}
//		});
	}

	/*public Boolean setnxex(final String key, final String value, final int seconds) {
		return redisTemplate.opsForValue().set(key, value);
//		return execute(new JedisCallback<Boolean>() {
//
//			@Override
//			public Boolean execute(Jedis jedis) {
//				String result = jedis.set(key, value, "NX", "EX", seconds);
//				return JedisUtils.isStatusOk(result);
//			}
//		});
	}*/
	
	public Boolean zadd(final String key, final String member, final double score) {
		return redisTemplate.opsForZSet().add(key, member, score);
//		return execute(new JedisCallback<Boolean>() {
//
//			@Override
//			public Boolean execute(Jedis jedis) {
//				return jedis.zadd(key, score, member) == 1 ? true : false;
//			}
//		});
	}

	public Long zcard(final String key) {
		return redisTemplate.opsForZSet().zCard(key);
//		return execute(new JedisCallback<Long>() {
//
//			@Override
//			public Long execute(Jedis jedis) {
//				return jedis.zcard(key);
//			}
//		});
	}

	public Boolean zrem(final String key, final String member) {
		return redisTemplate.opsForZSet().remove(key, member)== 1 ? true : false;
//		return execute(new JedisCallback<Boolean>() {
//
//			@Override
//			public Boolean execute(Jedis jedis) {
//				return jedis.zrem(key, member) == 1 ? true : false;
//			}
//		});
	}

	public Double zscore(final String key, final String member) {
		return redisTemplate.opsForZSet().score(key,member);
//		return execute(new JedisCallback<Double>() {
//
//			@Override
//			public Double execute(Jedis jedis) {
//				return jedis.zscore(key, member);
//			}
//		});
	}
	
	public Long zrank(final String key, final String member) 
	{
		return redisTemplate.opsForZSet().rank(key, member);
	}
	public Set<String> zrangeByScore(final String key, final double min, final double max) 
	{
		return redisTemplate.opsForZSet().rangeByScore(key, min, max);
	}
	public Boolean zadd(final String key, final double score, final String member) {
		return redisTemplate.opsForZSet().add(key, member, score);
	}
	public Map<String, String> hgetAll(final String key)
	{
		HashOperations<String,String,String>opt = redisTemplate.opsForHash();
		return opt.entries(key);
	}
	
	public void watch(final String key)
	{
		redisTemplate.watch(key);
		
	}
	public void unwatch() {
		
		redisTemplate.unwatch();
	}
	public List<Object> exec() {
		return redisTemplate.exec();
	}
	public void multi() {
		redisTemplate.multi();
		
	}
	
	public StringRedisTemplate getRedisTemplate()
	{
		return redisTemplate;
	}
}
