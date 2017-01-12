package io.openexchange.statistics.monitors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerMonitor {
    private final static String COUNTER_OPENEXCHANGE_SERVICE= "openexchange.api";
    private final MetricWriter metricWriter;

    @Autowired
    public ControllerMonitor(MetricWriter metricWriter) {
        this.metricWriter = metricWriter;
    }

    @Pointcut("execution(* *..*()) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMethods() {
    }

    @Around("requestMethods()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        String metricPrefix = COUNTER_OPENEXCHANGE_SERVICE + "." + pjp.getSignature().getDeclaringType().getName() + "." + pjp.getSignature().getName();
        long start = System.currentTimeMillis();
        Object output = pjp.proceed();
        metricWriter.set(new Metric<Number>(metricPrefix + ".request.time", System.currentTimeMillis() - start));
        return output;
    }
}