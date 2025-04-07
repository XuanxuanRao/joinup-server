package cn.org.joinup.course.service;

import cn.org.joinup.common.result.PageQuery;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.course.domain.vo.ScheduleVO;
import cn.org.joinup.course.domain.po.SignLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;

public interface ICourseService extends IService<SignLog> {
    Result<ScheduleVO> list(LocalDate date);

    ScheduleVO getScheduleByDate(String studentId, LocalDate date);

    Result<Void> sign(Integer courseScheduleId);

    Result<PageResult<SignLog>> query(PageQuery query, String studentId);
}
