package cn.org.joinup.course.service.impl;

import cn.org.joinup.course.domain.dto.CourseQueryDTO;
import cn.org.joinup.course.domain.dto.SignDTO;
import cn.org.joinup.course.domain.vo.ScheduleVO;
import cn.org.joinup.course.service.CourseCrawler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServerCourseCrawler implements CourseCrawler {

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    @Override
    public String supportChannel() {
        return "server";
    }

    @Override
    public ScheduleVO getScheduleByDate(String studentId, LocalDate date) {
        Map res = (Map) rabbitTemplate.convertSendAndReceive("course.direct", "course.query", new CourseQueryDTO(studentId, date));
        return objectMapper.convertValue(res, ScheduleVO.class);
    }

    @Override
    public Boolean signCourse(String studentId, Integer courseScheduleId) {
        try {
            rabbitTemplate.convertAndSend("course.direct", "course.sign", new SignDTO(studentId, courseScheduleId));
        } catch (Exception e) {
            log.error("Error signing course for studentId:{} courseScheduleId:{} - {}", studentId, courseScheduleId, e.getMessage());
            return false;
        }
        return true;
    }
}
