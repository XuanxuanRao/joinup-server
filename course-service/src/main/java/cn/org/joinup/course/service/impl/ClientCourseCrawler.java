package cn.org.joinup.course.service.impl;

import cn.org.joinup.api.client.WebSocketClient;
import cn.org.joinup.api.dto.CommandRequestDTO;
import cn.org.joinup.common.constant.SystemConstant;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.course.config.CrawlerConfig;
import cn.org.joinup.course.domain.vo.ScheduleVO;
import cn.org.joinup.course.service.CourseCrawler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Service
public class ClientCourseCrawler implements CourseCrawler {

    private final WebSocketClient webSocketClient;

    private final ObjectMapper objectMapper;

    private final CrawlerConfig crawlerConfig;

    @Override
    public String supportChannel() {
        return "client";
    }

    @Override
    public ScheduleVO getScheduleByDate(String studentId, LocalDate date) {
        Long targetUserId = findAvailableUserId();
        if (targetUserId == null) {
            log.warn("No available client found for fetching schedule");
            return null;
        }

        log.info("Find available userId for get schedule:{}", targetUserId);
        return doFetchSchedule(targetUserId, studentId, date);
    }

    @Override
    public Boolean signCourse(String studentId, Integer courseScheduleId) {
        Long targetUserId = findAvailableUserId();
        if (targetUserId == null) {
            log.warn("No available client found for signing course");
            return false;
        }

        log.info("Find available userId for sign course:{}", targetUserId);
        var commandRequestDTO = buildSignCourseCommand(targetUserId, studentId, courseScheduleId);
        var result = webSocketClient.pushCommand(commandRequestDTO);
        return Objects.equals(result.getCode(), Result.SUCCESS) && result.getData().getSuccess();
    }

    private Long findAvailableUserId() {
        Set<Long> onlineUserIds = crawlerConfig.getAppKeys().stream()
                .map(appKey -> webSocketClient.getOnlineUsers(SystemConstant.EXTERNAL_USER_TYPE, appKey).getData())
                .collect(HashSet::new, Set::addAll, Set::addAll);
        for (Long onlineUserId : onlineUserIds) {
            var result = webSocketClient.pushCommand(buildTestNetworkConditionCommand(onlineUserId));
            if (result.getData().getSuccess()) {
                return onlineUserId;
            }
        }
        return null;
    }

    private ScheduleVO doFetchSchedule(Long userId, String studentId, LocalDate date) {
        var commandRequestDTO = buildGetScheduleCommand(userId, studentId, date);
        var result = webSocketClient.pushCommand(commandRequestDTO);
        if (!Objects.equals(result.getCode(), Result.SUCCESS) || !result.getData().getSuccess()) {
            return null;
        }

        // 解析结果，构建 ScheduleVO 返回
        log.info("Fetch schedule result: {}", result.getData().getData());
        try {
            return objectMapper.convertValue(result.getData().getData(), ScheduleVO.class);
        } catch (Exception e) {
            log.error("Error while converting schedule data", e);
        }
        return null;
    }

    private CommandRequestDTO buildTestNetworkConditionCommand(Long userId) {
        CommandRequestDTO commandRequestDTO = new CommandRequestDTO();
        commandRequestDTO.setExecutorId(userId);
        commandRequestDTO.setCommandType("ping");
        commandRequestDTO.setParams(Map.of(
                "target", "iclass.buaa.edu.cn"
        ));
        return commandRequestDTO;
    }

    private CommandRequestDTO buildGetScheduleCommand(Long userId, String studentId, LocalDate date) {
        CommandRequestDTO commandRequestDTO = new CommandRequestDTO();
        commandRequestDTO.setExecutorId(userId);
        commandRequestDTO.setCommandType("getSchedule");
        commandRequestDTO.setParams(Map.of(
                "studentId", studentId,
                "date", date.toString()
        ));
        return commandRequestDTO;
    }

    private CommandRequestDTO buildSignCourseCommand(Long userId, String studentId, Integer courseScheduleId) {
        CommandRequestDTO commandRequestDTO = new CommandRequestDTO();
        commandRequestDTO.setExecutorId(userId);
        commandRequestDTO.setCommandType("signCourse");
        commandRequestDTO.setParams(Map.of(
                "studentId", studentId,
                "courseScheduleId", courseScheduleId
        ));
        return commandRequestDTO;
    }
}
