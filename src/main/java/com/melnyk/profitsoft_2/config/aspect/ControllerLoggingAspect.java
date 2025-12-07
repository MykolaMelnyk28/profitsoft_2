package com.melnyk.profitsoft_2.config.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Aspect for logging REST controller requests.
 */
@Component
@Aspect
@Slf4j
public class ControllerLoggingAspect {

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        long start = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long time = System.currentTimeMillis() - start;

            log.info("[HTTP] {} {} -> {} ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    time
            );
        }
    }

}