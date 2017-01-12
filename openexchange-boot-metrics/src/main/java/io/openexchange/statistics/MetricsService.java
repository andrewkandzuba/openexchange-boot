package io.openexchange.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.export.Exporter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
@EnableScheduling
@ConditionalOnProperty(name = "openexchange.statistic.metrics.print.enable", havingValue = "true")
public class MetricsService {
    private final Exporter exporter;

    @Autowired
    public MetricsService(Exporter exporter) {
        this.exporter = exporter;
    }

    @Scheduled(fixedDelayString = "${openexchange.statistic.metrics.print.rate:5000}", initialDelayString = "${openexchange.statistic.metrics.print.rate:5000}")
    public void export() {
        exporter.export();
    }

    @PreDestroy
    private void destroy() {
        export();
    }
}
