package org.smartjobs.core.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LogAspect {

    @Pointcut("execution(* org.smartjobs.core.ports.*.*.*(..))")
    private void portPlugs() {
    }

    @Before(value = "portPlugs()")
    public void logPortCalled(JoinPoint joinPoint) {
        if (log.isDebugEnabled()) {
            String name = joinPoint.getSignature().getName();
            log.debug("Method {} called", name);

        }
    }
}
