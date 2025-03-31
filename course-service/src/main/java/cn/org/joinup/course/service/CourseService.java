package cn.org.joinup.course.service;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.course.domain.ScheduleVO;

import java.time.LocalDate;

public interface CourseService {
    Result<ScheduleVO> list(LocalDate date);

    Result<Void> sign(Integer courseId);
}
