package com.dachen.redis;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.dachen.commons.exception.ServiceException;


/**
 * Created by test on 2017/6/9.
 */
@Aspect
@Component
public class RedisLockAspect {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Pointcut("@annotation(com.dachen.redis.RedisLock)")
    public void redisLockPointcut() {
    }

    @Around("redisLockPointcut()")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        String redisKey = redisLock.key();
        Annotation[][] annotations = method.getParameterAnnotations();
        Object[] getArgs = pjp.getArgs();
        redisKey += getLockedObject(annotations, getArgs);
        Object object = null;
        boolean lock = false;

        //过期时间
        Long expireTime = System.currentTimeMillis() + redisLock.expireTime() * 1000;

        lock = stringRedisTemplate.opsForValue()
            .setIfAbsent(redisKey, String.valueOf(expireTime));
        try {
            if (lock) {
                stringRedisTemplate.expire(redisKey, redisLock.expireTime(), TimeUnit.SECONDS);
                object = pjp.proceed();
            } else {

                //当前时间大于过期时间，判断为死锁，删除
                String redisValue = stringRedisTemplate.opsForValue().get(redisKey);
                if (StringUtils.isNotEmpty(redisValue)) {
                    expireTime = Long.valueOf(redisValue);
                    if (System.currentTimeMillis() > expireTime) {
                        stringRedisTemplate.delete(redisKey);
                    }
                }

                throw new ServiceException(redisLock.msg());
            }
        } finally {
            if (lock) {
                stringRedisTemplate.delete(redisKey);
            }
        }
        return object;
    }

    private String getLockedObject(Annotation[][] annotations, Object[] args)
        throws NoSuchFieldException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < annotations.length; i++) {
            for (int j = 0; j < annotations[i].length; j++) {
                if (annotations[i][j] instanceof RedisParameterLocked) {
                    sb.append(args[i].toString());
                }
            }
        }
        return sb.toString();
    }


}
