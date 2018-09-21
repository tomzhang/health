package com.dachen.commons.asyn.event.listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.dachen.commons.asyn.event.EventType;

@Component("EventListenerFactory")
public class ListenerFactory {
    private List<ListenerItem> listeners = new ArrayList<ListenerItem>();

    private ListenerFactory() {
    }

    void addListener(EventType type, Object listener, Method method) {
        ListenerItem item = new ListenerItem();
        item.type = type;
        item.listener = listener;
        item.method = method;
        listeners.add(item);
    }

    List<ListenerItem> getListener(EventType type) {
        List<ListenerItem> rtn = new ArrayList<ListenerItem>();
        for (ListenerItem item : listeners) {
            if (item.type == type)
                rtn.add(item);
        }
        return rtn;
    }

    @PreDestroy
    void clear() {
        listeners.clear();
    }

}
