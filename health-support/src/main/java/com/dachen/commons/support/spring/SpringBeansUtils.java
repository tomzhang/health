package com.dachen.commons.support.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringBeansUtils implements ApplicationContextAware {

	private static ApplicationContext context;

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) context.getBean(name);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBeane(Class<?> requiredType) {
		return (T) context.getBean(requiredType);
	}

	public static ApplicationContext getContext() {
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		context = ctx;
	}

}
