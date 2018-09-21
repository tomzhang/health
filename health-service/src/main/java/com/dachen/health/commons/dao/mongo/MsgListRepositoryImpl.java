package com.dachen.health.commons.dao.mongo;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.KeyBuilder;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.dao.MsgListRepository;

@Repository
public class MsgListRepositoryImpl extends NoSqlRepository implements MsgListRepository {

	@Override
	public void addToHotList(int cityId, int userId, String messageId, double score) {
		String hmilKey = String.format(KeyBuilder.HotMsgIdListTemplate, cityId);
		String uhmiKey = String.format(KeyBuilder.UserHotMsgIdTemplate, cityId);

//		JedisCallbackVoid callback = jedis -> {
//			// 消息不存在
//			
//		};
//		jedisTemplate.execute(callback);

		if (null == jedisTemplate.zrank(hmilKey, messageId)) {
			String userHotMsgId = jedisTemplate.hget(uhmiKey, String.valueOf(userId));
			userHotMsgId = null == userHotMsgId ? "" : userHotMsgId;
			//
			/*if (null == jedisTemplate.zrank(hmilKey, userHotMsgId)) {
				long length = jedisTemplate.zcard(hmilKey);
				if (length >= 200) {
					Set<String> members = jedisTemplate.zrangeByScore(hmilKey, 0, score);
					if (members.size() > 0) {
						Pipeline pipeline = jedis.pipelined();
						pipeline.zrem(hmilKey, members.toArray()[0].toString());
						pipeline.zadd(hmilKey, score, messageId);
						pipeline.hset(uhmiKey, String.valueOf(userId), messageId);
						pipeline.sync();
					}
				} else {
					Pipeline pipeline = jedis.pipelined();
					pipeline.zadd(hmilKey, score, messageId);
					pipeline.hset(uhmiKey, String.valueOf(userId), messageId);
					pipeline.sync();
				}
			} else {
				double hScore = jedisTemplate.zscore(hmilKey, userHotMsgId);
				if (score >= hScore) {
					Pipeline pipeline = jedis.pipelined();
					pipeline.zrem(hmilKey, userHotMsgId);
					pipeline.zadd(hmilKey, score, messageId);
					pipeline.hset(uhmiKey, String.valueOf(userId), messageId);
					pipeline.sync();
				}
			}*/
		} else {
			jedisTemplate.zadd(hmilKey, score, messageId);
		}
	}

	@Override
	public void addToLatestList(int cityId, int userId, String messageId) {
		String newMsgId = messageId;
		String latestMsgId = getLatestId(cityId, userId);

		String lmilKey = String.format(KeyBuilder.LatestMsgIdListTemplate, cityId);
		String ulmiKey = String.format(KeyBuilder.UserLatestMsgIdTemplate, cityId);

		/*JedisCallbackVoid callback = jedis -> {
			long count = jedis.lrem(lmilKey, 0, (null == latestMsgId ? "" : latestMsgId));
			// 未发表商务圈或最后一条商务圈不在最新人才榜
			if (0 == count) {
				long length = jedis.llen(lmilKey);
				if (length >= 200) {
					jedis.rpop(lmilKey);
				}
			}
			Pipeline pipeline = jedis.pipelined();
			pipeline.lpush(lmilKey, newMsgId);
			pipeline.hset(ulmiKey, String.valueOf(userId), newMsgId);

			pipeline.sync();
		};
		jedisTemplate.execute(callback);*/
	}

	@Override
	public String getHotId(int cityId, Object userId) {
		return null;
	}

	@Override
	public Object getHotList(int cityId, int pageIndex, int pageSize) {
		String key = String.format(KeyBuilder.HotMsgListTemplate, cityId);
//		return com.mongodb.util.JSON.parse(jedisTemplate.execute(new JedisCallback<String>() {
//
//			@Override
//			public String execute(Jedis jedis) {
//				return jedis.hget(key, String.valueOf(pageIndex));
//			}
//		}));
		return JSON.parse(jedisTemplate.hget(key, String.valueOf(pageIndex)));
	}

	@Override
	public String getLatestId(int cityId, Object userId) {
		String key = String.format(KeyBuilder.UserLatestMsgIdTemplate, cityId);
//		JedisCallback<String> callback = jedis -> {
//			return jedis.hget(key, String.valueOf(userId));
//		};
//		return jedisTemplate.execute(callback);
		return jedisTemplate.hget(key, String.valueOf(userId));
	}

	@Override
	public Object getLatestList(int cityId, int pageIndex, int pageSize) {
		String key = String.format(KeyBuilder.LatestMsgListTemplate, cityId);
//		return com.mongodb.util.JSON.parse(jedisTemplate.execute(new JedisCallback<String>() {
//
//			@Override
//			public String execute(Jedis jedis) {
//				return jedis.hget(key, String.valueOf(pageIndex));
//			}
//		}));
		return JSON.parse(jedisTemplate.hget(key, String.valueOf(pageIndex)));
	}

}
