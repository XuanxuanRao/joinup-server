package cn.org.joinup.course.controller;

import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.course.domain.dto.AddSignTaskDTO;
import cn.org.joinup.course.domain.po.AutoSignTask;
import cn.org.joinup.course.enums.SignTaskStatus;
import cn.org.joinup.course.service.IAutoSignTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/course/task")
@RequiredArgsConstructor
public class AutoSignTaskController {

    private final IAutoSignTaskService signTaskService;
    private final UserClient userClient;

    @GetMapping()
    public Result<List<AutoSignTask>> list(SignTaskStatus status) {
        UserDTO userInfo = userClient.getUserInfo().getData();

        List<AutoSignTask> tasks = signTaskService.lambdaQuery()
                .eq(AutoSignTask::getStatus, status.getValue())
                .eq(AutoSignTask::getStudentId, userInfo.getStudentId())
                .list();

        return Result.success(tasks);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody AddSignTaskDTO addSignTaskDTO) {
        return signTaskService.addTask(addSignTaskDTO);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return signTaskService.removeTask(id);
    }


}
