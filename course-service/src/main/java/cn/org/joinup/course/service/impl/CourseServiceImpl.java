package cn.org.joinup.course.service.impl;

import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.PageQuery;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.course.config.CrawlerConfig;
import cn.org.joinup.course.domain.dto.CourseQueryDTO;
import cn.org.joinup.course.domain.vo.ScheduleVO;
import cn.org.joinup.course.domain.po.SignLog;
import cn.org.joinup.course.mapper.SignLogMapper;
import cn.org.joinup.course.service.CourseCrawler;
import cn.org.joinup.course.service.ICourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@Slf4j
public class CourseServiceImpl extends ServiceImpl<SignLogMapper, SignLog> implements ICourseService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private UserClient userClient;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private CrawlerConfig crawlerConfig;

    private final Map<String, CourseCrawler> courseCrawlers;

    public CourseServiceImpl(List<CourseCrawler> courseCrawlerList) {
        courseCrawlers = courseCrawlerList.stream()
                .collect(java.util.stream.Collectors.toMap(CourseCrawler::supportChannel, v -> v));
    }

    @Override
    public Result<ScheduleVO> list(LocalDate date) {
        UserDTO userInfo = userClient.getUserInfo().getData();
        if (userInfo == null || userInfo.getStudentId() == null || !userInfo.getVerified()) {
            return Result.error("用户未认证");
        }
        return Result.success(courseCrawlers.get(crawlerConfig.getChannel()).getScheduleByDate(userInfo.getStudentId(), date));
    }

    @Override
    public ScheduleVO getScheduleByDate(String studentId, LocalDate date) {
        Map res = (Map) rabbitTemplate.convertSendAndReceive("course.direct", "course.query", new CourseQueryDTO(studentId, date));
        return objectMapper.convertValue(res, ScheduleVO.class);
    }

    @Override
    public Result<Void> sign(Integer courseScheduleId) {
        UserDTO userInfo = userClient.getUserInfo().getData();
        if (userInfo == null || userInfo.getStudentId() == null || !userInfo.getVerified()) {
            return Result.error("用户未认证");
        }
        if (courseCrawlers.get(crawlerConfig.getChannel()).signCourse(userInfo.getStudentId(), courseScheduleId)) {
            return Result.success();
        } else {
            return Result.error("签到失败");
        }
    }

    @Override
    public Result<PageResult<SignLog>> query(PageQuery query) {
        String studentId = userClient.getUserInfo().getData().getStudentId();
        Page<SignLog> page = page(query.toMpPage("create_time", false), new QueryWrapper<SignLog>().eq("student_id", studentId));
        return Result.success(PageResult.of(page, SignLog.class));
    }
}
