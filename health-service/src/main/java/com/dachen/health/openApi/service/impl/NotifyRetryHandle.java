package com.dachen.health.openApi.service.impl;

import com.dachen.commons.net.HttpHelper;
import com.dachen.util.StringUtil;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.retry.annotation.EnableRetry;

/**
 * @author liangcs
 * @desc
 * @date:2017/5/3 15:40
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Component
@EnableRetry
public class NotifyRetryHandle {

    private static final Logger logger = LoggerFactory.getLogger(NotifyRetryHandle.class);

    @Retryable(value= {RemoteAccessException.class},maxAttempts = 3,backoff = @Backoff(delay = 5000l,multiplier = 1))
    public void execute(String url, Map<String, String> params) throws Exception {

        String response = HttpHelper.post(url, params);
        if (StringUtil.isEmpty(response)) {
            logger.info("远程调用异常");
            throw new RemoteAccessException("远程调用异常");
        }

    }

}
