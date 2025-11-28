package cn.org.joinup.user.controller;

import cn.org.joinup.common.result.Result;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/test")
@Profile("!prod")
public class HelloController {

    @GetMapping("/hello")
    public Result<Map<String, String>> hello() {
        return Result.success(Map.of("title", "hello", "demand", "world"));
    }

}
