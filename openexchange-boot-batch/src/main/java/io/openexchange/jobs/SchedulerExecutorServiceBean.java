package io.openexchange.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class SchedulerExecutorServiceBean {
    private final static Logger logger = LoggerFactory.getLogger(SchedulerExecutorServiceBean.class);
    private ScheduledExecutorService scheduledExecutorService;

    @PostConstruct
    public void start() {
        scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    }

    @PreDestroy
    public void stop() {
        scheduledExecutorService.shutdown();
        try {
            if (!scheduledExecutorService.awaitTermination(100, TimeUnit.MICROSECONDS)) {
                if (!scheduledExecutorService.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                    logger.error("Pool is not shutting down!!!");
                }
            }
        } catch (InterruptedException e) {
            scheduledExecutorService.shutdownNow();
            logger.error("Shutdown has been interrupted", e);
        }
    }

    @Bean
    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
}
