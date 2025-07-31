package com.line.line_demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventMapping {
    int DEFAULT_PRIORITY_VALUE = -1;
    int DEFAULT_PRIORITY_FOR_EVENT_IFACE = 0;
    int DEFAULT_PRIORITY_FOR_STRING = 1;
    int DEFAULT_PRIORITY_FOR_IFACE = 10;
    int DEFAULT_PRIORITY_FOR_CLASS = 100;
    int DEFAULT_PRIORITY_FOR_PARAMETRIZED_TYPE = 1000;

    int priority() default -1;
}