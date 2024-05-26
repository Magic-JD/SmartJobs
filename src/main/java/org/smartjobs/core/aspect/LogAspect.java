package org.smartjobs.core.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
@Slf4j
@Profile({"dev", "prod"})
public class LogAspect {

    @Pointcut("execution(* org.smartjobs.core.ports.*.*.*(..))")
    private void portPlugs() {
    }

    @Around(value = "portPlugs()")
    public Object logPortCalledBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        if (log.isDebugEnabled()) {
            log.debug(">> {}() - {}", methodName, Arrays.toString(args));
        }
        Object result = joinPoint.proceed();
        if (log.isDebugEnabled()) {
            log.debug("<< {}() - {}", methodName, result);
        }
        return result;
    }
}
