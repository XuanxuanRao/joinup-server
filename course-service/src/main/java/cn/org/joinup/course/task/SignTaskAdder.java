package cn.org.joinup.course.task;

import cn.org.joinup.course.enums.SignTaskStatus;
import cn.org.joinup.course.service.IAutoSignTaskService;
import cn.org.joinup.course.util.SignTaskScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SignTaskAdder {

    private final IAutoSignTaskService signTaskService;
    private final SignTaskScheduler signTaskScheduler;

    // 每天凌晨4点执行
     @Scheduled(cron = "0 0 4 * * ?")
    public void scheduleDailyTask() {
        log.info("Running daily scheduling job...");
        signTaskService.query()
                .eq("status", SignTaskStatus.RUNNING)
                .list()
                .forEach(signTaskScheduler::setDelaySignTask);
        log.info("Daily scheduling job completed.");
    }

}
