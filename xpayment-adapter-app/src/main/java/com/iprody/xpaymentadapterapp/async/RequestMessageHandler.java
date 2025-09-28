package com.iprody.xpaymentadapterapp.async;


import com.iprody.async.*;
import com.iprody.xpaymentadapterapp.api.XPaymentProviderGateway;
import com.iprody.xpaymentadapterapp.checkstate.PaymentStateCheckRegistrar;
import com.iprody.xpaymentadapterapp.dto.ChargeResponseDto;
import com.iprody.xpaymentadapterapp.dto.CreateChargeRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;

@Component
@Slf4j
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {

    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final XPaymentProviderGateway xPaymentProviderGateway;
    private final PaymentStateCheckRegistrar paymentStateCheckRegistrar;

    @Autowired
    public RequestMessageHandler(AsyncSender<XPaymentAdapterResponseMessage> sender,
        XPaymentProviderGateway xPaymentProviderGateway, PaymentStateCheckRegistrar paymentStateCheckRegistrar) {
        this.sender = sender;
        this.xPaymentProviderGateway = xPaymentProviderGateway;
        this.paymentStateCheckRegistrar = paymentStateCheckRegistrar;
    }

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        log.info("Payment request received paymentGuid - {}, amount - {}, currency - {}",
            message.getPaymentGuid(), message.getAmount(), message.getCurrency());

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
            responseMessage.setStatus(chargeResponse.getStatus());

            responseMessage.setOccurredAt(OffsetDateTime.now());
            sender.send(responseMessage);
            paymentStateCheckRegistrar.register(
                chargeResponse.getId(),
                chargeResponse.getOrder(),
                chargeResponse.getAmount(),
                chargeResponse.getCurrency()
            );
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
}
