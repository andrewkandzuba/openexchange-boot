# Overview

**Scheduled and resume-able jobs framework**

The library discovers all visible methods annotated with @Job in @Configuration classes loaded into ApplicationContext and then try to schedule a repeatable tasks.

# Configuration

| Name | Default value | Description | 
|---|:---:|---|
| spring.jobs.jobs.restart.interval |1| An interval between the successful completion and the next launch of a certain job's instance execution |
| spring.jobs.jobs.restart.timeunit:MINUTES | MINUTES | An interval's time units |

     