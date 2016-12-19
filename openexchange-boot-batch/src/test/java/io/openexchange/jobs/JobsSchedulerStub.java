package io.openexchange.jobs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class JobsSchedulerStub extends JobsScheduler {
    private int parallelism;
    private long delayInterval;
    private TimeUnit delayIntervalTimeUnit;
    private long repeatInterval;
    private TimeUnit repeatIntervalTimeUnit;

    @Autowired
    public JobsSchedulerStub(ScheduledExecutorService scheduledExecutorService, ApplicationContext applicationContext) {
        super(scheduledExecutorService, applicationContext);
    }

    int getParallelism() {
        return parallelism;
    }

    long getDelayInterval() {
        return delayInterval;
    }

    TimeUnit getDelayIntervalTimeUnit() {
        return delayIntervalTimeUnit;
    }

    long getRepeatInterval() {
        return repeatInterval;
    }

    TimeUnit getRepeatIntervalTimeUnit() {
        return repeatIntervalTimeUnit;
    }

    @Override
    protected void apply(Object o, Method m, int parallelism,
                         long delayInterval, TimeUnit delayIntervalTimeUnit,
                         long repeatInterval, TimeUnit repeatIntervalTimeUnit) {
        this.parallelism = parallelism;
        this.delayInterval = delayInterval;
        this.delayIntervalTimeUnit = delayIntervalTimeUnit;
        this.repeatInterval = repeatInterval;
        this.repeatIntervalTimeUnit = repeatIntervalTimeUnit;
    }

}
