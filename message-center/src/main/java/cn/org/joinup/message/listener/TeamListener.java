package cn.org.joinup.message.listener;

import cn.org.joinup.api.dto.TeamDTO;
import cn.org.joinup.api.dto.UserJoinTeamDTO;
import cn.org.joinup.message.constant.MQConstant;
import cn.org.joinup.message.domain.po.Conversation;
import cn.org.joinup.message.service.IConversationParticipantService;
import cn.org.joinup.message.service.IConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TeamListener {

    private final IConversationService conversationService;

    private final IConversationParticipantService conversationParticipantService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConstant.USER_JOIN_TEAM_QUEUE, durable = "true"),
            exchange = @org.springframework.amqp.rabbit.annotation.Exchange(value = MQConstant.TEAM_EXCHANGE),
            key = {MQConstant.USER_JOIN_TEAM_KEY}
    ))
    public void handleUserJoinTeam(UserJoinTeamDTO userJoinTeamDTO) {
        Optional.ofNullable(conversationService.lambdaQuery()
                .eq(Conversation::getTeamId, userJoinTeamDTO.getUserId())
                .eq(Conversation::getType, "group")
                .one())
                .map(Conversation::getId)
                .ifPresentOrElse(
                    conversationId -> conversationParticipantService.addParticipant(conversationId, userJoinTeamDTO.getUserId()),
                    () -> log.error("Error: conversation not found for user {} join team {}",
                            userJoinTeamDTO.getUserId(), userJoinTeamDTO.getTeamId())
                );
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConstant.TEAM_ESTABLISH_QUEUE, durable = "true"),
            exchange = @org.springframework.amqp.rabbit.annotation.Exchange(value = MQConstant.TEAM_EXCHANGE),
            key = {MQConstant.TEAM_ESTABLISH_KEY}
    ))
    public void handleTeamEstablish(TeamDTO teamDTO) {
        Conversation conversation = new Conversation();
        conversation.setTeamId(teamDTO.getId());
        conversation.setType("group");
        conversation.setCreateTime(teamDTO.getCreateTime());
        conversationService.save(conversation);
        conversationParticipantService.addParticipant(conversation.getId(), teamDTO.getCreatorUserId());
    }


}
