package com.dachen.commons.schedule;

import net.greghaines.jesque.Job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.schedule.data.JobType;
import com.dachen.commons.schedule.data.JobVO;
import com.dachen.commons.schedule.task.JesqueJob;
import com.dachen.commons.schedule.task.TaskController;
import com.dachen.util.StringUtil;

public class JobExecutors {
	
	private static final Logger log = LoggerFactory.getLogger(JobExecutors.class);
	
	public static void createJob(String queueName,JobVO jobVO)
	{
		
		if(!TaskController.isOpen()){
			return;
		}
		log.info("create jesque start , queueName = "+queueName+" jobVo = "+jobVO);

		if(StringUtil.isEmpty(queueName))
		{
			throw new ServiceException("queueName is null");
		}
		if(jobVO==null || jobVO.getJobType()==null)
		{
			throw new ServiceException("参数错误，jobType不能为空");
		}
		
		Job job = convertJobVO(jobVO);
		if(jobVO.getJobType() == JobType.ONETIMEJOB)
		{
			JesqueJob.createOneTimeJob(queueName,job);
		}
		else 
		{
			//延迟多久执行(以提交任务的时间为基准)，单位秒
			Long delay = jobVO.getDelay();
			final long future = System.currentTimeMillis() + delay*1000; 
			if(jobVO.getJobType() == JobType.DELAYJOB)
			{
				JesqueJob.createDelayJob(queueName,job,future);
			}
			else if(jobVO.getJobType() == JobType.RECURRINGJOB)
			{
				//每隔多长时间执行一次,单位：秒
				Long frequency = jobVO.getFrequency();
				if(frequency==null)
				{
					throw new ServiceException("参数错误，recurringjob任务frequency不能为空");
				}
				JesqueJob.createRecurringJob(queueName,job,future,frequency*1000);
			}
			else
			{
				throw new ServiceException("参数错误，jobType类型错误。");
			}
		}
		
		log.info("create jesque end , queueName = "+queueName);
	}
	
	public static void stopRecurringQueue(String queueName,JobVO jobVO)
	{
		if(!TaskController.isOpen()){
			return;
		}
		Job job = convertJobVO(jobVO);
		JesqueJob.stopRecurringQueue(queueName,job);
	}
	
	public static void cancelDelayJob(String queueName , JobVO jobVo)
	{
		if(!TaskController.isOpen())
			return;
		Job job = convertJobVO(jobVo);
		JesqueJob.cancelDelayJob(queueName,job);
	}
	
	private static Job convertJobVO(JobVO jobVO)
	{
		Job job = new Job("Resolver", jobVO.getBeanName(), 
				jobVO.getMethod(), jobVO.getMethodParamTypes(), jobVO.getMethodArgs()); 
		return job;
	}
	
}
