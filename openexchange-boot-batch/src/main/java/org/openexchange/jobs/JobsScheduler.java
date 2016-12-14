package org.openexchange.jobs;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class JobsScheduler implements EmbeddedValueResolverAware {
    private static final Logger logger = LoggerFactory.getLogger(JobsScheduler.class);
    private static final String PROPERTY_PREFIX = "${";
    private static final String PROPERTY_SUFFIX = "}";
    private static final String PROPERTY_OPTION = ":";

    private final ScheduledExecutorService scheduledExecutorService;
    private final ApplicationContext applicationContext;

    private StringValueResolver stringValueResolver;

    @Autowired
    public JobsScheduler(ScheduledExecutorService scheduledExecutorService,
                         ApplicationContext applicationContext) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        this.stringValueResolver = stringValueResolver;
    }

    private String resolvePlaceholder(String property) {
        if (property.startsWith(PROPERTY_PREFIX) && property.endsWith(PROPERTY_SUFFIX)) {
            if (property.contains(PROPERTY_OPTION)) {
                return stringValueResolver.resolveStringValue(property);
            }
            String value = applicationContext.getEnvironment().getProperty(property.replace(PROPERTY_PREFIX, "").replace(PROPERTY_SUFFIX, ""));
            if (value == null) {
                throw new BeanCreationException(String.format("Property %s not found", property));
            }
            return value;
        }
        return property;
    }

    @PostConstruct
    private void postConstruct() {
        Map<String, Object> producerBeans = applicationContext.getBeansWithAnnotation(Configuration.class);
        producerBeans.forEach((s, o) -> {
            for (Method m : MethodUtils.getMethodsWithAnnotation(o.getClass(), Job.class)) {
                Job job = m.getAnnotation(Job.class);

                String parallelismString = resolvePlaceholder(job.parallelism());
                int parallelism = StringUtils.isEmpty(parallelismString) ? Runtime.getRuntime().availableProcessors() : Integer.valueOf(parallelismString);

                String delayIntervalString = resolvePlaceholder(job.delayInterval());
                long delayInterval = StringUtils.isEmpty(delayIntervalString) ? 0 : Long.valueOf(delayIntervalString);
                String delayIntervalTimeUnitString = resolvePlaceholder(job.delayIntervalTimeUnit());
                TimeUnit delayIntervalTimeUnit = StringUtils.isEmpty(delayIntervalTimeUnitString) ? TimeUnit.SECONDS : TimeUnit.valueOf(delayIntervalTimeUnitString);

                String repeatIntervalString = resolvePlaceholder(job.repeatInterval());
                long repeatInterval = StringUtils.isEmpty(repeatIntervalString) ? 0 : Long.valueOf(repeatIntervalString);
                String repeatIntervalTimeUnitString = resolvePlaceholder(job.repeatIntervalTimeUnit());
                TimeUnit repeatIntervalTimeUnit = StringUtils.isEmpty(repeatIntervalTimeUnitString) ? TimeUnit.SECONDS : TimeUnit.valueOf(repeatIntervalTimeUnitString);

                apply(o, m, parallelism, delayInterval, delayIntervalTimeUnit, repeatInterval, repeatIntervalTimeUnit);
            }
        });
    }

    protected void apply(Object o, Method m,
                         int parallelism,
                         long delayInterval, TimeUnit delayIntervalTimeUnit,
                         long repeatInterval, TimeUnit repeatIntervalTimeUnit) {
        for (int i = 0; i < parallelism; i++) {
            scheduledExecutorService.schedule(
                    new Restartable(o, m,
                            scheduledExecutorService,
                            repeatInterval,
                            repeatIntervalTimeUnit),
                    delayInterval,
                    delayIntervalTimeUnit);
        }
    }

    private static class Restartable implements Runnable {
        private final Object o;
        private final Method m;
        private final ScheduledExecutorService scheduledExecutorService;
        private final long restartInterval;
        private final TimeUnit restartTimeUnit;

        Restartable(Object o, Method m, ScheduledExecutorService scheduledExecutorService, long restartInterval, TimeUnit restartTimeUnit) {
            this.o = o;
            this.m = m;
            this.scheduledExecutorService = scheduledExecutorService;
            this.restartInterval = restartInterval;
            this.restartTimeUnit = restartTimeUnit;
        }

        @Override
        public void run() {
            try {
                m.invoke(o);
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            } finally {
                scheduledExecutorService.schedule(
                        new Restartable(o, m, scheduledExecutorService, restartInterval, restartTimeUnit),
                        restartInterval, restartTimeUnit);
            }
        }
    }
}
