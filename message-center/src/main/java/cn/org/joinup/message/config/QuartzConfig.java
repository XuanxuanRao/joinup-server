package cn.org.joinup.message.config;

import cn.org.joinup.message.task.RateFetchJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Value("${exchange.monitor.fetch-rate-cron:0 0 10 * * ?}")
    private String fetchRateCron;

    @Bean
    public JobDetail fetchRateJobDetail() {
        return JobBuilder.newJob(RateFetchJob.class)
                .withIdentity("rateFetchJob", "messageJobGroup")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger fetchRateJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(fetchRateJobDetail())
                .withIdentity("rateFetchJobTrigger", "messageJobTriggerGroup")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(fetchRateCron))
                .build();
    }

}
