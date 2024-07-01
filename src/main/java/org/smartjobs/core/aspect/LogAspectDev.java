package org.smartjobs.core.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component
@Aspect
@Slf4j
@Profile("dev")
public class LogAspectDev {

    @Pointcut("within(org.smartjobs..*)")
    private void portPlugs() {
    }

    @Around(value = "portPlugs()")
    public Object logPortCalledBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        String className = Arrays.stream(joinPoint.getSignature().getDeclaringTypeName().split("\\.")).toList().getLast();
        String id = UUID.randomUUID().toString();
        if (log.isDebugEnabled()) {
            log.debug("{} >> {} - {}() - {}", id, className, methodName, Arrays.toString(args));
        }
        Object result = joinPoint.proceed();
        if (log.isDebugEnabled()) {
            log.debug("{} << {} - {}() - {}", id, className, methodName, result);
        }
        return result;
    }
}
