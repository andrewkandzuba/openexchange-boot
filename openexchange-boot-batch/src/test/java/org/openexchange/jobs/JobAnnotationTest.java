package org.openexchange.jobs;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

public class JobAnnotationTest {

    @Test
    public void testAnnotationParams() throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                CorrectDefaultValueJobConfiguration.class,
                JobsSchedulerStub.class,
                SchedulerExecutorServiceBean.class
        );
        JobsSchedulerStub jobsScheduler = applicationContext.getBean(JobsSchedulerStub.class);
        Assert.assertEquals(4, jobsScheduler.getParallelism());
        Assert.assertEquals(10, jobsScheduler.getDelayInterval());
        Assert.assertEquals(TimeUnit.MILLISECONDS, jobsScheduler.getDelayIntervalTimeUnit());
        Assert.assertEquals(20, jobsScheduler.getRepeatInterval());
        Assert.assertEquals(TimeUnit.MINUTES, jobsScheduler.getRepeatIntervalTimeUnit());
    }

    @Test(expected = BeanCreationException.class)
    public void testAnnotationParamsNotFound() throws Exception {
        new AnnotationConfigApplicationContext(
                NoParallelismPropertySpecifiedJobConfiguration.class,
                JobsSchedulerStub.class,
                SchedulerExecutorServiceBean.class
        );
    }

    @Test
    public void testParallelismPropertySpecified() throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                ParallelismPropertySpecifiedJobConfiguration.class,
                JobsSchedulerStub.class,
                SchedulerExecutorServiceBean.class
        );
        JobsSchedulerStub jobsScheduler = applicationContext.getBean(JobsSchedulerStub.class);
        Assert.assertEquals(4, jobsScheduler.getParallelism());
    }

    @Configuration
    private static class CorrectDefaultValueJobConfiguration {
        public CorrectDefaultValueJobConfiguration() {
        }

        @Job(
                parallelism = "${spring.job.parallelism:4}",
                delayInterval = "${spring.job.delay.interval:10}",
                delayIntervalTimeUnit = "${spring.job.delay.interval:MILLISECONDS}",
                repeatInterval = "${spring.job.repeat.interval:20}",
                repeatIntervalTimeUnit = "${spring.job.repeat.interval:MINUTES}"
        )
        public void runJob() {
        }
    }

    @Configuration
    private static class NoParallelismPropertySpecifiedJobConfiguration {
        public NoParallelismPropertySpecifiedJobConfiguration() {
        }

        @Job(parallelism = "${spring.job.parallelism}")
        public void runJob() {
        }
    }

    @Configuration
    private static class ParallelismPropertySpecifiedJobConfiguration {
        public ParallelismPropertySpecifiedJobConfiguration() {
            System.setProperty("spring.job.parallelism", "4");
        }

        @Job(parallelism = "${spring.job.parallelism}")
        public void runJob() {
        }
    }
}
