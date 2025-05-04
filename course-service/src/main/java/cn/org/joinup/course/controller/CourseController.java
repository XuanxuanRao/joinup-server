package cn.org.joinup.course.controller;

import cn.org.joinup.common.result.PageQuery;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.course.domain.vo.ScheduleVO;
import cn.org.joinup.course.domain.po.SignLog;
import cn.org.joinup.course.service.ICourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "课程查询签到接口")
public class CourseController {

    @Resource
    private ICourseService courseService;

    @PostMapping("/sign")
    @ApiOperation("课程签到")
    public Result<Void> signClass(@RequestParam Integer courseScheduleId){
        return courseService.sign(courseScheduleId);
    }

    @GetMapping("/list")
    @ApiOperation("获得date日的课表")
    public Result<ScheduleVO> getClassList(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return courseService.list(date);
    }

    @PostMapping("/log")
    @ApiOperation("获得学生的签到记录")
    public Result<PageResult<SignLog>> getSignLog(@Validated @RequestBody PageQuery query){
        return courseService.query(query);
    }

}
