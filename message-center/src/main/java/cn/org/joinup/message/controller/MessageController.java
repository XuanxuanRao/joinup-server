package cn.org.joinup.message.controller;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.domain.EmailSendModel;
import cn.org.joinup.message.domain.SendMessageModel;
import cn.org.joinup.message.service.IMessageRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final IMessageRecordService messageRecordService;

    @PostMapping("/email/send")
    public Result<Void> sendEmail(@RequestBody EmailSendModel sendMessageModel) {
        messageRecordService.sendMessage(sendMessageModel);
        return Result.success();
    }

    @PostMapping("/wechat/send")
    public Result<Void> sendWechat(@RequestBody SendMessageModel sendMessageModel) {
        messageRecordService.sendMessage(sendMessageModel);
        return Result.success();
    }

}
