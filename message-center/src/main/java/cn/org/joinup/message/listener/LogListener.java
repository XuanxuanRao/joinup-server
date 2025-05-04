package cn.org.joinup.message.listener;

import cn.org.joinup.message.domain.po.LogEntry;
import cn.org.joinup.message.service.ILogEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
@RequiredArgsConstructor
public class LogListener {

    private final ILogEntryService logEntryService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "log.insert.queue", durable = "true"),
            exchange = @Exchange(value = "log.direct"),
            key = {"log.insert"}
    ))
    public void handleLogInsert(LogEntry logEntry) {
        logEntryService.save(logEntry);
        System.out.println("Log Inserted: " + logEntry);
    }

}
