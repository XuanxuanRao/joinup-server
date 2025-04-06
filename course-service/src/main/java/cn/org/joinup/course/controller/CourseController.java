package cn.org.joinup.course.controller;

import cn.org.joinup.common.result.PageQuery;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.course.domain.ScheduleVO;
import cn.org.joinup.course.domain.po.SignLog;
import cn.org.joinup.course.service.CourseService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    private CourseService courseService;

    @PostMapping("/sign")
    public Result<String> signClass(@RequestParam Integer courseId){
        Result<Void> result = courseService.sign(courseId);
        if (result.getCode() == 0) {
            return Result.success();
        } else {
            return Result.error("签到失败，请重试");
        }
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
