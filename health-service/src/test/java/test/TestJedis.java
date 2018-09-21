package test;

import redis.clients.jedis.Jedis;

public class TestJedis {

	public static void main(String[] args) {
		Jedis jedis = new Jedis("114.119.6.150", 6379);
		// jedis.flushDB();
		// jedis.set("fuckyou", "12300");
		// jedis.expire("fuckyou", 10);
		// Long a = jedis.ttl("fuckyou");
		// System.out.println(a);

		for (String key : jedis.keys("*"))
			System.out.println(key);
		jedis.close();
	}

}
