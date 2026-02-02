package cn.org.joinup.message.application.message.service;

import cn.org.joinup.message.domain.message.model.SendMessageModel;

public interface IMessageRecordService {

    void sendMessage(SendMessageModel sendMessageModel);

}
