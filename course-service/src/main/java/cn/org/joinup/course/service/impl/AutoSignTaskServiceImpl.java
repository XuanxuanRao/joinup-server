package cn.org.joinup.course.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.course.domain.dto.AddSignTaskDTO;
import cn.org.joinup.course.domain.po.AutoSignTask;
import cn.org.joinup.course.enums.SignTaskStatus;
import cn.org.joinup.course.mapper.AutoSignTaskMapper;
import cn.org.joinup.course.service.IAutoSignTaskService;
import cn.org.joinup.course.util.SignTaskScheduler;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class AutoSignTaskServiceImpl extends ServiceImpl<AutoSignTaskMapper, AutoSignTask> implements IAutoSignTaskService {

    private final UserClient userClient;
    private final SignTaskScheduler signTaskScheduler;

    @Override
    public Result<Void> addTask(AddSignTaskDTO addSignTaskDTO) {
        UserDTO userInfo = userClient.getUserInfo().getData();

        String studentId = userInfo.getStudentId();
        if (studentId == null || studentId.isEmpty()) {
            return Result.error("身份信息错误");
        }

        if (lambdaQuery().eq(AutoSignTask::getStudentId, studentId).count() >= 5) {
            return Result.error("最多只能添加5个签到任务");
        }

        if (lambdaQuery().eq(AutoSignTask::getStudentId, studentId).eq(AutoSignTask::getCourseId, addSignTaskDTO.getCourseId()).exists()) {
            return Result.error("该课程已存在签到任务");
        }

        AutoSignTask autoSignTask = BeanUtil.copyProperties(addSignTaskDTO, AutoSignTask.class);
        autoSignTask.setUserId(UserContext.getUserId());
        autoSignTask.setStudentId(studentId);
        autoSignTask.setStatus(SignTaskStatus.RUNNING);
        autoSignTask.setCreateTime(LocalDateTime.now());
        autoSignTask.setUpdateTime(LocalDateTime.now());

        if (!save(autoSignTask)) {
            return Result.error("添加任务失败，请重试");
        }

        signTaskScheduler.setDelaySignTask(autoSignTask);

        return Result.success();
    }

    @Override
    public Result<Void> removeTask(Long taskId) {


        boolean success = remove(new QueryWrapper<AutoSignTask>()
                .eq("id", taskId)
                .eq("user_id", UserContext.getUserId()));

        if (!success) {
            return Result.error("删除任务失败，请重试");
        }

        return Result.success();
    }
}
