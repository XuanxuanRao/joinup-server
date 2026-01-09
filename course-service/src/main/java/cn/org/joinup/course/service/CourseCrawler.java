package cn.org.joinup.course.service;

import cn.org.joinup.course.domain.vo.ScheduleVO;

import java.time.LocalDate;

public interface CourseCrawler {

    String supportChannel();

    ScheduleVO getScheduleByDate(String studentId, LocalDate date);

    Boolean signCourse(String studentId, Integer courseScheduleId);
}
