package com.melnyk.profitsoft_2.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for logging service method calls annotated with {@link LogServiceMethod}.
 */
@Component
@Aspect
@Slf4j
public class LogServiceMethodAspect {

    @Around("@annotation(logServiceMethod)")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint, LogServiceMethod logServiceMethod) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String shortMethodName = joinPoint.getSignature().toShortString();

        String type = matchType(methodName, "SERVICE");
        boolean logArgs = logServiceMethod.logArgs();

        if (logArgs && log.isDebugEnabled()) {
            log.debug("[{}] {} called with args: {}", type, shortMethodName, Arrays.toString(joinPoint.getArgs()));
        } else {
            log.info("[{}] {} called", type, shortMethodName);
        }

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long time = System.currentTimeMillis() - start;

            log.info("[{}] {} completed in {} ms", type, shortMethodName, time);

            return result;
        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;
            log.error("[{}] {} failed after {} ms: {}", type, shortMethodName, time, e.getMessage());
            throw e;
        }
    }

    private String matchType(String methodName, String defaultType) {
        if (methodName.matches("^(get|search|generateReport).*$")) {
            return "GET";
        }
        if (methodName.matches("^(create|upload).*$")) {
            return "CREATE";
        }
        if (methodName.matches("^(update).*$")) {
            return "UPDATE";
        }
        if (methodName.matches("^(delete).*$")) {
            return "DELETE";
        }
        return defaultType;
    }

}
