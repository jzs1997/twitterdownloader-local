package com.example.twitterdownloader.advices;

import com.example.twitterdownloader.exceptions.RateLimitException;
import io.github.bucket4j.Bucket;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
public class RateLimitAspect {

    Bucket bucket = Bucket.builder()
            .addLimit(limit -> limit.capacity(600).refillGreedy(600, Duration.ofMinutes(1)).initialTokens(600))
            .build();

    @Around("@annotation(RateLimited)")
    public Object rateLimitChecking(ProceedingJoinPoint pjp) throws Throwable {
        if(bucket.tryConsume(1)){
            Object retVal = pjp.proceed();
            return retVal;
        }else{
            throw new RateLimitException();
        }
    }
}
