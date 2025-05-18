package cn.org.joinup.message.controller;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.domain.po.LogEntry;
import cn.org.joinup.message.service.ILogEntryService;
import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/admin/log")
@RequiredArgsConstructor
@Api(tags = "系统日志管理")
public class AdminLogController {

    private final ILogEntryService iLogEntryService;

    @GetMapping("/count")
    public Result<Long> count() {
        Long count = iLogEntryService.count();
        return Result.success(count);
    }

    @GetMapping("/stats/path")
    @ApiOperation("按接口路径统计调用次数")
    public Result<List<Map<String, Object>>> statsByPath(@RequestParam(required = false) String start,   // YYYY-MM-DD
                                                         @RequestParam(required = false) String end) {

        // 1) 构造查询包装器
        QueryWrapper<LogEntry> qw = new QueryWrapper<>();

        if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
            qw.between("create_time", start + " 00:00:00", end + " 23:59:59");
        }

        qw.select("path", "COUNT(*) AS total")   // 只查两列
                .groupBy("path");                      // group by path

        List<Map<String, Object>> list = iLogEntryService.listMaps(qw);

        return Result.success(list);
    }

    @GetMapping("/stats/user")
    @ApiOperation("按接口路径统计调用次数")
    public Result<List<Map<String, Object>>> statsByUser(@RequestParam(required = false) String start,   // YYYY-MM-DD
                                                         @RequestParam(required = false) String end) {

        QueryWrapper<LogEntry> qw = new QueryWrapper<>();

        if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
            qw.between("create_time", start + " 00:00:00", end + " 23:59:59");
        }

        qw.select("user_id AS userId", "COUNT(*) AS total").groupBy("user_id");

        List<Map<String, Object>> list = iLogEntryService.listMaps(qw);
        return Result.success(list);
    }



}
