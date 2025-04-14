package cn.org.joinup.message.service;

import cn.org.joinup.message.domain.SendMessageModel;

public interface IMessageRecordService {

    void sendMessage(SendMessageModel sendMessageModel);

}
