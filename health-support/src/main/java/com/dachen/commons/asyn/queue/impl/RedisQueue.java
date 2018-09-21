package com.dachen.commons.asyn.queue.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.exceptions.JedisConnectionException;

import com.dachen.commons.asyn.queue.IQueue;
import com.dachen.commons.asyn.queue.QueueExecutors;
import com.dachen.commons.asyn.queue.QueueMessageListener;
import com.dachen.commons.asyn.queue.QueueThreadFactory;
import com.dachen.commons.support.jedis.JedisTemplate;

public class RedisQueue implements IQueue {
    private static final Logger logger = LoggerFactory.getLogger(RedisQueue.class);
    private static String PREF = "RedisListQueue:";
    private List<RedisQueueMessageListener> pubSubList = new ArrayList<RedisQueueMessageListener>();
    private Map<String, AtomicLong> queueSizeMap = new HashMap<String, AtomicLong>();
    private Map<String, Counter> counterMap = new HashMap<String, Counter>();
    private Lock lock = new ReentrantLock();
    
	protected JedisTemplate xedisClient;

    @Override
    public void init(JedisTemplate xedisClient) throws Exception {
    	this.xedisClient = xedisClient;
    }

    @Override
    public void stop() {
        for (RedisQueueMessageListener messageConsumer : pubSubList) {
            try {
                messageConsumer.unsubscribe();
            } catch (Exception ignore) {
            }
        }
    }

    private AtomicLong getQueueSize(String topic) {
        AtomicLong queueSize = queueSizeMap.get(topic);
        if (queueSize == null) {
            lock.lock();
            try {
                queueSize = queueSizeMap.get(topic);
                if (queueSize == null) {
                    queueSize = new AtomicLong();
                    queueSizeMap.put(topic, queueSize);
                }
            } finally {
                lock.unlock();
            }
        }
        return queueSize;
    }

    private Counter getCounter(String topic) {
        Counter counter = counterMap.get(topic);
        if (counter == null) {
            lock.lock();
            try {
                counter = counterMap.get(topic);
                if (counter == null) {
                    counter = new Counter();
                    counterMap.put(topic, counter);
                }
            } finally {
                lock.unlock();
            }
        }
        return counter;
    }

    @Override
    public void sendMessage(String topic, String json) {
        boolean needTry;
        int i;
        for (i = 0; i < 3; i++) {
            needTry = false;
            try {
                xedisClient.rpush(PREF + topic, json);
                getQueueSize(topic).incrementAndGet();
            } catch (Throwable ignore) {
                logger.error("sendMessage:   " + topic + "/" + json, ignore);
                if (ignore instanceof JedisConnectionException || ignore instanceof ClassCastException) {
                    // 遇到JedisConnectionException或者ClassCastException 则重试3次
                    needTry = true;
                }
            }
            if (!needTry) {
                break;
            }
        }
        if (i > 0) {
            if (i != 3) {
                logger.error("try sendMessage ok    " + topic + "/" + json);
            } else {
                logger.error("try sendMessage error " + topic + "/" + json);
            }
        }
    }

    @Override
    public void subscribe(String topic, QueueMessageListener listener) {
        RedisQueueMessageListener redisQueueMessageListener = 
        		new RedisQueueMessageListener(xedisClient,"RedisQueue-"+topic, PREF+topic, getQueueSize(topic), getCounter(topic), listener);
        Thread thread = new Thread(redisQueueMessageListener, redisQueueMessageListener.getName());
        thread.start();
        pubSubList.add(redisQueueMessageListener);
        logger.info("{} started!", thread.getName());
    }

    @Override
    public long size(String topic) {
        try {
            return xedisClient.llen(PREF + topic);
        } catch (Throwable ignore) {
            return 0;
        }
    }

    @Override
    public long qps(String topic) {
        return getCounter(topic).get();
    }

    private static class RedisQueueMessageListener implements Runnable {
        private String topic;
        private QueueMessageListener listener;
        private Executor defaultExecutor = null;
        private Lock lock = new ReentrantLock();
        private AtomicBoolean isStop = new AtomicBoolean(false);
        private String name = null;
        private AtomicLong inqueue = new AtomicLong(0L);
        private long maxInQueue = 5L;
        private long minInQueue = 1L;
        private AtomicLong queueSize;
        private Counter counter;
        private JedisTemplate xedisClient;
        
        public RedisQueueMessageListener(JedisTemplate xedisClient,
        		String name, 
        		String topic, 
        		AtomicLong queueSize, 
        		Counter counter, 
        		QueueMessageListener listener) {
            this.name = name;
            this.topic = topic;
            this.listener = listener;
            this.queueSize = queueSize;
            this.counter = counter;
            this.xedisClient = xedisClient;
        }

        public String getName() {
            return name;
        }

