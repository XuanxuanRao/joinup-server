package cn.org.joinup.course.service;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.course.domain.dto.AddSignTaskDTO;
import cn.org.joinup.course.domain.po.AutoSignTask;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IAutoSignTaskService extends IService<AutoSignTask> {
    Result<Void> addTask(AddSignTaskDTO addSignTaskDTO);

    Result<Void> removeTask(Long taskId);
}
