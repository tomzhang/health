package com.dachen.commons.asyn.event.listener;

import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.dachen.commons.asyn.event.EcEvent;
import com.dachen.commons.asyn.event.EventType;
import com.dachen.commons.asyn.event.annotation.EcEventListener;
import com.dachen.commons.asyn.event.annotation.EcEventMapping;

@Component("eventListenerConfigurer")
public class ListenerConfigurer implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ListenerConfigurer.class);
    private ApplicationContext applicationContext;
    @Autowired
    @Qualifier("EventListenerFactory")
    private ListenerFactory listenerFactory;

    @PostConstruct
    private void init() throws Exception {
        if (applicationContext != null) {
            Map<String, Object> handlerMap = applicationContext.getBeansWithAnnotation(EcEventListener.class);
            for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
                Class<?> clz = entry.getValue().getClass();
                if (clz.isAnnotationPresent(EcEventListener.class)) {
                    Method[] methods = clz.getDeclaredMethods();
                    Object linstener = entry.getValue();
                    for (Method m : methods) {
                        if (m.isAnnotationPresent(EcEventMapping.class)) {
                            Class<?>[] parameterTypes = m.getParameterTypes();
                            if (parameterTypes.length == 1 && EcEvent.class.equals(parameterTypes[0])) {
                                EcEventMapping eventMapping = m.getAnnotation(EcEventMapping.class);
                                for (EventType eventType : eventMapping.type()) {
                                    logger.info("add linstener " + clz + "." + m.getName() + " type:" + eventType.name());
                                    listenerFactory.addListener(eventType, linstener, m);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
