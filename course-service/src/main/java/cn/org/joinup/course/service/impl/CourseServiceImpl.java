package cn.org.joinup.course.service.impl;

import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.PageQuery;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.course.domain.dto.CourseQueryDTO;
import cn.org.joinup.course.domain.vo.ScheduleVO;
import cn.org.joinup.course.domain.dto.SignDTO;
import cn.org.joinup.course.domain.po.SignLog;
import cn.org.joinup.course.mapper.SignLogMapper;
import cn.org.joinup.course.service.ICourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl extends ServiceImpl<SignLogMapper, SignLog> implements ICourseService {

    private final RabbitTemplate rabbitTemplate;

    private final UserClient userClient;

    private final ObjectMapper objectMapper;

    @Override
    public Result<ScheduleVO> list(LocalDate date) {
        UserDTO userInfo = userClient.getUserInfo().getData();
        if (userInfo == null || userInfo.getStudentId() == null) {
            return Result.error("用户未认证");
        }
        return Result.success(getScheduleByDate(userInfo.getStudentId(), date));
    }

    @Override
    public ScheduleVO getScheduleByDate(String studentId, LocalDate date) {
        Map res = (Map) rabbitTemplate.convertSendAndReceive("course.direct", "course.query", new CourseQueryDTO(studentId, date));
        return objectMapper.convertValue(res, ScheduleVO.class);
    }

    @Override
    public Result<Void> sign(Integer courseScheduleId) {
        UserDTO userInfo = userClient.getUserInfo().getData();
        if (userInfo == null || userInfo.getStudentId() == null) {
            return Result.error("用户未认证");
        }
        rabbitTemplate.convertAndSend("course.direct", "course.sign", new SignDTO(userInfo.getStudentId(), courseScheduleId));
        return Result.success();
    }

    @Override
    public Result<PageResult<SignLog>> query(PageQuery query, String studentId) {
        Page<SignLog> page = page(query.toMpPage("create_time", false), new QueryWrapper<SignLog>().eq("student_id", studentId));
        return Result.success(PageResult.of(page, SignLog.class));
    }
}
