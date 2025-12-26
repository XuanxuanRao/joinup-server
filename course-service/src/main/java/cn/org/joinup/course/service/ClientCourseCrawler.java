package cn.org.joinup.course.service;

import cn.org.joinup.api.client.WebSocketClient;
import cn.org.joinup.api.dto.CommandRequestDTO;
import cn.org.joinup.common.constant.SystemConstant;
import cn.org.joinup.course.domain.vo.ScheduleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Service
public class ClientCourseCrawler {

    private final WebSocketClient webSocketClient;

//    private final CrawlerConfig crawlerConfig;

    public ScheduleVO getScheduleByDate(String studentId, LocalDate date) {
        Long targetUserId = findAvailableUserId();
        return null;
    }

    private Long findAvailableUserId() {
        Set<Long> onlineUserIds = webSocketClient.getOnlineUsers(SystemConstant.EXTERNAL_USER_TYPE, "buaa-classhopper-android")
                .getData();
        for (Long onlineUserId : onlineUserIds) {
            log.info("onlineUserId:{}", onlineUserId);
            var result = webSocketClient.pushCommand(buildTestNetworkConditionCommand(onlineUserId));
            if (result.getData().getSuccess()) {
                return onlineUserId;
            }
        }

        return 1L;
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
}
