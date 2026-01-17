package cn.org.joinup.message.listener;

import cn.org.joinup.message.monitor.domain.RateThresholdEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateMonitorListener {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue.rate.triggered.notification", durable = "true"),
            exchange = @Exchange(value = "exchange.rate.topic", type = ExchangeTypes.TOPIC),
            key = "rate.*.*.triggered"
    ))
    public void handleRateChangeEvent(RateThresholdEvent event) {
        log.info("Received rate change event: {}", event);
//        event.get
    }


}
