package com.iprody.xpaymentadapterapp.checkstate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import com.iprody.xpaymentadapterapp.checkstate.handler.PaymentStatusCheckHandler;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
@Slf4j
public class PaymentStateCheckListener {

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String routingKey;
    private final String dlxExchangeName;
    private final String dlxRoutingKey;
    private final PaymentStatusCheckHandler paymentStatusCheckHandler;

    @Value("${app.rabbitmq.max-retries:60}")
    private int maxRetries;
    @Value("${app.rabbitmq.interval-ms:60000}")
    private long intervalMs;

    @Autowired
    public PaymentStateCheckListener(RabbitTemplate rabbitTemplate,
        @Value("${app.rabbitmq.exchange-name}") String exchangeName,
        @Value("${app.rabbitmq.queue-name}") String routingKey,
        @Value("${app.rabbitmq.dlx-exchange-name}") String dlxExchangeName,
        @Value("${app.rabbitmq.dlx-routing-key}") String dlxRoutingKey,
        PaymentStatusCheckHandler paymentStatusCheckHandler
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.dlxExchangeName = dlxExchangeName;
        this.dlxRoutingKey = dlxRoutingKey;
        this.paymentStatusCheckHandler = paymentStatusCheckHandler;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue-name}")
    public void handle(PaymentCheckStateMessage message, Message raw) {
        final MessageProperties props = raw.getMessageProperties();
        final int retryCount = (int) props.getHeaders().getOrDefault("x-retry-count", 0);
        log.info("Processing payment {} with retryCount {}", message.getChargeGuid(), retryCount);
        final boolean paid = paymentStatusCheckHandler.handle(message.getChargeGuid());
        if (paid) {
            return;
        }
        if (retryCount < maxRetries) {
            final PaymentCheckStateMessage newMessage = new PaymentCheckStateMessage(message.getChargeGuid(),
                message.getPaymentGuid(), message.getAmount(), message.getCurrency()
            );
            rabbitTemplate.convertAndSend(
                exchangeName,
                routingKey,
                newMessage, m -> {
                    m.getMessageProperties().setHeader("x-delay", intervalMs);
                    m.getMessageProperties().setHeader("x-retry-count", retryCount + 1);
                    return m;
                }
            );
        } else {
            rabbitTemplate.convertAndSend(
                dlxExchangeName,
                dlxRoutingKey,
                message, m -> {
                    m.getMessageProperties().setHeader("x-retry-count", retryCount);
                    m.getMessageProperties().setHeader("x-final-status", "TIMEOUT");
                    m.getMessageProperties().setHeader("x-original-queue", props.getConsumerQueue());
                    return m;
                }
            );
        }
    }
}
