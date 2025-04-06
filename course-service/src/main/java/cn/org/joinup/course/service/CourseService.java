package cn.org.joinup.course.service;

import cn.org.joinup.common.result.PageQuery;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.course.domain.ScheduleVO;
import cn.org.joinup.course.domain.po.SignLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;

public interface CourseService extends IService<SignLog> {
    Result<ScheduleVO> list(LocalDate date);

    Result<Void> sign(Integer courseId);

    Result<PageResult<SignLog>> query(PageQuery query, String studentId);
}
