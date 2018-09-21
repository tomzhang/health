package com.dachen.health.task;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dachen.health.commons.service.UserManager;
import com.dachen.redis.RedisLock;

/**
 * 
 * @author 定时修改用户的身份 对过去的用户
 *
 */
@Component
public class SchedulerTask {

    private final static Logger logger = LoggerFactory.getLogger(SchedulerTask.class);

  
    private final static long FIVE_MINUTE = 60*60*1000L;    
 
    @Autowired
    UserManager userManager;
    
    
 //   @Scheduled(fixedRate = FIVE_MINUTE + 8732)
    @RedisLock(key = "handLimitPeriodUser-lock-01", expireTime = 60)
    public void handLimitPeriodUser() throws Exception {
    	Long startTime = System.currentTimeMillis();
    	
    	userManager.handLimitPeriodUser();
    	
    	logger.info("定时处理过期的用户:{}",System.currentTimeMillis()-startTime);
    }
    
    
   

   
}
