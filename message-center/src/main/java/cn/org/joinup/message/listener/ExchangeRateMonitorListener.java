package cn.org.joinup.message.listener;

import cn.org.joinup.api.client.MessageClient;
import cn.org.joinup.api.dto.SendEmailMessageDTO;
import cn.org.joinup.message.config.ExchangeRateMonitorConfig;
import cn.org.joinup.message.domain.po.ExchangeRateMonitorRule;
import cn.org.joinup.message.monitor.domain.RateThresholdEvent;
import cn.org.joinup.message.service.IExchangeRateRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateMonitorListener {

    private final MessageClient messageClient;
    private final ExchangeRateMonitorConfig monitorConfig;
    private final IExchangeRateRuleService exchangeRateRuleService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue.rate.triggered.notification", durable = "true"),
            exchange = @Exchange(value = "exchange.rate.topic", type = ExchangeTypes.TOPIC),
            key = "rate.*.*.triggered"
    ))
    public void handleRateChangeEvent(RateThresholdEvent event) {
        log.info("Received rate change event: {}", event);

        try {
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
            log.error("Failed to send email for rate change event: {}", event, e);
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
        }};
    }

}
