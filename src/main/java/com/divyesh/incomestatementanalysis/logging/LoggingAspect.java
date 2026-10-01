package com.divyesh.incomestatementanalysis.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    // Pointcut targeting all REST Controllers
    @Pointcut(LoggingConstants.POINTCUT_CONTROLLERS)
    public void controllerMethods() {}

    // Pointcut targeting all Services
    @Pointcut(LoggingConstants.POINTCUT_SERVICES)
    public void serviceMethods() {}

    // Pointcut targeting OCR and AI/Bedrock operations
    @Pointcut(LoggingConstants.POINTCUT_AI_AND_OCR)
    public void aiAndOcrMethods() {}

    /**
     * Around advice to log method entry, performance execution time, exit, and failures.
     */
    @Around("controllerMethods() || serviceMethods() || aiAndOcrMethods()")
    public Object logExecutionDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // 1. Log method entry
        log.info(LoggingConstants.LOG_ENTRY, className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();

        try {
            // 2. Proceed with target method execution
            Object result = joinPoint.proceed();

            long elapsedTime = System.currentTimeMillis() - startTime;

            // 3. Log method exit and duration
            log.info(LoggingConstants.LOG_EXIT, className, methodName, elapsedTime);

            return result;

        } catch (Throwable throwable) {
            long elapsedTime = System.currentTimeMillis() - startTime;

            // 4. Log exception details
            log.error(LoggingConstants.LOG_ERROR, className, methodName,
                    throwable.getClass().getSimpleName(), throwable.getMessage());

            throw throwable;
        }
    }
}