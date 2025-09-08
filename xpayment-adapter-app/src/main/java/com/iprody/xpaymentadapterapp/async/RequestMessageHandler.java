package com.iprody.xpaymentadapterapp.async;


import com.iprody.async.*;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {

    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final AsyncSender<XPaymentAdapterRequestMessage> deadLetterSender;
    private final XPaymentAdapterRequestValidator validator;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public RequestMessageHandler(AsyncSender<XPaymentAdapterResponseMessage> sender,
        AsyncSender<XPaymentAdapterRequestMessage> deadLetterSender,
        XPaymentAdapterRequestValidator validator) {
        this.sender = sender;
        this.deadLetterSender = deadLetterSender;
        this.validator = validator;
    }

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        if (!validator.isValid(message)) {
            log.warn("The message wasn't handled and was sent to DLT: {}", message);
            deadLetterSender.send(message);
            return;
        }

        scheduler.schedule(() -> {
            final XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());

            if (message.getAmount().remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0) {
                responseMessage.setStatus(XPaymentAdapterStatus.SUCCEEDED);
            } else {
                responseMessage.setStatus(XPaymentAdapterStatus.CANCELED);
            }

            responseMessage.setTransactionRefId(UUID.randomUUID());
            responseMessage.setOccurredAt(OffsetDateTime.now());
            sender.send(responseMessage);
            log.info("Response is sent: {}", responseMessage);
        }, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}