        @Override
        public void run() {
            Executor executor = getExecutor();
            ThreadFactory threadFactory = null;
            if (executor instanceof ThreadPoolExecutor) {
                threadFactory = ((ThreadPoolExecutor) executor).getThreadFactory();
                minInQueue = ((ThreadPoolExecutor) executor).getCorePoolSize();
                maxInQueue = ((ThreadPoolExecutor) executor).getMaximumPoolSize();
            }
            if (minInQueue <= 1L) minInQueue = 1L;

            for (long i = 0; i < minInQueue; i++) 
            {
                addExecutor(executor, threadFactory, false);
            }

            if (minInQueue != maxInQueue) 
            {
                long waitTime = 2000L;
                int checkTime = 0;
                int blockCheck = 0;
                while (!isStop.get()) {
                    try {
                        Thread.sleep(waitTime);
                    } catch (Exception ignore) {
                    }
                    waitTime = 2000L;
                    long len = queueSize.get();
                    if (len < 0L || checkTime <= 0) {
                        try {
                            Long lenObj = xedisClient.llen(topic);
                            if (lenObj == null){
                            	lenObj = 0L;
                            }
                            len = lenObj;
                            queueSize.set(len);
                            checkTime = 60;
                            if (len == 0L) waitTime = 10000L;
                        } catch (Throwable ignore) {
                        }
                    } else {
                        checkTime--;
                    }
                    len = len / 10;
                    long poolSize = inqueue.get();
                    long qps = counter.get();

                    if (poolSize < maxInQueue) {
                        boolean isBlock = false;
                        if (qps == 0L && len != 0L) {
                            isBlock = true;
                        } else if (len > qps) {
                            waitTime = 1000L;
                            addExecutor(executor, threadFactory, true);
                        }

                        if (isBlock) {
                            blockCheck++;
                            if (blockCheck > 10) {
                                blockCheck = 0;
                                addExecutor(executor, threadFactory, true);
                            }
                        } else {
                            blockCheck = 0;
                        }
                    } else {
                        blockCheck = 0;
                    }
                }
            }
        }

        private void addExecutor(Executor executor, ThreadFactory threadFactory, boolean autoStop) {
            RedisQueueMessageExecutor msgExecutor = new RedisQueueMessageExecutor(xedisClient, topic, listener, queueSize, inqueue, counter, isStop, autoStop);
            if (threadFactory != null) {
                Thread thread = threadFactory.newThread(msgExecutor);
                thread.start();
            } else {
                executor.execute(msgExecutor);
            }
        }

        public void unsubscribe() {
            isStop.set(true);
        }

        private Executor getExecutor() {
            Executor executor = listener.getExecutor();
            if (executor == null) {
                if (defaultExecutor == null) {
                    lock.lock();
                    try {
                        if (defaultExecutor == null) {
                            defaultExecutor = QueueExecutors.newCachedThreadPool(1, 5, new QueueThreadFactory(topic));
                        }
                    } finally {
                        lock.unlock();
                    }
                }
                executor = defaultExecutor;
            }
            return executor;
        }
    }

    private static class RedisQueueMessageExecutor implements Runnable {
        private String topic;
        private QueueMessageListener listener;
        private AtomicLong inqueue;
        private AtomicLong queueSize;
        private AtomicBoolean isStop;
        private boolean autoStop;
        private Counter counter;
        private JedisTemplate xedisClient;

        private RedisQueueMessageExecutor(JedisTemplate xedisClient,String topic, QueueMessageListener listener, 
        		AtomicLong queueSize, AtomicLong inqueue, Counter counter, AtomicBoolean isStop, boolean autoStop) {
            this.topic = topic;
            this.listener = listener;
            this.isStop = isStop;
            this.autoStop = autoStop;
            this.queueSize = queueSize;
            this.inqueue = inqueue;
            this.counter = counter;
            this.xedisClient = xedisClient;
            inqueue.incrementAndGet();
        }

        @Override
        public void run() {
            try {
                innerRun();
            } finally {
                inqueue.decrementAndGet();
            }
        }

        private void innerRun() {
            int emptyCount = 0;
            while (!isStop.get()) {
                boolean isOk = true;
                String msg = null;
                try {
                    if (queueSize.get() > 0L) {
                        msg = xedisClient.lpop(topic);
                    }
                } catch (Throwable ignore) {
                    logger.warn(ignore.getMessage(), ignore);
                    isOk = false;
                }
                if (!StringUtils.isEmpty(msg)) {
                    counter.add();
                    queueSize.decrementAndGet();
                    emptyCount = 0;
                    onMessage(topic, msg);
                } else if (isOk) {
                    emptyCount++;
                    if (emptyCount > 60 && autoStop) {
                        break;
                    } else {
                        try {
                            Thread.sleep(500L);
                        } catch (Exception ignore) {
                        }
                    }
                } else {
                    emptyCount = 0;
                }
            }
        }


        public void onMessage(String channel, String msg) {
            try {
                listener.recieveMessages(channel, msg);
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

}
