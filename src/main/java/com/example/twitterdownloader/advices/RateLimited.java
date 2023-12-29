package com.example.twitterdownloader.advices;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimited {
//    int capacity() default 10;
//    int refillNumber() default 10;
//    int ofMinutes() default 1;
//    int initialCap() default 1;
}
