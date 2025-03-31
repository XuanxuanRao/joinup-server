package cn.org.joinup.course.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.course.domain.CourseQueryDTO;
import cn.org.joinup.course.domain.ScheduleVO;
import cn.org.joinup.course.domain.SignDTO;
import cn.org.joinup.course.service.CourseService;
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
public class CourseServiceImpl implements CourseService {

    private final RabbitTemplate rabbitTemplate;

    private final UserClient userClient;

    private final ObjectMapper objectMapper;

    @Override
    public Result<ScheduleVO> list(LocalDate date) {
        UserDTO userInfo = userClient.getUserInfo().getData();
        log.info("userInfo = " + userInfo);
        if (userInfo == null || userInfo.getStudentId() == null) {
            return Result.error("用户未认证");
        }
        Map res = (Map) rabbitTemplate.convertSendAndReceive("course.direct", "course.query", new CourseQueryDTO(userInfo.getStudentId(), date));
        return Result.success(objectMapper.convertValue(res, ScheduleVO.class));
    }

    @Override
    public Result<Void> sign(Integer courseId) {
        UserDTO userInfo = userClient.getUserInfo().getData();
        if (userInfo == null || userInfo.getStudentId() == null) {
            return Result.error("用户未认证");
        }
        rabbitTemplate.convertAndSend("course.direct", "course.sign", new SignDTO(userInfo.getStudentId(), courseId));
        return Result.success();
    }
}
