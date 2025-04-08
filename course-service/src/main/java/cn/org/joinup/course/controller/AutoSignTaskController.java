package cn.org.joinup.course.controller;

import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.course.domain.dto.AddSignTaskDTO;
import cn.org.joinup.course.domain.po.AutoSignTask;
import cn.org.joinup.course.enums.SignTaskStatus;
import cn.org.joinup.course.service.IAutoSignTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/course/task")
@RequiredArgsConstructor
@Api(tags = "自动签到任务接口")
public class AutoSignTaskController {

    private final IAutoSignTaskService signTaskService;
    private final UserClient userClient;

    @GetMapping("/list")
    @ApiOperation("获取签到任务列表")
    public Result<List<AutoSignTask>> list(@RequestParam SignTaskStatus status) {
        UserDTO userInfo = userClient.getUserInfo().getData();

        List<AutoSignTask> tasks = signTaskService.lambdaQuery()
                .eq(AutoSignTask::getStatus, status.getValue())
                .eq(AutoSignTask::getStudentId, userInfo.getStudentId())
                .list();

        return Result.success(tasks);
    }

    @PostMapping("/add")
    @ApiOperation("添加签到任务")
    public Result<Void> add(@RequestBody AddSignTaskDTO addSignTaskDTO) {
        return signTaskService.addTask(addSignTaskDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除签到任务")
    public Result<Void> delete(@PathVariable Long id) {
        return signTaskService.removeTask(id);
    }


}
