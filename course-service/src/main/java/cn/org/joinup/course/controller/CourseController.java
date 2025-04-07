package cn.org.joinup.course.controller;

import cn.org.joinup.common.result.PageQuery;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.course.domain.vo.ScheduleVO;
import cn.org.joinup.course.domain.po.SignLog;
import cn.org.joinup.course.service.ICourseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    private ICourseService courseService;

    @PostMapping("/sign")
    public Result<Void> signClass(@RequestParam Integer courseScheduleId){
        return courseService.sign(courseScheduleId);
    }

    @GetMapping("/list")
    public Result<ScheduleVO> getClassList(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return courseService.list(date);
    }

    @GetMapping("/log/{studentId}")
    public Result<PageResult<SignLog>> getSignLog(@PathVariable String studentId, @Validated @RequestBody PageQuery query){
        return courseService.query(query, studentId);
    }

}
