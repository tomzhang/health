package com.dachen.commons.schedule.task;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

import net.greghaines.jesque.json.ObjectMapperFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.dachen.commons.support.spring.SpringBeansUtils;

public class Resolver implements Callable<Object> {
	private static final Logger log = LoggerFactory.getLogger(Resolver.class);
    private String beanName;
    private String methodName;
    private List<String> methodParamTypes;
    private List<?> methodArgs;

    public Resolver(String beanName, String methodName, List<String> methodParamTypes, List<?> methodArgs) {
        super();
        this.beanName = beanName;
        this.methodName = methodName;
        this.methodParamTypes = methodParamTypes;
        this.methodArgs = methodArgs;
    }

    @Override
    public Object call() {
    	Object result = null;
        try {
//            Class<?> c = Class.forName(className);
//            Object obj = c.newInstance();
        	Object obj = SpringBeansUtils.getBean(beanName);
        	if(obj==null)
        	{
        		throw new NoSuchBeanDefinitionException(beanName);
        	}
        	log.info("jesque job is start,{}",this.toString());
            if (methodParamTypes == null || methodParamTypes.size() == 0) {
                Method method = obj.getClass().getMethod(methodName);
                result = method.invoke(obj);
            } else {
                Class<?>[] parameterTypes = new Class<?>[methodParamTypes.size()];
                Object[] args = new Object[methodArgs.size()];
                for (int i = 0; i < methodParamTypes.size(); i++) {
                    parameterTypes[i] = Class.forName(methodParamTypes.get(i));
                    String src = ObjectMapperFactory.get().writeValueAsString(methodArgs.get(i));
                    args[i] = ObjectMapperFactory.get().readValue(src, parameterTypes[i]);
                }
                Method method = obj.getClass().getMethod(methodName, parameterTypes);
                result = method.invoke(obj, args);
            }
            log.info("jesque job is end,result ={}",result);
        } catch (SecurityException | IllegalArgumentException | ReflectiveOperationException | IOException e) {
        	log.error(e.getMessage());
        	throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public String toString() {
        return "Resolver [beanName=" + beanName + ", methodName=" + methodName + ", methodParamTypes="
                + methodParamTypes + ", methodArgs=" + methodArgs + "]";
    }

}
