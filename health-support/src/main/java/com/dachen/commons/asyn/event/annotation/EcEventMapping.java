package com.dachen.commons.asyn.event.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dachen.commons.asyn.event.EventType;

@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EcEventMapping {
    public abstract EventType[] type();
}
