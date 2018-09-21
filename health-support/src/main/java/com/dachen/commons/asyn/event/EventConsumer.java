package com.dachen.commons.asyn.event;

import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dachen.commons.asyn.event.listener.BaseEventConsumer;
import com.dachen.commons.asyn.event.listener.ListenerFactory;
import com.dachen.commons.asyn.queue.QueueExecutors;
import com.dachen.commons.asyn.queue.QueueFactory;
import com.dachen.commons.asyn.queue.QueueThreadFactory;
import com.dachen.commons.exception.ServiceException;
import com.dachen.util.PropertiesUtil;

@Component
public class EventConsumer extends BaseEventConsumer{
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	 private static EventConsumer instance = null;
	 @Autowired
	 private QueueFactory queueFactory;
	 @Autowired
	 private ListenerFactory listenerFactory;

	 private static final int maxPoolSize = 8;

	 private EventConsumer() {
		 instance = this;
	 }

	 public static EventConsumer getInstance() {
		 return instance;
	 }

	 @PostConstruct
	 private void init() throws Exception {
		 String eventEnabled = PropertiesUtil.getContextProperty("asyn.event.disabled");
		 if (StringUtils.isNotBlank(eventEnabled) && "true".equals(eventEnabled)) {
			 logger.warn("EventConsumer is disabled.");
			 return;
		 }
		 
		 for(EventType eventType:EventType.values())
		 {
			 subscribe(eventType.getQueueName(),maxPoolSize);
		 }
	 }
    
	 private void subscribe(String queueName,int maximumPoolSize)
	 {
		 String eventEnabled = PropertiesUtil.getContextProperty("asyn.event.disabled");
		 if (StringUtils.isNotBlank(eventEnabled) && "true".equals(eventEnabled)) {
			 throw new ServiceException("EventConsumer is disabled.");
		 }
		 
		 ExecutorService executor =QueueExecutors.newCachedThreadPool(1, maximumPoolSize, 
				new QueueThreadFactory(queueName));
    	
		 super.subscribe(queueFactory, queueName, executor, listenerFactory);
	 }
}
