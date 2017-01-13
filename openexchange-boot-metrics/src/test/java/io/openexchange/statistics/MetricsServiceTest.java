package io.openexchange.statistics;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.export.Exporter;
import org.springframework.boot.actuate.metrics.rich.RichGaugeRepository;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MetricsServiceTest.class)
@SpringBootApplication
@TestPropertySource(locations = "classpath:test.properties")
public class MetricsServiceTest {
    private final static String COUNTER_OPENEXCHANGE_BENCHMARK_SERVER = "counter.openexchange.benchmark.server.";
    @Autowired
    private RichGaugeRepository metricRepository;
    @Autowired
    private Exporter export;

    @Before
    public void setUp() throws Exception {
        metricRepository.reset(COUNTER_OPENEXCHANGE_BENCHMARK_SERVER + "total");
    }

    @Test
    public void testMetrics() throws Exception {
        metricRepository.set(new Delta<Number>(COUNTER_OPENEXCHANGE_BENCHMARK_SERVER + "total", 1));
        metricRepository.set(new Delta<Number>(COUNTER_OPENEXCHANGE_BENCHMARK_SERVER + "total", 2));
        metricRepository.set(new Delta<Number>(COUNTER_OPENEXCHANGE_BENCHMARK_SERVER + "total", 3));

        Assert.assertEquals(3L, metricRepository.findOne(COUNTER_OPENEXCHANGE_BENCHMARK_SERVER + "total").getCount());
        Assert.assertEquals(3L, metricRepository.findOne(COUNTER_OPENEXCHANGE_BENCHMARK_SERVER + "total").getValue(), 0.0);
        Assert.assertEquals(1.0, metricRepository.findOne(COUNTER_OPENEXCHANGE_BENCHMARK_SERVER + "total").getMin(), 0.0);
        Assert.assertEquals(3.0, metricRepository.findOne(COUNTER_OPENEXCHANGE_BENCHMARK_SERVER + "total").getMax(), 0.0);
        Assert.assertEquals(2.0, metricRepository.findOne(COUNTER_OPENEXCHANGE_BENCHMARK_SERVER + "total").getAverage(), 0.0);

        export.export();
    }

}
