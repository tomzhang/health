package com.dachen.commons.schedule.example;

import java.util.ArrayList;
import java.util.List;

import com.dachen.commons.schedule.JobExecutors;
import com.dachen.commons.schedule.data.JobType;
import com.dachen.commons.schedule.data.JobVO;

public class TestJesque {
	
	public static void testCreateJob(int type,String arg)
	{
		String beanName = "testMethod";
//        List<String> methodParamTypes = new ArrayList<String>();
//        methodParamTypes.add(" com.dachen.commons.schedule.example.TestVO");
//        methodParamTypes.add("java.lang.String");
//        TestVO arg1 = new TestVO();
//        arg1.setId(1);
//        arg1.setName("参数1");
//        List<String>list=new ArrayList<String>();
//        list.add("test");
//        arg1.setList(list);
        
//        String[]arg2 = new String[]{"这个是参数2",arg};
        String arg2 = "这个是参数2"+arg;
        
        final long delay = 30; // in seconds
        final long frequency = 60; // in seconds
        
        /**
         * 创建一个延迟30秒执行，然后每隔60秒执行一次的任务，
         */
        JobVO job = null;
        if(type==1){
        	job = new JobVO(JobType.RECURRINGJOB,delay, frequency);
        }
        else
        {
        	job = new JobVO(JobType.DELAYJOB,delay);
        }
        job.setBeanName(beanName);
        job.setMethod("testDelayedJob");
//        job.addParamTypes(arg1.getClass());
        job.addParamTypes(arg2.getClass());
//        job.addParamValue(arg1);
        job.addParamValue(arg2);
        JobExecutors.createJob("test_queue2",job);//指定队列名称为test_queue1
	}
	/*public static void testRemoveJob(String method,String arg)
	{
		String beanName = "testMethod";
		List<String> methodParamTypes = new ArrayList<String>();
        methodParamTypes.add("java.lang.String");
        List<String> methodArgs = new ArrayList<String>();
        methodArgs.add(arg);
		Job job = new Job("Resolver", beanName, method, methodParamTypes, methodArgs);
//		JesqueJob.stopRecurringQueue(job);
	}*/
	
	/*public static void testCreateBatchJob(String method,int count)
	{
		String beanName = "testMethod";
        List<String> methodParamTypes = new ArrayList<String>();
        methodParamTypes.add("java.lang.String");
       
		 final long delay = 60*1000; // in seconds
		 final long frequency = 120*1000; // in seconds
        
		 
		 for(int i=1;i<=120;i++)
		 {
			 List<String> methodArgs = new ArrayList<String>();
		     methodArgs.add(String.valueOf(i));
			 Job job = new Job("Resolver", beanName, method, methodParamTypes, methodArgs);
			 
			 final long future = System.currentTimeMillis() + delay; // timestamp
//			 JesqueJob.createDelayJob(job,future);
//			 try {
//				Thread.sleep(6);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		 }
	}
	public static void testMoveAllJob(String method,int count)
	{
		 for(int i=1;i<=count;i++)
		 {
			 testRemoveJob(method,String.valueOf(i));
		 }
	}*/
}
