package io.openexchange.jobs;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Job {
    String parallelism() default "";
    String delayInterval() default "0";
    String delayIntervalTimeUnit() default "SECONDS";
    String repeatInterval() default "1";
    String repeatIntervalTimeUnit() default "MINUTES";
}
