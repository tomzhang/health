package com.dachen.commons.schedule.task;

import static net.greghaines.jesque.utils.JesqueUtils.entry;
import static net.greghaines.jesque.utils.JesqueUtils.map;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import net.greghaines.jesque.Job;
import net.greghaines.jesque.worker.MapBasedJobFactory;
import net.greghaines.jesque.worker.Worker;
import net.greghaines.jesque.worker.WorkerEvent;
import net.greghaines.jesque.worker.WorkerListener;
import net.greghaines.jesque.worker.WorkerPool;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton") 
public class TaskController implements ApplicationContextAware{
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
//	private final int numWorkers = Runtime.getRuntime().availableProcessors();
	private final int numWorkers = 2;
	private WorkerPool worker;
	
	private Thread workerThread;
	
	private volatile AtomicBoolean isStarted = new AtomicBoolean(false);
	
	private static ApplicationContext context;
	private static AtomicBoolean isOpened = null;
	
	private TaskWorkerListener taskWorkerListener = new TaskWorkerListener();
	
	@PostConstruct
	private void init()
	{
		if(!isOpen()){
			return;
		}
		Set<String>queues = JesqueConfig.getAllRunQueue();
		if(queues!=null && queues.size()>0){
			start(queues);
		}
	}
	
	public ApplicationContext getContext() {
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		context = ctx;
	}
	
	private synchronized void start(Set<String>queues)
	{
		if(isStarted.get()){
			return;
		}
		try
		{
			WorkerImplFactory workFactory = new WorkerImplFactory(
					JesqueConfig.getRedisConfig(),
					queues,
					new MapBasedJobFactory(map(entry("Resolver", Resolver.class))),
					JesqueConfig.getJedisPool()
					);
			ThreadFactory threadFactory = Executors.defaultThreadFactory();
			worker = new WorkerPool(workFactory,numWorkers,threadFactory);
			
//			worker.getWorkerEventEmitter().addListener(new WorkerListener() {
//	            public void onEvent(WorkerEvent event, Worker worker, String queue, Job job, Object runner, Object result,
//	                    Throwable t) {
//	            	//如果是一次性的任务，删除 TODO
////	            	System.out.println(queue);
//	            }
//	        }, WorkerEvent.JOB_SUCCESS);
//			worker.getWorkerEventEmitter().addListener(new WorkerListener() {
//	            public void onEvent(WorkerEvent event, Worker worker, String queue, Job job, Object runner, Object result,
//	                    Throwable t) {
//	            	worker.end(true);
//	            }
//	        }, WorkerEvent.WORKER_STOP);
			
			worker.getWorkerEventEmitter().addListener(taskWorkerListener);
			
			workerThread = new Thread(worker);
			workerThread.setDaemon(true);
			workerThread.start();
			
			isStarted.set(true);
		} 
		catch (Exception e)
		{ 
			e.printStackTrace(); 
			isStarted.set(false);
		}
	}
	@PreDestroy
	public synchronized void destroy()
	{
		if (null != worker) {
			worker.end(true);
		}
		
		if (null != workerThread) {
			try
			{
				workerThread.join(); 
				JesqueConfig.getJedisPool().destroy();
			} 
			catch (Exception e)
			{ 
				e.printStackTrace(); 
			}	
		}
	}
	
	void addQueue(String queueName)
	{
		if(StringUtils.isEmpty(queueName))
		{
			return;
		}
		if(!isStarted.get()){
			Set<String>queueSet = new HashSet<String>();
			queueSet.add(queueName);
			start(queueSet);
			return;
		}
		Collection<String> queues = worker.getQueues();
		if(!queues.contains(queueName))
		{
			addWorkerQueue(queueName);
		}
	}
	
	private synchronized void addWorkerQueue(String queueName){
		Collection<String> queues = worker.getQueues();
		if(!queues.contains(queueName))
		{
			worker.addQueue(queueName);
		}
	}
	
	public static boolean isOpen(){
		if(isOpened!=null){
			return isOpened.get();
		}
		try{
			JesqueSwitch jesqueSwitch = (JesqueSwitch)context.getBean(JesqueSwitch.BEAN_ID);
			if(jesqueSwitch==null){
				return false;
			}
			boolean isOpen = jesqueSwitch.isOpen();
			isOpened = new AtomicBoolean(isOpen);
			if(isOpen){
	    		 String jesqueSpace = jesqueSwitch.getJesqueSpace();
	    		 JesqueConfig.setNamespace(jesqueSpace);
	    	}
			return isOpen;
		}catch(Exception e){
			return false;
		}
	}
	
	class TaskWorkerListener implements WorkerListener {
        final String tag = TaskWorkerListener.class.getSimpleName();
        @Override
        public void onEvent(WorkerEvent event, Worker worker, String queue, Job job, Object runner, Object result, Throwable t) {
        	
        	if (WorkerEvent.WORKER_STOP == event) {
                logger.info("{}. Got an STOP event, start to finish", tag);
                try {
                    destroy();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                return;
            }
        	
            if (WorkerEvent.JOB_SUCCESS != event) {
                return;
            }
//            logger.info("{}. Got an event:{}", tag, event);
//            logger.info("{}. worker={}", tag, worker);
            logger.info("{}. The Worker successfully executed a Job{}. queue={}", tag, job, queue);
//            logger.info("{}. job={}", tag, job);
//            logger.info("{}. runner={}", tag, runner);
//            logger.info("{}. result={}", tag, result);
//            logger.info("{}. t={}", tag, t);
        }
    }
}
