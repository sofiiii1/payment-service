package com.iprody.xpaymentadapterapp.async;


import com.iprody.async.*;
import com.iprody.xpaymentadapterapp.api.XPaymentProviderGateway;
import com.iprody.xpaymentadapterapp.dto.ChargeResponseDto;
import com.iprody.xpaymentadapterapp.dto.CreateChargeRequestDto;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
@Slf4j
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {

    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final AsyncSender<XPaymentAdapterRequestMessage> deadLetterSender;
    private final XPaymentAdapterRequestValidator validator;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final XPaymentProviderGateway xPaymentProviderGateway;

    @Autowired
    public RequestMessageHandler(AsyncSender<XPaymentAdapterResponseMessage> sender,
        AsyncSender<XPaymentAdapterRequestMessage> deadLetterSender,
        XPaymentAdapterRequestValidator validator, XPaymentProviderGateway xPaymentProviderGateway) {
        this.sender = sender;
        this.deadLetterSender = deadLetterSender;
        this.validator = validator;
        this.xPaymentProviderGateway = xPaymentProviderGateway;
    }

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        log.info("Payment request received paymentGuid - {}, amount - {}, currency - {}",
            message.getPaymentGuid(), message.getAmount(), message.getCurrency());
        if (!validator.isValid(message)) {
            log.warn("The message wasn't handled and was sent to DLT: {}", message);
            deadLetterSender.send(message);
            return;
        }
        final CreateChargeRequestDto createChargeRequest = new CreateChargeRequestDto();
        createChargeRequest.setAmount(message.getAmount());
        createChargeRequest.setCurrency(message.getCurrency());
        createChargeRequest.setOrder(message.getPaymentGuid());
        try {
            final ChargeResponseDto chargeResponse = xPaymentProviderGateway.createCharge(createChargeRequest);
            log.info("Payment request with paymentGuid - {} is sent for payment processing. Current status - ",
                chargeResponse.getStatus());
            final XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(chargeResponse.getOrder());
            responseMessage.setTransactionRefId(chargeResponse.getId());
            responseMessage.setAmount(chargeResponse.getAmount());
            responseMessage.setCurrency(chargeResponse.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.valueOf(chargeResponse.getStatus()));

            responseMessage.setOccurredAt(OffsetDateTime.now());
            sender.send(responseMessage);
        } catch (RestClientException ex) {
            log.error("Error in time of sending payment request with paymentGuid - {}", message.getPaymentGuid(), ex);
            final XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.CANCELED);
            responseMessage.setOccurredAt(OffsetDateTime.now());
            sender.send(responseMessage);
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}
