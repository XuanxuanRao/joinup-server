package cn.org.joinup.message.service;

import cn.org.joinup.message.domain.po.VerifyLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IVerifyLogService extends IService<VerifyLog> {
    LocalDateTime lastSendTime(String account);

    Integer getSendCount(String account, LocalDate date);

}