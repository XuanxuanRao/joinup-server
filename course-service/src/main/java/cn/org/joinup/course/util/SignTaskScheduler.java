package cn.org.joinup.course.util;

import cn.org.joinup.course.constants.MQConstants;
import cn.org.joinup.course.domain.Course;
import cn.org.joinup.course.domain.po.AutoSignTask;
import cn.org.joinup.course.domain.vo.ScheduleVO;
import cn.org.joinup.course.enums.SignStatus;
import cn.org.joinup.course.service.ICourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
@RequiredArgsConstructor
public class SignTaskScheduler {
    private final ICourseService courseService;
    private final RabbitTemplate rabbitTemplate;

    public void setDelaySignTask(AutoSignTask autoSignTask) {
        Integer courseId = autoSignTask.getCourseId();
        String studentId = autoSignTask.getStudentId();

        List<Course> target = Optional.ofNullable(courseService.getScheduleByDate(studentId, LocalDate.now()))
                .map(ScheduleVO::getResult)
                .orElseGet(List::of)
                .stream()
                .filter(course -> Objects.equals(course.getCourseId(), courseId))
                .collect(Collectors.toList());

        target.forEach(course -> {
            if (course.getSignStatus() == SignStatus.SIGNED) {
                return;
            }

            // 添加签到任务
            rabbitTemplate.convertAndSend(MQConstants.DELAY_EXCHANGE, MQConstants.DELAY_SIGN_KEY,
                    autoSignTask.getId(),
                    message -> {
                        message.getMessageProperties().setDelay(calcDelayTime(course));
                        return message;
                    });
        });
    }

    /**
     * 计算打卡延迟时间
     * @param course 课程信息
     * @return 打卡延迟时间，以毫秒为单位
     */
    private Integer calcDelayTime(Course course) {
        LocalDateTime begin = course.getClassBeginTime();
        LocalDateTime now = LocalDateTime.now();
        // 计算延迟时间, 在课程开始前4分钟打卡
        long delay = begin.minusMinutes(4).toInstant(java.time.ZoneOffset.of("+8")).toEpochMilli() - now.toInstant(java.time.ZoneOffset.of("+8")).toEpochMilli();
        return Math.toIntExact(delay > 0 ? delay : 0);
    }
}
