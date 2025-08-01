package com.line.line_demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Target({ElementType.TYPE})
@Component
@Retention(RetentionPolicy.RUNTIME)
public @interface LineMessageHandler {
}