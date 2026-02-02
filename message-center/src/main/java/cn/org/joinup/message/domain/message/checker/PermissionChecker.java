package cn.org.joinup.message.domain.message.checker;

import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.application.chat.service.IConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component("permissionChecker")
@RequiredArgsConstructor
public class PermissionChecker {

    private final IConversationService conversationService;

    private final UserClient userClient;

    public boolean hasAccessToConversation(String conversationId) {
        return conversationService.in(conversationId, UserContext.getUserId()) ||
                "ADMIN".equals(userClient.getUserInfo().getData().getRole());
    }
}
