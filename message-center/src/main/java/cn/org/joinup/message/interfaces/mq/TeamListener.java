package cn.org.joinup.message.interfaces.mq;

import cn.org.joinup.api.dto.TeamDTO;
import cn.org.joinup.api.dto.UserJoinTeamDTO;
import cn.org.joinup.api.dto.UserQuitTeamDTO;
import cn.org.joinup.message.infrastructure.constant.MQConstant;
import cn.org.joinup.message.domain.chat.entity.Conversation;
import cn.org.joinup.message.application.chat.service.IConversationParticipantService;
import cn.org.joinup.message.application.chat.service.IConversationService;
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
                .eq(Conversation::getTeamId, userJoinTeamDTO.getTeamId())
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
            value = @Queue(value = MQConstant.USER_QUIT_TEAM_QUEUE, durable = "true"),
            exchange = @org.springframework.amqp.rabbit.annotation.Exchange(value = MQConstant.TEAM_EXCHANGE),
            key = {MQConstant.USER_QUIT_TEAM_KEY}
    ))
    public void handleUserQuitTeam(UserQuitTeamDTO userQuitTeamDTO) {
        Optional.ofNullable(conversationService.lambdaQuery()
                        .eq(Conversation::getTeamId, userQuitTeamDTO.getTeamId())
                        .eq(Conversation::getType, "group")
                        .one())
                .map(Conversation::getId)
                .ifPresentOrElse(
                        conversationId -> conversationParticipantService.removeParticipant(conversationId, userQuitTeamDTO.getUserId()),
                        () -> log.error("Error: conversation not found for user {} quit team {}",
                                userQuitTeamDTO.getUserId(), userQuitTeamDTO.getTeamId())
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
