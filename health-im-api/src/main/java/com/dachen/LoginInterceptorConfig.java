package com.dachen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Configuration
@ConfigurationProperties(prefix = "login")
public class LoginInterceptorConfig {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private String[] excludePathPatterns;

    private String[] guestIncludePathPatterns;

    public String[] getExcludePathPatterns() {
        return excludePathPatterns;
    }

    public void setExcludePathPatterns(String[] excludePathPatterns) {
        this.excludePathPatterns = excludePathPatterns;
    }

    public String[] getGuestIncludePathPatterns() {
        return guestIncludePathPatterns;
    }

    public void setGuestIncludePathPatterns(String[] guestIncludePathPatterns) {
        this.guestIncludePathPatterns = guestIncludePathPatterns;
    }

    @PostConstruct
    public void printConfig() {
        String tag = "printConfig";
        logger.debug("{}. excludePathPatterns={}", tag, (null == excludePathPatterns?"": Arrays.toString(excludePathPatterns)));
    }
}

