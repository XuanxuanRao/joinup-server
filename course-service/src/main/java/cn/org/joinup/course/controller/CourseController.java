package cn.org.joinup.course.controller;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.course.domain.ScheduleVO;
import cn.org.joinup.course.service.CourseService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.format.annotation.DateTimeFormat;
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
        courseService.sign(courseId);
        return Result.success("签到成功");
    }

    @GetMapping("/list")
    public Result<ScheduleVO> getClassList(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate date){
        return courseService.list(date);
    }

}
