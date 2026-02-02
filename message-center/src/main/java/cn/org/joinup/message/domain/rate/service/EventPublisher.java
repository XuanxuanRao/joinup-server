package cn.org.joinup.message.domain.rate.service;

import cn.org.joinup.message.infrastructure.config.ExchangeRateMonitorConfig;
import cn.org.joinup.message.domain.rate.RateThresholdEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ExchangeRateMonitorConfig monitorConfig;
    
    // For suppression: key = triggerType, value = lastTriggerTime
    private final Map<String, Long> lastTriggeredMap = new ConcurrentHashMap<>();

    public void publish(RateThresholdEvent event) {
        if (!monitorConfig.getEvent().isEnabled()) {
            log.info("[EventPublisher] Event publishing is disabled. Event: {}", event);
            return;
        }

        // Suppression Logic
        long now = System.currentTimeMillis();
        long suppressDurationMs = monitorConfig.getEvent().getSuppressDurationInMinutes() * 60 * 1000L;
        Long lastTriggered = lastTriggeredMap.get(event.getTriggerType());

        if (lastTriggered != null && (now - lastTriggered) < suppressDurationMs) {
            log.info("[EventPublisher] Event suppressed. Type: {}, Last triggered: {}, Suppress duration: {}ms", 
                    event.getTriggerType(), lastTriggered, suppressDurationMs);
            return;
        }

        try {
            String routingKey = String.format(monitorConfig.getEvent().getRoutingKeyFormat(),
                    event.getMonitorRuleSnapshot().getBaseCurrency(), event.getMonitorRuleSnapshot().getQuoteCurrency());
            log.info("[EventPublisher] Publishing event to Exchange: {}, RoutingKey: {}", 
                    monitorConfig.getEvent().getExchangeName(), routingKey);
            
            rabbitTemplate.convertAndSend(
                    monitorConfig.getEvent().getExchangeName(),
                    routingKey,
                    event
            );
            log.info("[EventPublisher] Event published successfully: {}", event);
            lastTriggeredMap.put(event.getTriggerType(), now);
        } catch (Exception e) {
            log.error("[EventPublisher] Failed to publish event to MQ. Log content: {}", event, e);
        }
    }
}
