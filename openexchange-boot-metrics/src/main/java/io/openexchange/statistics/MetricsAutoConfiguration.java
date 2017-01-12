package io.openexchange.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.metrics.export.Exporter;
import org.springframework.boot.actuate.metrics.rich.InMemoryRichGaugeRepository;
import org.springframework.boot.actuate.metrics.rich.RichGaugeRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class MetricsAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MetricsAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(RichGaugeRepository.class)
    public RichGaugeRepository richGaugeRepository() {
        return new InMemoryRichGaugeRepository();
    }

    @Bean
    @ConditionalOnMissingBean(Exporter.class)
    public Exporter exporter(RichGaugeRepository richGaugeRepository) {
        return () -> richGaugeRepository.findAll().forEach(m -> {
            logger.info("Reporting metric {}={}", m.getName() + ".value", m.getValue());
            logger.info("Reporting metric {}={}", m.getName() + ".count", m.getCount());
            logger.info("Reporting metric {}={}", m.getName() + ".min", m.getMin());
            logger.info("Reporting metric {}={}", m.getName() + ".max", m.getMax());
            logger.info("Reporting metric {}={}", m.getName() + ".average", m.getAverage());
        });
    }
}
