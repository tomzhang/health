package com.dachen.commons.schedule.data;

public enum JobType {
	/**
	 * 该种类型的任务只执行一次，加入队列后立马执行
	 * 不建议使用此种类型做异步操作。建议@see OrderNotify
	 */
	ONETIMEJOB, 
    
	/**
	 * 该种类型的任务只执行一次，加入队列后指定延迟多长时间执行
	 */
    DELAYJOB,
    
    /**
	 * 该种类型的任务执行多次，加入队列后指定延迟多长时间开始执行，并且没隔多长时间执行一次。
	 */
    RECURRINGJOB;
}
