package com.dachen.commons.support.jedis;

import redis.clients.jedis.Jedis;

public interface JedisCallbackVoid {
	void execute(Jedis jedis);
}
