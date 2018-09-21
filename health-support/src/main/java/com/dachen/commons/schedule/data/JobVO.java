package com.dachen.commons.schedule.data;

import java.util.ArrayList;
import java.util.List;

import com.dachen.util.JSONUtil;

public class JobVO {
	
	private JobType jobType;
	
	/**
	 * future 延迟多久执行(以提交任务的时间为基准)，单位秒
	 */
	private Long delay=0L;
	
	/**
	 * frequency 每隔多长时间执行一次,单位：秒
	 */
	private Long frequency;
	/**
	 * 需要执行定时任务的bean
	 */
	private String beanName;
	
	/**
	 * 需要bean的哪个方法执行定时任务
	 */
	private String method;
	
	/**
	 * 该方法参数类型
	 */
	private List<String> methodParamTypes;
	
	/**
	 * 该方法参数的具体值
	 */
	private List<Object> methodArgs;
	
	public JobVO(JobType jobType)
	{
		this.jobType = jobType;
	}
	
	public JobVO(JobType jobType,long delay)
	{
		this.jobType = jobType;
		this.delay = delay;
	}
	
	public JobVO(JobType jobType,long delay,long frequency)
	{
		this.jobType = jobType;
		this.delay = delay;
		this.frequency = frequency;
	}
	
	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<String> getMethodParamTypes() {
		return methodParamTypes;
	}

//	public void setMethodParamTypes(List<String> methodParamTypes) {
//		this.methodParamTypes = methodParamTypes;
//	}

	public List<Object> getMethodArgs() {
		return methodArgs;
	}

//	public void setMethodArgs(List<Object> methodArgs) {
//		this.methodArgs = methodArgs;
//	}
	
	public void addParamTypes(Class<?> clazz)
	{
		if(methodParamTypes==null)
		{
			methodParamTypes = new ArrayList<String>();
		}
		methodParamTypes.add(clazz.getName());
	}
	
	public void addParamValue(Object value)
	{
		if(methodArgs==null)
		{
			methodArgs = new ArrayList<Object>();
		}
		methodArgs.add(value);
	}

	public JobType getJobType() {
		return jobType;
	}

	public Long getDelay() {
		return delay;
	}

	public void setDelay(Long delay) {
		this.delay = delay;
	}

	public Long getFrequency() {
		return frequency;
	}

	public void setFrequency(Long frequency) {
		this.frequency = frequency;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
	
}
