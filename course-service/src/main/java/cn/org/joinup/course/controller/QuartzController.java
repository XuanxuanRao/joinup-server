package cn.org.joinup.course.controller;

import cn.org.joinup.common.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/course/system/quartz")
@RequiredArgsConstructor
@Slf4j
public class QuartzController {

    private final SchedulerFactoryBean schedulerFactoryBean;

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @GetMapping("/triggers")
    public Result<List<QuartzTriggerDTO>> getTriggers() throws SchedulerException {
        List<QuartzTriggerDTO> result = new ArrayList<>();
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.getTriggerGroupNames().forEach(triggerGroupName -> {
            try {
                scheduler.getTriggerKeys(GroupMatcher.groupEquals(triggerGroupName)).forEach(triggerKey -> {
                    QuartzTriggerDTO quartzTriggerDTO = new QuartzTriggerDTO();
                    try {
                        Trigger trigger = scheduler.getTrigger(triggerKey);
                        Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
                        quartzTriggerDTO.setState(triggerState.name());
                        quartzTriggerDTO.setName(triggerKey.getName());
                        quartzTriggerDTO.setGroupName(triggerGroupName);
                        quartzTriggerDTO.setJobName(trigger.getJobKey().getName());
                        quartzTriggerDTO.setJobDataMap(scheduler.getJobDetail(trigger.getJobKey()).getJobDataMap());
                        quartzTriggerDTO.setPrevFireTime(
                                trigger.getPreviousFireTime() == null ? "" :
                                        trigger.getPreviousFireTime().toInstant()
                                                .atZone(ZoneId.systemDefault())
                                                .format(formatter));
                        quartzTriggerDTO.setNextFireTime(
                                trigger.getNextFireTime() == null ? "" :
                                        trigger.getNextFireTime().toInstant()
                                                .atZone(ZoneId.systemDefault())
                                                .format(formatter));
                        if (trigger instanceof SimpleTriggerImpl) {
                            quartzTriggerDTO.setRepeatInterval(((SimpleTriggerImpl) trigger).getRepeatInterval());
                        } else if (trigger instanceof CronTriggerImpl) {
                            quartzTriggerDTO.setCronExpression(((CronTriggerImpl) trigger).getCronExpression());
                        }
                        result.add(quartzTriggerDTO);
                    } catch (SchedulerException e) {
                        log.error("Got error when get triggers.", e);
                    }
                });
            } catch (SchedulerException e) {
                log.error("Got error when get triggers.", e);
            }
        });
        return Result.success(result);
    }

    @PutMapping("/triggers/{triggerGroupName}/{triggerName}/update-cron-expression")
    @ApiOperation("update cron expression of a trigger")
    public Result<Void> modifyCronExpression(@PathVariable String triggerGroupName,
                                            @PathVariable String triggerName,
                                            @RequestParam String cronExpression)
            throws SchedulerException, ParseException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(triggerName, triggerGroupName));
        if (!(trigger instanceof CronTriggerImpl)) {
            return Result.error("Trigger is not a cron trigger.");
        }
        CronTriggerImpl cronTrigger = (CronTriggerImpl) trigger;
        scheduler.pauseTrigger(TriggerKey.triggerKey(triggerName, triggerGroupName));
        cronTrigger.setCronExpression(cronExpression);
        scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName, triggerGroupName), cronTrigger);
        scheduler.resumeTrigger(TriggerKey.triggerKey(triggerName, triggerGroupName));
        return Result.success();
    }


    @Data
    public static class QuartzTriggerDTO {
        private String name;
        private String groupName;
        private String state;
        private String jobName;
        private String prevFireTime;
        private String nextFireTime;
        private Long repeatInterval;
        private String cronExpression;
        private JobDataMap jobDataMap;
    }

}
