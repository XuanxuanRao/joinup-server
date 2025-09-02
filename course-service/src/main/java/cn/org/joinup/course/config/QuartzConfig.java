package cn.org.joinup.course.config;

import cn.org.joinup.course.task.SignJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail signJobDetail() {
        return JobBuilder.newJob(SignJob.class)
                .withIdentity("signJob", "courseJobGroup")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger signJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(signJobDetail())
                .withIdentity("signJobTrigger", "courseJobTriggerGroup")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 4 * * ?"))
                .build();
    }

}
