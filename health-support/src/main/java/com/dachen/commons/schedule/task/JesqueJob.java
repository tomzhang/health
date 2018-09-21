package com.dachen.commons.schedule.task;

import net.greghaines.jesque.Job;
import net.greghaines.jesque.client.Client;
import net.greghaines.jesque.client.ClientPoolImpl;
import redis.clients.jedis.exceptions.JedisConnectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.spring.SpringBeansUtils;

public class JesqueJob {
	
	
	private static final Logger log = LoggerFactory.getLogger(JesqueJob.class);
	
	private static Client JESQUE_CLIENT_POOL = null;
	
	
	
	public static void createOneTimeJob(String queue,Job job) {
        try {
        	addQueue(queue);
        	getClient().enqueue(queue, job);
        } catch (Exception e) {
        	log.error("createOneTimeJob error  queue = ["+queue+"] , job = "+job );
        }
    }
    
    public static void createDelayJob(String queue,Job job, long future) {
    	try {
    		addQueue(queue);
        	getClient().delayedEnqueue(queue, job, future);
        } catch (Exception e) {
        	log.error("createDelayJob error  queue = ["+queue+"] , job = "+job );
        }
    }
    
    public static void createRecurringJob(String queue,Job job, long future,long frequency) {
    	try {
    		addQueue(queue);
        	getClient().recurringEnqueue(queue, job, future, frequency);
        } catch (Exception e) {
        	log.error("createDelayJob error  queue = ["+queue+"] , job = "+job );
        }
    }
    
    public static void stopRecurringQueue(String queue,Job job) {
    	 try {
    		 getClient().removeRecurringEnqueue(queue, job);
    	 } catch (JedisConnectionException e) {
    		 e.printStackTrace();
    	 }
    }

    private static Client getClient()
    {
    	if(JESQUE_CLIENT_POOL==null){
    		synchronized (JesqueJob.class) {
				if(JESQUE_CLIENT_POOL==null){
					JESQUE_CLIENT_POOL = new ClientPoolImpl(JesqueConfig.getRedisConfig(),JesqueConfig.getJedisPool());
				}
			}
    	}
    	return JESQUE_CLIENT_POOL;
    }
    
    private static void addQueue(String queueName)
    {
    	TaskController task = SpringBeansUtils.getBeane(TaskController.class);
		if(task==null)
		{
			throw new ServiceException("TaskController没有实例化");
		}
		task.addQueue(queueName);
    }

	public static void cancelDelayJob(String queueName, Job job) 
	{
		try {
			getClient().removeDelayedEnqueue(queueName, job);
		} catch (Exception e) {
			log.error("createDelayJob error  queue = ["+queueName+"] , job = "+job );
		}
	}

}
