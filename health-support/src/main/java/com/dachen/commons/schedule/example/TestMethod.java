package com.dachen.commons.schedule.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("testMethod")
public class TestMethod {

    private static final Logger log = LoggerFactory.getLogger(TestMethod.class);

    public void test(String args) {
        log.info("TestMethod.test({})", new Object[] { args });
    }
    
    public void testDelayedJob(String args) {
        log.info("TestMethod.testDelayedJob({})", new Object[] { args });
    }

    
    public void testRecurringJob(String args) {
        log.info("TestMethod.testRecurringJob({}) ", new Object[] { args });
    }

    public void testRecurringJob(TestVO arg1,String arg2) {
    	log.info("testRecurringJob,arg1={},arg2={}",arg1.toString(),arg2);
    }
}
