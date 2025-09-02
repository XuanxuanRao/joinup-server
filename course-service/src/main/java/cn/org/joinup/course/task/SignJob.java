package cn.org.joinup.course.task;

import cn.org.joinup.course.domain.po.AutoSignTask;
import cn.org.joinup.course.enums.SignTaskStatus;
import cn.org.joinup.course.service.IAutoSignTaskService;
import cn.org.joinup.course.util.SignTaskScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
@RequiredArgsConstructor
public class SignJob extends QuartzJobBean {

    private final IAutoSignTaskService signTaskService;
    private final SignTaskScheduler signTaskScheduler;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        log.info("Running daily job to add sign tasks into mq ...");
        signTaskService.lambdaQuery()
                .eq(AutoSignTask::getStatus, SignTaskStatus.RUNNING)
                .list()
                .forEach(signTaskScheduler::setDelaySignTask);
        log.info("Daily scheduling job completed.");
    }
}
