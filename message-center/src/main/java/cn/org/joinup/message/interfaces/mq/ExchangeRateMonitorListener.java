package cn.org.joinup.message.interfaces.mq;

import cn.org.joinup.api.client.MessageClient;
import cn.org.joinup.api.dto.SendEmailMessageDTO;
import cn.org.joinup.message.infrastructure.config.ExchangeRateMonitorConfig;
import cn.org.joinup.message.domain.rate.entity.ExchangeRateMonitorRule;
import cn.org.joinup.message.domain.rate.entity.RateThresholdEventLog;
import cn.org.joinup.message.domain.rate.RateThresholdEvent;
import cn.org.joinup.message.application.rate.service.IExchangeRateRuleService;
import cn.org.joinup.message.application.rate.service.IRateThresholdEventLogService;
import cn.org.joinup.message.infrastructure.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateMonitorListener {

    private final MessageClient messageClient;
    private final ExchangeRateMonitorConfig monitorConfig;
    private final IExchangeRateRuleService exchangeRateRuleService;
    private final IRateThresholdEventLogService eventLogService;
    private final TokenUtil tokenUtil;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue.rate.triggered.notification", durable = "true"),
            exchange = @Exchange(value = "exchange.rate.topic", type = ExchangeTypes.TOPIC),
            key = "rate.*.*.triggered"
    ))
    public void handleRateChangeEvent(RateThresholdEvent event) {
        log.info("Received rate change event: {}", event);

        // Idempotency check: check if eventId already exists in database
        long count = eventLogService.lambdaQuery()
                .eq(RateThresholdEventLog::getEventId, event.getEventId())
                .count();
        if (count > 0) {
            log.info("Event {} already processed, skipping.", event.getEventId());
            return;
        }

        try {
            // 1. Persistence
            saveEventLog(event);

            // 2. Notification
            ExchangeRateMonitorRule currentRule = exchangeRateRuleService.lambdaQuery()
                    .eq(ExchangeRateMonitorRule::getId, event.getMonitorRuleSnapshot().getId())
                    .eq(ExchangeRateMonitorRule::getActive, Boolean.TRUE)
                    .one();
            if  (currentRule == null) {
                log.warn("No active rule found for id: {}", event.getMonitorRuleSnapshot().getId());
                return;
            }

            messageClient.sendEmail(SendEmailMessageDTO.builder()
                    .email(currentRule.getEmail())
                    .templateCode("email-rate")
                    .params(buildParams(event, currentRule))
                    .build());
        } catch (Exception e) {
            log.error("Failed to handle rate change event: {}", event, e);
        }
    }

    private void saveEventLog(RateThresholdEvent event) {
        try {
            RateThresholdEventLog eventLog = RateThresholdEventLog.builder()
                    .eventId(event.getEventId())
                    .ruleId(event.getMonitorRuleSnapshot().getId())
                    .currencyPair(event.getCurrencyPair())
                    .currentRate(event.getCurrentRate())
                    .threshold(event.getThreshold())
                    .triggerType(event.getTriggerType())
                    .dataSource(event.getDataSource())
                    .message(event.getMessage())
                    .triggerTime(event.getTimestamp())
                    .createTime(LocalDateTime.now())
                    .build();
            eventLogService.save(eventLog);
        } catch (Exception e) {
            log.error("Failed to save event log: {}", event, e);
        }
    }


    private Map<String, Object> buildParams(RateThresholdEvent event, ExchangeRateMonitorRule currentRule) {
        return new HashMap<>() {{
            put("baseCurrency", event.getMonitorRuleSnapshot().getBaseCurrency().getCode());
            put("quoteCurrency", event.getMonitorRuleSnapshot().getQuoteCurrency().getCode());
            put("currentRate", String.valueOf(event.getCurrentRate()));
            put("currencyPair", event.getCurrencyPair());
            put("threshold", String.valueOf(event.getThreshold()));
            put("recipientEmail", currentRule.getEmail());
            put("triggerType", event.getTriggerType());
            put("dataSource", event.getDataSource());
            put("timestamp", event.getTimestamp().toString());
            put("eventId", event.getEventId());
            put("message", event.getMessage());
            put("detailsUrl", String.format(monitorConfig.getGoogleFinanceLink(),
                    event.getMonitorRuleSnapshot().getBaseCurrency(), event.getMonitorRuleSnapshot().getQuoteCurrency()));
            put("baseCurrencyName", event.getMonitorRuleSnapshot().getBaseCurrency().getDesc());
            put("quoteCurrencyName", event.getMonitorRuleSnapshot().getQuoteCurrency().getDesc());
            put("unsubscribeUrl", buildUnsubscribeUrl(event.getMonitorRuleSnapshot()));
        }};
    }

    private String buildUnsubscribeUrl(ExchangeRateMonitorRule rule) {
        String token = tokenUtil.generateToken(
                monitorConfig.getUnsubscribeBusinessCode(),
                Map.of(
                        "ruleId", String.valueOf(rule.getId())
                ),
                2 * 24 * 3600L // 2 days expiration
        );
        return String.format(monitorConfig.getUnsubscribeLink(), rule.getId(), token);
    }

}
