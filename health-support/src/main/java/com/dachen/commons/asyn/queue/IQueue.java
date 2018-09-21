package com.dachen.commons.asyn.queue;

import com.dachen.commons.support.jedis.JedisTemplate;

public interface IQueue {
    public void init(JedisTemplate xedisClient) throws Exception;

    public void stop();

    public void sendMessage(String topic, String json);

    public void subscribe(String topic, QueueMessageListener listener);

    public long size(String topic);

    public long qps(String topic);
}
