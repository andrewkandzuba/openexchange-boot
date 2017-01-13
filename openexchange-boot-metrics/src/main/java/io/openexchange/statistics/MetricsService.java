package io.openexchange.statistics;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.export.Exporter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = "openexchange.statistic.metrics.print.enable", havingValue = "true")
public class MetricsService {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(MetricsService.class);
    private final Exporter exporter;
    private final ScheduledExecutorService scheduler;

    @Value("${openexchange.statistic.metrics.print.rate:5000}")
    private long rate;

    @Autowired
    public MetricsService(Exporter exporter) {
        this.exporter = exporter;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @PostConstruct
    private void init(){
        scheduler.scheduleAtFixedRate(exporter::export, rate, rate, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdown();
        try {
            if(!scheduler.awaitTermination(rate, TimeUnit.MILLISECONDS)){
                scheduler.shutdownNow();
                if(!scheduler.awaitTermination(rate, TimeUnit.MILLISECONDS)){
                    logger.error("Unable to shutdown: " + scheduler);
                }
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        exporter.export();
    }
}
