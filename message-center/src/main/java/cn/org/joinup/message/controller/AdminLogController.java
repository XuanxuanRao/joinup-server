package cn.org.joinup.message.controller;

import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.domain.po.LogEntry;
import cn.org.joinup.message.service.ILogEntryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/admin/log")
@RequiredArgsConstructor
@Api(tags = "系统日志管理")
public class AdminLogController {

    private final ILogEntryService logEntryService;

    @ApiOperation("查询系统日志")
    @GetMapping("/list")
    public Result<PageResult<LogEntry>> query(@RequestParam(required = false) String path,
                                               @RequestParam(required = false) String method,
                                               @RequestParam(required = false) Long userId,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                               @RequestParam Integer pageSize,
                                               @RequestParam Integer pageNumber) {
        return Result.success(logEntryService.pageQuery(path, method, userId, startTime, endTime, pageSize, pageNumber));
    }

}
