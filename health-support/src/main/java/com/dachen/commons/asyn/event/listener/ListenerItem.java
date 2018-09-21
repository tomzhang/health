package com.dachen.commons.asyn.event.listener;

import java.lang.reflect.Method;

import com.dachen.commons.asyn.event.EventType;

class ListenerItem {
    EventType type;
    Object listener;
    Method method;
}
