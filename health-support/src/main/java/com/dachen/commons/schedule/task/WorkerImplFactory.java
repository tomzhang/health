package com.dachen.commons.schedule.task;

import java.util.Collection;
import java.util.concurrent.Callable;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;
import net.greghaines.jesque.Config;
import net.greghaines.jesque.worker.JobFactory;
import net.greghaines.jesque.worker.WorkerImpl;

/**
 * WorkerImplFactory is a factory for <code>WorkerImpl</code>s. Designed to be used with <code>WorkerPool</code>.
 */
public class WorkerImplFactory implements Callable<WorkerImpl> {
	private final Config config;
    private final Collection<String> queues;
    private final JobFactory jobFactory;
    
    private final Pool<Jedis> jedisPool;

	/**
     * Create a new factory. Returned <code>WorkerImpl</code>s will use the provided arguments.
     * @param config used to create a connection to Redis and the package prefix for incoming jobs
     * @param queues the list of queues to poll
     * @param jobFactory the job factory that materializes the jobs
     */
    public WorkerImplFactory(final Config config, final Collection<String> queues,
            final JobFactory jobFactory)
    {
    	 this.config = config;
         this.queues = queues;
         this.jobFactory = jobFactory;
         this.jedisPool = null;
    }
            
    public WorkerImplFactory(final Config config, final Collection<String> queues,
            final JobFactory jobFactory,final Pool<Jedis>jedisPool) {
        this.config = config;
        this.queues = queues;
        this.jobFactory = jobFactory;
        this.jedisPool = jedisPool;
    }

    /**
     * Create a new <code>WorkerImpl</code> using the arguments provided to this factory's constructor.
     * @return a new <code>WorkerImpl</code>
     */
    public WorkerImpl call() {
    	if(jedisPool==null)
    	{
    		return new WorkerImpl(this.config, this.queues, this.jobFactory);
    	}
    	Jedis jedis = jedisPool.getResource();
        return new WorkerImpl(this.config, this.queues, this.jobFactory,jedis);
    }
}
